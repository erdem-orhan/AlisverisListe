package com.odev.alisveris;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.odev.alisveris.ui.home.HomeFragment;
import com.odev.alisveris.ListViewAdapterDuzenle;
import com.odev.alisveris.ui.home.MyDialogFragment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ListeduzenleActivity extends AppCompatActivity {

    private int mYear, mMonth, mDay, mHour, mMinute;
    public static ArrayList<UrunModel> arrayList = new ArrayList<>();
    TextView txtDate, txtTime;
    static EditText alisverisadi ;
    static TextView alisveristarihi;
    static TextView alisverissaati;
    static EditText alisverisyeri;
    public static RecyclerView rc;
    static DatabaseReference database2;
    public static long alarmmilisec = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listeduzenle);

        FirebaseApp.initializeApp(getApplicationContext());

        alisveristarihi = findViewById(R.id.alisveristarih);
        alisverisadi = findViewById(R.id.alisverisadi);
        alisverisyeri = findViewById(R.id.alisverisyeri);
        alisverissaati = findViewById(R.id.alisverissaat);

        arrayList.clear();

        Bundle b = getIntent().getExtras();
        alisverisadi.setText(b.getString("listeadi"));

        database2 = FirebaseDatabase.getInstance().getReference().child("listeler").child(alisverisadi.getText().toString());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alisveristarihi.setText(snapshot.child("alisveristarihi").getValue(String.class));
                alisverisyeri.setText(snapshot.child("alisverisyeri").getValue(String.class));
                alisverissaati.setText(snapshot.child("alisverissaati").getValue(String.class));

                if(snapshot.hasChild("urunler")){
                    if(snapshot.child("urunler").hasChild("alinmayanlar")){

                        for( DataSnapshot postSnapshot : snapshot.child("urunler").child("alinmayanlar").getChildren()){

                            arrayList.add(new UrunModel(postSnapshot.child("urunadi").getValue(String.class),
                                    postSnapshot.child("urunadeti").getValue(String.class),
                                    postSnapshot.child("urunfiyati").getValue(String.class)
                            ));
                        }
                    }
                    if(snapshot.child("urunler").hasChild("alinanlar")){
                        for( DataSnapshot postSnapshot : snapshot.child("urunler").child("alinanlar").getChildren()){

                            arrayList.add(new UrunModel(postSnapshot.child("urunadi").getValue(String.class),
                                    postSnapshot.child("urunadeti").getValue(String.class),
                                    postSnapshot.child("urunfiyati").getValue(String.class)
                            ));
                        }
                    }
                }
                final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList,ListeduzenleActivity.this);
                rc.setAdapter(listViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        database2.addListenerForSingleValueEvent(valueEventListener);

        alisveristarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                alisveristarihi.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        alisverissaati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getApplicationContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                alisverissaati.setText(hourOfDay + ":" + minute);

                                String date = alisveristarihi.getText().toString()+" "+hourOfDay + ":" + minute;
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy H:m", Locale.ENGLISH);
                                LocalDateTime localDate = LocalDateTime.parse(date, formatter);
                                alarmmilisec = localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

                                Log.e("alartime", "alarmmilis: "+alarmmilisec +" currentmilis: "+ System.currentTimeMillis());

                                AlarmManager alarmMgr = (AlarmManager)MainActivity.activity.getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(MainActivity.activity, Alarm.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.activity.getApplicationContext(), 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmmilisec,10*60*1000*999999, pendingIntent);

                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        Button uruneklebtn = findViewById(R.id.button2);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new MyDialogFragment2();
                dialogFragment.show(getSupportFragmentManager(), "aa");
            }
        });

        rc = findViewById(R.id.recylcerurnler);
        final ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList,this);
        rc.setAdapter(listViewAdapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
        listViewAdapter.notifyDataSetChanged();

        final HashMap<String,String> hashMap = new HashMap<>();

        Log.e("firebasever", "dgir");

        Button kaydetbtn = findViewById(R.id.kaydetbtn);
        kaydetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alisverisadi.getText().toString().length()<1||
                        alisveristarihi.getText().toString().length()<1||
                        alisverisyeri.getText().toString().length()<1||
                        arrayList.size()<1){
                    AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                    alertDialog.setTitle("Hata");
                    alertDialog.setMessage("Tüm bilgileri girin ve en az 1 ürün ekleyin");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else {
                    DatabaseReference database3 = FirebaseDatabase.getInstance().getReference().child("listeler").child(alisverisadi.getText().toString());

                    hashMap.put("alisverisadi",alisverisadi.getText().toString());
                    hashMap.put("alisveristarihi",alisveristarihi.getText().toString());
                    hashMap.put("alisverissaati",alisverissaati.getText().toString());
                    hashMap.put("alisverisyeri",alisverisyeri.getText().toString());
                    database3.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                final HashMap<String,String> hashMapUrunler = new HashMap<>();

                                for (UrunModel myurunmodel: arrayList) {

                                    hashMapUrunler.put("urunadi",myurunmodel.getUrunadi());
                                    hashMapUrunler.put("urunfiyati",myurunmodel.getUrunfiyati());
                                    hashMapUrunler.put("urunadeti",myurunmodel.getUrunadeti());

                                    database3.child("urunler").child("alinmayanlar").child(myurunmodel.getUrunadi()).setValue(hashMapUrunler);
                                    hashMapUrunler.clear();
                                }

                                database2.removeEventListener(valueEventListener);


                                alisverisadi.setText("");
                                alisveristarihi.setText("");
                                alisverissaati.setText("");
                                alisverisyeri.setText("");
                                arrayList.clear();

                                ListViewAdapterDuzenle listViewAdapter = new ListViewAdapterDuzenle(arrayList,getApplicationContext());
                                rc.setAdapter(listViewAdapter);
/*
                            AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                            alertDialog.setTitle("Kaydedildi");
                            alertDialog.setMessage("Liste veritabanına kaydedildi");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();*/

                            }
                        }
                    });
                }

            }
        });


    }
}