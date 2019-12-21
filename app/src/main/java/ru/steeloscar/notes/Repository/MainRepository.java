package ru.steeloscar.notes.Repository;


import android.content.SharedPreferences;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.Repository.DB.Database.NotesDB;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;
import ru.steeloscar.notes.Repository.SharedPreference.NotePreference;


public class MainRepository implements MainContract.Repository {

    private static MainRepository instance;

    private MainRepository(){ }

    public static MainRepository getInstance() {

        if (instance == null) {
            instance = new MainRepository();
        }

        return instance;
    }

    @Override
    public Completable deleteDBNote(int id, NotesDB db) {
        return db.NotesDAO().delete(id);
    }

    @Override
    public Flowable<List<NotesEntity>> loadingDBNotes(NotesDB db) {
        return db.NotesDAO().getNotes();
    }

    @Override
    public Completable saveDBNotes(NotesEntity Note, NotesDB db) {
        return db.NotesDAO().insertALL(Note);
    }

    @Override
    public Completable updateDBNote(String[] Note, byte[] noteImage, NotesDB db) {
        return db.NotesDAO().update(Note[0], Note[1], Note[2], Note[3], noteImage);
    }

    @Override
    public Completable savePreferences(SharedPreferences sharedPreferences, int id) {
        return (new NotePreference(sharedPreferences ,id)).setSettings();
    }

    @Override
    public Observable<Integer> loadPreferences(SharedPreferences sharedPreferences) {
        return (new NotePreference(sharedPreferences)).getSettings();
    }
}