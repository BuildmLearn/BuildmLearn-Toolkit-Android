package org.buildmlearn.toolkit.videocollectiontemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * @brief Contains database util functions for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 19/5/16.
 */
public class VideoDb {

    private static final String EQUAL = " == ";
    private final VideoDBHelper dbHelper;
    private SQLiteDatabase db;

    public VideoDb(Context context) {
        dbHelper = new VideoDBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getVideosCursor() {

        return db.query(
                VideoContract.Videos.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getVideoCursorById(int id) {

        String selection = VideoContract.Videos._ID + EQUAL + id;

        return db.query(
                VideoContract.Videos.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
    }

    public long getCount() {

        return DatabaseUtils.queryNumEntries(db,
                VideoContract.Videos.TABLE_NAME);
    }

    public void deleteAll() {
        db.delete(VideoContract.Videos.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + VideoContract.Videos.TABLE_NAME + "';");
    }

    public int bulkInsert(@NonNull ContentValues[] values) {

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(VideoContract.Videos.TABLE_NAME, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;
    }
}
