package ru.steeloscar.notes.Presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.room.Room;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.Repository.DB.Database.NotesDB;
import ru.steeloscar.notes.Repository.DB.Entity.NotesEntity;
import ru.steeloscar.notes.Repository.MainRepository;
import ru.steeloscar.notes.View.NotesModel;

public class EditFragmentPresenter implements MainContract.EditFragmentPresenter {

    private MainContract.EditFragment editFragment;
    private MainContract.Repository repository;

    private CompositeDisposable disposables;
    private NotesDB db;
    private SharedPreferences sharedPreferences;

    public EditFragmentPresenter(MainContract.EditFragment editFragment, Context context, SharedPreferences sharedPreferences) {
        this.editFragment = editFragment;
        repository = MainRepository.getInstance();
        this.sharedPreferences = sharedPreferences;
        disposables = new CompositeDisposable();
        db = Room.databaseBuilder(context, NotesDB.class,"Notes-database").build();
    }


    @Override
    public void destroyDisposables() {
            disposables.dispose();
    }

    @Override
    public void onSaveDBNote() {
        NotesModel Note = editFragment.saveNotesDB();

        disposables.add(repository.savePreferences(sharedPreferences, Note.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        disposables.add(repository.saveDBNotes(modelToEntity(Note), db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void onUpdateDBNote() {
        NotesModel Note = editFragment.updateNoteDB();
        disposables.add(repository.updateDBNote(new String[]{Note.getTitle(), Note.getDetail(), String.valueOf(Note.getID()), String.valueOf(Note.getColor())},bitmapToByte(Note.getBitmap()), db)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void getLastNoteIdSharedPreferences() {
        disposables.add(repository.loadPreferences(sharedPreferences)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                         editFragment.setID(integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    private NotesEntity modelToEntity(NotesModel Note){
        return new NotesEntity(Note.getTitle(), Note.getDetail(), Note.getDate(), String.valueOf(Note.getColor()), bitmapToByte(Note.getBitmap()));
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,70, stream);
            return stream.toByteArray();
        }
        return new byte[]{(byte) 1};
    }

    public File createTempFile() {
        File tempDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        tempDir=new File(tempDir.getAbsolutePath() + "/Note");

        if(!tempDir.exists()) { tempDir.mkdir(); }

        try {
            return File.createTempFile("Note", ".jpg", tempDir);
        } catch (IOException e) {
            editFragment.failedCreateTempFile();
        }

        return null;
    }
}