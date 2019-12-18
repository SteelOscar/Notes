package ru.steeloscar.notes.Repository.DB.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;

@Dao
public interface NotesDAO {

    //Add to Database
    @Insert
    Completable insertALL(NotesEntity note);

    @Delete
    Completable delete(NotesEntity note);

    @Query("UPDATE NotesEntity SET title=:title, detail=:detail, color=:color, image=:image WHERE id=:id")
    Completable update(String title, String detail, String id, String color, byte[] image);

    @Query("SELECT * FROM NotesEntity ORDER BY id DESC")
    Flowable<List<NotesEntity>> getNotes();
}