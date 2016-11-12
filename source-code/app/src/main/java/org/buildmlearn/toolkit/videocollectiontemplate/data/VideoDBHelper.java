package org.buildmlearn.toolkit.videocollectiontemplate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.videocollectiontemplate.data.VideoContract.Videos;

/**
 * @brief DatabaseHelper for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 13/5/16.
 */

class VideoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "video.db";
    private static final int DATABASE_VERSION = 1;

    public VideoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE = "CREATE TABLE " + Videos.TABLE_NAME + " (" +
                Videos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Videos.TITLE + "  TEXT," +
                Videos.DESCRIPTION + "  TEXT," +
                Videos.LINK + "  TEXT," +
                Videos.THUMBNAIL_URL + "  TEXT)";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Videos.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}