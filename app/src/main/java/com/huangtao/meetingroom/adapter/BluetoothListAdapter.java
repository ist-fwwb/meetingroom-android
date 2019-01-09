package com.huangtao.meetingroom.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huangtao.meetingroom.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothVH> {

    List<BluetoothDevice> data;

    public BluetoothListAdapter(List<BluetoothDevice> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BluetoothVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bluetooth, viewGroup, false);
        return new BluetoothVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothVH viewHolder, final int i) {
        viewHolder.name.setText(data.get(i).getName() != null ? data.get(i).getName() : "");
        viewHolder.mac.setText(data.get(i).getAddress());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<BluetoothDevice> data) {
        this.data = data;
    }

    public static class BluetoothVH extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.mac)
        TextView mac;

        public BluetoothVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
