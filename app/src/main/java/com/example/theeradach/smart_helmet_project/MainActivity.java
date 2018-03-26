package com.example.theeradach.smart_helmet_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hololo.tutorial.library.TutorialActivity;

import org.w3c.dom.Text;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch swVoice , swBattery;
    private TextView txtVoice ,txtName , txtNumber;
    private int RequestPermissionCode = 1;
    public String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
    private ImageButton imgBtnContract , imgBtnInstruction;

    /*------------------- variables for bluetooth  -----------------------------*/

    private TextView txtAcc, txtGyro, txtAX, txtAY, txtAZ , txtDetect;

    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;


    /*------------------- variables for bluetooth  -----------------------------*/


    // variable for checking if voice switch is clicked : running in background (onpause)
    public static int checkedSW;


    // Navigator part
    public static double cur_lat;
    public static double cur_long;
    public static double des_lat;
    public static double des_long;
    GPSTracker gps;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    // Navigator part

    //SMS part
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    // SMS part

    // variable for bluetooth value on pause :
    public static double SumAcc;
    public static double SumGyro;
    public static double NumAX;
    public static double NumAY;
    public static double NumAZ;

    // variable for bluetooth value on pause :

    SharedPreferences sharedpreferences;

    // speech recognizerManager class
    private SpeechRecognizerManager mSpeechManager;

    // set variables for sharePreferences !
    public static final String MyPREFERENCES = "Emergency" ;
    public static final String Name = "nameKey";
    public static final String Phone = "phoneKey";
    // End set variables for sharePreferences !

    // Battery level
    String[] batteryLevel = { "90", "80","70" , "60" , "50" , "40" ,"30" , "20" , };
    public static int level;
    public static int adapterLevel;
    private static int switchState = 0;
    private Spinner spinnerBattery;
    // Battery level




    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swVoice = (Switch) findViewById(R.id.swVoice);
        swBattery = (Switch) findViewById(R.id.swBattery);
        txtVoice = (TextView) findViewById(R.id.txtVoice);
        imgBtnContract = (ImageButton) findViewById(R.id.imgBtnContract);
        txtName = (TextView) findViewById(R.id.txtName);
        txtNumber = (TextView) findViewById(R.id.txtNumber);
        spinnerBattery = (Spinner) findViewById(R.id.spinnerBattery);
        swBattery = (Switch) findViewById(R.id.swBattery);
        imgBtnInstruction = (ImageButton) findViewById(R.id.imgBtnInstruction);


        txtAcc = (TextView) findViewById(R.id.txtAcc);
        txtGyro = (TextView) findViewById(R.id.txtGyro);
        txtAX = (TextView) findViewById(R.id.txtAX);
        txtAY = (TextView) findViewById(R.id.txtAY);
        txtAZ = (TextView) findViewById(R.id.txtAZ);
        txtDetect = (TextView) findViewById(R.id.txtDetect);

        imgBtnInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TutorialView.class);
                startActivity(intent);
            }
        });

        registerBatteryLevelReceiver();

        spinnerBattery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Battery Level: " + batteryLevel[position], Toast.LENGTH_LONG).show();
                adapterLevel = Integer.parseInt(batteryLevel[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // adapter for battery spinner
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, batteryLevel);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerBattery.setAdapter(aa);


        swBattery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(MainActivity.this, "Battery Notify ON", Toast.LENGTH_SHORT).show();
                    switchState = 1;
                }else{
                    Toast.makeText(MainActivity.this, "Battery Notify OFF", Toast.LENGTH_SHORT).show();
                    switchState = 0;
                }
            }
        });

        swVoice.setOnCheckedChangeListener(this);

        // set share preferences
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // image contract button is clicked
        imgBtnContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);

            }
        });
        // end image contract button is clicked


        // Emergency SMS text
        if (sharedpreferences.contains(Name)) {
            txtName.setText(sharedpreferences.getString(Name, ""));
        }
        if (sharedpreferences.contains(Phone)) {
            txtNumber.setText(sharedpreferences.getString(Phone, ""));

        }else {
            txtName.setText("");
            txtNumber.setText("");
        }
        // End Emergency SMS text

        /* --------------------------- Read Phone State ------------------------*/

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        PhoneStateListener callStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Toast.makeText(getApplicationContext(), "สายเรียกเข้า \n" + "เบอร์โทร : " + incomingNumber,
                            Toast.LENGTH_LONG).show();

                    Log.e("Incoming Number : ", ": " + incomingNumber);

                    //SetSpeechListener();

