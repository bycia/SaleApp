package com.example.saleapp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saleapp.databinding.ActivitySaleBinding;


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
    }
    public void addClick(View view){

    }
    public void payClick(View view){
        receipt.addSaleItem(new SaleItem(3,"ddd",3,0));
//        receipt.addPayItem(new PayItem(0,500));
        int receiptNUmb--
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog errorDialog = alertDialogBuilder.create();
        errorDialog.setMessage(String.valueOf(saveReceipt(receipt)));
        errorDialog.setTitle("OK");
        errorDialog.show();
    }
    public native int saveReceipt(receiptData r);
}
