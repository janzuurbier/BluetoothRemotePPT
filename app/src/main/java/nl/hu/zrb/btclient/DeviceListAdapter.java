package nl.hu.zrb.btclient;

import java.util.ArrayList;
import java.util.Iterator;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {
	
	private ArrayList<BluetoothDevice> lijst = new ArrayList<BluetoothDevice>();
	private LayoutInflater inflater;
    private Bitmap mIcon1;        
    private Bitmap mIcon2;
    private Bitmap mIcon3;
	
	public DeviceListAdapter(Context c){
		inflater = LayoutInflater.from(c);		
        mIcon1 = BitmapFactory.decodeResource(c.getResources(), R.drawable.phone);            
        mIcon2 = BitmapFactory.decodeResource(c.getResources(), R.drawable.computer);
        mIcon3 = BitmapFactory.decodeResource(c.getResources(), R.drawable.robot);
	}
	

	@Override
	public int getCount() {
		return lijst.size();
	}

	@Override
	public Object getItem(int arg0) {
		return lijst.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
		BluetoothDevice device = lijst.get(arg0);
		ViewHolder holder;
		if(v == null){
			v = inflater.inflate(R.layout.devicerow, null);
            holder = new ViewHolder();                
            holder.text = (TextView) v.findViewById(R.id.textView1);                
            holder.icon = (ImageView) v.findViewById(R.id.imageView1);
            v.setTag(holder);
		}
		else {
			holder = (ViewHolder) v.getTag();
		}
		holder.text.setText(device.getName());
		if(device.getBluetoothClass().getMajorDeviceClass()== BluetoothClass.Device.Major.PHONE)
			holder.icon.setImageBitmap(mIcon1);
		if(device.getBluetoothClass().getMajorDeviceClass()== BluetoothClass.Device.Major.COMPUTER)
			holder.icon.setImageBitmap(mIcon2);
        if(device.getBluetoothClass().getMajorDeviceClass()== BluetoothClass.Device.Major.TOY)
            holder.icon.setImageBitmap(mIcon3);
		return v;
	}
	
	public void add(BluetoothDevice founddevice){
		for(Iterator<BluetoothDevice> iter = lijst.iterator(); iter.hasNext(); ){
			BluetoothDevice device = iter.next();
			if(device.getName()!= null && device.getName().equals(founddevice.getName())) 
				iter.remove();
		}
		lijst.add(founddevice);
	}
	
	public void clear(){
		lijst.clear();
	}
	
    class ViewHolder {            
    	TextView text;            
    	ImageView icon;        
    }	

}