//                    // Speak incoming number
//                    String getName;
//                    getName = getContactName(incomingNumber, getApplicationContext());
//                    if (getName == "") {
//                        Toast.makeText(getApplicationContext(), "Number : " + incomingNumber, Toast.LENGTH_SHORT).show();  // Toast shows if not found name
//                        // call Text To Speech from class
//                        MyTTS.getInstance(getApplicationContext())
//                                .setEngine("com.google.android.tts")
//                                .speak("someone calling you");
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Name : " + getName, Toast.LENGTH_SHORT).show();  // Toast shows name from number
//
//                        // call Text To Speech from class
//                        MyTTS.getInstance(getApplicationContext())
//                                .setEngine("com.google.android.tts")
//                                .speak(" " + getName + "is calling you");  // **  Can't say Thai words  **
//                    }
//                    // end Speak incoming number

                }
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Toast.makeText(getApplicationContext(), "กำลังอยู่ในสาย",
                            Toast.LENGTH_LONG).show();
                    if(mSpeechManager!=null) {
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                    }
                }

                if (state == TelephonyManager.CALL_STATE_IDLE) {
//                    Toast.makeText(getApplicationContext(), "phone is neither ringing nor in a call",
//                            Toast.LENGTH_LONG).show();
                }
            }
        };

        // Listen phone status
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        /* --------------------------- Read Phone State ------------------------*/

        /* ---------------------------   Bluetooth  section  ------------------------*/
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                          // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();							//get length of data received
                        
                        if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                        {

                            String temp = recDataString.substring(1, dataLength).trim();  //  Ex. >  1.63,2.79,36.24,19.26,42.60
                            

                                String[] data = new String[10];
                                data = temp.split(",");

                                            //get sensor value from string
                                SumAcc = Double.parseDouble(data[0].toString());
                                SumGyro = Double.parseDouble(data[1].toString());
                                NumAX = Double.parseDouble(data[2].toString());
                                NumAY = Double.parseDouble(data[3].toString());
                                NumAZ = Double.parseDouble(data[4].toString());

                                txtAcc.setText("" + SumAcc);       //update the textviews with sensor values
                                txtGyro.setText("" + SumGyro);
                                txtAX.setText("" + NumAX + "|");
                                txtAY.setText("" + NumAY + "|");
                                txtAZ.setText("" + NumAZ);

                                if(SumAcc >= 3.0 || SumGyro >= 320) {
                                    txtDetect.setText("ตรวจสอบการล้ม");
                                    Toast.makeText(MainActivity.this, "ตรวจสอบว่าเกิดการล้มหรือชน", Toast.LENGTH_SHORT).show();
                                    MyTTS.getInstance(getApplicationContext())
                                            .setEngine("com.google.android.tts")
                                            .setLocale(new Locale("th"))
                                            .speak("เกิดการล้ม");

                                    gps = new GPSTracker(MainActivity.this);
                                    cur_lat = gps.getLatitude();
                                    cur_long = gps.getLongitude();
                                    String curAddress = getCompleteAddressString(cur_lat , cur_long);  // string for current address
                                    //Toast.makeText(this, "Address : " + curAddress , Toast.LENGTH_SHORT).show();
                                    String message = "คาดว่าเกิดอุบัติเหตุกับเบอร์นี้ กรุณาตรวจสอบสถานที่ตามลิงค์ด้านล่างนี้ : https://www.google.com/maps/search/?api=1&query=" + cur_lat + "," + cur_long;  // ข้อความครั้งละ 5 บาท
                                    //Toast.makeText(this , " " + message , Toast.LENGTH_SHORT).show();

                                    // ------------  send SMS   ------------------------  //

                                    sendSMS(txtNumber.getText().toString(), message);

                                    // ------------  send SMS   ------------------------  //

                                }if(NumAX >= 80 || NumAY >= 70 || NumAZ >= 70 && SumAcc >= 2.8){
//                                    txtDetect.setText("ตรวจสอบการล้ม");
//                                    MyTTS.getInstance(getApplicationContext())
//                                            .setEngine("com.google.android.tts")
//                                            .setLocale(new Locale("th"))
//                                            .speak("ตรวจสอบการล้ม");

                                    // ------------  send SMS   ------------------------  //

                                    //sendSMS(txtNumber.getText().toString(), message);

                                    // ------------  send SMS   ------------------------  //
                                }
                        }
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        /* ---------------------------   Bluetooth  section   ------------------------------*/


    }   // end on create

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    // broadcast Receiver for battery
    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            int scale = intent.getIntExtra("scale", -1);
            int rawlevel = intent.getIntExtra("level", -1);
            level = 0;

