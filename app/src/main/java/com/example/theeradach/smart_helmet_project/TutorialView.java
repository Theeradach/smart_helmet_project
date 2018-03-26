package com.example.theeradach.smart_helmet_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Color;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;


public class TutorialView extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tutorial_view);

        // slide 1
        addFragment(new Step.Builder().setTitle("ยินดีต้อนรับสู่ Smart Helmet")
                .setContent("Smart Helmet เป็นแอพพลิเคชั่นที่จัดทำขึ้นมาเพื่อให้ผู้ขับขี่รถจักรยาน" +
                        "มีความสะดวกและปลอดภัยมากยิ่งขึ้นขณะขับขี่  แอพพลิเคชั่น มีฟังก์ชั่นรองรับการใช้งาน" +
                        "ต่าง ๆ โดยจะอธิบายดังต่อไปนี้")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.helmet) // int top drawable
                .build());


        // slide 2
        addFragment(new Step.Builder().setTitle("ฟังก์ชั่นการสั่งการโดยใช้เสียง")
                .setContent("ขั้นแรกทำการกดที่ สวิตช์ ที่เมนู Voice Command เพื่อเปิดไมค์ให้พร้อมรับคำสั่งเสียง" +
                        "ดังนี้  \n > โทรหา + <ชื่อในรายการ> \n >   รับสาย   /   วางสาย \n >  นำทางไปยัง + <สถานที่> \n >  สถานที่ปัจจุบัน   \n > ส่งข้อความฉุกเเฉิน   " )
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.voice_command) // int top drawable
                .build());

        // slide 3
        addFragment(new Step.Builder().setTitle("การโทรออก ")
                .setContent("ฟังก์ชั่นนี้ สามารถโทรออกไปยังเบอร์ที่อยู่ในรายการชื่อได้ โดยพูดว่า " +
                        "\"โทรหา \" พร้อมกับ \"ชื่อผู้ที่ต้องการโทรหา\" ตัวอย่างดังนี้ \"โทรหา ธีรเดช\"")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.call_out) // int top drawable
                .build());

        // slide 4
        addFragment(new Step.Builder().setTitle("การนำทาง ")
                .setContent("ฟังก์ชั่นนี้ สามารถนำทางไปยังปลายทางที่ต้องการได้ โดยพูดว่า \"นำทางไปยัง \" พร้อมกับ \"สถานที่ที่ต้องการไป\" ตัวอย่างดังนี้  \n \" นำทางไปยัง ฟิวเจอร์พาร์ครังสิต\"")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.navigator) // int top drawable
                .build());

        // slide 5
        addFragment(new Step.Builder().setTitle("การรับสายเรียกเข้า")
                .setContent("ฟังก์ชั่นนี้ สามารถรับสายเรียกเข้าได้ โดยพูดว่า \"รับสาย\" ก็จะรับสายเรียกเข้าอัติโนมัติ")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.accept_and_reject_incomming_call) // int top drawable
                .build());

        // slide 6
        addFragment(new Step.Builder().setTitle("การปฏิเสธสายเรียกเข้า")
                .setContent("ฟังก์ชั่นนี้ สามารถรับสายเรียกเข้าได้ โดยพูดว่า \"วางสาย\" ก็จะปฎิเสธสายเรียกเข้าอัติโนมัติ")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.accept_and_reject_incomming_call) // int top drawable
                .build());

        // slide 7
        addFragment(new Step.Builder().setTitle("แจ้งเตือนเมื่อเกิดอุบัติเหตุ")
                .setContent("การใช้ฟังก็ชั่นนี้ ให้ผู้ใช้เรียก กดที่ปุ่มเลือกรายชื่อ ในเมนู Emergency Sms เพื่อเลือกรายชื่อ" +
                        "ของผู้ที่ต้องการให้แจ้งเตือนไปเมื่อเกิดอุบัติเหตุ")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.crash_detection) // int top drawable
                .build());

        // slide 8
        addFragment(new Step.Builder().setTitle("แจ้งเตือนเมื่อแบตเตอรี่ต่ำ")
                .setContent("การใช้ฟังก์ชั่นนี้ ให้ผู้ใช้กำหนดเปอร์เซนต์ของแบตเตอรี่้ที่ต้องการให้แจ้งเตือนผ่านเสียง" +
                        "เมื่อแบตเตอรี่อยู่ในระดับต่ำกว่าที่กำหนด")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setDrawable(R.drawable.low_battery) // int top drawable
                .build());
    }
}

