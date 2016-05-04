package com.MORR.DB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.MORR.speechbubble.Message;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "history.db";
    public static final String TABLE_NAME = "messages";
    public static final String ISMINE = "isMine";
    public static final String PERSON = "person";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " " +
                        "(" + ISMINE + " text," + PERSON + " text," + MESSAGE + " text," + TIME + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "");
        onCreate(db);
    }

    public boolean insert(String isMine, String person, String message, String order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ISMINE, isMine);
        contentValues.put(PERSON, person);
        contentValues.put(MESSAGE, message);
        contentValues.put(TIME, order);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<Message> getData(String person) {
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where "+PERSON+"=" + person + " ORDER BY " + TIME + "", null);
        Cursor res = db.query(TABLE_NAME, new String[]{MESSAGE, ISMINE}, PERSON + "=?", new String[]{person}, null,null ,TIME );
        res.moveToFirst();
        ArrayList<Message> data = new ArrayList<Message>();
        while (!res.isAfterLast()) {
            String ismine = res.getString(res.getColumnIndex(ISMINE));
            String message = res.getString(res.getColumnIndex(MESSAGE));
            data.add(new Message(message, Boolean.valueOf(ismine)));
            res.moveToNext();
        }
        return data;
    }

//    public int numberOfRows() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
//        return numRows;
//    }

//    public boolean updateContact(Integer id, String name, String phone, String email, String street, String place) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.update("contacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
//        return true;
//    }

    public void delete(String person) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, PERSON + "=?", new String[]{person});
//        db.delete(TABLE_NAME,null,null);
    }

    public ArrayList getAllCotacts() {

        ArrayList array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex(MESSAGE)));
            res.moveToNext();
        }
        return array_list;
    }

    public static int getTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd,mm,yyyy,HH,mm,ss");
        String strDate = sdf.format(c.getTime());
        String[] splited = strDate.split(",");
        return Integer.parseInt(splited[0]) + Integer.parseInt(splited[1]) + Integer.parseInt(splited[2]) + Integer.parseInt(splited[3]) + Integer.parseInt(splited[4]) + Integer.parseInt(splited[5]);
    }

}