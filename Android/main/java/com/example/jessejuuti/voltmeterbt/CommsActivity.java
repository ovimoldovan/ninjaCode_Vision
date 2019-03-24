package com.example.jessejuuti.voltmeterbt;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.util.Timer;
import java.util.*;

import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

public class CommsActivity extends AppCompatActivity {

    public BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "CommsActivity";
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    private EditText et = null;
    private TextToSpeech tts = null;

    Speakerbox speakerbox;

    private Handler mHandler;

    private boolean enteredFreeFall = false;

    public  class ConnectThread extends Thread implements TextToSpeech.OnInitListener{
        public ConnectThread(BluetoothDevice device) throws IOException {
            /*if (mmSocket != null) {
                if(mmSocket.isConnected()) {
                    send();
                }
            }*/
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
                tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            BTAdapter.cancelDiscovery();
           // try {
                mmSocket.connect();
            //} catch (IOException connectException) {
               // Log.v(TAG, "Connection exception!");
               // try {
                   // mmSocket.close();
                    /*mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                    mmSocket.connect();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } */
               // } catch (IOException closeException) {

                //}
            //}

                send();
        }

            public void send() throws IOException {
                speakerbox = new Speakerbox(getApplication());
                String msg = "mesaj";
                OutputStream mmOutputStream = mmSocket.getOutputStream();

                mmOutputStream.write(msg.getBytes());
                String messageReceived = receive();


                if(messageReceived.equals("OBJECT AHEAD")){
                    speakerbox.play(messageReceived);
                }

            if(messageReceived.equals("FREE FALL") && enteredFreeFall==false){
                speakerbox.play(messageReceived);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String phoneNumber = "0743112939";
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
                enteredFreeFall=true;
            }



                //speakerbox.setActivity(fragment.getActivity());
                /*if(messageReceived == "Free fall"){
                    String phoneNumber = "0743112939";
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
                */

        }

        public String receive() throws IOException {
            InputStream mmInputStream = mmSocket.getInputStream();
            byte[] buffer = new byte[256];
            int bytes;
            String readMessage=null;

            try {
                    bytes = mmInputStream.read(buffer);
                    readMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received: " + readMessage);
                    TextView voltageLevel = (TextView) findViewById(R.id.Voltage);
                    voltageLevel.setText("\n" +"Mesaj: " + readMessage);
                    //mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Problems occurred!");

                }
            return readMessage;
            }

        @Override
        public void onInit(int status) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comms);


        mHandler=new Handler();
        mStatusChecker.run();


    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                final Intent intent = getIntent();
                final String address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);
                Button voltButton = (Button) findViewById(R.id.measVoltage);
                final BluetoothDevice device = BTAdapter.getRemoteDevice(address);

                try {
                    new ConnectThread(device).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            } finally {

                mHandler.postDelayed(mStatusChecker, 500);
            }
        }
    };


    //@Override
    /*protected void onStop() {
        super.onStop();
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}