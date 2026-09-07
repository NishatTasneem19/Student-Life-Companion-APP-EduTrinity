package com.example.stu;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Locale;

public class TFLiteRiskPredictor {

    private static final String MODEL_FILE = "sleep_risk_classifier.tflite";
    private static final float RISK_THRESHOLD = 0.5f;
    private Interpreter tflite;

    public TFLiteRiskPredictor(Context context) throws IOException {
        loadModelFromAssets(context);
    }

    private void loadModelFromAssets(Context context) throws IOException {
        try (FileInputStream fis = new FileInputStream(context.getAssets().openFd(MODEL_FILE).getFileDescriptor())) {
            FileChannel fileChannel = fis.getChannel();
            long startOffset = context.getAssets().openFd(MODEL_FILE).getStartOffset();
            long declaredLength = context.getAssets().openFd(MODEL_FILE).getDeclaredLength();

            ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
            buffer.order(ByteOrder.nativeOrder());
            tflite = new Interpreter(buffer);
            Log.d("TFLiteRiskPredictor", "Model loaded successfully.");
        } catch (IOException e) {
            Log.e("TFLiteRiskPredictor", "Error loading TFLite model: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Predicts insomnia risk based on 7 days of sleep duration.
     * @param lastSevenDurations A list containing the 7 most recent sleep durations (float).
     * @return 1 for Insomnia Risk, 0 for No Risk. Returns -1 on error.
     */
    public int predictRisk(List<Float> lastSevenDurations) {
        if (tflite == null || lastSevenDurations.size() != 7) {
            Log.e("TFLiteRiskPredictor", "Error: Interpreter is null or data size is not 7.");
            return -1; // Error or insufficient data
        }

        // Input shape [1,7]
        float[][] input = new float[1][7];
        for (int i = 0; i < 7; i++) {
            input[0][i] = lastSevenDurations.get(i);
        }

        // Output shape [1,1]
        float[][] output = new float[1][1];

        // Run the model inference
        tflite.run(input, output);

        float riskProbability = output[0][0];

        // *** নতুন ফিক্স: মডেলের প্রোবাবিলিটি লগ করা হলো ***
        Log.i("TFLitePredict", String.format(Locale.US, "Prob: %.4f, Threshold: %.2f", riskProbability, RISK_THRESHOLD));

        // ML Model's output is classified based on the threshold.
        return (riskProbability > RISK_THRESHOLD) ? 1 : 0;
    }

    /**
     * Closes the TFLite interpreter to free up resources.
     */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
    }
}