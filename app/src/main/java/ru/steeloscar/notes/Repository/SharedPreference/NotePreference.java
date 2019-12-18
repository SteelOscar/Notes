package ru.steeloscar.notes.Repository.SharedPreference;


import android.content.SharedPreferences;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;

public class NotePreference {

    private static final String APP_PREFERENCES = "note_settings";
    private static final String KEY = "id";

    private SharedPreferences notePreferences;
    private int id;

    public NotePreference(SharedPreferences sharedPreferences, int id) {
        this.notePreferences = sharedPreferences;
        this.id = id;
    }

    public NotePreference(SharedPreferences sharedPreferences) {
        this.notePreferences = sharedPreferences;
    }

    public Completable setSettings() {

        notePreferences.edit().putInt(KEY, id).apply();

        return new Completable() {
            @Override
            protected void subscribeActual(CompletableObserver observer) {

            }
        };
    }

    public Observable<Integer> getSettings() {
        Observable<Integer> getID;

        if (notePreferences.contains(APP_PREFERENCES)) {
            getID = Observable.just(notePreferences.getInt(KEY,1));
        } else {
            getID = Observable.just(1);
        }

        return getID;
    }

}