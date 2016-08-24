package org.buildmlearn.toolkit.infotemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by Anupam (opticod) on 20/6/16.
 */

/**
 * @brief Contains database util functions for info template's simulator.
 */
public class InfoDb {

    private static final String EQUAL = " == ";
    private final InfoDBHelper dbHelper;
    private SQLiteDatabase db;

    public InfoDb(Context context) {
        dbHelper = new InfoDBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getInfosCursor() {

        return db.query(
                InfoContract.Info.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getInfoCursorById(int id) {

        String selection = InfoContract.Info._ID + EQUAL + id;

        return db.query(
                InfoContract.Info.TABLE_NAME,
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
                InfoContract.Info.TABLE_NAME);
    }

    public void deleteAll() {
        db.delete(InfoContract.Info.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + InfoContract.Info.TABLE_NAME + "';");
    }

    public int bulkInsert(@NonNull ContentValues[] values) {

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(InfoContract.Info.TABLE_NAME, null, value);
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