//            Bundle bundle = intent.getExtras();
//            Log.i("BatteryLevel", bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                //String info = "Battery Level: " + level;
            }

            batterySwitch(adapterLevel, level);

        }

    };
    // End broadcast Receiver for battery

    public void batterySwitch(int adapterLevel, int level) {
        if(switchState == 1) {
            if (adapterLevel >= level) {
                Toast.makeText(this, "แบตเตอรี่ต่ำ", Toast.LENGTH_SHORT).show();
                String strBattery = "แบตเตอรี่ต่ำกว่า " + adapterLevel + "เปอร์เซ็นต์";
                // call Text To Speech from class
                MyTTS.getInstance(getApplicationContext())
                        .setEngine("com.google.android.tts")
                        .setLocale(new Locale("th"))
                        .speak(strBattery);
            } else {
                Toast.makeText(this, "แบตเตอรี่เพียงพอ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(battery_receiver, filter);
    }


    // end Battery zone

    // control switch on/off speech recognizer
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        if(PermissionHandler.checkPermission(this,PermissionHandler.RECORD_AUDIO)) {

            if (isChecked) {

                checkedSW = 1;

                if (mSpeechManager == null) {
                    SetSpeechListener();
                } else if (!mSpeechManager.ismIsListening()) {
                    mSpeechManager.destroy();
                    SetSpeechListener();
                }
                txtVoice.setText(getString(R.string.you_may_speak));
            } else {

                checkedSW = 0;
                if (mSpeechManager != null) {
                    txtVoice.setText(getString(R.string.destroied));
                    mSpeechManager.destroy();
                    mSpeechManager = null;
                }
            }
        }else {
            PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO,this);
        }
    }


    // control switch on/off battery notification




    // set speech listener to get text
    private void SetSpeechListener()
    {
        mSpeechManager=new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {

                if(results!=null && results.size() > 0)
                {
                    txtVoice.setText(results.get(0));      // get result from speech here
                    commandVoice(results.get(0));           // *** Voice Commands ***
                    Toast.makeText(MainActivity.this, "Result:" + results.get(0), Toast.LENGTH_SHORT).show();
                }
                else
                    txtVoice.setText(getString(R.string.no_results_found));
            }
        });
    }

    // request record audio Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case PermissionHandler.RECORD_AUDIO:
                if(grantResults.length>0) {
                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                        //start_listen_btn.performClick();
                        swVoice.performClick();

                    }
                }
                break;

        }
    }

    // function get phone number and name from contract
    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;

                    int IDresultHolder ;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult) ;

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                txtName.setText(" "+ TempNameHolder);
                                txtNumber.setText(" "+ TempNumberHolder);

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(Name, "" + TempNameHolder);
                                editor.putString(Phone, "" + TempNumberHolder);
                                editor.commit();
                            }
                        }

                    }
                }
                break;
        }
    }
    // function get phone number and name from contract


    // permission for read contact
    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }


    // Voice command function !!
    private void commandVoice(String s) {
        if(s.contains("โทรหา")){   // call from name

            // get name from calling
            String getContactName;
            int index , length ;
            index = s.indexOf("า");
            length = s.length();

            // get name
            getContactName = s.substring(index+1 , length);
            //Toast.makeText(this, "index :" + index +"\n"+ "length : "+ length, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "ชื่อ : "+ getContactName , Toast.LENGTH_SHORT).show();

            // get number from name and call
            NumNameGet getContact = new NumNameGet();
            String temp = getContact.get_Number(getContactName, getApplicationContext());
            Toast.makeText(this, "เบอร์โทรศัพท์ : "+ temp , Toast.LENGTH_SHORT).show();
            if(temp == ""){
                MyTTS.getInstance(getApplicationContext())
                        .setEngine("com.google.android.tts")
                        .setLocale(new Locale("th"))
                        .speak("ไม่มีเบอร์ที่ต้องการติดต่อค่ะ");
            }else{
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + temp ));
//              callIntent.setData(Uri.parse("tel:0992980430" ));
                startActivity(callIntent);
            }


