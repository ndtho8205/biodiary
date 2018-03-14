package edu.bk.thesis.biodiary.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Diary implements Serializable
{
    private String mTitle;
    private Date   mDate;
    private String mContent;

    public Diary(String mTitle, Date mDate, String mContent)
    {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mContent = mContent;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public Date getDate()
    {
        return mDate;
    }

    public String getDateInString()
    {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        return df.format(mDate.getDate());
    }

    public void setDate(Date mDate)
    {
        this.mDate = mDate;
    }

    public String getContent()
    {
        return mContent;
    }

    public void setContent(String mContent)
    {
        this.mContent = mContent;
    }
}
