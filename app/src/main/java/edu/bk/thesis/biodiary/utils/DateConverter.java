package edu.bk.thesis.biodiary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public final class DateConverter
{
    public static final String PATTERN_SHORT_DATE = "dd/MM/yyyy";
    public static final String PATTERN_LONG_DATE  = "MMM dd, yyyy";

    static final Locale LOCALE = Locale.US;

    public static String date2String(Date date, String pattern)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, LOCALE);
        return dateFormat.format(date);
    }

    public static Date string2Date(String string, String pattern) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, LOCALE);
        return dateFormat.parse(string);
    }

    public static Date millisecond2Date(long millisecond)
    {
        return new Date(millisecond);
    }

    public static long date2Millisecond(Date date)
    {
        return date.getTime();
    }
}
