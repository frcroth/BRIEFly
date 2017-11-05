package com.briefly.nicolai.de.briefly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class registrieren extends AppCompatActivity {

    EditText name, passwort;
    ImageButton registrieren, login;

    private String Name, Passwort, Zwischenspeicher, BenutzervonDatenbank = "";
    private Intent intentMain;
    private Boolean namevorhanden = false, loginfehlgeschlagen = true;

    private String[] zs, passwort2;

    DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference Duser = RootRef.child("user");

    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrieren);
        name = (EditText)findViewById(R.id.Name);
        passwort = (EditText)findViewById(R.id.Passwort);
        registrieren = (ImageButton)findViewById(R.id.Registrieren);
        login = (ImageButton)findViewById(R.id.Login);
        Name = ((EditText)findViewById(R.id.Name)).getText().toString();
        Passwort = ((EditText)findViewById(R.id.Passwort)).getText().toString();
        intentMain = new Intent(this, MainActivity.class);
        editor = MainActivity.username.edit();


        registrieren.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                registrieren2();
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                login();
            }
        });
    }

    public void registrieren2() {
        Name = ((EditText)findViewById(R.id.Name)).getText().toString();
        Passwort = ((EditText)findViewById(R.id.Passwort)).getText().toString();
        if (!Name.equals("") && !Passwort.equals("")) {
            zs = BenutzervonDatenbank.split(";");
            for (int i = 0; i < zs.length; i++) {
                passwort2 = zs[i].split("-");
                if (Name.equals(passwort2[0])) {
                    namevorhanden = true;
                }
            }

            if (namevorhanden) {
                Toast.makeText(this, "Der Name ist bereits vorhanden!", Toast.LENGTH_SHORT).show();
            } else {
                Zwischenspeicher = BenutzervonDatenbank;  //a-a;b-b;c-c;
                Zwischenspeicher = Zwischenspeicher + Name + "-" + Passwort + ";";
                Duser.setValue(Zwischenspeicher);
                Toast.makeText(this, "Registration erfolgreich!", Toast.LENGTH_SHORT).show();
                editor.putString("Name", Name);
                editor.apply();
                startActivity(intentMain);
            }
        }else{
            Toast.makeText(this, "Eingabe fehlt!", Toast.LENGTH_SHORT).show();
        }
    }

    public void login(){
        Name = ((EditText)findViewById(R.id.Name)).getText().toString();
        Passwort = ((EditText)findViewById(R.id.Passwort)).getText().toString();
        if (!Name.equals("") && !Passwort.equals("")) {
            zs = BenutzervonDatenbank.split(";");
            for (int i = 0; i < zs.length; i++) {
                passwort2 = zs[i].split("-");
                if (Name.equals(passwort2[0])) {
                    if (Passwort.equals(passwort2[1])) {
                        editor.putString("Name", Name);
                        editor.apply();
                        Toast.makeText(this, "Login erfolgreich.", Toast.LENGTH_SHORT).show();
                        loginfehlgeschlagen = false;
                        startActivity(intentMain);
                    }
                }
            }
            if (loginfehlgeschlagen){
                Toast.makeText(this, "Login fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Eingabe fehlt!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Duser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BenutzervonDatenbank = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

