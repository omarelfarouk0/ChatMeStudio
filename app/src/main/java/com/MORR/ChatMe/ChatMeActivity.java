package com.MORR.ChatMe;

import com.MORR.chatme.R;
import com.MORR.speechbubble.ChatActivity;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class ChatMeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_me);

        AnimationDrawable rocketAnimation;

        ImageView rocketImage = (ImageView) findViewById(R.id.imageView);
        rocketImage.setBackgroundResource(R.drawable.animatorframe);
        rocketAnimation = (AnimationDrawable) rocketImage.getBackground();

        rocketAnimation.start();

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
                    String reg = sharedPreferences.getString("reg", "");
                    if (reg.equals("")) {
                        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(),
                                ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        if( !isConnectingToInternet() ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        finish();
                    }

                }
            }).start();
            Toast.makeText(this,"Check your internet connection!",Toast.LENGTH_LONG).show();
        }else {
            thread.start();
        }
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }


}
