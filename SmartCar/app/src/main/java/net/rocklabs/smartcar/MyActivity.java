package net.rocklabs.smartcar;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MyActivity extends Activity {
    // Debugging
    private static final String TAG = "SmartCar";
    private static final String address = "00:13:03:26:13:56";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private Button btnConnect;
    private Button btnEngine;
    private SeekBar sbAcel;
    private ToggleButton btnRelay1;
    private ToggleButton btnRelay2;
    private ToggleButton btnRelay3;
    private ToggleButton btnRelay4;
    private ToggleButton btnRelay5;

    private TextView txtNameBT;
    private TextView txtAcel;

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        btAdapter = BluetoothAdapter.getDefaultAdapter();


        txtAcel = (TextView)findViewById(R.id.txtAcel);
        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnEngine = (Button)findViewById(R.id.btnEngine);
        btnRelay1 = (ToggleButton)findViewById(R.id.btnRelay1);
        btnRelay2 = (ToggleButton)findViewById(R.id.btnRelay2);
        btnRelay3 = (ToggleButton)findViewById(R.id.btnRelay3);
        btnRelay4 = (ToggleButton)findViewById(R.id.btnRelay4);
        btnRelay5 = (ToggleButton)findViewById(R.id.btnRelay5);
        sbAcel = (SeekBar)findViewById(R.id.sbAcel);



        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If BT is not on, request that it be enabled.
                // setupChat() will then be called during onActivityResult
                if (!btAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    // Otherwise, setup the chat session
                } else {
                    connect();
                }
            }
        });

        btnEngine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                String code = "5";
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(TAG, "ActionDown");
                    code += "0";
                    mConnectedThread.write(code.getBytes());
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.i(TAG, "ActionUp");
                    code += "1";
                    mConnectedThread.write(code.getBytes());
                }

                return false;
            }
        });

        btnRelay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "1";
                if (btnRelay1.isChecked())
                    code += "0";
                else
                    code += "1";
                mConnectedThread.write(code.getBytes());
            }

        });

        btnRelay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "2";
                if (btnRelay2.isChecked())
                    code += "0";
                else
                    code += "1";
                mConnectedThread.write(code.getBytes());
            }
        });

        btnRelay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "3";
                if (btnRelay3.isChecked())
                    code += "0";
                else
                    code += "1";
                mConnectedThread.write(code.getBytes());
            }
        });

        btnRelay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "4";
                if (btnRelay4.isChecked())
                    code += "0";
                else
                    code += "1";
                mConnectedThread.write(code.getBytes());
            }
        });

        btnRelay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = "5";
                if (btnRelay5.isChecked())
                    code += "0";
                else
                    code += "1";
                mConnectedThread.write(code.getBytes());
            }
        });

        sbAcel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String val = "9" + i;
                txtAcel.setText("Value: " + val);
                //mConnectedThread.write(val.getBytes());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                //tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);

                //tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                tmp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device, 1);
            } catch (Exception e) {
                Log.e(TAG, "Socket create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.i(TAG, "Exception mConnectThread: " + e.toString());
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //ConnectedThread manageConnectedThread = new ConnectedThread();
            connected(mmSocket, mmDevice);

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    Log.i(TAG, "Read buffer: " + bytes);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void connect() {
        Log.d(TAG, "connect ");
        // Get the device MAC address
        //String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        //mChatService.connect(device, secure);
        Toast.makeText(this, "Connecting to: " + btDevice.getName(), Toast.LENGTH_LONG).show();
        //connAsynTask.execute(btDevice);
        mConnectThread = new ConnectThread(btDevice);
        mConnectThread.start();
    }

    private void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected to: " + device.getName());
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        //connectDevice();
        /*switch (requestCode) {
            connectDevice();
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice();
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice();
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "BT not enabled", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e(TAG, "+ ON RESUME +");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (btAdapter != null)
            btAdapter.cancelDiscovery();
        if (btSocket != null){
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
