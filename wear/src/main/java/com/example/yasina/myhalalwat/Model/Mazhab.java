package com.example.yasina.myhalalwat.Model;

/**
 * Created by yasina on 10.08.15.
 */
public class Mazhab {

    private boolean hanafi;
    private int id;

    public Mazhab(boolean hanafi) {
        this.hanafi = hanafi;
    }

    public Mazhab() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHanafi() {
        return hanafi;
    }

    public void setHanafi(boolean hanafi) {
        this.hanafi = hanafi;
    }
}
