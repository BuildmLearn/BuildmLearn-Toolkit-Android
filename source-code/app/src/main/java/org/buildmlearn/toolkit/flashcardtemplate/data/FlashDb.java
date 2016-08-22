package org.buildmlearn.toolkit.flashcardtemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.buildmlearn.toolkit.flashcardtemplate.data.FlashContract.FlashCards;

/**
 * Created by Anupam (opticod) on 10/8/16.
 */

/**
 * @brief Contains database util functions for flash card template's simulator.
 */
public class FlashDb {

    private static final String EQUAL = " == ";
    private final FlashDBHelper dbHelper;
    private SQLiteDatabase db;

    public FlashDb(Context context) {
        dbHelper = new FlashDBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getFlashCursorById(int id) {

        String selection = FlashCards._ID + EQUAL + id;

        return db.query(
                FlashCards.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
    }

    public long getCountFlashCards() {

        return DatabaseUtils.queryNumEntries(db,
                FlashCards.TABLE_NAME);
    }

    public int bulkInsertFlashCards(@NonNull ContentValues[] values) {

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(FlashCards.TABLE_NAME, null, value);
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

    public void deleteAll() {
        db.delete(FlashContract.FlashCards.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + FlashContract.FlashCards.TABLE_NAME + "';");
    }

}
