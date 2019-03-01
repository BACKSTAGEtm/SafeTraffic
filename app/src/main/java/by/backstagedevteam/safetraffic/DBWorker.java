package by.backstagedevteam.safetraffic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;

public class DBWorker {

    DBHelper dbHelper;
    private static final double DEFAULT_RADIUS_MARKER = 0.0002;

    public ArrayList<Markers> getMarkers() {
        ArrayList<Markers> markers = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        Log.d("DBWorker", "getMarkers");
        if (cursor.moveToFirst()) {
            do {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int latitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LATITUDE);
            int longtitudeIndex = cursor.getColumnIndex(DBHelper.KEY_LONGITUDE);
                Log.d("SQLite", "id=" + cursor.getInt(idIndex) +
                        ", lat=" + cursor.getDouble(latitudeIndex) +
                        ", lon=" + cursor.getDouble(longtitudeIndex));
                markers.add(new Markers(cursor.getDouble(latitudeIndex), cursor.getDouble(longtitudeIndex),MarkerType.Crosswalk));
            } while (cursor.moveToNext());
        } else {
            Log.d("SQLite", "0 rows");
        }
        cursor.close();
        database.close();
        return markers;
    }

    public ArrayList<Markers> getMarkersOfArea() {
        ArrayList<Markers> markers = new ArrayList<>();
        //TODO: implements return Markers after checked area
        return markers;
    }

    public void initImportGPX() {
        ArrayList<Markers> markers = GPXParser.initAppParse();
        addMarkers(markers);
    }

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

    DBWorker(Context context) {
        dbHelper = new DBHelper(context);
    }
    /*
    public boolean close() {
        dbHelper.close();
        return true;
    }*/
}
