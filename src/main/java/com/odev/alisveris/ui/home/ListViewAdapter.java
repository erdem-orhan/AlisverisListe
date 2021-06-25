package com.odev.alisveris.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.odev.alisveris.R;
import com.odev.alisveris.UrunModel;

import java.util.ArrayList;

public class ListViewAdapter extends RecyclerView.Adapter<com.odev.alisveris.ui.home.ListViewAdapter.ViewHolder>{

    private ArrayList<UrunModel> urunmodels = new ArrayList<>();
    private Context context;

    public ListViewAdapter(ArrayList<UrunModel> urunmodels, Context context) {
        this.urunmodels = urunmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pricerow, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("hey burak 123 deneme","times called!");


        holder.name1.setText(urunmodels.get(position).getUrunadi());
        holder.name2.setText(urunmodels.get(position).getUrunadeti());
        holder.price1.setText(urunmodels.get(position).getUrunfiyati());

        holder.name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.silbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.arrayList.remove(position);
                final ListViewAdapter listViewAdapter = new ListViewAdapter(HomeFragment.arrayList,context);
                HomeFragment.rc.setAdapter(listViewAdapter);
            }
        });
        holder.duzenlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new MyDialogFragment();
                Bundle b = new Bundle();
                b.putString("urunadi", holder.name1.getText().toString()); //Your id
                b.putString("urunfiyati", holder.name2.getText().toString()); //Your id
                b.putString("urunadeti", holder.price1.getText().toString()); //Your id
                b.putString("callclass", "list"); //Your id
                b.putInt("index", position); //Your id
                dialogFragment.setArguments(b); //Put your id
                dialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "aa");
            }
        });
    }

    @Override
    public int getItemCount() {
        return urunmodels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name1,name2;
        Button duzenlebtn, silbtn;
        TextView price1,value1;
        LinearLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1  = itemView.findViewById(R.id.name1);
            name2  = itemView.findViewById(R.id.name2);
            price1  = itemView.findViewById(R.id.price1);
            value1 = itemView.findViewById(R.id.value1);
            duzenlebtn = itemView.findViewById(R.id.button4);
            silbtn = itemView.findViewById(R.id.button5);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
