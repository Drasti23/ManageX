
package ca.gbc.managex;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import ca.gbc.managex.user.LoginActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get reference to the ProgressBar
        ProgressBar loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Animate progress over 3 seconds
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(loadingProgressBar, "progress", 0, 100);
        progressAnimator.setDuration(2000); // 2 seconds
        progressAnimator.start();

        // Delay for 3 seconds before transitioning to the MainActivity
        new Handler().postDelayed(() -> {
            // Start the main activity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the SplashActivity
        }, 1000); // 1000 milliseconds = 1 seconds
    }
}
