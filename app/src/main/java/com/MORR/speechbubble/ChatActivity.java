package com.MORR.speechbubble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.MORR.ChatMe.RegistrationActivity;
import com.MORR.DB.DBHelper;
import com.MORR.chatme.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    ArrayList<String> users;
    static ArrayList<String> onlineUsers;
    ArrayList<Drawable> images;

    String firstName;
    String secondName;
    String phoneNumber;
    String ip;

    CustomList adapter;
    String currentUser;

    Socket socket;
    PrintWriter out;
    OutputStream outStream;
    InputStream inStream;
    Map<String, AwesomeAdapter> chatBox;

    ListView chatBoxList;
    EditText message;
    //    DBConnection db;
    DBHelper db;
    static Map<String, String> userNames;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        chatBoxList = (ListView) findViewById(R.id.chatboxlist);
        message = (EditText) findViewById(R.id.text);

//      db = new DBConnection(this);
        db = new DBHelper(this);
        users = new ArrayList<String>();
        onlineUsers = new ArrayList<String>();
        images = new ArrayList<Drawable>();
        chatBox = new HashMap<String, AwesomeAdapter>();
        userNames = new HashMap<String, String>();
        adapter = new CustomList(ChatActivity.this, users, images, onlineUsers);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", 0);
        firstName = sharedPreferences.getString(RegistrationActivity.TAG1, "");
        secondName = sharedPreferences.getString(RegistrationActivity.TAG2, "");
        phoneNumber = sharedPreferences.getString(RegistrationActivity.TAG3, "");
        ip = "41.38.245.31";
        currentUser = "Public";
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        users.add(currentUser);
        chatBox.put(currentUser, new AwesomeAdapter(this, new ArrayList<Message>()));
        chatBoxList.setAdapter(chatBox.get(currentUser));
        images.add(getResources().getDrawable(R.drawable.users));
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        setTitle("Go Chat Users");

        createSocket();


        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("ChatMe Users");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }


        Button btn = (Button) findViewById(R.id.speak);
        btn.setClickable(true);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PackageManager pm = getPackageManager();
                List<ResolveInfo> activities = pm
                        .queryIntentActivities(new Intent(
                                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
                if (activities.size() == 0) {
                    Toast.makeText(ChatActivity.this,
                            "Voice recognizer not present", Toast.LENGTH_SHORT)
                            .show();
                } else {
//                    Intent intent = new Intent(
//                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//
//                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
//                            getClass().getPackage().getName());
//                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
//                    startActivityForResult(intent,
//                            VOICE_RECOGNITION_REQUEST_CODE);

                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
                    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                message.append(matches.get(0) + " ");
            }
        }
    }

    //    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void sendMessage(View view) {
        String t = message.getText().toString();
        if (t.isEmpty())
            return;
        if (currentUser.equals("Public")) {
            send("msgpublic," + t);
        } else {
            send("msg," + currentUser + "," + t);
            db.insert("true", currentUser, t, chatBox.get(currentUser).mMessages.size() + "");
        }
        chatBox.get(currentUser).mMessages.add(new Message(t, true));
        chatBox.get(currentUser).notifyDataSetChanged();
        chatBoxList.setSelection(chatBox.get(currentUser).mMessages.size());
        message.setText("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_me, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.clear) {
            db.delete(currentUser);
            chatBox.get(currentUser).mMessages.clear();
            chatBox.get(currentUser).notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            String f = users.get(position);
            Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();
        }
    }

    private void selectItem(int position) {

        currentUser = users.get(position);
        chatBoxList.setAdapter(chatBox.get(currentUser));
        chatBox.get(currentUser).notifyDataSetChanged();
        mDrawerList.setItemChecked(position, true);
        setTitle(users.get(position));
        mDrawerLayout.closeDrawers();

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = userNames.get(title);
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public void createSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Handler h = new Handler(Looper.getMainLooper());
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, 5000);
                    outStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                    out = new PrintWriter(outStream, true);
                    out.println(firstName + "," + secondName + "," + phoneNumber);//"Android,Hi,012222222");
                    h.post(new Runnable() {

                        @Override
                        public void run() {
                            if (socket != null)
                                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), "Service not available now!", Toast.LENGTH_LONG).show();
                        }
                    });
                    readListener();
                } catch (UnknownHostException e1) {

                    e1.printStackTrace();

                } catch (IOException e1) {

                    e1.printStackTrace();
                }
            }
        }).start();
    }

    public void readListener() {
        Thread f = new Thread() {
            @Override
            public void run() {
                BufferedReader inputStream = new BufferedReader(
                        new InputStreamReader(inStream));
                while (socket != null && socket.isConnected()) {
                    try {
                        final String recvedMessage = inputStream.readLine();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                parseMessage(recvedMessage);
                            }
                        });

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        f.start();
    }

    public void parseMessage(String message) {
        if (message == null)
            return;
        String splited[];
        splited = message.split(",");
        if (splited[0].equals("msg")) { // In private room from phoneForSender
            String phoneForSender = splited[1];
            String Message = splited[2];
            db.insert("false", phoneForSender, splited[2], chatBox.get(phoneForSender).mMessages.size() + "");
            chatBox.get(phoneForSender).mMessages.add(new Message(splited[2], false));
            chatBox.get(currentUser).notifyDataSetChanged();
            MediaPlayer mp = MediaPlayer.create(this, R.raw.patatap);
            mp.start();
            if (!phoneForSender.equals(currentUser)) {
                showPopup(ChatActivity.this, new Point(0, 300), userNames.get(phoneForSender), phoneForSender);
            }
        } else if (splited[0].equals("msgpublic")) { // In public room from phoneForSender
            String phoneForSender = splited[1];
            String Message = splited[2];
            chatBox.get("Public").mMessages.add(new Message(userNames.get(phoneForSender) + ":" + splited[2], false));
            chatBox.get(currentUser).notifyDataSetChanged();
            MediaPlayer mp = MediaPlayer.create(this, R.raw.patatap);
            mp.start();
            if (!currentUser.equals("Public")) {
                showPopup(ChatActivity.this, new Point(0, 300), userNames.get(phoneForSender), "In public!");
            }
        }


        splited = message.split("-");
        if (splited[0].equals("all")) {
            ArrayList<String[]> Users = new ArrayList<String[]>();
            for (int i = 1; i < splited.length; i++) {
                String[] temp = splited[i].split(",");
                Log.d("All", temp[0] + "," + temp[1] + "," + temp[2]);
                if (temp[0].equals(phoneNumber) || users.contains(temp[0]))
                    continue;
                Users.add(temp);
                users.add(temp[0]);
                userNames.put(temp[0], temp[1] + " " + temp[2]);
                images.add(getResources().getDrawable(R.drawable.user));
                chatBox.put(temp[0], new AwesomeAdapter(this, new ArrayList<Message>()));
                ArrayList<Message> data = db.getData(temp[0]);
                for (int k = 0; k < data.size(); k++) {
                    chatBox.get(temp[0]).mMessages.add(data.get(k));
                }
            }
            adapter.notifyDataSetChanged();

            // you now hava all users in users array list
        } else if (splited[0].equals("online")) {
            ArrayList<String[]> Users = new ArrayList<String[]>();
            for (int i = 1; i < splited.length; i++) {
                String[] temp = splited[i].split(",");
                Log.d("Online", temp[0] + "," + temp[1] + "," + temp[2]);
                if (!temp[0].equals(phoneNumber)) {
                    if (!onlineUsers.contains(temp[0]))
                        onlineUsers.add(temp[0]);
                }
                Users.add(temp);
            }
            adapter.notifyDataSetChanged();
            // you now hava online users in users array list
        } else if (splited[0].equals("offline")) {
            ArrayList<String[]> Users = new ArrayList<String[]>();
            for (int i = 1; i < splited.length; i++) {
                String[] temp = splited[i].split(",");
                Log.d("Offline", temp[0] + "," + temp[1] + "," + temp[2]);
                if (!temp[0].equals(phoneNumber)) {
                    if (onlineUsers.contains(temp[0])) {
                        onlineUsers.remove(temp[0]);
                    }
                }
                Users.add(temp);
            }
            adapter.notifyDataSetChanged();
            // you now hava offline users in users array list
        }
    }

    public void send(String m) {
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            out.println(m);
            out.flush();
        }
    }


    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    private void showPopup(final Activity context, Point p, String name, String phone) {
        int popupWidth = 600;
        int popupHeight = 480;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        Log.d("Layout", (layout == null) + "");

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        TextView nameText = (TextView) layout.findViewById(R.id.sender_name);
        TextView phoneText = (TextView) layout.findViewById(R.id.sender_phone);

        nameText.setTextColor(Color.WHITE);
        phoneText.setTextColor(Color.WHITE);

        nameText.setText(name);
        phoneText.setText(phone);


        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);


    }

    @Override
    public void onDestroy() {
        send("dc");
        Toast.makeText(this, " disconnected !!!", Toast.LENGTH_LONG).show();
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        send("dc");
        Toast.makeText(this, " disconnected !!!", Toast.LENGTH_LONG).show();
        finish();
        super.onBackPressed();
    }

//    @Override
//    protected void onUserLeaveHint() {
//        send("dc");
//        Toast.makeText(this, " disconnected !!!", Toast.LENGTH_LONG).show();
//        finish();
//        super.onUserLeaveHint();
//    }
}