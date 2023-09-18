package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("ResponseCode", "99");
                setResult(Activity.RESULT_OK,getIntent());
                finish();
            }
        });
        findViewById(R.id.CashButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("ResponseCode", "0");
                getIntent().putExtra("PaymentType",1);
                setResult(Activity.RESULT_OK,getIntent());
                finish();
            }
        });
        findViewById(R.id.CreditButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("ResponseCode", "0");
                getIntent().putExtra("PaymentType",2);
                setResult(Activity.RESULT_OK,getIntent());
                finish();
            }
        });
        findViewById(R.id.QRButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("ResponseCode", "0");
                getIntent().putExtra("PaymentType",3);
                setResult(Activity.RESULT_OK,getIntent());
                finish();
            }
        });
    }
}


