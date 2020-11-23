package com.example.examcam;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    private final LinkedList<BluetoothDevice> deviceList;
    private LayoutInflater mInflater;
    public SharedViewModel sharedViewModel;

    public DeviceListAdapter(Context context, LinkedList<BluetoothDevice> deviceList, SharedViewModel sharedViewModel) {
        mInflater = LayoutInflater.from(context);
        this.deviceList = deviceList;
        this.sharedViewModel = sharedViewModel;
    }

    @NonNull
    @Override
    public DeviceListAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.devicelist_item, parent, false);
        return new DeviceViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListAdapter.DeviceViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = deviceList.get(position);
        holder.deviceName.setText(bluetoothDevice.getName());
        if(bluetoothDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.COMPUTER_DESKTOP || bluetoothDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.COMPUTER_LAPTOP){
            Drawable img = holder.imgDevice.getResources().getDrawable(R.drawable.ic_baseline_laptop_24);
            holder.imgDevice.setImageDrawable(img);
        }
        else{
            Drawable img = holder.imgDevice.getResources().getDrawable(R.drawable.ic_baseline_phone_android_24);
            holder.imgDevice.setImageDrawable(img);
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView deviceName;
        public final ImageView imgDevice;
        final DeviceListAdapter mAdapter;

        public DeviceViewHolder(View itemView, DeviceListAdapter adapter) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.txt_device_name);
            imgDevice = itemView.findViewById(R.id.img_device);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int mPosition = getLayoutPosition();
            BluetoothDevice bluetoothDevice = deviceList.get(mPosition);
            sharedViewModel.setBluetoothDevice(bluetoothDevice);
            Navigation.findNavController(v).navigate(R.id.action_connectFragment_to_scanFragment);
        }
    }
}
