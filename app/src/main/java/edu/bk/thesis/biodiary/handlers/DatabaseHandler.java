package edu.bk.thesis.biodiary.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.bk.thesis.biodiary.models.Diary;


public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int    DATABASE_VERSION = 1;
    private static final String DATABASE_NAME    = "biodiaryDB.db";

    public DatabaseHandler(Context context,
                           String name,
                           SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Diary.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Diary.TABLE_NAME);
        onCreate(db);
    }

    public long insertEntry(Diary.Entry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Diary.COLUMN_TIMESTAMP, entry.getTimestamp());
        values.put(Diary.COLUMN_CONTENT, entry.getContent());

        long id = db.insert(Diary.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public Diary.Entry getEntry(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Diary.TABLE_NAME,
                                 new String[]{ Diary.COLUMN_ID, Diary.COLUMN_TIMESTAMP,
                                               Diary.COLUMN_CONTENT },
                                 Diary.COLUMN_ID + "=?",
                                 new String[]{ String.valueOf(id) },
                                 null,
                                 null,
                                 null,
                                 null);

        Diary.Entry entry = null;
        if (cursor != null && cursor.moveToFirst()) {
            entry = new Diary.Entry(cursor.getInt(cursor.getColumnIndex(Diary.COLUMN_ID)),
                                    cursor.getLong(cursor.getColumnIndex(Diary.COLUMN_TIMESTAMP)),
                                    cursor.getString(cursor.getColumnIndex(Diary.COLUMN_CONTENT)));
            cursor.close();
        }

        // need?
        db.close();

        return entry;
    }

    public List<Diary.Entry> getAllEntries()
    {
        List<Diary.Entry> entries = new ArrayList<>();

        String selectQuery =
                "SELECT * FROM " + Diary.TABLE_NAME
                + " ORDER BY " + Diary.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db     = getReadableDatabase();
        Cursor         cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Diary.Entry entry
                        = new Diary.Entry(cursor.getInt(cursor.getColumnIndex(Diary.COLUMN_ID)),
                                          cursor.getLong(cursor.getColumnIndex(Diary.COLUMN_TIMESTAMP)),
                                          cursor.getString(cursor.getColumnIndex(Diary.COLUMN_CONTENT)));
                entries.add(entry);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return entries;
    }

    public int getEntriesCount()
    {
        String countQuery = "SELECT  * FROM " + Diary.TABLE_NAME;

        SQLiteDatabase db     = this.getReadableDatabase();
        Cursor         cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    public int updateEntry(Diary.Entry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Diary.COLUMN_CONTENT, entry.getContent());

        return db.update(Diary.TABLE_NAME, values, Diary.COLUMN_ID + " = ?",
                         new String[]{ String.valueOf(entry.getId()) });
    }

    public void deleteEntry(Diary.Entry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Diary.TABLE_NAME, Diary.COLUMN_ID + " = ?",
                  new String[]{ String.valueOf(entry.getId()) });

        db.close();
    }
}
