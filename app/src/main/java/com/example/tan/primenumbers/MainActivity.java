package com.example.tan.primenumbers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText txtFrom, txtTo;
    private RadioButton rBtnPrim, rBtnSec1, rBtnSec2;
    private ListView lstPrime;
    private ArrayAdapter<Long> adapter;
    // List<Long> for the content of the listview component and an associated adapter
    private List<Long> primes = new ArrayList<>();
    private Handler messageHandler;
    private Handler primeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtFrom = findViewById(R.id.etFrom);
        txtTo = findViewById(R.id.etTo);
        rBtnPrim = findViewById(R.id.rBtnPrim);
        rBtnSec1 = findViewById(R.id.rBtnSec1);
        rBtnSec2 = findViewById(R.id.rBtnSec2);
        lstPrime = findViewById(R.id.lstPrimes);
        lstPrime.setAdapter(adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, primes));
    }

    public void onCalculate(View view) {
        try{
            long from = Long.parseLong(txtFrom.getText().toString());
            long to = Long.parseLong(txtTo.getText().toString());
            if (from > 0 && to > from && to - from <= 100){
                if(rBtnPrim.isChecked()) calc1(from, to);
                else if (rBtnSec1.isChecked()) calc2(from, to);
                else calc3(from, to);
                return;
            }
        } catch (Exception ex){}
    }

    private void calc3(long from, long to) {
        final long a = from;
        final long b = to;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(long t = a; t <= b; ++t) if (isPrime(t))
                {
                    Message msg = primeHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putLong("prime", t);
                    msg.setData(bundle);
                    primeHandler.sendMessage(msg);
                }
                messageHandler.sendEmptyMessage(0);
            }
        };
        (new Thread(runnable)).start();
    }

    private void calc2(long from, long to) {
        final long a = from;
        final long b = to;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(long t = a; t <= b; ++t) if (isPrime(t))
                {
                    adapter.add(t);
                }
            }
        };
        (new Thread(runnable)).start();
    }

    private void calc1(long a, long b) {
        for (long t = a; t <= b; t++) if (isPrime(t))
        {
            adapter.add(t);
        }
    }

    private boolean isPrime(long n) {
        if (n == 2 || n == 3 || n == 5 || n == 7) return true;
        if (n < 11 || n % 2 == 0) return false;
        for (long t = 3, m = (long)Math.sqrt(n) + 1; t <= m; t += 2)
            if (n % t == 0)
                return false;
        return true;
    }


    public void onClear(View view) {
    }
}
//Both classes override the method handleMessage() which is a callback method
// performed by the primary thread. Difference which parameters are transmitted among others
class MessageHandler extends Handler{
    private Context context;
    public MessageHandler(Context context){ this.context = context; }

    @Override
    public void handleMessage(Message msg) {
        Toast.makeText(context, "Primes generated", Toast.LENGTH_SHORT).show();
    }
}
class PrimeHandler extends Handler{
    private Context context;
    private ArrayAdapter<Long> adapter;
    public PrimeHandler(Context context, ArrayAdapter<Long> adapter){
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        Long prime = bundle.getLong("prime");
        adapter.add(prime);
    }
}
