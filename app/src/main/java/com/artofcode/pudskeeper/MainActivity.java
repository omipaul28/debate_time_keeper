package com.artofcode.pudskeeper;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView, currentDebatorDis;
    private ProgressBar circularProgressBar;
    private Button startBtn, resetBtn, nextBtn, pauseBtn;
    private ImageButton resetAllBtn, optionBtn;
    private MediaPlayer media;
    private Vibrator vibrator;
    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private int currentStage = 0;

    private final int[] stageDurations = {5, 5, 5, 5, 5, 5, 3, 3}; // in minutes
    private final String[] stageNames = {
            "Prime Minister", "Leader of the Opposition", "Deputy Prime Minister",
            "Deputy Leader of the Opposition", "Member of the Government",
            "Member of the Opposition", "Government Whip", "Opposition Whip"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        updateStageDisplay();

        startBtn.setOnClickListener(v -> startStage());
        pauseBtn.setOnClickListener(v -> pauseStage());
        nextBtn.setOnClickListener(v -> goToNextStage());
        resetBtn.setOnClickListener(v -> resetCurrentStage());
        resetAllBtn.setOnClickListener(v -> resetAllStages());
        optionBtn.setOnClickListener(v -> showDeveloperInfo());
    }

    private void initializeViews() {
        timerTextView = findViewById(R.id.timerTextView);
        currentDebatorDis = findViewById(R.id.currentPlayerDis);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        startBtn = findViewById(R.id.startBtn);
        pauseBtn = findViewById(R.id.pauseBtn);
        resetBtn = findViewById(R.id.resetBtn);
        nextBtn = findViewById(R.id.nextBtn);
        resetAllBtn = findViewById(R.id.resetAllBtn);
        optionBtn = findViewById(R.id.optionBtn);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void updateStageDisplay() {
        currentDebatorDis.setText(stageNames[currentStage]);
        timerTextView.setText(String.format("%02d:00", stageDurations[currentStage]));
        circularProgressBar.setProgress(100);
        timeLeftInMillis = stageDurations[currentStage] * 60 * 1000; // Set default time for the stage
    }

    private void startStage() {
        if (!isTimerRunning) {
            countDownTimer = createCountDownTimer(timeLeftInMillis); // Use remaining time or full time
            countDownTimer.start();
            isTimerRunning = true;

            startBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
        }
    }

    private void pauseStage() {
        if (isTimerRunning) {
            countDownTimer.cancel();  // Pause the timer
            isTimerRunning = false;

            startBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
        }
    }

    private CountDownTimer createCountDownTimer(long millis) {
        return new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;  // Update the remaining time

                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

                int progress = (int) ((millisUntilFinished / (float) millis) * 100);
                circularProgressBar.setProgress(progress);

                if (currentStage < 6) {  // First six stages (5 minutes each)
                    if ((minutes == 4 && seconds == 0) || (minutes == 1 && seconds == 0) || (minutes == 0 && seconds == 0)) {
                        playSignal();
                    }
                } else {  // Last two stages (3 minutes each)
                    if ((minutes == 2 && seconds == 0) || (minutes == 1 && seconds == 0) || (minutes == 0 && seconds == 0)) {
                        playSignal();
                    }
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;

                startBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.GONE);
                resetBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
            }
        };
    }

    private void goToNextStage() {
        if (currentStage < stageNames.length - 1) {
            currentStage++;
            updateStageDisplay();
            stopStage();  // Ensure the timer is stopped

            startBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
            resetBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    private void resetCurrentStage() {
        stopStage();
        updateStageDisplay();
    }

    private void resetAllStages() {
        stopStage();
        currentStage = 0;
        updateStageDisplay();

        startBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.GONE);
        resetBtn.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
    }

    private void stopStage() {
        if (isTimerRunning && countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;

            startBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
        }
    }

    private void playSignal() {
        if (media != null) {
            media.release();
        }
        media = MediaPlayer.create(MainActivity.this, R.raw.bell);
        media.start();
        media.setOnCompletionListener(mp -> media.release());

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500);  // Vibrate for 500 milliseconds
        }
    }

    private void showDeveloperInfo() {
        new AlertDialog.Builder(this)
                .setTitle("About the Developers")
                .setMessage("The app credit goes to Premier University Debating Society (PUDS)\nApp Developer: Omi Paul")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
