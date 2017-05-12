package nl.hu.zrb.theclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class ConnectActivity extends AppCompatActivity  {
	
	DeviceListAdapter adapter1, adapter2;
	BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
    String tag = "BluetoothRemoteControl";
    int REQUEST_ENABLE_BT = 234;

    RecyclerView rv1, rv2;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (btadapter == null) {
            String text = "Your device does not support bluetooth.";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        Log.i(tag, "oncreate");
        setContentView(R.layout.activity_connect);
        rv1 = (RecyclerView) findViewById(R.id.recycler_view1);
        adapter1 = new DeviceListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        rv1.setLayoutManager(mLayoutManager1);
        rv1.setItemAnimator(new DefaultItemAnimator());
        rv1.setAdapter(adapter1);
        rv2 = (RecyclerView) findViewById(R.id.recycler_view2);
        adapter2 = new DeviceListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        rv2.setLayoutManager(mLayoutManager2);
        rv2.setItemAnimator(new DefaultItemAnimator());
        rv2.setAdapter(adapter2);


        if(!btadapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else {
            Set<BluetoothDevice> pairedDevices = btadapter.getBondedDevices();
            for(BluetoothDevice dev: pairedDevices)
                adapter2.add(dev);
        }
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
                adapter2.add(dev);

        }
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {    
    	public void onReceive(Context context, Intent intent) {
    		String action = intent.getAction();              
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {            
    			// Get the BluetoothDevice object from the Intent            
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);            
    			// Add the device to an array adapter to show in a ListView            
    			adapter1.add(device);
    			Log.i(tag, "gevonden device " + device.getName());
    		}
    	}
    };


}