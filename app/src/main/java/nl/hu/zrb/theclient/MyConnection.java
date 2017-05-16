package nl.hu.zrb.theclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by JZuurbier on 26-9-2015.
 */
public class MyConnection {
    private UUID MY_UUID = UUID.fromString("B62C4E8D-62CC-404b-BBBF-BF3E3BBB1374");

    private boolean connected = false;
    BluetoothSocket theSocket;
    private OutputStream out;
    private InputStream in;
    private MessageListener listener;
    private final String TAG = "MyConnection";


    public MyConnection(MessageListener listener){
        this.listener = listener;
    }

    public boolean isConnected(){
        return connected;
    }


    public void connectToDevice(BluetoothDevice device)  {
        if (connected) return;
        new ConnectTask().execute(device);
    }

    public void closeConnection(){
        if(!connected) return;
        connected = false;
        try {
            theSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        if(!connected) return;
        try {
            out.write((command+"\n").getBytes());
        } catch (IOException e) {
            Log.i(TAG, e.toString());
            listener.onMessage("Connection lost: " + e.getMessage());
            closeConnection();
        }
    }


    private class ConnectTask extends AsyncTask<BluetoothDevice, Void, String> {


        @Override
        protected String doInBackground(BluetoothDevice... arg0) {
            BluetoothDevice device = arg0[0];
            try{
                theSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                theSocket.connect();
                out = theSocket.getOutputStream();
                in = theSocket.getInputStream();
                connected = true;
                return "connected";

            }
            catch(IOException e){
                e.printStackTrace();
                return "error connecting";

            }

        }

        @Override
        protected void onPostExecute(String result){
            listener.onMessage(result);
        }

    }



}
