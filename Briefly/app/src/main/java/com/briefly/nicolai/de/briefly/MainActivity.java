package com.briefly.nicolai.de.briefly;
/*
allgemeine Informationen:
packagename:          com.briefly.nicolai.de.briefly
ShA1:                 1A:49:8F:42:E0:3A:F8:C9:88:2B:A7:AD:14:68:38:9A:89:9F:86:81
API key:              AAAAqmjgYao:APA91bHAIcAPfw3s_Kw_9pjviGX-Jf9UpRwWl3ugOghA8_a9cespxNtr2vlU_yAhSkUns9Lp-i0QG5RonCuHO3QuTemMkA2HJ2BkzV7EfTfXH3QyCNLyxTKp5H4xml-Ai_8C8MUE63gQ
Sender ID:            731903975850
OneSignal code:       93ebca7b-fe1d-4d52-8610-a23caa6e08cd
*/
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    ListView lv;
    Button newkontakt;

    static String Anzeigeübergabe = "";
    static String Nachicht;
    Intent registrieren, senden;

    String[] zs;
    ArrayList<String> LV = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    DatabaseReference mDatenbank = FirebaseDatabase.getInstance().getReference();
    DatabaseReference Dmessages = mDatenbank.child("message");
    static SharedPreferences username;
    static SharedPreferences freundesliste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.startInit(this).init();
        tv = (TextView)findViewById(R.id.Text);
        lv = (ListView)findViewById(R.id.listview);
        newkontakt = (Button)findViewById(R.id.nk);
        adapter = new ArrayAdapter<String>(this, R.layout.textview, LV);
        username = getSharedPreferences("Name", 0);
        freundesliste = getSharedPreferences("Freunde", 0);
        registrieren = new Intent(this, registrieren.class);
        senden = new Intent (this, senden.class);
        start();




        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Anzeigeübergabe = (String) parent.getItemAtPosition(position);
                startActivity(senden);
            }
        });
        newkontakt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newkontakt();
            }
        });
    }

    public void start(){
        if (username.getString("Name", "").equals("")) {
            startActivity(registrieren);
        }
        zs = freundesliste.getString("Freunde", "").split(";");
        for (int i = 0; i < zs.length; i++){
            LV.add(zs[i]);
        }
        Collections.sort(LV);
        lv.setAdapter(adapter);
        OneSignal.sendTag("User_ID", username.getString("Name", ""));
    }

    public void newkontakt(){
        Anzeigeübergabe = "";
        startActivity(senden);
    }

    protected void onStart() {
        super.onStart();
        Dmessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Nachicht = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

}
