package com.odev.alisveris.ui.home;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.odev.alisveris.Alarm;
import com.odev.alisveris.MainActivity;
import com.odev.alisveris.R;
import com.odev.alisveris.UrunModel;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private int mYear, mMonth, mDay, mHour, mMinute;
    public static ArrayList<UrunModel> arrayList = new ArrayList<>();
    TextView txtDate, txtTime;
    static EditText alisverisadi ;
    static TextView alisveristarihi;
    static TextView alisverissaati;
    static EditText alisverisyeri;
    static EditText alisverisnotu;
    public static RecyclerView rc;

    public static long alarmmilisec = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        alisveristarihi = root.findViewById(R.id.alisveristarih);
        alisverissaati = root.findViewById(R.id.alisverissaat);

        alisverisadi = root.findViewById(R.id.alisverisadi);
        alisverisyeri = root.findViewById(R.id.alisverisyeri);
        alisverisnotu = root.findViewById(R.id.alisverisnotu);

        alisveristarihi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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
        alisverisyeri.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 39.94289110189845, 32.81573654948698);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } else {

                }
            }
        });
        Button uruneklebtn = root.findViewById(R.id.button2);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(getParentFragmentManager(), "asda");
            }
        });

        rc = root.findViewById(R.id.recylcerurnler);
        final ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList,getActivity());
        rc.setAdapter(listViewAdapter);
        rc.setLayoutManager(new LinearLayoutManager(getActivity()));
        listViewAdapter.notifyDataSetChanged();

        FirebaseApp.initializeApp(getContext());

        final HashMap<String,String> hashMap = new HashMap<>();

        Button kaydetbtn = root.findViewById(R.id.kaydetbtn);
        kaydetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(alisverisadi.getText().toString().length()<1||
                        alisveristarihi.getText().toString().length()<1||
                        alisverisyeri.getText().toString().length()<1||
                        arrayList.size()<1){
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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
                    hashMap.put("alisverisnotu",alisverisnotu.getText().toString());
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

                                alisverisadi.setText("");
                                alisveristarihi.setText("");
                                alisverisyeri.setText("");
                                alisverissaati.setText("");
                                alisverisnotu.setText("");
                                arrayList.clear();

                                ListViewAdapter listViewAdapter = new ListViewAdapter(arrayList,getContext());
                                rc.setAdapter(listViewAdapter);

                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Kaydedildi");
                                alertDialog.setMessage("Liste veritabanına kaydedildi");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }
                        }
                    });
                }
            }
        });

        return root;
    }

    public void clear(){

    }
    public static void setSharedPreference(Activity activity, String key, String value){
        SharedPreferences myPrefs = activity.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor;
        prefsEditor = myPrefs.edit();
//strVersionName->Any value to be stored
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }
    public String getSharedPreference(Activity activity, String key){
        SharedPreferences myPrefs;
        myPrefs = activity.getSharedPreferences("myPrefs", MODE_PRIVATE);
        String StoredValue=myPrefs.getString(key, "");
        return StoredValue;
    }
}