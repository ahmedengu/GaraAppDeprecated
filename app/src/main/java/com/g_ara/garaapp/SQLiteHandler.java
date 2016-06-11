package com.g_ara.garaapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.HashMap;

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
    private static final String  ID = "ID";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String    NAME= "name";
    private static final String    USERNAME= "username";
    private static final String    STUDENTEMAIL= "studentEmail";
    private static final String    PHONENUMBER= "phoneNumber";
    private static final String BIRTHDATE= "birthDate";
    private static final String   ACTIVITED= "activited";
    private static final String    GENDER= "gender";
    private static final String    PASSWORD= "password";
    private static final String   COLLEGEID= "collegeID";
    private static final String    SALT= "salt";
    private static final String    PIC= "pic";
    private static final String    BLOODTYPE= "bloodType";
    private static final String    EMERGENCYNUMBER= "emergencyNumber";
    private static final String   BALANCE= "balance";
    private static final String    STUDENTEMAILACTIVATIONCODE= "studentEmailActivationCode";
    private static final String   RIDEID= "rideID";
    private static final String   MEMBERGROUPID= "memberGroupID";
    private static final String    LONGITUDE= "longitude";
    private static final String    LATITUDE= "latitude";
    private static final String    PIN= "pin";
    private static final String   UNIVERSITYID= "universityID";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE Member (ID INTEGER PRIMARY KEY, TIMESTAMP timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, name text NOT NULL, username text NOT NULL, studentEmail text NOT NULL, phoneNumber varchar(15), birthDate timestamp, activited integer(1) DEFAULT '0', gender varchar(255) DEFAULT 'M', password text NOT NULL, collegeID integer(11), salt varchar(10), pic text, bloodType varchar(2), emergencyNumber text, balance integer(11) DEFAULT 0, studentEmailActivationCode varchar(10), rideID integer(11), memberGroupID integer(11), longitude double(10), latitude double(10), pin text, universityID integer(11));";

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
     * */
    public void addMember(String NAME,String USERNAME,String STUDENTEMAIL, String PASSWORD, String PHONENUMBER) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(this.NAME, NAME);
        values.put(this.USERNAME, USERNAME);
        values.put(this.STUDENTEMAIL, STUDENTEMAIL);
        values.put(this.PASSWORD, PASSWORD);
        values.put(this.PHONENUMBER, PHONENUMBER);
//        values.put("ID",1);

        // Inserting Row
        long id = db.insert(MEMBER, null, values);
        HashMap<String, String> memberDetails = getMemberDetails();

        db.close(); // Closing database connection

        Log.d(TAG, "New member inserted into sqlite: " + id);
    }

    /**
     * Getting member data from database
     * */
    public HashMap<String, String> getMemberDetails() {
        HashMap<String, String> member = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + MEMBER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            member.put("ID", cursor.getString(0));
            member.put("TIMESTAMP", cursor.getString(1));
            member.put("name", cursor.getString(2));
            member.put("username", cursor.getString(3));
            member.put(STUDENTEMAIL, cursor.getString(4));
            member.put("phoneNumber", cursor.getString(5));
            member.put("birthDate", cursor.getString(6));
            member.put("activited", cursor.getString(7));
            member.put("gender", cursor.getString(8));
            member.put("password", cursor.getString(9));
            member.put("collegeID", cursor.getString(10));
            member.put("salt", cursor.getString(11));
            member.put("pic", cursor.getString(12));
            member.put("bloodType", cursor.getString(13));
            member.put("emergencyNumber", cursor.getString(14));
            member.put("balance", cursor.getString(15));
            member.put("studentEmailActivationCode", cursor.getString(16));
            member.put("rideID", cursor.getString(17));
            member.put("memberGroupID", cursor.getString(18));
            member.put("longitude", cursor.getString(19));
            member.put("latitude", cursor.getString(20));
            member.put("pin", cursor.getString(21));
            member.put("universityID", cursor.getString(22));

        }
        cursor.close();
        db.close();
        // return member
        Log.d(TAG, "Fetching member from Sqlite: " + member.toString());

        return member;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteMembers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(MEMBER, null, null);
        db.close();

        Log.d(TAG, "Deleted all member info from sqlite");
    }

}