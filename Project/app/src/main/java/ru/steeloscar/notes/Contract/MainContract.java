package ru.steeloscar.notes.Contract;

import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import ru.steeloscar.notes.Repository.DB.Database.NotesDB;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;
import ru.steeloscar.notes.View.NotesModel;

public interface MainContract {

    interface ViewFragment {
        void showViewFragmentNotes(ArrayList<NotesModel> Notes);
        NotesDB createDB();
    }

    interface EditFragment {
        NotesModel saveNotesDB();
        NotesModel updateNoteDB();
        void setID(int id);
        void failedCreateTempFile();
        NotesDB createDB();
    }

    interface ViewFragmentPresenter {
        void onRestoreDBNotes();
        void onDeleteDBNote(String id);
        void destroyDisposables();
    }

    interface EditFragmentPresenter {
        void destroyDisposables();
        void onSaveDBNote();
        void onUpdateDBNote();
        void getLastNoteIdSharedPreferences();
        File createTempFile();
    }

    interface Repository {
        Flowable<List<NotesEntity>> loadingDBNotes(NotesDB db);
        Completable saveDBNotes(NotesEntity Note, NotesDB db);
        Completable updateDBNote(String[] Note, byte[] noteImage, NotesDB db);
        Completable savePreferences(SharedPreferences sharedPreferences, int id);
        Completable deleteDBNote(String id, NotesDB db);
        Observable<Integer> loadPreferences(SharedPreferences sharedPreferences);
    }
}