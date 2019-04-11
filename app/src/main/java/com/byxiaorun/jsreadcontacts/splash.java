package com.byxiaorun.jsreadcontacts;

import android.content.Intent;
import android.os.Bundle;

import com.example.student.jsreadcontacts.R;

public class splash extends CheckPermissionsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread myThred=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(5000);
                    Intent it=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(it);
                    finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThred.start();
    }
}
