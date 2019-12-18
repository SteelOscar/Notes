package ru.steeloscar.notes.View;

import android.graphics.Bitmap;

public class NotesModel {

    private String title;
    private String detail;
    private String date;
    private int id;
    private int color;
    private Bitmap bitmap;

    public NotesModel(String title, String detail, String date, int id, int color, Bitmap bitmap){
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.id = id;
        this.color = color;
        this.bitmap = bitmap;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail(){
        return detail;
    }

    void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDate(){
        return date;
    }

    public int getID(){
        return id;
    }

    public int getColor(){
        return color;}

    public void setColor(int color) {
        this.color = color;
    }

    public Bitmap getBitmap(){
        return  bitmap;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}