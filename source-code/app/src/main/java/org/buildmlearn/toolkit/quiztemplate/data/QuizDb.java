package org.buildmlearn.toolkit.quiztemplate.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.buildmlearn.toolkit.quiztemplate.Constants;
import org.buildmlearn.toolkit.quiztemplate.data.QuizContract.Questions;

import java.util.Arrays;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Contains database util functions for quiz template's simulator.
 */
public class QuizDb {

    private static final String EQUAL = " == ";
    private final QuizDBHelper dbHelper;
    private SQLiteDatabase db;

    public QuizDb(Context context) {
        dbHelper = new QuizDBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void markAnswered(int id, int answer) {

        ContentValues values = new ContentValues();
        values.put(Questions.ANSWERED, answer);
        values.put(Questions.ATTEMPTED, 1);

        db.update(Questions.TABLE_NAME, values, Questions._ID + " = ?",
                new String[]{String.valueOf(id)});

    }

    public void markUnAnswered(int id) {

        ContentValues values = new ContentValues();
        values.put(Questions.ANSWERED, "");
        values.put(Questions.ATTEMPTED, 0);

        db.update(Questions.TABLE_NAME, values, Questions._ID + " = ?",
                new String[]{String.valueOf(id)});

    }

    public void deleteAll() {
        db.delete(QuizContract.Questions.TABLE_NAME, null, null);
        db.execSQL("delete from sqlite_sequence where name='" + QuizContract.Questions.TABLE_NAME + "';");
    }

    public void resetCount() {
        for (int i = 1; i <= getCountQuestions(); i++) {
            markUnAnswered(i);
        }
    }

    public int[] getStatistics() {
        int stat[] = new int[3];
        Arrays.fill(stat, 0);

        for (int i = 1; i <= getCountQuestions(); i++) {
            Cursor cursor = getQuestionCursorById(i);
            cursor.moveToFirst();

            String correct_answer = cursor.getString(Constants.COL_CORRECT_ANSWER);
            int attempted = cursor.getInt(Constants.COL_ATTEMPTED);
            if (attempted == 1) {
                String answer = cursor.getString(Constants.COL_ANSWERED);
                if (answer.equals(correct_answer)) {
                    stat[0]++;
                } else {
                    stat[1]++;
                }
            } else {
                stat[2]++;
            }
        }
        return stat;
    }

    public Cursor getQuestionCursorById(int id) {

        String selection = Questions._ID + EQUAL + id;

        return db.query(
                Questions.TABLE_NAME,
                null,
                selection,
                null,
                null,
                null,
                null
        );
    }

    public long getCountQuestions() {

        return DatabaseUtils.queryNumEntries(db,
                Questions.TABLE_NAME);
    }

    public int bulkInsertQuestions(@NonNull ContentValues[] values) {

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = db.insert(Questions.TABLE_NAME, null, value);
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
