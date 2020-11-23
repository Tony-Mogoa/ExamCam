package com.example.examcam;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread {
    private static final String SERVICE_UUID_STRING = "8ae8da39-8753-4ced-9489-a868989bbbc9";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter btAdapter;
    private File file;
    private boolean connectedState = false;
    private ProgressBar progressBar;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter btAdapter, File file, ProgressBar progressBar) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.file = file;
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.btAdapter = btAdapter;
        this.progressBar = progressBar;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID_STRING));

        } catch (IOException e) {
            Log.e("test", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.i("test", "Connected");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("test", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);
        connectedState = true;
        createNewConnectedThread();
    }

    // Closes the client socket and causes the thread to finish.
    public void createNewConnectedThread(){
        new ConnectedThread(mmSocket, file, progressBar).start();
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("test", "Could not close the client socket", e);
        }
    }

    public boolean getConnectedState(){
        return connectedState;
    }

    public void setFile(File file){
        this.file = file;
    }
}
