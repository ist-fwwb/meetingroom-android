package com.huangtao.meetingroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.common.MyRecyclerViewAdapter;
import com.huangtao.meetingroom.model.Meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerAttendantsAdapter extends MyRecyclerViewAdapter<Map.Entry<String, String>, RecyclerAttendantsAdapter.ViewHolder> {
    public RecyclerAttendantsAdapter(Context context, List<Map.Entry<String, String>> attendants) {
        super(context);
        setData(attendants);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_busy, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Map.Entry<String, String> attendant = getItem(i);
        viewHolder.attendant.setText(attendant.getKey());
        viewHolder.time.setText(attendant.getValue());
    }

    class ViewHolder extends MyRecyclerViewAdapter.ViewHolder{
        TextView attendant;
        TextView time;
        public ViewHolder(View view) {
            super(view);
            attendant = view.findViewById(R.id.attendant);
            time = view.findViewById(R.id.time);
        }
    }
}
