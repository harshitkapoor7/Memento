package com.example.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.List;

public class BottomSheetNearby extends BottomSheetDialogFragment {
    private BottomSheetDialog.BottomSheetListener listener;
    RecyclerView recyclerView;
    List<HashMap<String, String>> list;
    String type;

    BottomSheetNearby(List<HashMap<String, String>> list, String type) {
        this.list = list;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_nearby, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ListItemsAdapter listItemsAdapter=new ListItemsAdapter(list,type);
        recyclerView.setAdapter(listItemsAdapter);
        listItemsAdapter.setOnItemClickListener(new ListItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String address=list.get(position).get("name")+" "+list.get(position).get("vicinity");
                dismiss();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(address);
                bottomSheetDialog.show(getFragmentManager(), "bottom");
            }
        });
        return view;
    }

    public interface BottomSheetNearbyListener{
        void onCardClick();
    }

}
