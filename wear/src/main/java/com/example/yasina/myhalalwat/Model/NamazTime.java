package com.example.yasina.myhalalwat.Model;

/**
 * Created by yasina on 05.08.15.
 */
public class NamazTime {

    private String namazName;
    private int hours, min, id;

    public NamazTime(String namazName, int hours, int min) {
        this.namazName = namazName;
        this.hours = hours;
        this.min = min;
    }

    public NamazTime() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamazName() {
        return namazName;
    }

    public void setNamazName(String namazName) {
        this.namazName = namazName;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
