package com.example.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ListItemHolder> {

    private List<HashMap<String, String>> list;
    RecyclerView recyclerView;
    String type;
    private OnItemClickListener mListener;

    public ListItemsAdapter(List<HashMap<String, String>> list, String type) {
        this.list = list;
        this.type = type;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ListItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView status, rating, address, name, placeType;

        public ListItemHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
//            placeType=itemView.findViewById(R.id.place_type);
            status = itemView.findViewById(R.id.status);
            rating = itemView.findViewById(R.id.rating);
            address = itemView.findViewById(R.id.place_address);
            name = itemView.findViewById(R.id.name_of_place);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_items, parent, false);

        return new ListItemHolder(view, mListener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        HashMap<String, String> hashMap = list.get(position);
        holder.address.setText(hashMap.get("vicinity"));
        if (hashMap.containsKey("rating")) {
            holder.rating.setText(hashMap.get("rating"));
        } else {
            holder.rating.setVisibility(View.GONE);
        }
        if (type == "res")
            holder.imageView.setImageResource(R.drawable.ic_restaurant);
        else if (type == "malls")
            holder.imageView.setImageResource(R.drawable.ic_malls);
        else if (type == "hos")
            holder.imageView.setImageResource(R.drawable.ic_hospital);
        else if (type == "hotel")
            holder.imageView.setImageResource(R.drawable.ic_hotel);
        else if (type == "atm")
            holder.imageView.setImageResource(R.drawable.ic_atm);
        holder.name.setText(hashMap.get("name"));
        if (hashMap.containsKey("status")) {
            if (hashMap.get("status").equals("true")) {
                holder.status.setText("Open Now");
//                holder.status.setTextColor(R.color.colorGreen);
            } else {
                holder.status.setText("Closed");
                holder.status.setTextColor(R.color.colorRed);
            }
        } else {
            holder.status.setVisibility(View.GONE);
        }
//        holder.placeType.setText(hashMap.get("type"));
//        String url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+hashMap.get("photoRef")
//                +"&key=AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";
//        ImageLoader imageLoader=new ImageLoader(imageView);
//        imageLoader.execute(url);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
