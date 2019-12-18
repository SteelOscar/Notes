package ru.steeloscar.notes.View;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import ru.steeloscar.notes.R;

public class PalletsArrayAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Pallet> pallets;
    private LayoutInflater inflater;
    private ArrayList<ViewHolder> holders = new ArrayList<ViewHolder>();

    PalletsArrayAdapter(Context context, ArrayList<Pallet> pallets){
        this.context = context;
        this.pallets = pallets;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder {
        CardView colorView;
        int color;
    }

    @Override
    public int getCount() {
        return pallets.size();
    }

    @Override
    public Object getItem(int position) {
        return pallets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.pallet_layout, parent, false);
            holder.colorView = view.findViewById(R.id.palletCardView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.color = pallets.get(position).getColor();
        holder.colorView.setCardBackgroundColor(context.getResources().getColor(pallets.get(position).getColor()));

        holders.add(holder);

        return view;
    }

    int getColorViewHolder(int position) {
        return holders.get(position+1).color;
    }

}
