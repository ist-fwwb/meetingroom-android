package com.huangtao.meetingroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.common.MyRecyclerViewAdapter;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.network.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerMeetingAdapter extends MyRecyclerViewAdapter<Meeting, RecyclerMeetingAdapter.MyViewHolder> {
    public RecyclerMeetingAdapter(Context context, List<Meeting> meetings) {
        super(context);
        setData(meetings);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_free, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Meeting meeting = getItem(i);
        holder.title.setText(meeting.getHeading().isEmpty() ? "无主题" : meeting.getHeading());
        switch(meeting.getType()){
            case COMMON:
                holder.type.setText("普通");
                holder.type.setTextColor(ContextCompat.getColor(getContext(), R.color.douban_gray_55_percent));
                break;
            case URGENCY:
                holder.type.setText("紧急");
                holder.type.setTextColor(ContextCompat.getColor(getContext(), R.color.douban_red));
                break;
        }

        holder.time.setText(CommonUtils.getFormatTime(meeting.getStartTime(), meeting.getEndTime()));
        holder.host.setText(meeting.getHost().getName());
    }

    class MyViewHolder extends MyRecyclerViewAdapter.ViewHolder{
        View view;
        TextView title;
        TextView host;
        TextView type;
        TextView time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            title = view.findViewById(R.id.title);
            host = view.findViewById(R.id.host);
            type = view.findViewById(R.id.type);
            time = view.findViewById(R.id.time);
        }
    }
}
