package com.odev.alisveris;

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
import com.odev.alisveris.ui.home.ListViewAdapter;

public class MyDialogFragment2 extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater l = getActivity().getLayoutInflater();
        View view = l.inflate(R.layout.dialogfragment, null);

        EditText editTexturunadi = view.findViewById(R.id.urunadiedittext);
        EditText editTexturunmiktari = view.findViewById(R.id.urunmiktariedittext);
        EditText editTexturunfiyati = view.findViewById(R.id.urunfiyatiedittext);

        Button uruneklebtn = view.findViewById(R.id.uruneklebtn);
        uruneklebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListeduzenleActivity.arrayList.add(new UrunModel(editTexturunadi.getText().toString(),
                        editTexturunfiyati.getText().toString(),
                        editTexturunmiktari.getText().toString()
                        ));
                final ListViewAdapter listViewAdapter = new ListViewAdapter(ListeduzenleActivity.arrayList,getActivity());
                ListeduzenleActivity.rc.setAdapter(listViewAdapter);
                dismiss();
            }
        });
        b.setView(view);
        return b.create();
    }

    public MyDialogFragment2() {
        super();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}