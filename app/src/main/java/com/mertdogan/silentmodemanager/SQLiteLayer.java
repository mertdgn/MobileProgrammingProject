package com.mertdogan.silentmodemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteLayer extends SQLiteOpenHelper {

    private static final String DB_NAME="SilentModeManager.db";
    private static final String DB_TABLE="SilentModeSetting";
    private static final String DB_TABLE2="Locations";
    private static final String startTime = "startTime";
    private static final String endTime = "endTime";
    private static final String days = "days";
    private static final String mode = "mode";
    private static final String id = "id";
    private static final String title = "title";
    private static final String location = "location";
    private static final String setType = "setType";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ("+ id +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ title +" TEXT NOT NULL, "+startTime +" TEXT NOT NULL, "+endTime +" TEXT NOT NULL, "+
            days +" TEXT NOT NULL, "+mode +" TEXT NOT NULL, "+setType +" INTEGER NOT NULL "+") ";
    private static final String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + DB_TABLE2 + " ("+ id +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ title +" TEXT NOT NULL, "+location +" TEXT NOT NULL, "+setType +" INTEGER NOT NULL "+") ";

    //SQLiteDatabase dbs;


    public SQLiteLayer(Context c){
        super(c , DB_NAME, null, 1);

        }


    public boolean insertData(
            String startTime,
            String endTime,
            String days,
            String mode,
            String title,
            int setType
    )    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(this.startTime, startTime);
        val.put(this.endTime, endTime);
        val.put(this.days, days);
        val.put(this.mode, mode);
        val.put(this.title, title);
        val.put(this.setType, setType);
        long result = db.insert(DB_TABLE, null, val);
        db.close();
        return result != -1;
    }

    public boolean insertDataLoc(
            String location,
            String title,
            int setType
    )    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(this.location, location);
        val.put(this.title, title);
        val.put(this.setType, setType);
        long result = db.insert(DB_TABLE2, null, val);
        db.close();
        return result != -1;
    }

    public boolean updateData(SilentModeSetting sms,
                              String startTime,
                              String endTime,
                              String days,
                              String mode,
                              String title,
                              int setType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(this.startTime, startTime);
        val.put(this.endTime,endTime);
        val.put(this.days, days);
        val.put(this.mode, mode);
        val.put(this.title, title);
        val.put(this.setType, setType);
        int id=sms.getId();
        db.update(DB_TABLE, val, "id=?", new String[]{Integer.toString(id)});
        db.close();
        return true;
    }

    public boolean updateDataLoc(int idLoc,
                              String location,
                              String title,
                              int setType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(this.location, location);
        val.put(this.title, title);
        val.put(this.setType, setType);
        int id=idLoc;
        db.update(DB_TABLE2, val, "id=?", new String[]{Integer.toString(id)});
        db.close();
        return true;
    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ DB_TABLE;
        Cursor cursor= db.rawQuery(query,null);
        return cursor;
    }

    public Cursor viewDataLoc() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+ DB_TABLE2;
        Cursor cursor= db.rawQuery(query,null);
        return cursor;
    }

    public void deleteData(SilentModeSetting sms) {
        int id=sms.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, "id=" + id, null);
    }

    public void deleteDataLoc(int idLoc) {
        int id=idLoc;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE2, "id=" + id, null);
    }


    @Override
    public void onCreate(SQLiteDatabase dbs){
        dbs.execSQL(CREATE_TABLE);
        dbs.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dbs,
                          int oldVersion, int newVersion){
        dbs.execSQL("DROP TABLE IF EXISTS SilentModeSetting");
        onCreate(dbs);
    }


}
