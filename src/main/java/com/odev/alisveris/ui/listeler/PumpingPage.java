package com.odev.alisveris.ui.listeler;

import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.odev.alisveris.AlisverislisteModel;
import com.odev.alisveris.ListeduzenleActivity;
import com.odev.alisveris.MainActivity;
import com.odev.alisveris.R;

import java.util.ArrayList;
import java.util.Map;

public class PumpingPage extends Fragment {

    private DashboardViewModel dashboardViewModel;
    public static RecyclerView recyclerView;
    public static MyRecyclerViewAdapter2 adapter;
    static TextView textView;
    public static FragmentManager fragmentManager;
    DatabaseReference databasepay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        textView = root.findViewById(R.id.text_dashboard);
        databasepay = FirebaseDatabase.getInstance().getReference().child("listeler");

        fragmentManager = getParentFragmentManager();

        Query query = databasepay;

        FirebaseRecyclerOptions<AlisverislisteModel> options =
                new FirebaseRecyclerOptions.Builder<AlisverislisteModel>()
                        .setQuery(query, AlisverislisteModel.class)
                        .build();

        FirebaseRecyclerAdapter<AlisverislisteModel, UserViewHolder2> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AlisverislisteModel, UserViewHolder2>(
                options
        ) {
            @NonNull
            @Override
            public UserViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.nodelaytransaction, parent, false);
                return new UserViewHolder2(view);
            }

            @Override
            protected void onBindViewHolder(final UserViewHolder2 userViewHolder2, int i, @NonNull final AlisverislisteModel transaction) {
                final String key = getRef(i).getKey();
                databasepay.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("alisveristarihi")) {

                            final String amount = dataSnapshot.child("alisverisadi").getValue().toString();
                            final String status = dataSnapshot.child("alisveristarihi").getValue().toString();
                            final String saat = dataSnapshot.child("alisverissaati").getValue().toString();
                            final String datetext = dataSnapshot.child("alisverisyeri").getValue().toString();

                            userViewHolder2.setAmount(amount);
                            userViewHolder2.setStatus(status);
                            userViewHolder2.setDate( datetext);
                            userViewHolder2.setSaat(saat);

                            if(dataSnapshot.hasChild("urunler")){
                                int alinmayanlar = (int)dataSnapshot.child("urunler").child("alinmayanlar").getChildrenCount();
                                int alinanlar = (int)dataSnapshot.child("urunler").child("alinanlar").getChildrenCount();

                                int toplam = alinanlar+alinmayanlar;

                                userViewHolder2.setUrunadet(alinanlar+"/"+toplam+" adet ürün alınmış");
                            }
                            textView.setVisibility(View.GONE);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();

        RecyclerView rc2;

        rc2 = root.findViewById(R.id.recview);
        rc2.setHasFixedSize(true);
        rc2.setLayoutManager(new LinearLayoutManager(getContext()) {

        });

        rc2.setAdapter(firebaseRecyclerAdapter);


        return root;
    }

    static String urunlist = "";
    public class UserViewHolder2 extends RecyclerView.ViewHolder {
        View mView;
        TextView satatustext;
        TextView amounttext;
        TextView datetext;
        TextView saattext;
        TextView urunedettext;
        Button silbtn, duzenlebtn,paylasbtn;

        public UserViewHolder2(View itemView){
            super(itemView);
            mView = itemView;
            satatustext = mView.findViewById(R.id.statustext);
            amounttext = mView.findViewById(R.id.Amounttext);
            saattext = mView.findViewById(R.id.statustext2);
            datetext = mView.findViewById(R.id.exptime);
            urunedettext = mView.findViewById(R.id.urunadettext);
            paylasbtn = mView.findViewById(R.id.button3);

            silbtn =mView.findViewById(R.id.silbtn);
            duzenlebtn = mView.findViewById(R.id.duzenlebtn);

            duzenlebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), ListeduzenleActivity.class);
                    Bundle b = new Bundle();
                    b.putString("listeadi", amounttext.getText().toString()); //Your id
                    i.putExtras(b); //Put your id to your next Intent
                    startActivity(i);
                }
            });
            paylasbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    urunlist = "";
                    databasepay.child(amounttext.getText().toString()).child("urunler").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot ds : snapshot.child("alinanlar").getChildren()) {
                                urunlist = urunlist +"\n"+ ds.getKey();
                            }
                            for(DataSnapshot ds : snapshot.child("alinmayanlar").getChildren()) {
                                urunlist = urunlist +"\n"+ ds.getKey();
                            }

                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"aaabbbccc@gmail.com"});
                            i.putExtra(Intent.EXTRA_SUBJECT, "Paylas");
                            i.putExtra(Intent.EXTRA_TEXT   , "liste adi: "+amounttext.getText().toString()+
                                    "\nliste tarihi: "+datetext.getText().toString()+
                                    "\n Alışveriş yeri: "+satatustext.getText().toString()+
                                    "\n"+urunedettext.getText().toString()+
                                    "\n Ürünler: \n"+
                                    "\n"+urunlist);
                            //i.setType("application/octet-stream");
                            //i.setData(Uri.parse("mailto:"));
                            //i.setType("message/rfc822");
                            try {
                                MainActivity.activity.startActivity(Intent.createChooser(i, "Mail gönder..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getContext(), "Hiç mail gönderme aracı yüklü değil.", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

            silbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databasepay.child(amounttext.getText().toString()).removeValue();
                }
            });


        }
        public void setName(DataSnapshot name){
        }
        public void setStatus(String status){
            TextView UserNameView2 = mView.findViewById(R.id.statustext);
            UserNameView2.setText(status);
        }
        public void setDate(String time){
            TextView UserNameView2 = mView.findViewById(R.id.exptime);
            UserNameView2.setText(time);
        }
        public void setSaat(String time){
            TextView UserNameView2 = mView.findViewById(R.id.statustext2);
            UserNameView2.setText(time);
        }
        public void setAmount(String amount){
            TextView UserNameView2 = mView.findViewById(R.id.Amounttext);
            UserNameView2.setText(amount);

        }
        public void setUrunadet(String amount){
            TextView UserNameView2 = mView.findViewById(R.id.urunadettext);
            UserNameView2.setText(amount);
        }

    }

}