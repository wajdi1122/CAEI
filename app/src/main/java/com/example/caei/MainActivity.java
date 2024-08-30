package com.example.caei;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Afficher l'écran de démarrage pendant 2 secondes, puis passer à MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Principale.class);
                startActivity(intent);
                finish(); // Fermer l'activité d'écran de démarrage
            }
        }, 2000); // 2000 millisecondes = 2 secondes
    }
}
