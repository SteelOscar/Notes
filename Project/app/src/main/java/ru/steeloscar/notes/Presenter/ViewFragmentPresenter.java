package ru.steeloscar.notes.Presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.Room;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.Repository.DB.Database.NotesDB;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;
import ru.steeloscar.notes.Repository.MainRepository;
import ru.steeloscar.notes.View.NotesModel;

public class ViewFragmentPresenter implements MainContract.ViewFragmentPresenter {

    private MainContract.ViewFragment viewFragment;
    private MainContract.Repository repository;
    private CompositeDisposable disposables;
    private NotesDB db;

    public ViewFragmentPresenter(MainContract.ViewFragment _viewFragment, Context _context) {
        viewFragment = _viewFragment;
        repository = MainRepository.getInstance();
        disposables = new CompositeDisposable();
        db = Room.databaseBuilder(_context, NotesDB.class,"Notes-database").build();
    }

    @Override
    public void onRestoreDBNotes() {
        disposables.add(repository.loadingDBNotes(db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NotesEntity>>() {
                    @Override
                    public void accept(List<NotesEntity> notesEntities) {
                        viewFragment.showViewFragmentNotes(refactorData(notesEntities));
                    }
                }));
    }

    @Override
    public void onDeleteDBNote(String id) {
        disposables.add(repository.deleteDBNote(id, db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void destroyDisposables() {
        disposables.dispose();
    }

    private ArrayList<NotesModel> refactorData(@NotNull List<NotesEntity> Notes) {
        ArrayList<NotesModel> NotesModels = new ArrayList<NotesModel>();

        for (NotesEntity NoteEntity:
                Notes) {
            Bitmap bitmapImage = (NoteEntity.getImage().length == 1)? null: BitmapFactory.decodeByteArray(NoteEntity.getImage(), 0, NoteEntity.getImage().length);
            NotesModels.add(new NotesModel(NoteEntity.getTitle(),NoteEntity.getDetail(), NoteEntity.getDate(), NoteEntity.id, Integer.parseInt(NoteEntity.getColor()), bitmapImage));
        }

        return NotesModels;
    }
}