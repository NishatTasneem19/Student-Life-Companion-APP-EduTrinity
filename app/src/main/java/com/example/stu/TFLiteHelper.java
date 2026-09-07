package com.example.stu;

import android.content.Context;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteHelper {

    private Interpreter tflite;

    // Load model from assets
    public void loadModelFromAssets(Context context, String modelName) throws IOException {
        MappedByteBuffer tfliteModel = loadModelFile(context, modelName);
        tflite = new Interpreter(tfliteModel);
    }

    private MappedByteBuffer loadModelFile(Context context, String MODEL_FILE) throws IOException {
        FileInputStream fis = context.getAssets().openFd(MODEL_FILE).createInputStream();
        FileChannel fileChannel = fis.getChannel();
        long startOffset = context.getAssets().openFd(MODEL_FILE).getStartOffset();
        long declaredLength = context.getAssets().openFd(MODEL_FILE).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Predict sleep quality class
    public int predictSleepQuality(float duration, float weekday, float consistency) {
        if (tflite == null) throw new IllegalStateException("Model not loaded");

        // Input shape [1,3]
        float[][] input = new float[1][3];
        input[0][0] = duration;
        input[0][1] = weekday;
        input[0][2] = consistency;

        // Output shape [1,4] (4 classes)
        float[][] output = new float[1][4];

        tflite.run(input, output);

        // Find max index
        float[] row = output[0];
        int bestIndex = 0;
        float max = row[0];
        for (int i = 1; i < row.length; i++) {
            if (row[i] > max) {
                max = row[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public void close() {
        if (tflite != null) tflite.close();
    }
}
