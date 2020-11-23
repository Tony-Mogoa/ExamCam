package com.example.examcam;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private static final String TAG = "test";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private File file;
    private ProgressBar progressBar;

    public ConnectedThread(BluetoothSocket socket, File file, ProgressBar progressBar) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.file = file;
        this.progressBar = progressBar;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
            Log.i("test", "Great");
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        int numBytes; // bytes returned from read()
        Log.i("test", "running");
        sendDataToServer();
    }

    private void sendDataToServer() {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                }
            });

            int bytesSent = 0;
            FileInputStream fis = new FileInputStream(file);
            sendFileLength(file.length());
            sendFileName(file.getName());
            Log.i(TAG, file.getName());

            byte[] buffer = new byte[1024];
            int c;
            while (true) {
                c = fis.read(buffer);
                if (c == -1) {
                    Log.d(TAG, "End of reading");
                    break;
                } else {
                    mmOutStream.write(buffer, 0, c);
                    bytesSent += c;
                    double progress = ((double) bytesSent / file.length() )* 100;
                    Handler progressHandler = new Handler(Looper.getMainLooper());
                    progressHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress((int) progress);
                        }
                    });
                }
            }
            fis.close();
            mmOutStream.flush();

            Log.d(TAG, "Successful sending of files");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFileLength(Long size) {
        if (mmOutStream != null) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(mmOutStream);
                dataOutputStream.writeLong(size);
                dataOutputStream.flush();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    private void sendFileName(String filename) {
        if (mmOutStream != null) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(mmOutStream);
                dataOutputStream.writeUTF(filename);
                dataOutputStream.flush();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}

