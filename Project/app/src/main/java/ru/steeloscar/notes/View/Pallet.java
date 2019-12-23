package ru.steeloscar.notes.View;

import java.util.ArrayList;

import ru.steeloscar.notes.R;

public class Pallet {

    private int color;

    public Pallet(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public static ArrayList<Pallet> getPallet(){
        ArrayList<Pallet> pallets = new ArrayList<>();
        pallets.add(new Pallet(R.color.palletColor1));
        pallets.add(new Pallet(R.color.palletColor2));
        pallets.add(new Pallet(R.color.palletColor3));
        pallets.add(new Pallet(R.color.palletColor4));
        pallets.add(new Pallet(R.color.palletColor5));
        pallets.add(new Pallet(R.color.palletColor6));
        pallets.add(new Pallet(R.color.palletColor7));
        pallets.add(new Pallet(R.color.palletColor8));
        pallets.add(new Pallet(R.color.palletColor9));
        pallets.add(new Pallet(R.color.palletColor10));
        pallets.add(new Pallet(R.color.palletColor11));
        pallets.add(new Pallet(R.color.palletColor12));
        pallets.add(new Pallet(R.color.palletColor13));
        pallets.add(new Pallet(R.color.palletColor14));

        return pallets;
    }
}
