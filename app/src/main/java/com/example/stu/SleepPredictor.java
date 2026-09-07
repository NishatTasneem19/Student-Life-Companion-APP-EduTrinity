package com.example.stu;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class SleepPredictor {

    private Interpreter interpreter;
    private String[] labels;
    private float mean, std;

    public SleepPredictor(Context context) throws IOException {
        // Load TFLite model
        ByteBuffer buffer = loadModelFile(context, "sleep_model.tflite");
        interpreter = new Interpreter(buffer);

        // Load JSON metadata using Gson
        InputStream is = context.getAssets().open("sleep_meta.json");
        Gson gson = new Gson();
        JsonObject meta = gson.fromJson(new InputStreamReader(is), JsonObject.class);

        // labels
        int labelCount = meta.getAsJsonArray("labels").size();
        labels = new String[labelCount];
        for (int i = 0; i < labelCount; i++) {
            labels[i] = meta.getAsJsonArray("labels").get(i).getAsString();
        }

        // mean & std
        mean = meta.get("mean").getAsFloat();
        std = meta.get("std").getAsFloat();
    }

    private ByteBuffer loadModelFile(Context context, String filename) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(filename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    public String predictSleepClass(float avgDuration) {
        float[][] input = new float[1][1];
        input[0][0] = (avgDuration - mean) / std;

        float[][] output = new float[1][labels.length];
        interpreter.run(input, output);

        int bestIndex = 0;
        float max = output[0][0];
        for (int i = 1; i < labels.length; i++) {
            if (output[0][i] > max) {
                max = output[0][i];
                bestIndex = i;
            }
        }
        return labels[bestIndex];
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
}
