package ru.steeloscar.notes.View;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.Presenter.ViewFragmentPresenter;
import ru.steeloscar.notes.R;



public class ViewFragment extends Fragment implements ActivityFragmentContract.ActivityViewInterface, MainContract.ViewFragment {

    private ActivityFragmentContract.ViewActivityInterface viewActivityInterface;
    private MainContract.ViewFragmentPresenter viewFragmentPresenter;

    private static ViewFragment instance;

    private ArrayList<NotesModel> values = new ArrayList<>();
    private NotesArrayAdapter adapter;


    private ViewFragment(){}

    static ViewFragment getInstance() {

        if (instance == null) {
            instance = new ViewFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getContext();
        adapter = new NotesArrayAdapter(context, values);
        viewFragmentPresenter = new ViewFragmentPresenter(this, context);
        if (values.isEmpty()) viewFragmentPresenter.onRestoreDBNotes();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        viewActivityInterface = (ActivityFragmentContract.ViewActivityInterface) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view, container, false);
        final ListView listView = view.findViewById(R.id.notes_layout);
        listView.setAdapter(adapter);

        View fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivityInterface.onFabClicked();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotesModel Note = values.get(position);
                viewActivityInterface.onNotesItemClicked(Note.getTitle(), Note.getDetail(), Note.getDate(), position, Note.getID(), Note.getColor(), Note.getBitmap());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap imageBitmap = values.get(position).getBitmap();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View builderImageView = layoutInflater.inflate(R.layout.image_layout, null);
                ImageView imageView = builderImageView.findViewById(R.id.view_image);

                imageView.setImageBitmap(imageBitmap);

                builder.setView(builderImageView).create().show();

                return true;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewFragmentPresenter.destroyDisposables();
    }

    @Override
    public void showViewFragmentNotes(ArrayList<NotesModel> Notes) {
        values.addAll(Notes);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setViewNote(String title, String detail, String date, int id, int color, Bitmap bitmap) {
        values.add(0, new NotesModel(title, detail, date, id, color, bitmap));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateViewNote(String title, String detail, int position, int color, Bitmap bitmap) {
        NotesModel Note = values.get(position);

        Note.setTitle(title);
        Note.setDetail(detail);
        Note.setColor(color);
        Note.setBitmap(bitmap);
        adapter.notifyDataSetChanged();
    }
}