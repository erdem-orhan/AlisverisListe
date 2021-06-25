package com.odev.alisveris.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


import com.odev.alisveris.ListeduzenleActivity;
import com.odev.alisveris.R;
import com.odev.alisveris.UrunModel;

public class MyDialogFragment extends AppCompatDialogFragment {

    String urunadi = "";
    String urunfiyati = "";
    String urunadeti = "";
    String callclass = "";
    int index = 0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater l = getActivity().getLayoutInflater();
        View view = l.inflate(R.layout.dialogfragment, null);

        EditText editTexturunadi = view.findViewById(R.id.urunadiedittext);
        EditText editTexturunmiktari = view.findViewById(R.id.urunmiktariedittext);
        EditText editTexturunfiyati = view.findViewById(R.id.urunfiyatiedittext);

        if(getArguments() != null){

            urunadi = getArguments().getString("urunadi");
            urunfiyati = getArguments().getString("urunfiyati");
            urunadeti = getArguments().getString("urunadeti");

            callclass = getArguments().getString("callclass");

            index = getArguments().getInt("index");
            editTexturunadi.setText(urunadi);
            editTexturunfiyati.setText(urunfiyati);
            editTexturunmiktari.setText(urunadeti);
        }

        Button uruneklebtn = view.findViewById(R.id.uruneklebtn);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urunadi.length()==0){
                    HomeFragment.arrayList.add(new UrunModel(editTexturunadi.getText().toString(),
                            editTexturunfiyati.getText().toString(),
                            editTexturunmiktari.getText().toString()
                    ));
                    final ListViewAdapter listViewAdapter = new ListViewAdapter(HomeFragment.arrayList,getActivity());
                    HomeFragment.rc.setAdapter(listViewAdapter);
                    dismiss();
                }else if(callclass.equals("list")){
                    HomeFragment.arrayList.get(index).setUrunadeti(editTexturunmiktari.getText().toString());
                    HomeFragment.arrayList.get(index).setUrunadi(editTexturunadi.getText().toString());
                    HomeFragment.arrayList.get(index).setUrunfiyati(editTexturunfiyati.getText().toString());

                    final ListViewAdapter listViewAdapter = new ListViewAdapter(HomeFragment.arrayList,getActivity());
                    HomeFragment.rc.setAdapter(listViewAdapter);
                    dismiss();
                }else {

                    ListeduzenleActivity.arrayList.get(index).setUrunadeti(editTexturunmiktari.getText().toString());
                    ListeduzenleActivity.arrayList.get(index).setUrunadi(editTexturunadi.getText().toString());
                    ListeduzenleActivity.arrayList.get(index).setUrunfiyati(editTexturunfiyati.getText().toString());

                    final ListViewAdapter listViewAdapter = new ListViewAdapter(ListeduzenleActivity.arrayList,getActivity());
                    ListeduzenleActivity.rc.setAdapter(listViewAdapter);
                    dismiss();
                }
            }
        });
        b.setView(view);
        return b.create();
    }

    public MyDialogFragment() {
        super();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}