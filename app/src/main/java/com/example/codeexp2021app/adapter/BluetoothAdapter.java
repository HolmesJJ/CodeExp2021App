package com.example.codeexp2021app.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codeexp2021app.R;
import com.example.codeexp2021app.listener.OnItemClickListener;
import com.example.codeexp2021app.utils.ContextUtils;

import java.util.List;

/**
 * @author Administrator
 * @des ${TODO}
 * @verson $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class BluetoothAdapter extends RecyclerView.Adapter<BluetoothAdapter.BluetoothViewHolder> {

    private static final String TAG = "BluetoothAdapter";
    private Context context;
    private List<BluetoothDevice> bluetoothDevices;

    private LayoutInflater mInflater = null;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public BluetoothAdapter(Context context, List<BluetoothDevice> bluetoothDevices) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
    }

    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_bluetooth, parent, false);
        final BluetoothViewHolder holder = new BluetoothViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mOnItemClickListener.onItemClick(position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {
        holder.mIvBluetooth.setImageResource(R.drawable.ic_bluetooth);
        holder.mTvName.setText(bluetoothDevices.get(position).getName());
        holder.mTvMacAddress.setText(bluetoothDevices.get(position).getAddress());
        int state = bluetoothDevices.get(position).getBondState();
        if (state == BluetoothDevice.BOND_NONE) {
            holder.mTvPair.setText(R.string.unpaired);
        } else if (state == BluetoothDevice.BOND_BONDING) {
            holder.mTvPair.setText(R.string.pairing);
        } else {
            holder.mTvPair.setText(R.string.paired);
            holder.mTvPair.setTextColor(ContextCompat.getColor(ContextUtils.getContext(), R.color.light_green));
        }
    }

    @Override
    public int getItemCount() {
        return bluetoothDevices.size();
    }

    public class BluetoothViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvBluetooth;
        private TextView mTvName;
        private TextView mTvMacAddress;
        private TextView mTvPair;
        private TextView mTvRssi;
        private TextView mTvCollect;

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            mIvBluetooth = itemView.findViewById(R.id.iv_bluetooth);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvMacAddress = itemView.findViewById(R.id.tv_mac_address);
            mTvPair = itemView.findViewById(R.id.tv_pair);
            mTvRssi = itemView.findViewById(R.id.tv_rssi);
            mTvCollect = itemView.findViewById(R.id.tv_collect);
        }
    }
}
