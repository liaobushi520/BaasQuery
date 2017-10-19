package com.liaobusi.baasquery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.liaobushi.query.BaasCall;
import com.liaobushi.query.BaasQuery;
import com.liaobusi.baasquery.api.BaasService;
import com.liaobusi.baasquery.bean.SmileyPackageBean;

import java.util.List;


public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        final BaasService baasService = BaasQuery.query(BaasService.class);
        if (baasService != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("success", baasService.listSmileyPackages("小", 0, 2000, 2).size() + "");
                }
            }).start();
            baasService.listSmileyPackages2("小", 0, 2000, 2).enqueue(new BaasCall.Callback<List<SmileyPackageBean>>() {
                @Override
                public void onResponse(List<SmileyPackageBean> response) {
                    Log.e("success", "" + response.size() + "");
                }
            });
        }
    }
}
