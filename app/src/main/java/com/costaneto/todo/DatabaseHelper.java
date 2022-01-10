package com.costaneto.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "TASKS_DATABASE.DB";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_TABLE = "USERS";
    static final String TASK_ID = "_id";
    static final String TASK_TEXT = "task_text";
    static final String TASK_STATUS = "task_status";

    private static final String CREATE_DB_QUERY = "CREATE TABLE " + DATABASE_TABLE + "( "
                                                    + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + TASK_TEXT + " TEXT NOT NULL, "
                                                    + TASK_STATUS + " INTEGER NOT NULL);";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }
}
