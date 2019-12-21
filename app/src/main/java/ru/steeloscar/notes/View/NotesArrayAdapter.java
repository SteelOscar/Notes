package ru.steeloscar.notes.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.steeloscar.notes.R;

public class NotesArrayAdapter extends RecyclerView.Adapter<NotesArrayAdapter.ViewHolder> {

    private ArrayList<NotesModel> values;
    private LayoutInflater inflater;
    private NoteItemListener listener;

    NotesArrayAdapter(Context context, NoteItemListener listener, ArrayList<NotesModel> values) {
        this.values = values;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView colorView;
        TextView title;
        TextView detail;
        TextView date;
        ImageView image;
        View foregroundView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.note_color_cardView);
            title = itemView.findViewById(R.id.note_title);
            detail = itemView.findViewById(R.id.note_detail);
            date = itemView.findViewById(R.id.note_date);
            image = itemView.findViewById(R.id.note_image);
            foregroundView = itemView.findViewById(R.id.view_foreground);
        }
    }

    @NonNull
    @Override
    public NotesArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notes_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesArrayAdapter.ViewHolder holder, final int position) {
        NotesModel NoteModel = values.get(position);

        holder.colorView.setCardBackgroundColor(NoteModel.getColor());
        holder.title.setText(NoteModel.getTitle());
        holder.detail.setText(NoteModel.getDetail());
        holder.date.setText(NoteModel.getDate());

        if (NoteModel.getBitmap() != null) {
            holder.image.setImageBitmap(NoteModel.getBitmap());
            holder.image.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setOnClickListener(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return  listener.setOnLongClickListener(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public void removeItem(int position) {
        values.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, values.size());
    }
}