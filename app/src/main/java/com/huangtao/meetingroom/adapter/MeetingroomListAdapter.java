package com.huangtao.meetingroom.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huangtao.meetingroom.R;
import com.huangtao.meetingroom.model.Meeting;
import com.huangtao.meetingroom.model.MeetingRoom;
import com.huangtao.meetingroom.model.meta.MeetingRoomUtils;
import com.huangtao.meetingroom.model.meta.Status;
import com.huangtao.meetingroom.network.Network;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingroomListAdapter extends RecyclerView.Adapter<MeetingroomListAdapter.MeetingroomVH> {

    List<MeetingRoom> data;

    public MeetingroomListAdapter(List<MeetingRoom> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MeetingroomVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_meetingroom, viewGroup, false);
        return new MeetingroomVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MeetingroomVH viewHolder, final int i) {
        MeetingRoom meetingRoom = data.get(i);

        viewHolder.location.setText(meetingRoom.getLocation());
        viewHolder.status.setText(meetingRoom.getDeviceId() == null ? "未绑定" : "已绑定");

        for(MeetingRoomUtils utils : meetingRoom.getUtils()){
            switch (utils){
                case AIRCONDITIONER:
                    viewHolder.airConditioner.setVisibility(View.VISIBLE);
                    break;
                case TABLE:
                    viewHolder.desk.setVisibility(View.VISIBLE);
                    break;
                case PROJECTOR:
                    viewHolder.projector.setVisibility(View.VISIBLE);
                    break;
                case BLACKBOARD:
                    viewHolder.blackboard.setVisibility(View.VISIBLE);
                    break;
                case POWERSUPPLY:
                    viewHolder.power.setVisibility(View.VISIBLE);
                    break;
            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(i);
            }
        });

        // 会议室状态
        Calendar calendar = Calendar.getInstance();
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class MeetingroomVH extends RecyclerView.ViewHolder{

        @BindView(R.id.pic)
        ImageView pic;

        @BindView(R.id.location)
        TextView location;

        @BindView(R.id.status)
        TextView status;

        @BindView(R.id.air_conditioner)
        ImageView airConditioner;

        @BindView(R.id.blackboard)
        ImageView blackboard;

        @BindView(R.id.desk)
        ImageView desk;

        @BindView(R.id.projector)
        ImageView projector;

        @BindView(R.id.power)
        ImageView power;

        public MeetingroomVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
