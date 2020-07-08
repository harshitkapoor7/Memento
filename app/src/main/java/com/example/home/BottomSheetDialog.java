package com.example.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener listener;
    String title;
    TextView locName;
    Button reminder,close;
    BottomSheetDialog(String title){
        this.title=title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet,container,false);
        locName=view.findViewById(R.id.locName);
        locName.setText(title);

        reminder=view.findViewById(R.id.reminderButton);
        close=view.findViewById(R.id.closeButton);

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBtnClick(title);
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    public interface BottomSheetListener{
        void onBtnClick(String str);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener= (BottomSheetListener) context;
    }
}
