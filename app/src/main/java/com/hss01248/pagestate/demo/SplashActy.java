package com.hss01248.pagestate.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;




/**
 * Created by huangshuisheng on 2017/10/16.
 */

public class SplashActy extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acty_splash);
       findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this,CustomUIActy.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this,XmlActivity.class));
                break;
        }
    }
}
