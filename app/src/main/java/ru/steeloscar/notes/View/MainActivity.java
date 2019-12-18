package ru.steeloscar.notes.View;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import ru.steeloscar.notes.R;

public class MainActivity extends AppCompatActivity implements ActivityFragmentContract.ViewActivityInterface, ActivityFragmentContract.EditActivityInterface {

    private Fragment viewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        viewFragment = ViewFragment.getInstance();
        ft.add(R.id.frame_container, viewFragment);
        ft.commit();

    }

    @Override
    public void onFabClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment editFragment = new EditFragment(false);
        ft.addToBackStack(null);
        ft.replace(R.id.frame_container, editFragment);
        ft.commit();
    }

    @Override
    public void onNotesItemClicked(final String title, final String detail, final String date, final int position, final int id, final int color, final Bitmap bitmap) {

        Fragment editFragment = new EditFragment(true);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.frame_container, editFragment);
        ft.commit();

        final ActivityFragmentContract.ActivityEditInterface editInterface = (ActivityFragmentContract.ActivityEditInterface) editFragment;

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                editInterface.setNoteEditFragment(title, detail, date, position, id, color, bitmap);
            }
        }, 0);
    }

    @Override
    public void onEditFragmentSaveClicked(String title, String detail, String date, int id, int color, Bitmap bitmap) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, viewFragment);
        ft.commit();

        ActivityFragmentContract.ActivityViewInterface viewInterface = (ActivityFragmentContract.ActivityViewInterface) viewFragment;
        viewInterface.setViewNote(title, detail, date, id, color, bitmap);
    }

    @Override
    public void onViewFragmentUpdateNote(String title, String detail, int position, int color, Bitmap bitmap) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, viewFragment);
        ft.commit();

        ActivityFragmentContract.ActivityViewInterface viewInterface = (ActivityFragmentContract.ActivityViewInterface) viewFragment;
        viewInterface.updateViewNote(title, detail, position, color, bitmap);
    }
}