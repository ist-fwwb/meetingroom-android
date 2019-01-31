package com.huangtao.meetingroom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.User;
import com.huangtao.meetingroom.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendantsAdapter extends BaseAdapter {
    List<Map.Entry<String, String>> attendants;
    public AttendantsAdapter(List<Map.Entry<String, String>> attendants){
        this.attendants = attendants;
    }
    @Override
    public int getCount() {
        return attendants.size();
    }

    @Override
    public Object getItem(int position) {
        return attendants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        Map.Entry<String, String> data = attendants.get(position);
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_busy, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else viewHolder = (ViewHolder) convertView.getTag();
        Network.getInstance().queryUserById(data.getKey()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                viewHolder.attendant.setText(user.getName());
                viewHolder.time.setText(data.getValue().isEmpty() ? "--:--" : data.getValue());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        return convertView;
    }

    public List<Map.Entry<String, String>> getAttendants() {
        return attendants;
    }

    public void setAttendants(List<Map.Entry<String, String>> attendants) {
        this.attendants = attendants;
    }

    private class ViewHolder{
        TextView attendant;
        TextView time;

        ViewHolder(View view){
            attendant = view.findViewById(R.id.attendant);
            time = view.findViewById(R.id.time);
        }
    }
}
