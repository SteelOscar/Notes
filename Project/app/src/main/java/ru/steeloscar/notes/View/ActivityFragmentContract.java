package ru.steeloscar.notes.View;

import android.graphics.Bitmap;

public interface ActivityFragmentContract {

    interface ViewActivityInterface {
        void onFabClicked();
        void onNotesItemClicked(String title, String detail, String date, int position, int id, int color, Bitmap bitmap);
    }

    interface ActivityViewInterface {
        void setViewNote(String title, String detail, String date, int id, int color, Bitmap bitmap);
        void updateViewNote(String title, String detail, int position, int color, Bitmap bitmap);
    }

    interface ActivityEditInterface {
        void setNoteEditFragment(String title, String detail, String date, int id, int position, int color, Bitmap bitmap);
    }

    interface EditActivityInterface {
        void onEditFragmentSaveClicked(String title, String detail, String date, int id, int color, Bitmap bitmap);
        void onViewFragmentUpdateNote(String title, String detail, int position, int color, Bitmap bitmap);
    }
}