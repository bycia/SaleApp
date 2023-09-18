package com.example.saleapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saleapp.databinding.ActivitySaleBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SaleActivity extends AppCompatActivity {

    final int REQUEST_CODE_FOR_PAYMENTAPP=1;
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
        public int getTotalAmount(){
            int total=0;
            for(SaleItem i:this.items)
                if(i!=null)
                    total+=i.itemPrice;
                else
                    break;
            return total;
        }

        public int getTheUnpaidAmount()
        {
            int total=this.getTotalAmount();
            for(PayItem i:this.payItems)
                if(i!=null)
                    total-=i.PayAmount;
                else
                    break;
            return total;
        }
    }

    static {
        System.loadLibrary("saleapp");
    }
    private ActivitySaleBinding binding;
    final Context ctx=this;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    receiptData receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        binding = ActivitySaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.informationText.setText("Total: 0");
        receipt=new receiptData();
        receipt.receiptStartDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        DigitsKeyListener numbers=DigitsKeyListener.getInstance("0123456789");

        binding.ProductId.setFilters(new InputFilter[]
                {
                        new InputFilterMinMax(1, 9999),
                        new InputFilter.LengthFilter(4)
                });
        binding.ProductId.setKeyListener(numbers);
        binding.ProductName.setFilters(new InputFilter[]
                {
                     new InputFilter.LengthFilter(20)
                });
        binding.Price.setFilters(new InputFilter[]{
                new InputFilterMinMax(1,1000),
                new InputFilter.LengthFilter(4)
        });
        binding.Price.setKeyListener(numbers);
        binding.VAT.setFilters(new InputFilter[]{
                new InputFilterMinMax(0,99),
                new InputFilter.LengthFilter(2)
        });
        binding.VAT.setKeyListener(numbers);

    }
    public void addClick(View view){
        Boolean err=false;
        if(binding.ProductId.getText().toString().length()==0) {
            binding.ProductId.setError("Cannot be blank");
            err=true;
        }
        if(binding.ProductName.getText().toString().length()==0){
            binding.ProductName.setError("Cannot be blank");
            err=true;
        }
        if(binding.Price.getText().toString().length()==0){
            binding.Price.setError("Cannot be blank");
            err=true;
        }
        if(binding.VAT.getText().toString().length()==0){
            binding.VAT.setError("Cannot be blank");
            err=true;
        }
        if(err)
            return;
        receipt.addSaleItem(new SaleItem(Integer.parseInt(binding.ProductId.getText().toString()),
                binding.ProductName.getText().toString(),
                Integer.parseInt(binding.Price.getText().toString()),
                Integer.parseInt(binding.VAT.getText().toString())
        ));
        binding.informationText.setText("Total: "+String.valueOf(receipt.getTotalAmount()));
        new AlertDialog.Builder(this)
                .setMessage("It's added")
                .show();
        binding.ProductName.setText("");
        binding.ProductId.setText("");
        binding.Price.setText("");
        binding.VAT.setText("");
        binding.ProductId.requestFocus();
    }
    public void payClick(View view){
        final int UnpaidAmount=receipt.getTheUnpaidAmount();
        if (UnpaidAmount==0)
            return;
        EditText payAmount=new EditText(this);
        payAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        payAmount.setFilters(new InputFilter[]
                {
                        new InputFilterMinMax(1, UnpaidAmount),
                        new InputFilter.LengthFilter(String.valueOf(UnpaidAmount).length())
                });
        payAmount.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        new AlertDialog.Builder(ctx)
                .setTitle("Pay amount")
                .setMessage("Please enter the amount you'd like to pay")
                .setView(payAmount)
                .setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(payAmount.getText().toString().length()>0)
                        {
                            //PaymentApp
                            Intent PaymentApp = new Intent();
                            PaymentApp.setClassName("com.example.paymentapp","com.example.paymentapp.MainActivity");
                            PaymentApp.putExtra("payAmount",Integer.parseInt(payAmount.getText().toString()));
                            PaymentApp.putExtra("UnpaidAmount",UnpaidAmount);
                            activityLauncher.launch(PaymentApp,result->{
                                if(result.getResultCode()==Activity.RESULT_OK){
//                                    new AlertDialog.Builder(ctx)
//                                            .setMessage("OK")
//                                            .show();
                                    String responseCode = result.getData().getStringExtra("ResponseCode");
                                    int payAmount = result.getData().getIntExtra("payAmount", 0);
                                    int UnpaidAmount = result.getData().getIntExtra("UnpaidAmount", 0);
                                    int PaymentType = result.getData().getIntExtra("PaymentType", 0);
                                    if (responseCode != null && responseCode.equals("0") == true) {
                                        receipt.addPayItem(new PayItem(PaymentType, payAmount));
                                        if (UnpaidAmount-payAmount == 0) {
                                            int receiptNo = saveReceipt(receipt);
                                            if (receiptNo > 0) {
                                                new AlertDialog.Builder(ctx)
                                                        .setTitle("Receipt no: " + receiptNo)
                                                        .setMessage("Receipt is created successfully")
                                                        .show();
                                            } else {
                                                new AlertDialog.Builder(ctx)
                                                        .setTitle("Opps!")
                                                        .setMessage("Something went wrong.")
                                                        .show();
                                            }
                                            cancelClick(null);
                                            binding.informationText.setText("Total: 0");
                                        } else {
                                            binding.informationText.setText("Total: " + (UnpaidAmount-payAmount));
                                        }
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
    }
    public void cancelClick(View view){
        binding.ProductId.setText("");
        binding.ProductName.setText("");
        binding.Price.setText("");
        binding.VAT.setText("");
        binding.ProductId.requestFocus();
        receipt=new receiptData();
        receipt.receiptStartDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        binding.informationText.setText("Total: 0");
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
    public native int saveReceipt(receiptData r);

}
