package com.rr.CPing.model;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHandler {

    public DateTimeHandler() {
    }

    public void setCalender(Calendar calender, String time) {
        Date date = convertISO8601ToDate(time);
        assert date != null;
        calender.setTime(date);
        calender.add(Calendar.MINUTE, -date.getTimezoneOffset());
    }

    private Date convertISO8601ToDate(String dateString) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCompleteDetails(Calendar calendar) {
        String s = calendar.getTime().toString();
        return s.substring(0, 16) + " " + s.substring(30);
    }

    public String getDate(Calendar calendar) {
        String s = calendar.getTime().toString();
        return s.substring(8, 10) + " " + s.substring(4, 7) + ", " + s.substring(30);
    }

    public String getTime(Calendar calendar) {
        return calendar.getTime().toString().substring(11, 16);
    }
}
