package com.MORR.ChatMe;

import com.MORR.ChatMe.ChatMeActivity;
import com.MORR.chatme.R;
import com.MORR.speechbubble.ChatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity {
	public static String TAG1 = "com.MORR.ChatMe.firstName";
	public static String TAG2 = "com.MORR.ChatMe.lastName";
	public static String TAG3 = "com.MORR.ChatMe.phone";
	
	private EditText firstName, lastName, phone;
	private Button register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		firstName = (EditText) findViewById(R.id.firstName);
		lastName = (EditText) findViewById(R.id.lastName);
		phone = (EditText) findViewById(R.id.phone);
		register = (Button) findViewById(R.id.button1);
		register.setOnClickListener(new OnClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				String first = firstName.getText().toString();
				String last = lastName.getText().toString();
				String phoneNumber = phone.getText().toString();
				
				
				if(first.length()==0 || last.length()==0 || phoneNumber.length()==0 ){
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"There are empty fields!", Toast.LENGTH_LONG).show();
						}
					};

					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(runnable);
					return;
				}
				
				
				
				if ((!isValidName(first) || !isValidName(last)) && !isValidPhoneNumber(phoneNumber)){
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Invalid User Name & Phone number!", Toast.LENGTH_LONG).show();
						}
					};

					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(runnable);
					return;
				}
				
				if (!isValidName(first) || !isValidName(last)) {
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Invalid User Name", Toast.LENGTH_LONG).show();
						}
					};

					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(runnable);
					return;
				}

				if (!isValidPhoneNumber(phoneNumber)) {
					Runnable runnable = new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"Invalid Phone Number", Toast.LENGTH_LONG).show();
						}
					};

					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(runnable);
					
					return;
				}

				Intent intent = new Intent(getApplicationContext(),
						ChatActivity.class);
                SharedPreferences sharedPreferences=getSharedPreferences("LOGIN",0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(TAG1,first);
                editor.putString(TAG2,last);
                editor.putString(TAG3,phoneNumber);
                editor.putString("reg","1");
                editor.commit();
				startActivity(intent);
                finish();
			}

		});

	}

	protected boolean isValidPhoneNumber(String number) {
		if (number == null || number.length() <= 10)
			return false;
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		return true;
	}

	
	private boolean isValidName(String name) {
		if (name.contains(" "))
			return false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if ((c < 'a' && c > 'z') && (c < 'A' && c > 'Z') && c != '_')
				return false;
		}
		return true;
	}

}
