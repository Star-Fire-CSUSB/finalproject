package com.finalproject.app;

import android.content.Intent;
import android.os.Bundle;
import org.jetbrains.annotations.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent( this, MainActivity.class));
        finish();
    }

}
