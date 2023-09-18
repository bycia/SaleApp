package com.example.saleapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.saleapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    // Used to load the 'saleapp' library on application startup.
    static {
        System.loadLibrary("saleapp");
    }

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init(getApplication().getDataDir().toString()+"/data.db");
        binding.idfield.requestFocus();
    }
    public void buttonClick(View view){
        boolean err=false;
        if(binding.idfield.getText().length()==0) {
            binding.idfield.setError("Cannot be blank");
            err=true;
        }
        if(binding.passfield.getText().length()==0) {
            binding.passfield.setError("Cannot be blank");
            err=true;
        }
        if(err)
            return;

        boolean userId_and_PasswordMatched=login(Integer.parseInt(binding.idfield.getText().toString()),binding.passfield.getText().toString());
        if(userId_and_PasswordMatched) {
            SwitchToSaleActivity();
        }
        else
        {
            new AlertDialog.Builder(this)
                .setMessage("ID and Password didnt match")
                .setTitle("Error")
                .show();
        }
    }

    private void SwitchToSaleActivity() {
        Intent switchActivityIntent = new Intent(this, SaleActivity.class);
        startActivity(switchActivityIntent);
    }

    /**
     * A native method that is implemented by the 'saleapp' native library,
     * which is packaged with this application.
     */

    public native void init(String path);

    public native boolean login(int id, String password);
}