package com.example.vetlinia.ActivitiesAndFragments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.vetlinia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        imageView = findViewById(R.id.splash);

        imageView.setScaleX(0);
        imageView.setScaleY(0);

        imageView.animate().scaleXBy(1).scaleYBy(1).setDuration(3000);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle());
            }
        }
        else {
            TimerTask task = new TimerTask()
            {
                @Override
                public void run()
                {
                    Intent i = new Intent(getApplicationContext(), CardWizardActivity.class);
                    startActivity(i);
                    finish();
                }
            };
            new Timer().schedule(task, 4000);
        }
    }

    @Override
    public void onBackPressed() {
    }
}