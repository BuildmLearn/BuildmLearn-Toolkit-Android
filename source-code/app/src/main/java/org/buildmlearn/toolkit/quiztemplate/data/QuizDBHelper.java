package org.buildmlearn.toolkit.quiztemplate.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.buildmlearn.toolkit.quiztemplate.data.QuizContract.Questions;

/**
 * Created by Anupam (opticod) on 11/8/16.
 */

/**
 * @brief DatabaseHelper for quiz template's simulator.
 */
class QuizDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;

    public QuizDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE__TABLE_A = "CREATE TABLE " + Questions.TABLE_NAME + " (" +
                Questions._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Questions.QUESTION + "  TEXT," +
                Questions.OPTION_1 + "  TEXT," +
                Questions.OPTION_2 + "  TEXT," +
                Questions.OPTION_3 + "  TEXT," +
                Questions.OPTION_4 + "  TEXT," +
                Questions.CORRECT_ANSWER + "  INTEGER," +
                Questions.ANSWERED + "  INTEGER," +
                Questions.ATTEMPTED + "  INTEGER )";

        sqLiteDatabase.execSQL(SQL_CREATE__TABLE_A);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Questions.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}