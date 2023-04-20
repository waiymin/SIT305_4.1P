package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText workoutEditText, restEditText;
    private TextView workoutTextView, restTextView;
    private ProgressBar timerProgressBar;
    private Button startButton, stopButton;

    private CountDownTimer workoutTimer, restTimer;
    private Handler handler = new Handler();
    private boolean isWorkoutPhase = true;
    private long workoutTime, restTime;
    private long workoutTimeRemaining, restTimeRemaining;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workoutEditText = findViewById(R.id.workout_time_edit_text);
        restEditText = findViewById(R.id.rest_time_edit_text);
        workoutTextView = findViewById(R.id.workout_time_text_view);
        restTextView = findViewById(R.id.rest_time_text_view);
        timerProgressBar = findViewById(R.id.progress_bar);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });
    }

    private void startTimer() {
        // Get input values from EditTexts
        String workoutTimeInput = workoutEditText.getText().toString();
        String restTimeInput = restEditText.getText().toString();

        // Validate input values
        if (workoutTimeInput.isEmpty() || restTimeInput.isEmpty()) {
            Toast.makeText(this, "Please enter workout and rest durations", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert input values to longs
        workoutTime = Long.parseLong(workoutTimeInput) * 1000;
        restTime = Long.parseLong(restTimeInput) * 1000;

        // Start the timer
        startWorkoutPhase();
    }

    private void stopTimer() {
        if (workoutTimer != null) {
            workoutTimer.cancel();
            workoutTimer = null;
        }
        if (restTimer != null) {
            restTimer.cancel();
            restTimer = null;
        }
        handler.removeCallbacksAndMessages(null);
        workoutTextView.setText("");
        restTextView.setText("");
        timerProgressBar.setProgress(0);
    }

    private void startWorkoutPhase() {
        isWorkoutPhase = true;
        workoutTimeRemaining = workoutTime;
        updateUI();
        workoutTimer = new CountDownTimer(workoutTimeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                workoutTimeRemaining = millisUntilFinished;
                updateUI();
            }

            @Override
            public void onFinish() {
                workoutTimer = null;
                startRestPhase();
                vibrateDevice();
            }
        }.start();
    }

    private void startRestPhase() {
        isWorkoutPhase = false;
        restTimeRemaining = restTime;
        updateUI();
        restTimer = new CountDownTimer(restTimeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                restTimeRemaining = millisUntilFinished;
                updateUI();
            }

            @Override
            public void onFinish() {
                restTimer = null;
                startWorkoutPhase();
                vibrateDevice();
            }
        }.start();
    }    private void updateUI() {
        if (isWorkoutPhase) {
            workoutTextView.setText(getTimeString(workoutTimeRemaining));
            int progress = (int) (workoutTimeRemaining * 100 / workoutTime);
            timerProgressBar.setProgress(progress);
        } else {
            restTextView.setText(getTimeString(restTimeRemaining));
            int progress = (int) (restTimeRemaining * 100 / restTime);
            timerProgressBar.setProgress(progress);
        }
    }

    private String getTimeString(long millis) {
        int seconds = (int) millis / 1000;
        return String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(1000);
        }
    }

}



