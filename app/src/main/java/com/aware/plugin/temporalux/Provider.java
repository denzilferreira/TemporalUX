package com.aware.plugin.temporalux;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.util.HashMap;

/**
 * Created by denzil on 25/6/15.
 */
public class Provider extends ContentProvider {

    public static String AUTHORITY = "com.aware.plugin.temporalux.provider.temporalux";
    public static final int DATABASE_VERSION = 1;

    private static final int TEMPORALUX = 1;
    private static final int TEMPORALUX_ID = 2;

    public static final String DATABASE_NAME = "plugin_temporalux.db";
    public static final String[] DATABASE_TABLES = {"plugin_temporalux"};
    public static final String[] TABLES_FIELDS = {
            TemporalUX_Data._ID + " integer primary key autoincrement," +
            TemporalUX_Data.TIMESTAMP + " real default 0," +
            TemporalUX_Data.DEVICE_ID + " text default ''," +
            TemporalUX_Data.PACKAGE_NAME + " text default ''," +
            TemporalUX_Data.IS_BUNDLED + " integer default 0,"+
            "UNIQUE (" + TemporalUX_Data.TIMESTAMP + "," + TemporalUX_Data.DEVICE_ID + ")"
    };

    public static final class TemporalUX_Data implements BaseColumns {
        private TemporalUX_Data(){}
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/plugin_temporalux");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aware.plugin.temporalux";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aware.plugin.temporalux";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";
        public static final String PACKAGE_NAME = "package_name";
        public static final String IS_BUNDLED = "is_bundled";
    }

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( database == null || ! database.isOpen() ) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null );
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.temporalux"; //make AUTHORITY dynamic
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], TEMPORALUX); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", TEMPORALUX_ID); //URI for a single record

        tableMap = new HashMap<String, String>();
        tableMap.put(TemporalUX_Data._ID, TemporalUX_Data._ID);
        tableMap.put(TemporalUX_Data.TIMESTAMP, TemporalUX_Data.TIMESTAMP);
        tableMap.put(TemporalUX_Data.DEVICE_ID, TemporalUX_Data.DEVICE_ID);
        tableMap.put(TemporalUX_Data.PACKAGE_NAME, TemporalUX_Data.PACKAGE_NAME);
        tableMap.put(TemporalUX_Data.IS_BUNDLED, TemporalUX_Data.IS_BUNDLED);

        return true; //let Android know that the database is ready to be used.
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case TEMPORALUX:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(tableMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG) Log.e(Aware.TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TEMPORALUX:
                return TemporalUX_Data.CONTENT_TYPE;
            case TEMPORALUX_ID:
                return TemporalUX_Data.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues new_values) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        ContentValues values = (new_values != null) ? new ContentValues(new_values) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case TEMPORALUX:
                long _id = database.insert(DATABASE_TABLES[0],TemporalUX_Data.DEVICE_ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(TemporalUX_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY, "Database unavailable...");
            return 0;
        }

        int count;
        switch (sUriMatcher.match(uri)) {
            case TEMPORALUX:
                count = database.delete(DATABASE_TABLES[0], selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case TEMPORALUX:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