//            SetSpeechListener();  // keep speech recognition carries on after phone call

        }if (s.contains("นำทางไปยัง")){   // Navigator

            // get name from calling
            String getLocation;
            int index , length ;
            index = s.indexOf("ป");
            length = s.length();

            // get location name
            getLocation = s.substring(index+4 , length);
            Toast.makeText(this, "สถานที่ : "+ getLocation , Toast.LENGTH_SHORT).show();

            // source address lat , long
            gps = new GPSTracker(MainActivity.this);
            cur_lat = gps.getLatitude();
            cur_long = gps.getLongitude();

            // destination address lat , long
            if(getLocation.contains("จตุจักร")){
                des_lat = 13.800082;
                des_long = 100.551019;
            }if(getLocation.contains("สยามพารากอน")){
                des_lat = 13.7469421;
                des_long = 100.5327445;
            }if(getLocation.contains("เซ็นทรัลเวิร์ด")){
                des_lat = 13.7462328;
                des_long = 100.5376301;
            }if(getLocation.contains("ภาควิชาวิศวกรรมคอมพิวเตอร์")){
                des_lat = 14.0359804;
                des_long = 100.7150835;
            }if(getLocation.contains("ราชมงคลคลอง 6") || getLocation.contains("ราชมงคลธัญบุรี")){
                des_lat = 14.0344248;
                des_long = 100.7184643;
            }if(getLocation.contains("อนุสาวรีย์ชัยสมรภูมิ")){
                des_lat = 13.7638482;
                des_long = 100.5354999;
            }if(getLocation.contains("ซอยพรธิสาร")){
                des_lat = 14.0378531;
                des_long = 100.7352134;
            }if(getLocation.contains("พิพิธภัณฑ์วิทยาศาสตร์แห่งชาติ")){
                des_lat = 14.048569;
                des_long = 100.7150835;
            }else{
                convertAddress(getLocation);
            }

            if (des_lat == 0.0 && des_long == 0.0  ){
                MyTTS.getInstance(getApplicationContext())
                        .setEngine("com.google.android.tts")
                        .setLocale(new Locale("th"))
                        .speak("ไม่มีสถานที่ที่คุณต้องการไปค่ะ");
            }else {
                //String direction = "https://www.google.com/maps/dir/" + des_lat + ",+" + des_long + "/" + cur_lat + ",+" + cur_long + "/";  // -- not working --
                String direction = "http://maps.google.com/maps?saddr="+ cur_lat +","+ cur_long + "&daddr="+des_lat+","+des_long;
                //String direction = cur_lat  + "  " +  cur_long;

                Context context = getApplicationContext();
                Toast.makeText(context, "direction :" + direction , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(direction));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }


        }if (s.contains("ส่งข้อความฉุกเฉิน")  || s.contains("emergency")){

            gps = new GPSTracker(MainActivity.this);
            cur_lat = gps.getLatitude();
            cur_long = gps.getLongitude();
            String curAddress = getCompleteAddressString(cur_lat , cur_long);  // string for current address
            //Toast.makeText(this, "Address : " + curAddress , Toast.LENGTH_SHORT).show();
            //https://www.google.com/maps/search/?api=1&query=36.26577,-92.54324
            String message = "คาดว่าเกิดอุบัติเหตุกับเบอร์นี้ กรุณาตรวจสอบสถานที่ตามลิงค์ด้านล่างนี้ : https://www.google.com/maps/search/?api=1&query=" + cur_lat + "," + cur_long;  // ข้อความครั้งละ 5 บาท

            Toast.makeText(this , " " + message , Toast.LENGTH_SHORT).show();

            // ------------  send SMS   ------------------------  //

            sendSMS(txtNumber.getText().toString(), message);

            // ------------  send SMS   ------------------------  //

        }if (s.contains("สวัสดี")){

            Toast.makeText(this, "สวัสดีค่ะ มีอะไรให้รับใช้หรอค่ะ", Toast.LENGTH_SHORT).show();
            // call Text To Speech from class
            MyTTS.getInstance(getApplicationContext())
                    .setEngine("com.google.android.tts")
                    .setLocale(new Locale("th"))
                    .speak("สวัสดีค่ะ");

        }if (s.contains("เป็นอย่างไรบ้าง") || s.contains("เป็นไงบ้าง")){

            Toast.makeText(this, "ดิฉันสบายดีค่ะ", Toast.LENGTH_SHORT).show();
            // call Text To Speech from class
            MyTTS.getInstance(getApplicationContext())
                    .setEngine("com.google.android.tts")
                    .setLocale(new Locale("th"))
                    .speak("สบายดีค่ะ");

        }if (s.contains("ยินดีที่ได้รู้จัก")) {

            Toast.makeText(this, "ยินดีที่ได้รู้จักเช่นกันค่ะ", Toast.LENGTH_SHORT).show();
            // call Text To Speech from class
            MyTTS.getInstance(getApplicationContext())
                    .setEngine("com.google.android.tts")
                    .setLocale(new Locale("th"))
                    .speak("ยินดีที่ได้รู้จักเช่นกันค่ะ");

        }if (s.contains("สถานที่ปัจจุบัน")){
            gps = new GPSTracker(MainActivity.this);
            cur_lat = gps.getLatitude();
            cur_long = gps.getLongitude();

            String speak = getCurAddress(cur_lat, cur_long);

            Toast.makeText(this, " " + speak , Toast.LENGTH_SHORT).show();
            // call Text To Speech from class
            MyTTS.getInstance(getApplicationContext())
                    .setEngine("com.google.android.tts")
                    .setLocale(new Locale("th"))
                    .speak(" " + speak);

        }if (s.contains("รับสาย") || s.contains("answer")){  // Answer incoming call

            //answerCall();   // function answer incomming call DOSEN'T WORK

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec("input keyevent " +
                                Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                    } catch (IOException e) {
                        // Runtime.exec(String) had an I/O problem, try to fall back
                        String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                        Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                                Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_HEADSETHOOK));
                        Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                                Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                        KeyEvent.KEYCODE_HEADSETHOOK));
                        Context mContext = getApplicationContext();
                        mContext.sendOrderedBroadcast(btnDown, enforcedPerm);
                        mContext.sendOrderedBroadcast(btnUp, enforcedPerm);
                    }
                }

            }).start();

        }if (s.contains("วางสาย") || s.contains("reject")){ // Reject incoming call

            disconnectCall();   // function reject incomming call WORK!!

        }if (s.contains("หยุดการทำงาน") || s.contains("stop working")){
            MyTTS.getInstance(getApplicationContext())
                    .setEngine("com.google.android.tts")
                    .setLocale(new Locale("th"))
                    .speak("เป็นเกียรติที่ได้รับใช้ค่ะ");
            mSpeechManager.destroy();
            mSpeechManager=null;
        }
    }

    // End - Voice command function !!


    //-------------  send SMS   ----------------------  //

    public void sendSMS(String number , String message){


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        int messageCount = parts.size();

        Log.i("Message Count", "Message Count: " + messageCount);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < messageCount; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS ถูกส่งเรียบร้อยแล้ว",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "กรุณาเติมเงินโทรศัพท์ค่ะ",
                                Toast.LENGTH_SHORT).show();
                        MyTTS.getInstance(getApplicationContext())    // เตือนเมื่อเงินโทรศัพท์หมดไม่สามารถส่งได้
                                .setEngine("com.google.android.tts")
                                .setLocale(new Locale("th"))
                                .speak("กรุณาเติมเงินโทรศัพท์ค่ะ");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS delivered",
