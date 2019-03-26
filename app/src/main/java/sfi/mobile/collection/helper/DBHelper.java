package sfi.mobile.collection.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "mobile_collection.db";
    private static final int DATABASE_VERSION = 1;


    public DBHelper(Context context){
        super (context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //create table DKHC
        String sql = "create table DKH (ID integer primary key autoincrement, BRANCH_ID text null, BRANCH_NAME text null, PIC text null, NOMOR_KONTRAK text null, NAMA_KOSTUMER text null, TANGGAL_JATUH_TEMPO text null, OVERDUE_DAYS int null, ANGSURAN_KE int null, JUMLAH_ANGSURAN_OVERDUE double null, TENOR int null, ANGSURAN_BERJALAN double null, ANGSURAN_TERTUNGGAK double null, DENDA double null, TITIPAN double null, TOTAL_TAGIHAN double null, OUTSTANDING_AR double null, ALAMAT_KTP text null, NOMOR_TELEPON_RUMAH text null, NOMOR_HANDPHONE text null, ALAMAT_KANTOR text null, NOMOR_TELEPON_KANTOR text null, ALAMAT_SURAT text null, NOMOR_TELEPON_SURAT text null, PIC_TERAKHIR text null, PENANGANAN_TERAKHIR text null, TANGGAL_JANJI_BAYAR text null, DailyCollectibility text null,OvdDaysHarian int null,TglJatuhTempoHarian text null,ARIN double null,FlowUp int null,TglTarikHarian text null,TglTerimaKlaim text null,LATITUDE text null,LONGITUDE text null,APPROVAL int null, IS_COLLECT int null, PERIOD text null);";
        Log.d( "Data","onCreate: "+sql);
        //create table KUNJUNGAN
        String sql2 = "create table RESULT (ID integer primary key autoincrement, CONTRACT_ID text null, QUESTION text null, ANSWER text null, CREATE_DATE text null, USER text null, BRANCH_ID text null);";
        Log.d( "Data","onCreate: "+sql2);
        String sql3 = "create table TBimage( ID integer primary key autoincrement, CONTRACT_ID text null, IMAGE BLOB, CREATE_DATE text null)";
        Log.d( "Data","onCreate: "+sql3);
        db.execSQL(sql);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DKH");
        db.execSQL("DROP TABLE IF EXISTS RESULT");
        db.execSQL("DROP TABLE IF EXISTS TBimage");
        // create new tables
        onCreate(db);
    }

    public ArrayList<HashMap<String, String>> getData30Days() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS FROM DKH WHERE IS_COLLECT=0" ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getStatus() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT DISTINCT A.CONTRACT_ID, B.NAMA_KOSTUMER, A.CREATE_DATE AS DATE FROM RESULT A LEFT JOIN DKH B ON A.CONTRACT_ID = B.NOMOR_KONTRAK" ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("CONTRACT_ID", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("DATE", String.valueOf(cursor.getString(2)));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        database.close();

        return wordList;
    }

    public ArrayList<HashMap<String, String>> getPriority() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 order by OVERDUE_DAYS desc limit 5";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHTagihanTerbesar() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 order by TOTAL_TAGIHAN desc limit 5";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHTagihanTerendah() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 order by TOTAL_TAGIHAN asc limit 5";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHODTerendah() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 order by OVERDUE_DAYS asc limit 5";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getDKHODTertinggi() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT NOMOR_KONTRAK, NAMA_KOSTUMER, TOTAL_TAGIHAN, TANGGAL_JATUH_TEMPO, LATITUDE, LONGITUDE, OVERDUE_DAYS,TANGGAL_JANJI_BAYAR FROM DKH WHERE IS_COLLECT = 0 order by OVERDUE_DAYS desc limit 5";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("NOMOR_KONTRAK", cursor.getString(0));
                map.put("NAMA_KOSTUMER", cursor.getString(1));
                map.put("TOTAL_TAGIHAN", String.valueOf(cursor.getInt(2)));
                map.put("TANGGAL_JATUH_TEMPO", cursor.getString(3));
                map.put("LAT", cursor.getString(4));
                map.put("LNG", cursor.getString(5));
                map.put("OVERDUE_DAYS", cursor.getString(6));
                map.put("TANGGAL_JANJI_BAYAR", cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //Log.e("select sqlite ", "" + wordList);

        database.close();
        return wordList;
    }
}
