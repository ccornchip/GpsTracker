package com.example.gpstracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private boolean serviceIsStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        serviceIsStarted = false;
        updateButtonText();
        mButton.setOnClickListener(v -> {
            serviceIsStarted = !serviceIsStarted;
            updateButtonText();
        });
    }

    private void updateButtonText() {
        if (serviceIsStarted) {
            mButton.setText(R.string.start_text);
        } else {
            mButton.setText(R.string.stop_text);
        }
    }
}