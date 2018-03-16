package edu.bk.thesis.biodiary.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import edu.bk.thesis.biodiary.models.Diary;


public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = "biodiaryDB.db";

    public static final String TABLE_DIARY = "Diary";

    public static final String COLUMN_ID      = "DiaryId";
    public static final String COLUMN_TITLE   = "DiaryTitle";
    public static final String COLUMN_DATE    = "DiaryDate";
    public static final String COLUMN_CONTENT = "DiaryContent";

    public DatabaseHandler(Context context,
                           String name,
                           SQLiteDatabase.CursorFactory factory,
                           int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_DIARY_TABLE = "CREATE TABLE " + TABLE_DIARY + "(" + COLUMN_ID +
                                    " INTEGER PRIMARYKEY," + COLUMN_TITLE + " TEXT," + COLUMN_DATE +
                                    " TEXT," + COLUMN_CONTENT + " TEXT" + ")";
        db.execSQL(CREATE_DIARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }

    public void addDiary(Diary diary)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, diary.getTitle());
        values.put(COLUMN_DATE, diary.getDateInString());
        values.put(COLUMN_CONTENT, diary.getContent());

        db.insert(TABLE_DIARY, null, values);
        db.close();
    }

    public Diary getDiary(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DIARY,
                                 new String[]{ COLUMN_ID, COLUMN_TITLE, COLUMN_DATE,
                                               COLUMN_CONTENT },
                                 COLUMN_ID + "=?",
                                 new String[]{ String.valueOf(id) },
                                 null,
                                 null,
                                 null,
                                 null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Diary diary = new Diary(Integer.parseInt(cursor.getString(0)),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3))
    }

    public List<Diary> getAllDiaries()
    {
    }

    public int getDiaryCount()
    {
    }

    public int updateDiary(Diary diary)
    {
    }

    public void deleteDiary(Diary diary)
    {
    }
}
