package nl.hu.zrb.theclient;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class ControlActivity extends AppCompatActivity implements MessageListener {

    int CONNECT_BT = 2351;
    private static final int LARGE_MOVE = 60;

    TextView tv;
    String tag = "ControlActivity";
    MenuItem connectItem;
    MyConnection laptopConnection;
    private GestureDetector gestureDetector;

    public ControlActivity() {
        laptopConnection = new MyConnection(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        tv = (TextView) findViewById(R.id.textView);
        gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {


                        if (e1.getX() - e2.getX() > LARGE_MOVE) {
                            laptopConnection.sendCommand("Previous");
                            return true;

                        } else if (e2.getX() - e1.getX() > LARGE_MOVE) {
                            laptopConnection.sendCommand("Next");
                            return true;
                        }

                        return false;
                    } });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
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
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        if(resultCode == RESULT_OK && requestCode == CONNECT_BT){
            BluetoothDevice device = data.getParcelableExtra("theDevice");
            laptopConnection.connectToDevice(device);
        }
    }

    @Override
    protected void onDestroy() {
        laptopConnection.closeConnection();
        super.onDestroy();
    }

    @Override
    public void onMessage(String msg) {
        tv.setText(msg);
    }

}
