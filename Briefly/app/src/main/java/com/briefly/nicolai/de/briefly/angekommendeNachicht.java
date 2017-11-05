package com.briefly.nicolai.de.briefly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class angekommendeNachicht extends AppCompatActivity {

    TextView Adresse, Nachichteninhalt;
    Button Antworten;

    Intent senden;

    private String[] zs, zs2, zwischenspeicher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angekommende_nachicht);
        Antworten = (Button)findViewById(R.id.Antworten);
        Adresse = (TextView)findViewById(R.id.Adresse);
        Nachichteninhalt = (TextView)findViewById(R.id.Nachricht);
        senden = new Intent(this, senden.class);
        start();

        Antworten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                antworten();
            }
        });
    }

    public void start(){
        zs = MainActivity.Nachicht.split(";");
        for (int i = 0; i < zs.length; i++){
            zs2 = zs[i].split("-");
            if (zs2[1].equals(MainActivity.username.getString("Name", ""))){
                zwischenspeicher = zs[i].split("-");
                Adresse.setText(zwischenspeicher[0]);
                Nachichteninhalt.setText(zwischenspeicher[2]);
            }
        }
    }

    public void antworten(){
        MainActivity.Anzeigeübergabe = zwischenspeicher[0];
        startActivity(senden);
    }
}