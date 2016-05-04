package com.MORR.speechbubble;

import java.util.ArrayList;

import com.MORR.chatme.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CustomList extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<String> web;
    ArrayList<Drawable> imageId;
    ArrayList<String> onlineUsers;

    public CustomList(Activity context, ArrayList<String> web, ArrayList<Drawable> imageId, ArrayList<String> onlineUsers) {
        super(context, R.layout.drawer_list_item, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.onlineUsers = onlineUsers;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.drawer_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView state = (TextView) rowView.findViewById(R.id.state);
        TextView online = (TextView) rowView.findViewById(R.id.online);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web.get(position));
        online.setTextColor(Color.parseColor("#C0C0C0"));
        online.setTextSize(10);
        if (web.get(position).equals("Public")) {
            online.setTextColor(Color.parseColor("#6666FF"));
            online.setText("  " + String.valueOf(onlineUsers.size()) + " Online!");
        } else if (onlineUsers.contains(web.get(position))) {
            state.setBackgroundResource(R.drawable.online);
        } else {
            state.setBackgroundResource(R.drawable.offline);
        }
        online.setText(ChatActivity.userNames.get(web.get(position)));
        imageView.setImageDrawable(imageId.get(position));
        return rowView;
    }
}