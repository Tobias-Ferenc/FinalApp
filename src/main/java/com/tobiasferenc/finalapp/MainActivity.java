package com.tobiasferenc.finalapp;


import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    EditText usernameS, passwordS;
    ImageView PFPS;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Register(View view){
        usernameS = findViewById(R.id.username);
        passwordS = findViewById(R.id.PASSWORD);

        String username = usernameS.getText().toString();
        String password = passwordS.getText().toString();

        if(username.isEmpty() || password.isEmpty() ){
            CharSequence text = "obě hodnoty";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this , text, duration);
            toast.show();

        }else{
            CharSequence text = "pecka";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this , text, duration);
            toast.show();
        }
    }



}