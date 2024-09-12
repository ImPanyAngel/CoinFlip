package com.example.coinflip;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private ImageView coinFront;
    private ImageView coinBack;
    private AnimatorSet backAnimation = new AnimatorSet();
    private AnimatorSet frontAnimation = new AnimatorSet();
    boolean isFront = true;
    private final Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnFlip = findViewById(R.id.btnFlip);
        tvResult = findViewById(R.id.tvResult);
        coinFront = findViewById(R.id.ivFront);
        coinBack = findViewById(R.id.ivBack);

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        coinFront.setCameraDistance(8000 * scale);
        coinBack.setCameraDistance(8000 * scale);

        frontAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_animation);
        backAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_animation);

        btnFlip.setOnClickListener(v -> {
            tvResult.setText(R.string.flipping);
            boolean flip = random.nextBoolean();
            flipCoin(flip);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void flipCoin(boolean flip) {
        tvResult.setText(R.string.flipping);
        String result = flip ? getString(R.string.heads) : getString(R.string.tails);

        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 0; i < 4; i++) {
            handler.postDelayed(() -> {
                if (isFront) {
                    frontAnimation.setTarget(coinFront);
                    backAnimation.setTarget(coinBack);
                    frontAnimation.start();
                    backAnimation.start();
                    isFront = false;
                } else {
                    frontAnimation.setTarget(coinBack);
                    backAnimation.setTarget(coinFront);
                    frontAnimation.start();
                    backAnimation.start();
                    isFront = true;
                }
            }, i * 1000); // Delay of 1000ms between each iteration
        }

// Delay the final result display after the last animation iteration
        handler.postDelayed(() -> {
            if (isFront && getString(R.string.tails).equals(result)) {
                frontAnimation.setTarget(coinFront);
                backAnimation.setTarget(coinBack);
                frontAnimation.start();
                backAnimation.start();
                isFront = false;
            } else if (!isFront && getString(R.string.heads).equals(result)) {
                frontAnimation.setTarget(coinBack);
                backAnimation.setTarget(coinFront);
                backAnimation.start();
                frontAnimation.start();
                isFront = true;
            }

            // Show the result after the final flip is complete
            handler.postDelayed(() -> tvResult.setText(result), 100); // Additional delay to ensure the final spin completes

        }, 4 * 1000); // Delay for the total duration of all spins

    }
}