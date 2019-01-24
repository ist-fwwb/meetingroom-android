package com.huangtao.meetingroom.adapter;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.helper.CommonUtils;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.network.Network;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingAdapter extends BaseAdapter {

    private List<Meeting> datas = new ArrayList<>();

    public void setDatas(List<Meeting> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        Meeting meeting = datas.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_free, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(meeting.getHeading().isEmpty() ? "无主题" : meeting.getHeading());
        switch(meeting.getType()){
            case COMMON:
                holder.type.setText("普通");
                holder.type.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.douban_gray_55_percent));
                break;
            case URGENCY:
                holder.type.setText("紧急");
                holder.type.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.douban_red));
                break;
        }

        holder.time.setText(CommonUtils.getFormatTime(meeting.getStartTime(), meeting.getEndTime()));

        Network.getInstance().queryUserById(meeting.getHostId()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.body() != null)
                    holder.host.setText(response.body().getName());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView host;
        TextView type;
        TextView time;

        ViewHolder(View view) {
            title = view.findViewById(R.id.title);
            host = view.findViewById(R.id.host);
            type = view.findViewById(R.id.type);
            time = view.findViewById(R.id.time);
        }
    }
}
