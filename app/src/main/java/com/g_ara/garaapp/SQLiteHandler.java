package com.g_ara.garaapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
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


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE Member (localid INTEGER PRIMARY KEY,ID INTEGER, TIMESTAMP timestamp DEFAULT CURRENT_TIMESTAMP , name text , username text , studentEmail text , phoneNumber varchar(15), birthDate timestamp, activited integer(1) DEFAULT '0', gender varchar(255) DEFAULT 'M', password text , collegeID integer(11), salt varchar(10), pic text, bloodType varchar(2), emergencyNumber text, balance integer(11) DEFAULT 0, studentEmailActivationCode varchar(10), rideID integer(11), memberGroupID integer(11), longitude double(10), latitude double(10), pin text, universityID integer(11), accesstoken text );";

        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MEMBER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing member details in database
     */
    public void addMember(String ID, String NAME, String USERNAME, String STUDENTEMAIL, String PASSWORD, String PHONENUMBER, String PIC, String ACCESSTOKEN) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(this.ID, ID);

        values.put(this.NAME, NAME);
        values.put(this.USERNAME, USERNAME);
        values.put(this.STUDENTEMAIL, STUDENTEMAIL);
        values.put(this.PASSWORD, PASSWORD);
        values.put(this.PHONENUMBER, PHONENUMBER);
        values.put(this.ACCESSTOKEN, ACCESSTOKEN);
        values.put(this.PIC, PIC);

//        values.put("ID",1);

        // Inserting Row
        long id = db.insert(MEMBER, null, values);
        HashMap<String, String> memberDetails = getMemberDetails();

        db.close(); // Closing database connection

        Log.d(TAG, "New member inserted into sqlite: " + id);
    }

    public void addMember(Map<String, String> data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();


        for (Map.Entry<String, String> e : data.entrySet()) {
            if (e.getKey().equals("pic")) {
                if(e.getValue()==null||e.getValue().equals("null"))
                    e.setValue("http://www.g-ara.com/assets/images/team/2.jpg");
            }
            try {
                if(!e.getValue().equals("null")) {
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

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteMembers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(MEMBER, null, null);
        db.close();

        Log.d(TAG, "Deleted all member info from sqlite");
    }

}