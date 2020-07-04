package com.example.home;

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

    private List<HashMap<String,String>> list;
    RecyclerView recyclerView;
    ImageView imageView;

    public ListItemsAdapter(List<HashMap<String,String>> list)
    {
        this.list=list;
    }

    @NonNull
    @Override
    public ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =  inflater.inflate(R.layout.list_items,parent, false);


        return new ListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemHolder holder, int position) {
        HashMap<String,String> hashMap =list.get(position);
        holder.address.setText(hashMap.get("vicinity"));
        holder.rating.setText(hashMap.get("rating"));
        holder.placeType.setText(hashMap.get("type"));
        holder.name.setText(hashMap.get("name"));
        if(hashMap.get("status")=="true")
            holder.status.setText(hashMap.get("Open Now"));
        else
            holder.status.setText(hashMap.get("Closed"));
        String url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+hashMap.get("photoRef")
                +"&key=AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";
        ImageLoader imageLoader=new ImageLoader(imageView);
        imageLoader.execute(url);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListItemHolder extends RecyclerView.ViewHolder{


        TextView placeType,status,rating,address,name;
        public ListItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.img);
            placeType=itemView.findViewById(R.id.place_type);
            status=itemView.findViewById(R.id.status);
            rating=itemView.findViewById(R.id.rating);
            address=itemView.findViewById(R.id.place_address);
            name=itemView.findViewById(R.id.name_of_place);

        }
    }
}
