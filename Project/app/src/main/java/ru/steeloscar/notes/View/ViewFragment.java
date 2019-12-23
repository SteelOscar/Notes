package ru.steeloscar.notes.View;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.Presenter.ViewFragmentPresenter;
import ru.steeloscar.notes.R;



public class ViewFragment extends Fragment implements ActivityFragmentContract.ActivityViewInterface, MainContract.ViewFragment {

    private ActivityFragmentContract.ViewActivityInterface viewActivityInterface;
    private MainContract.ViewFragmentPresenter viewFragmentPresenter;

    private static ViewFragment instance;

    private RecyclerView recyclerView;

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
        Context context = getContext();

        NoteItemListener listener = new NoteItemListener() {
            @Override
            public void setOnClickListener(int position) {
                NotesModel Note = values.get(position);
                viewActivityInterface.onNotesItemClicked(Note.getTitle(), Note.getDetail(), Note.getDate(), position, Note.getID(), Note.getColor(), Note.getBitmap());
            }

            @Override
            public Boolean setOnLongClickListener(int position) {
                Bitmap imageBitmap = values.get(position).getBitmap();

                if (imageBitmap == null) return false;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View builderImageView = layoutInflater.inflate(R.layout.image_layout, null);
                ImageView imageView = builderImageView.findViewById(R.id.view_image);

                imageView.setImageBitmap(imageBitmap);

                builder.setView(builderImageView).create().show();

                return true;
            }
        };

        adapter = new NotesArrayAdapter(context, listener, values);
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
        recyclerView = view.findViewById(R.id.notes_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        View fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivityInterface.onFabClicked();
            }
        });

        setItemTouchHelpListener();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewFragmentPresenter.destroyDisposables();
    }

    @Override
    public void showViewFragmentNotes(ArrayList<NotesModel> Notes) {
        values = Notes;
        adapter.setValues(values);
    }

    @Override
    public int deleteNotesDB(int position) {
        return values.get(position).getID();
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

    private void setItemTouchHelpListener() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    viewFragmentPresenter.onDeleteDBNote(String.valueOf(values.get(position).getID()));
                    adapter.removeItem(position);
                }
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((NotesArrayAdapter.ViewHolder) viewHolder).foregroundView;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((NotesArrayAdapter.ViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((NotesArrayAdapter.ViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((NotesArrayAdapter.ViewHolder) viewHolder).foregroundView;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}