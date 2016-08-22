package org.buildmlearn.toolkit.dictationtemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by Anupam (opticod) on 10/7/16.
 */

/**
 * @brief Contains database util functions for dictation template's simulator.
 */
public class DictDb {

    private static final String EQUAL = " == ";
    private final DictDBHelper dbHelper;
    private SQLiteDatabase db;

    public DictDb(Context context) {
        dbHelper = new DictDBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getDictsCursor() {

        return db.query(
                DictContract.Dict.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public Cursor getDictCursorById(int id) {

        String selection = DictContract.Dict._ID + EQUAL + id;

        return db.query(
                DictContract.Dict.TABLE_NAME,
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
                DictContract.Dict.TABLE_NAME);
    }

    public void deleteAll() {
        db.delete(DictContract.Dict.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + DictContract.Dict.TABLE_NAME + "';");
    }

    public int bulkInsert(@NonNull ContentValues[] values) {

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(DictContract.Dict.TABLE_NAME, null, value);
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
