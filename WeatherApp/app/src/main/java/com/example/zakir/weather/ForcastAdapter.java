package com.example.zakir.weather;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zakir on 03-Jan-18.
 */

public class ForcastAdapter extends RecyclerView.Adapter<ForcastAdapter.ForcastViewHolder>{
    private Context context;
    private ArrayList<ForcastDetails> forcastDetails;

    public ForcastAdapter(Context context, ArrayList<ForcastDetails> forcastDetails) {
        this.context = context;
        this.forcastDetails = forcastDetails;
    }

    @Override
    public ForcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.weather_single_row,parent,false);
        return new ForcastAdapter.ForcastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ForcastViewHolder holder, int position) {
        String image = forcastDetails.get(position).getImage();
        Uri iconUri = Uri.parse("http://openweathermap.org/img/w/" + image + ".png");
        Picasso.with(context).load(iconUri).into(holder.image);

        holder.status.setText(forcastDetails.get(position).getStatus());
        holder.day.setText(forcastDetails.get(position).getDay());
        holder.temp.setText(forcastDetails.get(position).getTemp()+MainActivity.tempSign);
        holder.minTemp.setText(forcastDetails.get(position).getMinTemp()+MainActivity.tempSign);
        holder.maxTemp.setText(forcastDetails.get(position).getMaxTemp()+MainActivity.tempSign);
        holder.sunRise.setText(forcastDetails.get(position).getSunRise()+"%");
        holder.sunSet.setText(forcastDetails.get(position).getSunSet()+" mb");


    }

    @Override
    public int getItemCount() {
        return forcastDetails.size();
    }

    public class ForcastViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView status;
        TextView day;
        TextView temp;
        TextView maxTemp;
        TextView minTemp;
        TextView sunRise;
        TextView sunSet;
    public ForcastViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageView);
        status = itemView.findViewById(R.id.status);
        day = itemView.findViewById(R.id.day);
        temp = itemView.findViewById(R.id.temp);
        maxTemp = itemView.findViewById(R.id.maxTemp);
        minTemp = itemView.findViewById(R.id.minTemp);
        sunRise = itemView.findViewById(R.id.sunRise);
        sunSet = itemView.findViewById(R.id.sunSet);
        image = itemView.findViewById(R.id.imageView);
    }
}
}
