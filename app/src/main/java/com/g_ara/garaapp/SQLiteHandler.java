package com.g_ara.garaapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "garaSqlite";

    // Login table name
    private static final String MEMBER = "Member";
    private static final String DRIVER = "Driver";
    private static final String CAR = "Car";

    // Login Table Columns names
    private static final String LOCALID = "localid";
    private static final String ID = "ID";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String NAME = "name";
    private static final String USERNAME = "username";
    private static final String STUDENTEMAIL = "studentEmail";
    private static final String PHONENUMBER = "phoneNumber";
    private static final String BIRTHDATE = "birthDate";
    private static final String ACTIVITED = "activited";
    private static final String GENDER = "gender";
    private static final String PASSWORD = "password";
    private static final String COLLEGEID = "collegeID";
    private static final String SALT = "salt";
    private static final String PIC = "pic";
    private static final String BLOODTYPE = "bloodType";
    private static final String EMERGENCYNUMBER = "emergencyNumber";
    private static final String BALANCE = "balance";
    private static final String STUDENTEMAILACTIVATIONCODE = "studentEmailActivationCode";
    private static final String RIDEID = "rideID";
    private static final String MEMBERGROUPID = "memberGroupID";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String PIN = "pin";
    private static final String UNIVERSITYID = "universityID";
    private static final String ACCESSTOKEN = "accesstoken";

    private static final String MEMBERID = "memberID";
    private static final String LICENSENUMBER = "licenseNumber";
    private static final String LICENSEPIC = "licensePic";
    private static final String IDENTYCARDPIC = "identyCardPic";
    private static final String LICENSEEXPIREDATE = "licenseExpireDate";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBER_TABLE = "CREATE TABLE Member (localid INTEGER PRIMARY KEY,ID INTEGER, TIMESTAMP timestamp DEFAULT CURRENT_TIMESTAMP , name text , username text , studentEmail text , phoneNumber varchar(15), birthDate timestamp, activited integer(1) DEFAULT '0', gender varchar(255) DEFAULT 'M', password text , collegeID integer(11), salt varchar(10), pic text, bloodType varchar(2), emergencyNumber text, balance integer(11) DEFAULT 0, studentEmailActivationCode varchar(10), rideID integer(11), memberGroupID integer(11), longitude double(10), latitude double(10), pin text, universityID integer(11), accesstoken text );";

        String CREATE_DRIVER_TABLE = "CREATE TABLE Driver (localid INTEGER PRIMARY KEY,ID INTEGER , memberID integer(11) , licenseNumber text , licensePic text , identyCardPic text , licenseExpireDate date );";

        String CREATE_CAR_TABLE = "CREATE TABLE Car (localid INTEGER PRIMARY KEY,ID INTEGER, driverID integer(11) , plateNumber text , platePic text, carModelID integer(11) , frontPic text, backPic text, sidePic text, insidePic text, licenseNumber text , licensePic text, licenseExpireDate date , DistLongitude double(10), DistLatitude double(10), availableSeats integer(11), state integer(1) DEFAULT '0');";

        db.execSQL(CREATE_MEMBER_TABLE);
        db.execSQL(CREATE_DRIVER_TABLE);
        db.execSQL(CREATE_CAR_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + DRIVER);
        db.execSQL("DROP TABLE IF EXISTS " + CAR);

        // Create tables again
        onCreate(db);
    }


    public void addMember(Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        for (Map.Entry<String, String> e : data.entrySet()) {
            if (e.getKey().toLowerCase().contains("pic")) {
                if (e.getValue() == null || e.getValue().equals("null"))
                    e.setValue("https://pbs.twimg.com/profile_images/610486974990913536/5MdbcHvF.png");
            }
            try {
                if (!e.getValue().equals("null")) {
                    Field declaredField = getClass().getDeclaredField(e.getKey().toUpperCase());
                    Object o = declaredField.get(this);
                    values.put(o.toString(), e.getValue());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }


        // Inserting Row
        long id = db.insert(MEMBER, null, values);
        HashMap<String, String> memberDetails = getMemberDetails();

        db.close(); // Closing database connection

        Log.d(TAG, "New member inserted into sqlite: " + id);
    }

    /**
     * Getting member data from database
     */
    public HashMap<String, String> getMemberDetails() {
        HashMap<String, String> member = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + MEMBER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String[] columnNames = cursor.getColumnNames();
            for (int i = 0; i < columnNames.length; i++) {
                member.put(columnNames[i], cursor.getString(i));
            }
        }
        cursor.close();
        db.close();
        // return member
        Log.d(TAG, "Fetching member from Sqlite: " + member.toString());

        return member;
    }

    public List<HashMap<String, String>> getMembersDetails() {
        List<HashMap<String, String>> member = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + MEMBER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        ;

        while (cursor.moveToNext()) {
            if (cursor.getCount() > 0) {
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put(columnNames[i], cursor.getString(i));
                    member.add(stringStringHashMap);
                }
            }
        }

        cursor.close();
        db.close();
        // return member
        Log.d(TAG, "Fetching member from Sqlite: " + member.toString());

        return member;
    }


    public void deleteMembers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(MEMBER, null, null);
        db.close();

        Log.d(TAG, "Deleted all member info from sqlite");
    }


    public List<HashMap<String, String>> selectAll(String Table) {
        List<HashMap<String, String>> list = new ArrayList<>();
        try {
            Field declaredField = getClass().getDeclaredField(Table.toUpperCase());
            Object o = declaredField.get(this);
            String selectQuery = "SELECT  * FROM " + o.toString();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                if (cursor.getCount() > 0) {
                    String[] columnNames = cursor.getColumnNames();
                    for (int i = 0; i < columnNames.length; i++) {
                        HashMap<String, String> stringStringHashMap = new HashMap<>();
                        stringStringHashMap.put(columnNames[i], cursor.getString(i));
                        list.add(stringStringHashMap);
                    }
                }
            }

            cursor.close();
            db.close();
            // return member
            Log.d(TAG, "Fetching table from Sqlite: " + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error " + e.getMessage());

        }

        return list;
    }


    public HashMap<String, String> selectOne(String Table) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            Field declaredField = getClass().getDeclaredField(Table.toUpperCase());
            Object o = declaredField.get(this);
            String selectQuery = "SELECT  * FROM " + o.toString();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    hashMap.put(columnNames[i], cursor.getString(i));
                }
            }


            cursor.close();
            db.close();

            Log.d(TAG, "Fetching table from Sqlite: " + hashMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error " + e.getMessage());

        }

        return hashMap;
    }

    public void insert(String Table, Map<String, String> data) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            Field declaredField = getClass().getDeclaredField(Table.toUpperCase());
            Object o = declaredField.get(this);

            for (Map.Entry<String, String> e : data.entrySet()) {
                if (e.getKey().toLowerCase().contains("pic")) {
                    if (e.getValue() == null || e.getValue().equals("null"))
                        e.setValue("https://pbs.twimg.com/profile_images/610486974990913536/5MdbcHvF.png");
                }
                try {
                    if (!e.getValue().equals("null")) {
                        declaredField = getClass().getDeclaredField(e.getKey().toUpperCase());
                        o = declaredField.get(this);
                        values.put(o.toString(), e.getValue());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            declaredField = getClass().getDeclaredField(Table.toUpperCase());
            o = declaredField.get(this);

            long id = db.insert(o.toString(), null, values);

            db.close(); // Closing database connection

            Log.d(TAG, "New row inserted into " + o + ": " + id);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "insert error: " + e.getMessage());

        }
    }

    public void dropTable(String Table) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            // Delete All Rows
            Field declaredField = getClass().getDeclaredField(Table.toUpperCase());
            Object o = declaredField.get(this);
            db.delete(o.toString(), null, null);
            db.close();

            Log.d(TAG, "Deleted all from " + o);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error Deleteing  " + e.getMessage());

        }
    }
}