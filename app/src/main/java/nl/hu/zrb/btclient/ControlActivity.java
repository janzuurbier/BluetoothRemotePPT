package nl.hu.zrb.btclient;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class ControlActivity extends Activity {

    int CONNECT_BT = 2351;
    private Wachtrij<String> messageQueue;
    TextView tv;
    String tag = "ControlActivity";
    ThreadThatSendsCommandsByBT t;
    MenuItem connectItem;
    RobotConnection myRobot = new RobotConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        tv = (TextView) findViewById(R.id.textView);
        messageQueue = new Wachtrij<String>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.connect){
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivityForResult(intent,CONNECT_BT);
            item.setEnabled(false);
            connectItem = item;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        if(resultCode == RESULT_OK && requestCode == CONNECT_BT){
            BluetoothDevice device = data.getParcelableExtra("theDevice");
            new ConnectTask().execute(device);
        }
    }

    @Override
    protected void onDestroy() {
        myRobot.closeConnection();
        super.onDestroy();
    }


    private class ConnectTask extends AsyncTask<BluetoothDevice, Void, String> {


        @Override
        protected String doInBackground(BluetoothDevice... arg0) {
            BluetoothDevice device = arg0[0];
            try{

               myRobot.connectToDevice(device);
                t = new ThreadThatSendsCommandsByBT();
                t.start();
                return "connected";

            }
            catch(IOException e){
                e.printStackTrace();
                return "error connecting";

            }

        }

        @Override
        protected void onPostExecute(String result){
               tv.setText(result);
               connectItem.setEnabled(true);
        }

    }



    private class ThreadThatSendsCommandsByBT extends Thread {

        @Override
        public void run() {
            while (true) {
                String message = messageQueue.neemWeg();
                Log.i(tag, message);
                byte[] command = myRobot.createWriteCommand(message);

                try {
                    myRobot.sendCommand(command);

                } catch (IOException e) {
                    Log.i(tag, e.toString());
                    Message m = new Message();
                    m.getData().putString("theText", "Connection lost: " + e.getMessage());
                    h.sendMessage(m);
                    break;
                }
            }

            myRobot.closeConnection();

        }
    }

    private Handler h = new Handler(){
        public void handleMessage(Message message){
            String s = (String) message.getData().get("theText");
            tv.setText(s);
            connectItem.setEnabled(true);
        }
    };



    public void onUp(View v){
        messageQueue.voegToe("UP");
    }

    public void onDown(View v){
        messageQueue.voegToe("DOWN");
    }

    public void onLeft(View v){
        messageQueue.voegToe("LEFT");
    }

    public void onRight(View v){
        messageQueue.voegToe("RIGHT");
    }

    public void onFire(View v){
        messageQueue.voegToe("FIRE");
    }

    public void onA(View v){
        messageQueue.voegToe("A");
    }

    public void onB(View v){
        messageQueue.voegToe("B");
    }

    public void onC(View v){
        messageQueue.voegToe("C");
    }



}
