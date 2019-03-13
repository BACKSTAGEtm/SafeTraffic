package by.backstagedevteam.safetraffic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;

/**
 * This class implements the functions of working with the database
 * <p>
 * * @author Dmitry Kostyuchenko
 * * @see by.backstagedevteam.safetraffic
 * * @since 2019
 */
public class DBWorker {
    DBHelper dbHelper;

    /**
     * This method return All markers in SQLite
     *
     * @return array markers
     */
    public ArrayList<Markers> getMarkers() {
        ArrayList<Markers> markers = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        //Log.d("DBWorker", "getMarkers");
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int latitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
                int longtitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
//                Log.d("SQLite", "id=" + cursor.getInt(idIndex) +
//                        ", lat=" + cursor.getDouble(latitudeIndex) +
//                        ", lon=" + cursor.getDouble(longtitudeIndex));
                markers.add(new Markers(cursor.getDouble(latitudeIndex), cursor.getDouble(longtitudeIndex), MarkerType.Crosswalk));
            } while (cursor.moveToNext());
        } else {
            //Log.d("SQLite", "0 rows");
        }
        cursor.close();
        database.close();
        return markers;
    }

    /**
     * This method return markers of area. p1 - left-top, p2 - right-bottom
     *
     * @param p1
     * @param p2
     * @return
     */
    public ArrayList<Markers> getMarkersOfArea(Point p1, Point p2) {
        ArrayList<Markers> markers = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //TODO:fix query!
        String selection = "latitude > ? latitude < ? longitude > ? longitude < ?";
        String[] selectionArgs = new String[]{
                String.valueOf(p2.getLatitude()),
                String.valueOf(p1.getLatitude()),
                String.valueOf(p1.getLongitude()),
                String.valueOf(p2.getLongitude())
        };
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                int latitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
                int longtitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
                Log.d("SQLite", "id=" + cursor.getInt(idIndex) +
                        ", lat=" + cursor.getDouble(latitudeIndex) +
                        ", lon=" + cursor.getDouble(longtitudeIndex));
                markers.add(new Markers(cursor.getDouble(latitudeIndex), cursor.getDouble(longtitudeIndex), MarkerType.Crosswalk));
            } while (cursor.moveToNext());
        } else {
            Log.d("SQLite", "0 rows");
        }
        cursor.close();
        database.close();
        return markers;
    }

    /**
     * Temporarily!
     * This method load data to Data Base
     */
    public void initImportGPX(Context context) {
        //ArrayList<Markers> markers = GPXParser.initAppParse();
        ArrayList<Markers> markers = GPXParser.initAppParse(context);
        addMarkers(markers);
    }

    /**
     * This method adding array markers to Data Base
     *
     * @param markers
     */
    public void addMarkers(ArrayList<Markers> markers) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        for (Markers item :
                markers) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_LATITUDE, item.getLatitude());
            contentValues.put(DBHelper.KEY_LONGITUDE, item.getLongitude());
            contentValues.put(DBHelper.KEY_TYPE, String.valueOf(MarkerType.Crosswalk));
            database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
        }
        database.close();
    }

    /**
     * This method implements checks intersection with marker ареа
     *
     * @param location checked position
     * @return {@code Markers} if there is an intersection
     * or {@code null} if there is no intersection
     */
    public Markers checkIntersection(Point location) {
        //TODO:Test implements!
        ArrayList<Markers> markers = getMarkers();
        for (Markers item :
                markers) {
            if (item.checkIntersection(location)) {
                return item;
            }
        }
        return null;
    }


    public void clear() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_CONTACTS, null, null);
    }

    public DBWorker(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean checkDublicate() {
        boolean isDublicate = false;
        //TODO: check dublicate
        return isDublicate;
    }
    /*
    public boolean close() {
        dbHelper.close();
        return true;
    }*/
}
