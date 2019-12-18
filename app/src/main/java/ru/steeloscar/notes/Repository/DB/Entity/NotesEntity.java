package ru.steeloscar.notes.Repository.DB.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NotesEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String title;
    private String detail;
    private String date;
    private String color;
    private byte[] image;


    public NotesEntity(String title, String detail, String date, String color, byte[] image) {
        this.title = title;
        this.detail = detail;
        this.date = date;
        this.color = color;
        this.image = image;
    }

    public String getTitle(){
        return title;
    }

    public String getDetail(){
        return detail;
    }

    public String getDate() {
        return date;
    }

    public String getColor() {
        return color;
    }

    public byte[] getImage() {
        return image;
    }
}