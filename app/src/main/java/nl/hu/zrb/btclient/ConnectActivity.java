package nl.hu.zrb.btclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectActivity extends ListActivity  {
	
	DeviceListAdapter adapter;
	BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
    Button b;
    String tag = "BluetoothRemoteControl";
    int REQUEST_ENABLE_BT = 234;


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (btadapter == null) {
            /*String text = "Your device does not support bluetooth.";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.show();*/
            ListView lv = getListView();
            TextView tv = new TextView(this);
            tv.setText("Your device does not support bluetooth");
            ((ViewGroup)lv.getParent()).addView(tv);
            lv.setEmptyView(tv);
            return;
        }
        Log.i(tag, "oncreate");
        adapter = new DeviceListAdapter(this);
        ListView lv = getListView();
        TextView tv = new TextView(this);
        tv.setText("no devices");
        ((ViewGroup)lv.getParent()).addView(tv);
        lv.setEmptyView(tv);

        if(!btadapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            Set<BluetoothDevice> pairedDevices = btadapter.getBondedDevices();
            for(BluetoothDevice dev: pairedDevices)
                adapter.add(dev);
        }
        setListAdapter(adapter);   

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh && btadapter != null){
            onContentChanged();
            boolean succes = btadapter.startDiscovery();
            if(succes)
                Log.i(tag, "discovery started");
            else
                Log.i(tag, "discovery not started");
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    public void onStop(){
        super.onStop();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i ){
        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
            Set<BluetoothDevice> pairedDevices = btadapter.getBondedDevices();
            for(BluetoothDevice dev: pairedDevices)
                adapter.add(dev);

        }
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {    
    	public void onReceive(Context context, Intent intent) {
    		String action = intent.getAction();              
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {            
    			// Get the BluetoothDevice object from the Intent            
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);            
    			// Add the device to an array adapter to show in a ListView            
    			adapter.add(device);   
    			onContentChanged();
    			Log.i(tag, "gevonden device " + device.getName());
    		}
    	}
    };

	

	@Override
	public void onListItemClick(ListView l, View arg1, int arg2, long arg3) {
        btadapter.cancelDiscovery();
		BluetoothDevice device = (BluetoothDevice)adapter.getItem(arg2);
		Log.i(tag, device.getName());
        Intent myIntent = new Intent();
        myIntent.putExtra("theDevice", device);
        setResult(RESULT_OK, myIntent);
        finish();
    }
	

	

}