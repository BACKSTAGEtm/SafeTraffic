package by.backstagedevteam.safetraffic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class implements the base functions of working with the SQLite
 *
 *  * @author Dmitry Kostyuchenko
 *  * @see by.backstagedevteam.safetraffic
 *  * @since 2019
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "markersDB";
    public static final String TABLE_CONTACTS = "markers";

    public static final String KEY_ID = "_id";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TYPE = "type";
    public static final String KEY_HINT = "hint";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID
            + " integer primary key," + KEY_LATITUDE + " double," + KEY_LONGITUDE
                + " double," + KEY_TYPE + " text," + KEY_HINT + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS);

        onCreate(db);
    }
}