//                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        //smsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
        sms.sendMultipartTextMessage(number, null, parts, sentIntents, deliveryIntents);
    }


    //-------------  send SMS   ----------------------  //



    //-------------  function Convert Address to Longtitude and Latitude  ----------------------  //

    public void convertAddress(String address) {
        if (address != null && !address.isEmpty()) {
            try {
                Context context = getApplicationContext();
                //Locale locale = Locale.ENGLISH;
                Locale locale = new Locale("th_TH");  // support Thai language
                Geocoder geoCoder = new Geocoder(context, locale);
                List<Address> addressList = geoCoder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    des_lat = addressList.get(0).getLatitude();
                    des_long = addressList.get(0).getLongitude();
                    //Toast.makeText(context, "Lat :" + des_lat + "\nLong :" + des_long, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    } // end convertAddress

    //-------------  function Convert Address to Longtitude and Latit0ude  ----------------------  //


    // ------------ function : Get Address from lat and long ------------------------------- //


    private String getCurAddress(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Locale locale = new Locale("th_TH");  // support Thai language
        Geocoder geocoder = new Geocoder(this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {

                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                strAdd =  city + " " + country;
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd ;
    }


    // ------------ function : Get full Address from lat and long ------------------------------- //

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        //Locale locale = Locale.ENGLISH;
        Locale locale = new Locale("th_TH");  // support Thai language
        Geocoder geocoder = new Geocoder(this, locale);

        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    // ------------  Get Address from lat and long --------------------- //


    //  -----------    Disconnect Call function    ----------------------  //
    public void disconnectCall() {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL ERROR :",
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("Ex Object", "Exception object: " + e);
        }
    }
    //  -----------    Disconnect Call function    ----------------------  //

    //  -----------    Answer  Call function    ----------------------  //
    public void answerCall(){

        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyAnswerCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);

            telephonyAnswerCall = telephonyClass.getMethod("answerRingingCall");
            telephonyAnswerCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL ERROR :",
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("Ex Object", "Exception object: " + e);
        }


//        try {
//            // logger.debug("execute input keycode headset hook");
//            Runtime.getRuntime().exec("input keyevent " +
//                    Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
//
//
//        } catch (IOException e) {
//
////            Log.e("Call Exception ",e.toString());
////            HelperMethods.showToastS(getBaseContext(),"Call Exception one "+e.toString());
//            // Runtime.exec(String) had an I/O problem, try to fall back
//            //    logger.debug("send keycode headset hook intents");
//            String enforcedPerm = "android.permission.CALL_PRIVILEGED";
//            Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
//                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
//                            KeyEvent.KEYCODE_HEADSETHOOK));
//            Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
//                    Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
//                            KeyEvent.KEYCODE_HEADSETHOOK));
//
//            sendOrderedBroadcast(btnDown, enforcedPerm);
//            sendOrderedBroadcast(btnUp, enforcedPerm);
//        }


    }

    //  -----------    Answer  Call function    ----------------------  //


    //  -----------  on application is pause   ----------------------  //
    @Override
    protected void onPause() {

        super.onPause();
        if(checkedSW == 1){
            SetSpeechListener();  // still speaking while somebody calling
        }else {
            if(mSpeechManager!=null) {
                mSpeechManager.destroy();
                mSpeechManager = null;
            }
        }

//        if(SumAcc >= 3.0 || SumGyro >= 320) {
//            txtDetect.setText("ตรวจสอบการล้ม");
//            Toast.makeText(MainActivity.this, "ตรวจสอบว่าเกิดการล้มหรือชน", Toast.LENGTH_SHORT).show();
//            MyTTS.getInstance(getApplicationContext())
//                    .setEngine("com.google.android.tts")
//                    .setLocale(new Locale("th"))
//                    .speak("เกิดการล้ม");
//
//            gps = new GPSTracker(MainActivity.this);
//            cur_lat = gps.getLatitude();
//            cur_long = gps.getLongitude();
//            String curAddress = getCompleteAddressString(cur_lat , cur_long);  // string for current address
//            //Toast.makeText(this, "Address : " + curAddress , Toast.LENGTH_SHORT).show();
//            String message = "คาดว่าเกิดอุบัติเหตุกับเบอร์นี้ กรุณาตรวจสอบสถานที่ตามลิงค์ด้านล่างนี้ : https://www.google.com/maps/search/?api=1&query=" + cur_lat + "," + cur_long;  // ข้อความครั้งละ 5 บาท
//            //Toast.makeText(this , " " + message , Toast.LENGTH_SHORT).show();
//
//            // ------------  send SMS   ------------------------  //
//
//            sendSMS(txtNumber.getText().toString(), message);
//
//            // ------------  send SMS   ------------------------  //
//
//        }if(NumAX >= 80 || NumAY >= 70 || NumAZ >= 70 && SumAcc >= 2.8){
//            txtDetect.setText("ตรวจสอบการล้ม");
//            MyTTS.getInstance(getApplicationContext())
//                    .setEngine("com.google.android.tts")
//                    .setLocale(new Locale("th"))
//                    .speak("ตรวจสอบการล้ม");
//
//            // ------------  send SMS   ------------------------  //
//
//            //sendSMS(txtNumber.getText().toString(), message);
//
//            // ------------  send SMS   ------------------------  //
//        }
//        /* ------------------------------  Bluetooth section --------------------------------------- */
//        try
//        {
//            //Don't leave Bluetooth sockets open when leaving activity
//            btSocket.close();
//        } catch (IOException e2) {
//            //insert code to deal with this
//        }
//        /* ------------------------------  Bluetooth section --------------------------------------- */
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSpeechManager!=null) {
            mSpeechManager.destroy();
            mSpeechManager = null;
        }
    }

    /* ------------------------------  Bluetooth section --------------------------------------- */
    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /* ------------------------------  Bluetooth section --------------------------------------- */

}
