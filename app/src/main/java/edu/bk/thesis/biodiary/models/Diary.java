package edu.bk.thesis.biodiary.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Diary implements Serializable
{
    public static final String TABLE_NAME = "Diary";

    public static final String COLUMN_ID        = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_CONTENT   = "content";
    public static final String CREATE_TABLE     =
            "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TIMESTAMP + " INTEGER,"
            + COLUMN_CONTENT + " TEXT"
            + ")";

    private List<Entry> mEntryList;

    public Diary()
    {
        mEntryList = new ArrayList<>();
    }

    public Diary(List<Entry> entryList)
    {
        mEntryList = entryList;
    }

    public List<Entry> getEntryList()
    {
        return mEntryList;
    }

    public void setEntryList(List<Entry> entryList)
    {
        mEntryList = entryList;
    }


    public static class Entry implements Serializable
    {
        private long   mId;
        private long   mTimestamp;
        private String mDate;
        private String mContent;


        public Entry(String date, String content)
        {
            mTimestamp = new Date().getTime();
            mDate = date;
            mContent = content;
        }

        public Entry(long id, long timestamp, String content)
        {
            mId = id;
            mTimestamp = timestamp;
            mContent = content;
        }

        public long getId()
        {
            return mId;
        }

        public long getTimestamp()
        {
            return mTimestamp;
        }

        public void setTimestamp(long timestamp)
        {
            mTimestamp = timestamp;
        }

        public String getContent()
        {
            return mContent;
        }

        public void setContent(String content)
        {
            mContent = content;
        }

        public String getDateInString()
        {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss", Locale.US);
            return df.format(getDate());
        }

        public Date getDate()
        {
            return new Date(mTimestamp);
        }

        public String getShortContent(int wordLength)
        {
            //TODO: implement get substring by word, instead of characters.
            return wordLength > mContent.length()
                   ? mContent
                   : mContent.substring(0, wordLength) + "...";
        }
    }
}
