package ru.steeloscar.notes.Repository.DB.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.steeloscar.notes.Repository.DB.DAO.NotesDAO;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;

@Database(entities = NotesEntity.class, version = 1, exportSchema = false)
abstract public class NotesDB extends RoomDatabase {
    abstract public NotesDAO NotesDAO();
}