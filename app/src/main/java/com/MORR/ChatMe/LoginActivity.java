package com.MORR.ChatMe;



import com.MORR.chatme.R;
import com.MORR.speechbubble.ChatActivity;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class LoginActivity extends ActionBarActivity {
	ActionBarActivity acc = this;
	LinearLayout layout;
	int width;
	int height;
	Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		layout = (LinearLayout) findViewById(R.id.progressbar_view);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		new Task().execute();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
				super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	@Override
	protected void onDestroy() {
//		db.close();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	class Task extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			layout.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			layout.setVisibility(View.GONE);
			Intent intent=new Intent(context,ChatActivity.class );
			startActivity(intent);
            finish();
			super.onPostExecute(result);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			LoginActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView txt = (TextView) findViewById(R.id.loading);
					txt.setText(txt.getText() + "\nDatabase loaded");
				}
			});


			LoginActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView txt = (TextView) findViewById(R.id.loading);
					txt.setText(txt.getText() + "\nDatabase tables loaded");
				}
			});

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			LoginActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView txt = (TextView) findViewById(R.id.loading);
					txt.setText(txt.getText() + "\nconnecting...");
				}
			});
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			final String number = tm.getDeviceId();

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LoginActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView txt = (TextView) findViewById(R.id.loading);
					txt.setText(txt.getText() + "\nusing ID:"+number);
				}
			});
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
