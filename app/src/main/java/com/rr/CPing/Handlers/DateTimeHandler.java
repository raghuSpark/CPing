package com.rr.CPing.Handlers;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHandler {

    public DateTimeHandler() {
    }

    public static StringBuilder hr_24To12Format(StringBuilder hr_24Time) {
        int h1 = (int) hr_24Time.charAt(0) - '0';
        int h2 = (int) hr_24Time.charAt(1) - '0';

        int hh = h1 * 10 + h2;
        String Meridian = (hh < 12) ? "AM" : "PM";

        hh %= 12;
        StringBuilder hr_12Time;
        if (hh == 0) {
            hr_12Time = new StringBuilder("12");
        } else {
            hr_12Time = new StringBuilder(Integer.toString(hh));
        }
        for (int i = 2; i < 5; i++) hr_12Time.append(hr_24Time.charAt(i));
        hr_12Time.append(" ").append(Meridian);
        return hr_12Time;
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
        int n = s.length();
        return s.substring(0, 16) + " " + s.charAt(n - 4) + s.charAt(n - 3) + s.charAt(n - 2) + s.charAt(n - 1);
    }

    public String getDate(Calendar calendar) {
        String s = calendar.getTime().toString();
        int n = s.length();
        return s.substring(8, 10) + " " + s.substring(4, 7) + ", " + s.charAt(n - 4) + s.charAt(n - 3) + s.charAt(n - 2) + s.charAt(n - 1);
    }

    public String getTime(Calendar calendar) {
        return calendar.getTime().toString().substring(11, 16);
    }
}
