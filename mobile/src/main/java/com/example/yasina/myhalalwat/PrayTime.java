package com.example.yasina.myhalalwat;

//--------------------- Copyright Block ----------------------
/*

PrayTime.java: Prayer Times Calculator (ver 1.0)
Copyright (C) 2007-2010 PrayTimes.org

Java Code By: Hussain Ali Khan
Original JS Code By: Hamid Zarrabi-Zadeh

License: GNU LGPL v3.0

TERMS OF USE:
	Permission is granted to use this code, take or
	without modification, in any website or application
	provided that credit is given to the original work
	take a link back to PrayTimes.org.

This program is distributed in the hope that it will
be useful, but WITHOUT ANY WARRANTY.

PLEASE DO NOT REMOVE THIS COPYRIGHT BLOCK.

*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class PrayTime {

    // ---------------------- Global Variables --------------------
    private int    calcMethod; // caculation method
    private int    asrJuristic; // Juristic method for Asr
    private int    dhuhrMinutes; // minutes after mid-day for Dhuhr
    private int    timeFormat; // time format
    private double lat; // latitude
    private double lng; // longitude
    private double timeZone; // time-zone
    private double JDate; // Julian date

    public int getCalcMethod() {
        return this.calcMethod;
    }

    public int getAsrJuristic() {
        return this.asrJuristic;
    }

    public int getDhuhrMinutes() {
        return this.dhuhrMinutes;
    }

    public int getTimeFormat() {
        return this.timeFormat;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public double getTimeZone() {
        return this.timeZone;
    }

    public double getJDate() {
        return this.JDate;
    }

    private int getISNA() {
        return this.ISNA;
    }

    private int getMWL() {
        return this.MWL;
    }

    public void setCalcMethod(int calcMethod) {
        this.calcMethod = calcMethod;
    }

    public void setAsrJuristic(int asrJuristic) {
        this.asrJuristic = asrJuristic;
    }

    public void setDhuhrMinutes(int dhuhrMinutes) {
        this.dhuhrMinutes = dhuhrMinutes;
    }

    public void setTimeFormat(int timeFormat) {
        this.timeFormat = timeFormat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public void setJDate(double JDate) {
        this.JDate = JDate;
    }

    public void setCalculationMethod(CalculationMethod calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    private void setISNA(int ISNA) {
        this.ISNA = ISNA;
    }

    private void setMWL(int MWL) {
        this.MWL = MWL;
    }

    public void setJuristicMethod(CalcMethod juristicMethod) {
        this.juristicMethod = juristicMethod;
    }

    public void setAdjustinMethod(AdjustinMethod adjustinMethod) {
        this.adjustinMethod = adjustinMethod;
    }

    // ------------------------------------------------------------
    // Calculation Methods
    public enum CalculationMethod {
        JAFARI, KARACHI, ISNA, MWL, MAKKAH, EGYPT, CUSTOM, TEHRAN
    }

    private CalculationMethod calculationMethod;

    private int Jafari  = 0; // Ithna Ashari
    private int Karachi = 1; // University of Islamic Sciences, Karachi
    private int ISNA    = 2; // Islamic Society of North America (ISNA)
    private int MWL     = 3; // Muslim World League (MWL)
    private int Makkah  = 4; // Umm al-Qura, Makkah
    private int Egypt   = 5; // Egyptian General Authority of Survey
    private int Tehran  = 6; // Institute of Geophysics, University of Tehran
    private int Custom  = 7; // Custom Setting

    private CalcMethod juristicMethod;

    private int Shafii = 0; // Shafii (standard)
    private int Hanafi = 1; // Hanafi
    // Adjusting Methods for Higher Latitudes

    public enum AdjustinMethod {
        NONE(0), MID_NIGHT(1), ONE_SEVENTH(2), ANGLE_BASED(3);

        int index;

        AdjustinMethod(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }

    private AdjustinMethod adjustinMethod = AdjustinMethod.MID_NIGHT;
    // Time Formats
    private         int            Time24         = 0; // 24-hour format
    private         int            Time12         = 1; // 12-hour format
    private         int            Time12NS       = 2; // 12-hour format take no suffix
    private         int            Floating       = 3; // floating point number
    // Time Names
    private String InvalidTime; // The string used for invalid times

    // ------------------- Calc Method Parameters --------------------
    private HashMap<Integer, double[]> methodParams;

    /*
     * this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);
     *
     * fa : fajr angle
     * ms : maghrib selector (0 = angle; 1 = minutes after sunset)
     * mv : maghrib parameter value (in angle or minutes)
     * is : isha selector (0 = angle; 1 = minutes after maghrib)
     * iv : isha parameter value (in angle or minutes)
     */
    private double[] prayerTimesCurrent;
    private int[]    offsets;

    public PrayTime() {
        // Initialize vars

        this.setCalcMethod(0);
        this.setAsrJuristic(0);
        this.setDhuhrMinutes(0);
        this.setTimeFormat(0);

        InvalidTime = "-----"; // The string used for invalid times

        // --------------------- Technical Settings --------------------

        // ------------------- Calc Method Parameters --------------------

        // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
        offsets = new int[]{0, 0, 0, 0, 0, 0, 0};

        /*
         *
         * fa : fajr angle
         * ms : maghrib selector (0 = angle; 1 = minutes after sunset)
         * mv : maghrib parameter value (in angle or minutes)
         * is : isha selector (0 = angle; 1 = minutes after maghrib)
         * iv : isha parameter value (in angle or minutes)
         */
        methodParams = new HashMap<Integer, double[]>();

        // Jafari
        double[] Jvalues = {16, 0, 4, 0, 14};
        methodParams.put(this.getJafari(), Jvalues);

        // Karachi
        double[] Kvalues = {18, 1, 0, 0, 18};
        methodParams.put(this.getKarachi(), Kvalues);

        // ISNA
        double[] Ivalues = {15, 1, 0, 0, 15};
        methodParams.put(this.getISNA(), Ivalues);

        // MWL
        double[] MWvalues = {18, 1, 0, 0, 17};
        methodParams.put(this.getMWL(), MWvalues);

        // Makkah
        double[] MKvalues = {18.5, 1, 0, 1, 90};
        methodParams.put(this.getMakkah(), MKvalues);

        // Egypt
        double[] Evalues = {19.5, 1, 0, 0, 17.5};
        methodParams.put(this.getEgypt(), Evalues);

        // Tehran
        double[] Tvalues = {17.7, 0, 4.5, 0, 14};
        methodParams.put(this.getTehran(), Tvalues);

        // Custom
        double[] Cvalues = {18, 1, 0, 0, 15};
        methodParams.put(this.getCustom(), Cvalues);

    }

    // ---------------------- Trigonometric Functions -----------------------
    // range reduce angle in degrees.
    private double fixangle(double a) {

        a = a - (360 * (Math.floor(a / 360.0)));

        a = a < 0 ? (a + 360) : a;

        return a;
    }

    // range reduce hours to 0..23
    private double fixhour(double a) {
        a = a - 24.0 * Math.floor(a / 24.0);
        a = a < 0 ? (a + 24) : a;
        return a;
    }

    // radian to degree
    private double radiansToDegrees(double alpha) {
        return ((alpha * 180.0) / Math.PI);
    }

    // deree to radian
    private double DegreesToRadians(double alpha) {
        return ((alpha * Math.PI) / 180.0);
    }

    // degree sin
    private double dsin(double d) {
        return (Math.sin(DegreesToRadians(d)));
    }

    // degree cos
    private double dcos(double d) {
        return (Math.cos(DegreesToRadians(d)));
    }

    // degree tan
    private double dtan(double d) {
        return (Math.tan(DegreesToRadians(d)));
    }

    // degree arcsin
    private double darcsin(double x) {
        double val = Math.asin(x);
        return radiansToDegrees(val);
    }

    // degree arccos
    private double darccos(double x) {
        double val = Math.acos(x);
        return radiansToDegrees(val);
    }

    // degree arctan
    private double darctan(double x) {
        double val = Math.atan(x);
        return radiansToDegrees(val);
    }

    // degree arctan2
    private double darctan2(double y, double x) {
        double val = Math.atan2(y, x);
        return radiansToDegrees(val);
    }

    // degree arccot
    private double darccot(double x) {
        double val = Math.atan2(1.0, x);
        return radiansToDegrees(val);
    }

    // ---------------------- Time-Zone Functions -----------------------

    // compute base time-zone of the system
    private static double getBaseTimeZone() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = (timez.getRawOffset() / 1000.0) / 3600;
        return hoursDiff;
    }

    // detect daylight saving in a given date
    private double detectDaylightSaving() {
        TimeZone timez = TimeZone.getDefault();
        double hoursDiff = timez.getDSTSavings();
        return hoursDiff;
    }

    // ---------------------- Julian Date Functions -----------------------
    // calculate julian date from a calendar date
    private static double julianDate(int year, int month, int day) {

        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        double A = Math.floor(year / 100.0);

        double B = 2 - A + Math.floor(A / 4.0);

        double JD = Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5;

        return JD;
    }

    // convert a calendar date to julian date (second method)
    private double calcJD(int year, int month, int day) {
        double J1970 = 2440588.0;
        Date date = new Date(year, month - 1, day);

        double ms = date.getTime(); // # of milliseconds since midnight Jan 1,
        // 1970
        double days = Math.floor(ms / (1000.0 * 60.0 * 60.0 * 24.0));
        return J1970 + days - 0.5;

    }

    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://www.ummah.net/astronomy/saltime
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute declination angle of sun and equation of time
    private double[] sunPosition(double jd) {

        double D = jd - 2451545;
        double g = fixangle(357.529 + 0.98560028 * D);
        double q = fixangle(280.459 + 0.98564736 * D);
        double L = fixangle(q + (1.915 * dsin(g)) + (0.020 * dsin(2 * g)));

        // double R = 1.00014 - 0.01671 * [self dcos:g] - 0.00014 * [self dcos:
        // (2*g)];
        double e = 23.439 - (0.00000036 * D);
        double d = darcsin(dsin(e) * dsin(L));
        double RA = (darctan2((dcos(e) * dsin(L)), (dcos(L)))) / 15.0;
        RA = fixhour(RA);
        double EqT = q / 15.0 - RA;
        double[] sPosition = new double[2];
        sPosition[0] = d;
        sPosition[1] = EqT;

        return sPosition;
    }

    // compute equation of time
    private double equationOfTime(double jd) {
        return sunPosition(jd)[1];
    }

    // compute declination angle of sun
    private double sunDeclination(double jd) {
        return sunPosition(jd)[0];
    }

    // compute mid-day (Dhuhr, Zawal) time
    private double computeMidDay(double t) {
        double T = equationOfTime(this.getJDate() + t);
        return fixhour(12 - T);
    }

    // compute time for a given angle G
    private double computeTime(double G, double t) {
        double D = sunDeclination(this.getJDate() + t);
        double Z = computeMidDay(t);
        double Beg = -dsin(G) - dsin(D) * dsin(this.getLat());
        double Mid = dcos(D) * dcos(this.getLat());
        double V = darccos(Beg / Mid) / 15.0;

        return Z + (G > 90 ? -V : V);
    }

    // compute the time of Asr
    // Shafii: step=1, Hanafi: step=2
    private double computeAsr(double step, double t) {
        double D = sunDeclination(this.getJDate() + t);
        double G = -darccot(step + dtan(Math.abs(this.getLat() - D)));
        return computeTime(G, t);
    }

    // ---------------------- Misc Functions -----------------------
    // compute the difference between two times
    private double timeDiff(double time1, double time2) {
        return fixhour(time2 - time1);
    }

    // -------------------- Interface Functions --------------------
    // return prayer times for a given date
    private List<String> getDatePrayerTimes(int year, int month, int day,
                                            double latitude, double longitude, double tZone) {
        this.setLat(latitude);
        this.setLng(longitude);
        this.setTimeZone(tZone);
        this.setJDate(julianDate(year, month, day));
        double lonDiff = longitude / (15.0 * 24.0);
        this.setJDate(this.getJDate() - lonDiff);
        return computeDayTimes();
    }

    // return prayer times for a given date
    private List<String> getPrayerTimes(Calendar date, double latitude,
                                        double longitude, double tZone) {

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        return getDatePrayerTimes(year, month + 1, day, latitude, longitude, tZone);
    }

    // set custom values for calculation parameters
    private void setCustomParams(double[] params) {

        for (int i = 0; i < 5; i++) {
            if (params[i] == -1) {
                params[i] = methodParams.get(this.getCalcMethod())[i];
                methodParams.put(this.getCustom(), params);
            } else {
                methodParams.get(this.getCustom())[i] = params[i];
            }
        }
        this.setCalcMethod(this.getCustom());
    }

    // set the angle for calculating Fajr
    public void setFajrAngle(double angle) {
        double[] params = {angle, -1, -1, -1, -1};
        setCustomParams(params);
    }

    // set the angle for calculating Maghrib
    public void setMaghribAngle(double angle) {
        double[] params = {-1, 0, angle, -1, -1};
        setCustomParams(params);

    }

    // set the angle for calculating Isha
    public void setIshaAngle(double angle) {
        double[] params = {-1, -1, -1, 0, angle};
        setCustomParams(params);

    }

    // set the minutes after Sunset for calculating Maghrib
    public void setMaghribMinutes(double minutes) {
        double[] params = {-1, 1, minutes, -1, -1};
        setCustomParams(params);

    }

    // set the minutes after Maghrib for calculating Isha
    public void setIshaMinutes(double minutes) {
        double[] params = {-1, -1, -1, 1, minutes};
        setCustomParams(params);

    }

    // convert double hours to 24h format
    public String floatToTime24(double time) {

        String result;

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixhour(time + 0.5 / 60.0); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60.0);

        if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
            result = "0" + hours + ":0" + Math.round(minutes);
        } else if ((hours >= 0 && hours <= 9)) {
            result = "0" + hours + ":" + Math.round(minutes);
        } else if ((minutes >= 0 && minutes <= 9)) {
            result = hours + ":0" + Math.round(minutes);
        } else {
            result = hours + ":" + Math.round(minutes);
        }
        return result;
    }

    // convert double hours to 12h format
    public String floatToTime12(double time, boolean noSuffix) {

        if (Double.isNaN(time)) {
            return InvalidTime;
        }

        time = fixhour(time + 0.5 / 60); // add 0.5 minutes to round
        int hours = (int) Math.floor(time);
        double minutes = Math.floor((time - hours) * 60);
        String suffix, result;
        if (hours >= 12) {
            suffix = "pm";
        } else {
            suffix = "am";
        }
        hours = ((((hours + 12) - 1) % (12)) + 1);
        /*hours = (hours + 12) - 1;
        int hrs = (int) hours % 12;
        hrs += 1;*/
        if (!noSuffix) {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes) + " "
                        + suffix;
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes) + " " + suffix;
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes) + " " + suffix;
            } else {
                result = hours + ":" + Math.round(minutes) + " " + suffix;
            }

        } else {
            if ((hours >= 0 && hours <= 9) && (minutes >= 0 && minutes <= 9)) {
                result = "0" + hours + ":0" + Math.round(minutes);
            } else if ((hours >= 0 && hours <= 9)) {
                result = "0" + hours + ":" + Math.round(minutes);
            } else if ((minutes >= 0 && minutes <= 9)) {
                result = hours + ":0" + Math.round(minutes);
            } else {
                result = hours + ":" + Math.round(minutes);
            }
        }
        return result;

    }

    // convert double hours to 12h format take no suffix
    public String floatToTime12NS(double time) {
        return floatToTime12(time, true);
    }

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private double[] computeTimes(double[] times) {

        double[] t = dayPortion(times);

        double Fajr = computeTime(180 - methodParams.get(getCalcMethod())[0], t[0]);

        double Sunrise = computeTime(180 - 0.833, t[1]);

        double Dhuhr = computeMidDay(t[2]);
        double Asr = computeAsr(1 + getAsrJuristic(), t[3]);
        double Sunset = computeTime(0.833, t[4]);

        double Maghrib = computeTime(methodParams.get(getCalcMethod())[2], t[5]);
        double Isha = computeTime(methodParams.get(getCalcMethod())[4], t[6]);

        return new double[]{Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha};

    }

    // compute prayer times at given julian date
    private List<String> computeDayTimes() {
        double[] times = {5, 6, 12, 13, 18, 18, 18}; // default times
        times = computeTimes(times);
        times = adjustTimes(times);
        times = tuneTimes(times);

        return adjustTimesFormat(times);
    }

    // adjust times in a prayer time array
    private double[] adjustTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] += this.getTimeZone() - this.getLng() / 15;
        }

        times[2] += this.getDhuhrMinutes() / 60; // Dhuhr
        if (methodParams.get(this.getCalcMethod())[1] == 1) // Maghrib
        {
            times[5] = times[4] + methodParams.get(this.getCalcMethod())[2] / 60;
        }
        if (methodParams.get(this.getCalcMethod())[3] == 1) // Isha
        {
            times[6] = times[5] + methodParams.get(this.getCalcMethod())[4] / 60;
        }

        if (adjustinMethod != AdjustinMethod.NONE) {
            times = adjustHighLatTimes(times);
        }

        return times;
    }

    // convert times array to given time format
    private List<String> adjustTimesFormat(double[] times) {

        List<String> result = new ArrayList<>();

        if (this.getTimeFormat() == this.getFloating()) {
            for (double time : times) {
                result.add(String.valueOf(time));
            }
            return result;
        }

        for (int i = 0; i < 7; i++) {
            if (this.getTimeFormat() == this.getTime12()) {
                result.add(floatToTime12(times[i], false));
            } else if (this.getTimeFormat() == this.getTime12NS()) {
                result.add(floatToTime12(times[i], true));
            } else {
                result.add(floatToTime24(times[i]));
            }
        }
        return result;
    }

    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private double[] adjustHighLatTimes(double[] times) {
        double nightTime = timeDiff(times[4], times[1]); // sunset to sunrise

        // Adjust Fajr
        double FajrDiff = nightPortion(methodParams.get(this.getCalcMethod())[0]) * nightTime;

        if (Double.isNaN(times[0]) || timeDiff(times[0], times[1]) > FajrDiff) {
            times[0] = times[1] - FajrDiff;
        }

        // Adjust Isha
        double IshaAngle = (methodParams.get(this.getCalcMethod())[3] == 0) ? methodParams.get(this.getCalcMethod())[4] : 18;
        double IshaDiff = this.nightPortion(IshaAngle) * nightTime;
        if (Double.isNaN(times[6]) || this.timeDiff(times[4], times[6]) > IshaDiff) {
            times[6] = times[4] + IshaDiff;
        }

        // Adjust Maghrib
        double MaghribAngle = (methodParams.get(this.getCalcMethod())[1] == 0) ? methodParams.get(this.getCalcMethod())[2] : 4;
        double MaghribDiff = nightPortion(MaghribAngle) * nightTime;
        if (Double.isNaN(times[5]) || this.timeDiff(times[4], times[5]) > MaghribDiff) {
            times[5] = times[4] + MaghribDiff;
        }

        return times;
    }

    // the night portion used for adjusting times in higher latitudes
    private double nightPortion(double angle) {
        switch (adjustinMethod) {
            case ANGLE_BASED:
                return (angle) / 60.0;
            case MID_NIGHT:
                return 0.5;
            case ONE_SEVENTH:
                return 0.14286;
            default:
                return 0;
        }
    }

    // convert hours to day portions
    private double[] dayPortion(double[] times) {
        double[] dayPortions = new double[times.length];
        for (int i = 0; i < 7; i++) {
            dayPortions[i] = times[i] / 24;
        }
        return dayPortions;
    }

    // Tune timings for adjustments
    // Set time offsets
    public void tune(int[] offsetTimes) {
        System.arraycopy(offsetTimes, 0, this.offsets, 0, offsetTimes.length);
    }

    private double[] tuneTimes(double[] times) {
        for (int i = 0; i < times.length; i++) {
            times[i] = times[i] + this.offsets[i] / 60.0;
        }
        return times;
    }

    public static List<String> calculatePrayTimes(Calendar date, double lat, double lon, CalcMethod method) {
        return calculatePrayTimes(date, lat, lon, getBaseTimeZone(), method);
    }

    private static void recalculateFadjrAndIsha(List<String> prayerTimes) {
        String fadjr = fadjrFromSunrise(prayerTimes.get(1));
        prayerTimes.set(0, fadjr);
        String isha = ishaFromMagrib(prayerTimes.get(5));
        prayerTimes.set(6, isha);
    }

    private static String ishaFromMagrib(String magrib) {
        int sunsetMinutes = stringToMinutesFromDayStart(magrib);
        sunsetMinutes += 90;
        return sunsetMinutes / 60 + ":" + sunsetMinutes % 60;
    }

    private static String fadjrFromSunrise(String sunset) {
        int sunsetMinutes = stringToMinutesFromDayStart(sunset);
        sunsetMinutes -= 120;
        return sunsetMinutes / 60 + ":" + sunsetMinutes % 60;
    }


    private static boolean lessThan40MinutesBetween(String magrib, String isha) {
        int magribMinutes = stringToMinutesFromDayStart(magrib);
        int ishaMinutes = stringToMinutesFromDayStart(isha);
        return Math.abs(ishaMinutes - magribMinutes) < 40;
    }

    private static int stringToMinutesFromDayStart(String time) {
        String[] times = time.split(":");
        return Integer.valueOf(times[0]) * 60 + Integer.valueOf(times[1]);
    }


    private int getJafari() {
        return Jafari;
    }

    private void setJafari(int jafari) {
        Jafari = jafari;
    }

    private int getKarachi() {
        return Karachi;
    }

    private void setKarachi(int karachi) {
        Karachi = karachi;
    }

    private int getMakkah() {
        return Makkah;
    }

    private void setMakkah(int makkah) {
        Makkah = makkah;
    }

    private int getEgypt() {
        return Egypt;
    }

    private void setEgypt(int egypt) {
        Egypt = egypt;
    }

    private int getCustom() {
        return Custom;
    }

    private void setCustom(int custom) {
        Custom = custom;
    }

    private int getTehran() {
        return Tehran;
    }

    private void setTehran(int tehran) {
        Tehran = tehran;
    }

    private int getShafii() {
        return Shafii;
    }

    private void setShafii(int shafii) {
        Shafii = shafii;
    }

    private int getHanafi() {
        return Hanafi;
    }

    private void setHanafi(int hanafi) {
        Hanafi = hanafi;
    }

    private int getTime24() {
        return Time24;
    }

    private void setTime24(int time24) {
        Time24 = time24;
    }

    private int getTime12() {
        return Time12;
    }

    private void setTime12(int time12) {
        Time12 = time12;
    }

    private int getTime12NS() {
        return Time12NS;
    }

    private void setTime12NS(int time12ns) {
        Time12NS = time12ns;
    }

    private int getFloating() {
        return Floating;
    }

    private void setFloating(int floating) {
        Floating = floating;
    }

    public enum CalcMethod {
        SHAFII(1), HANAFI(2);

        int number;

        CalcMethod(int number) {
            this.number = number;
        }

        public int getNumber() {
            return this.number;
        }
    }



    public static List<String> calculatePrayTimes(Calendar date, double lat, double lon, double timeZone, CalcMethod method) {

        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.Custom);
        if (method == CalcMethod.SHAFII) {
            prayers.setAsrJuristic(prayers.Shafii);
        } else {
            prayers.setAsrJuristic(prayers.Hanafi);
        }

        prayers.setAdjustinMethod(AdjustinMethod.ANGLE_BASED);

        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        List<String> prayerTimes = prayers.getPrayerTimes(date, lat, lon, timeZone);
        String fadjr = prayerTimes.get(0);
        String isha = prayerTimes.get(6);
        if(lessThan40MinutesBetween(fadjr, isha)) {
            recalculateFadjrAndIsha(prayerTimes);
        }
        prayerTimes.remove(4); // remove sunset
        return prayerTimes;
    }

    public static int[] calculateNamazesTimeInMinutesFromDayStart(List<String> prayTimes) {
        int[] namazesTimesInMinutes = new int[prayTimes.size()];
        for (int i = 0; i < prayTimes.size(); i++) {
            namazesTimesInMinutes[i] = stringToMinutesFromDayStart(prayTimes.get(i));
        }
        return namazesTimesInMinutes;
    }

   /* public static String getTimeSpanToNearestNamaz(City city, Context context, CalcMethod method){
        Calendar now = Calendar.getInstance();

        List<String> prayerTimes = PrayTime.calculatePrayTimes(Calendar.getInstance(), city.getLatitude(),
                city.getLongitude(), city.getTimeZone(), method);

        int[] times = PrayTime.calculateNamazesTimeInMinutesFromDayStart(prayerTimes);
        int currentOffsetFromDayStart = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
        for (int time : times) {
            if (time > currentOffsetFromDayStart) {
                int minutesToNamaz = time - currentOffsetFromDayStart;
                return Strings.getReadableTimeSpanToNamaz(minutesToNamaz, context);
            }
        }
        return getSpanToFirstTomorrowPrayer(city, context, currentOffsetFromDayStart, method);
    }

    private static String getSpanToFirstTomorrowPrayer(City city, Context context, int currentOffsetFromDayStart, CalcMethod method) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        List<String> prayerTimesForTomorrow = PrayTime.calculatePrayTimes(tomorrow, city.getLatitude(),
                city.getLongitude(), city.getTimeZone(), method);

        int[] timesTomorrow = PrayTime.calculateNamazesTimeInMinutesFromDayStart(prayerTimesForTomorrow);
        int minutesToNamaz = timesTomorrow[0] + 24 * 60 - currentOffsetFromDayStart;
        return Strings.getReadableTimeSpanToNamaz(minutesToNamaz, context);
    }
*/
}