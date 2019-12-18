package ru.steeloscar.notes.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import ru.steeloscar.notes.R;

public class NotesArrayAdapter extends BaseAdapter {

    private ArrayList<NotesModel> values;
    private LayoutInflater inflater;

    NotesArrayAdapter(Context context, ArrayList<NotesModel> values) {
        this.values = values;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.notes_layout,parent,false);
        }

        NotesModel NoteModel = values.get(position);

        CardView colorView = view.findViewById(R.id.note_color_cardView);
        TextView title = view.findViewById(R.id.note_title);
        TextView detail = view.findViewById(R.id.note_detail);
        TextView date = view.findViewById(R.id.note_date);
        ImageView image = view.findViewById(R.id.note_image);

        colorView.setCardBackgroundColor(NoteModel.getColor());
        title.setText(NoteModel.getTitle());
        detail.setText(NoteModel.getDetail());
        date.setText(NoteModel.getDate());

        if (NoteModel.getBitmap() != null) {
            image.setImageBitmap(NoteModel.getBitmap());
            image.setVisibility(View.VISIBLE);
        }

        return view;
    }
}