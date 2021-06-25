package com.odev.alisveris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.odev.alisveris.ListeduzenleActivity;
import com.odev.alisveris.ui.home.MyDialogFragment;
import com.odev.alisveris.ui.listeler.PumpingPage;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterDuzenle extends RecyclerView.Adapter<ListViewAdapterDuzenle.ViewHolder>{

    private ArrayList<UrunModel> urunmodels = new ArrayList<>();
    private Context context;

    public ListViewAdapterDuzenle(ArrayList<UrunModel> urunmodels, Context context) {
        this.urunmodels = urunmodels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pricerow2, parent, false);
        ViewHolder holder = new ViewHolder(view);

        MultiDex.install(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("hey  123 deneme","times called!");

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
                ListeduzenleActivity.arrayList.remove(position);
                final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(ListeduzenleActivity.arrayList,context);
                ListeduzenleActivity.rc.setAdapter(listViewAdapter);
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
                b.putString("callclass", "listduzenle"); //Your id
                b.putInt("index", position); //Your id
                dialogFragment.setArguments(b); //Put your id
                dialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "aa");

            }
        });
        ListeduzenleActivity.database2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("urunler")){
                    if(snapshot.child("urunler").hasChild("alinmayanlar")){
                        if(snapshot.child("urunler").child("alinmayanalar").hasChild(holder.name1.getText().toString())){
                            holder.checkBox.setChecked(false);
                        }
                    }else {
                        holder.checkBox.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ListeduzenleActivity.database2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("urunler")){

                            if(isChecked){

                                final HashMap<String,String> hashMapUrunler = new HashMap<>();

                                hashMapUrunler.put("urunadi",holder.name1.getText().toString());
                                hashMapUrunler.put("urunfiyati",holder.name2.getText().toString());
                                hashMapUrunler.put("urunadeti",holder.price1.getText().toString());

                                ListeduzenleActivity.database2.child("urunler").child("alinmayanlar").child(holder.name1.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        ListeduzenleActivity.database2.child("urunler").child("alinanlar").child(holder.name1.getText().toString()).setValue(hashMapUrunler);
                                        hashMapUrunler.clear();
                                    }
                                });


                            }else {

                                final HashMap<String,String> hashMapUrunler = new HashMap<>();

                                hashMapUrunler.put("urunadi",holder.name1.getText().toString());
                                hashMapUrunler.put("urunfiyati",holder.name2.getText().toString());
                                hashMapUrunler.put("urunadeti",holder.price1.getText().toString());

                                ListeduzenleActivity.database2.child("urunler").child("alinanlar").child(holder.name1.getText().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        ListeduzenleActivity.database2.child("urunler").child("alinmayanlar").child(holder.name1.getText().toString()).setValue(hashMapUrunler);
                                        hashMapUrunler.clear();
                                    }
                                });


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return urunmodels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name1,name2;
        Button duzenlebtn, silbtn,paylasbtn;
        TextView price1,value1;
        LinearLayout parentLayout;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name1  = itemView.findViewById(R.id.name1);
            name2  = itemView.findViewById(R.id.name2);
            price1  = itemView.findViewById(R.id.price1);
            value1 = itemView.findViewById(R.id.value1);
            paylasbtn = itemView.findViewById(R.id.button3);
            duzenlebtn = itemView.findViewById(R.id.button4);
            silbtn = itemView.findViewById(R.id.button5);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            checkBox = itemView.findViewById(R.id.checkBox4);
        }
    }
}
