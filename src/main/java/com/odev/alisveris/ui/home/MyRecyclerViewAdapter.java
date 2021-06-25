package com.odev.alisveris.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.odev.alisveris.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private List<String> mLogo;
    private List<String> mCurrprices;
    private List<String> mPricechanges;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data, List<String> logo, List<String> currprices, List<String> pricechanges) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mLogo = logo;
        this.mCurrprices = currprices;
        this.mPricechanges = pricechanges;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layrecycle, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            // Do your binding here
            String coinname = mData.get(position);
            holder.myTextView.setText(coinname);
            String logourl = mLogo.get(position);
            Picasso.get().load(logourl).placeholder(R.drawable.ic_baseline_help_outline_24).into(holder.myImageView);
            String currprice = mCurrprices.get(position);
            holder.myTextView2.setText(currprice+"$");
            String pricechange = mPricechanges.get(position);

            //if(HomeFragment.izlenenmarket.equals("binance")){
            if(pricechange.contains("-")){
                holder.myTextView3.setTextColor(Color.argb(255,240,10,10));
            }else if(pricechange.equals("0")){
                holder.myTextView3.setTextColor(Color.argb(240,10,10,10));
            }else {
                holder.myTextView3.setTextColor(Color.argb(255,10,240,10));
            }/*
        }else if(HomeFragment.izlenenmarket.equals("worldcoinindex")){
            if(pricechange.contains("-")){
                holder.myTextView3.setTextColor(Color.argb(255,240,10,10));
            }else if(pricechange.contains("0.00")){
                holder.myTextView3.setTextColor(Color.argb(255,10,10,10));
            }else {
                holder.myTextView3.setTextColor(Color.argb(255,10,240,10));
                pricechange = "+"+pricechange.substring(0,pricechange.length()-1);
            }
        }else{
            if(pricechange.contains("-")){
                holder.myTextView3.setTextColor(Color.argb(255,240,10,10));
            }else if(pricechange.contains("0.00")){
                holder.myTextView3.setTextColor(Color.argb(255,10,10,10));
            }else {
                holder.myTextView3.setTextColor(Color.argb(255,10,240,10));
                pricechange = "+"+pricechange.substring(0,pricechange.length()-1);
            }
        }*/

            holder.myTextView3.setText(pricechange+"%");

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView2;
        TextView myTextView3;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            myTextView2 = itemView.findViewById(R.id.logotexttemp);
            myTextView3 = itemView.findViewById(R.id.pricechange);
            myImageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}