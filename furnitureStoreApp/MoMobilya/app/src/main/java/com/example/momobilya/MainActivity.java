package com.example.momobilya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.momobilya.databinding.ActivityMainBinding;
import com.google.firebase.firestore.model.Values;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

    }

    public void login(View view){
        String admin=binding.kullaniciAdiEditText.getText().toString();
        String password=binding.sifreEditText.getText().toString();
        if(admin.matches("admin") && password.matches("123")){
            Intent intent=new Intent(MainActivity.this,DetailsActivity.class);
            startActivity(intent);

        }
    }
}