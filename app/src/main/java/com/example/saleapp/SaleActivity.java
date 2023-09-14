package com.example.saleapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saleapp.databinding.ActivitySaleBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SaleActivity extends AppCompatActivity {
    public class SaleItem{
        int itemId;
        String itemName;
        int itemPrice;
        int vatRate;
        SaleItem(int itemId,String itemName,int itemPrice,int vatRate){
            this.itemId=itemId;
            this.itemName=itemName;
            this.itemPrice=itemPrice;
            this.vatRate=vatRate;
        }
    }
    private class PayItem{
        int PayType;
        int PayAmount;
        PayItem(int PayType,int PayAmount){
            this.PayType=PayType;
            this.PayAmount=PayAmount;
        }
    }

    private class receiptData{
        String receiptStartDate;
        SaleItem[] items = new SaleItem[256];
        PayItem[] payItems=new PayItem[16];
        public void addSaleItem(SaleItem s){
            for(int i=0;i<items.length;i++)
                if(items[i]==null) {
                    items[i] = s;
                    break;
                }
        }
        public SaleItem getSaleItem(int itemId){
            for(SaleItem s:items)
                if(s.itemId==itemId)
                    return s;
            return null;
        }

        public void addPayItem(PayItem p){
            for(int i=0;i<payItems.length;i++)
                if(payItems[i]==null) {
                    payItems[i] = p;
                    break;
                }
        }
    }

    static {
        System.loadLibrary("saleapp");
    }
    private ActivitySaleBinding binding;
    receiptData receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        binding = ActivitySaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.informationText.setText("");
        receipt=new receiptData();
        receipt.receiptStartDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
    public void addClick(View view){
        receipt.addSaleItem(new SaleItem(1,"a",4,10));
//        receipt.addSaleItem(new SaleItem(Integer.parseInt(binding.productid.getText().toString()),
//                binding.ProductName.getText().toString(),
//                Integer.parseInt(binding.price.getText().toString()),
//                Integer.parseInt(binding.VAT.getText().toString())
//        ));
    }
    public void payClick(View view){
        receipt.addPayItem(new PayItem(1,4));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog errorDialog = alertDialogBuilder.create();
        errorDialog.setMessage(String.valueOf(saveReceipt(receipt)));
        errorDialog.setTitle("OK");
        errorDialog.show();
    }
    public void cancelClick(View view){
        binding.productid.setText("");
        binding.ProductName.setText("");
        binding.price.setText("");
        binding.VAT.setText("");
        receipt=new receiptData();
        receipt.receiptStartDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
    public native int saveReceipt(receiptData r);
}
