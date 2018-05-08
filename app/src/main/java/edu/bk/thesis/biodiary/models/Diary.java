package edu.bk.thesis.biodiary.models;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.bk.thesis.biodiary.utils.DateConverter;


public class Diary implements Serializable
{

    public static final String TABLE_NAME = "Diary";

    public static final String COLUMN_ID        = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DATE      = "date";
    public static final String COLUMN_CONTENT   = "content";
    public static final String CREATE_TABLE     =
        "CREATE TABLE " + TABLE_NAME + "("
        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + COLUMN_TIMESTAMP + " INTEGER,"
        + COLUMN_CONTENT + " TEXT,"
        + COLUMN_DATE + " INTEGER"
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
        private String mContent;
        private long   mDate;

        public Entry(String content, String date, String pattern) throws ParseException
        {
            mTimestamp = new Date().getTime();
            mContent = content;
            setDate(date, pattern);
        }

        public Entry(long id, long timestamp, String content, long date)
        {
            mId = id;
            mTimestamp = timestamp;
            mContent = content;
            setDate(date);
        }

        public Entry(long id, long timestamp, String content, String date, String pattern)
            throws ParseException
        {
            mId = id;
            mTimestamp = timestamp;
            mContent = content;
            setDate(date, pattern);
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

        public String getShortContent(int wordLength)
        {
            //TODO: implement get substring by word, instead of characters.
            return wordLength > mContent.length()
                   ? mContent
                   : mContent.substring(0, wordLength) + "...";
        }

        public Date getDate()
        {

            return DateConverter.millisecond2Date(mDate);
        }

        public void setDate(long date)
        {
            mDate = date;
        }

        public void setDate(Date date)
        {
            mDate = DateConverter.date2Millisecond(date);
        }

        public long getDateInMilisecond()
        {
            return mDate;
        }

        public String getDateInString(String pattern)
        {
            return DateConverter.date2String(DateConverter.millisecond2Date(mDate), pattern);
        }

        public void setDate(String string, String pattern) throws ParseException
        {
            mDate = DateConverter.date2Millisecond(DateConverter.string2Date(string, pattern));
        }
    }
}
