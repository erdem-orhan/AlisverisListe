package com.odev.alisveris.ui.listeler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.odev.alisveris.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerViewAdapter2 extends RecyclerView.Adapter<MyRecyclerViewAdapter2.ViewHolder> {

    private List<String> mData;
    private List<String> mLogo;
    private List<String> mPricechanges;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter2(Context context, List<String> data, List<String> logo, List<String> pricechanges) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mLogo = logo;
        this.mPricechanges = pricechanges;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.layrecyclepumppage, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String coinname = mData.get(position);
        holder.myTextView.setText(coinname);
        String logourl = mLogo.get(position);
        Picasso.get().load(logourl).into(holder.myImageView);

        String pricechange = mPricechanges.get(position);

        //holder.myTextView2.setTextColor(Color.argb(255,10,240,10));
        holder.myTextView2.setText("Son 10 dakikada %"+pricechange+" yükseldi!");
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
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            myTextView2 = itemView.findViewById(R.id.logotexttemp);
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