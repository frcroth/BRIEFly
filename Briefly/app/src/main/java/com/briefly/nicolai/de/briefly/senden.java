package com.briefly.nicolai.de.briefly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class senden extends AppCompatActivity {

    EditText An, Text;
    Button Senden;

    private String userlist = "", messages = "", empfänger, endempfänger, inhalt, Empfänger;
    final String message = "Sie haben eine neue Nachricht von " + MainActivity.username.getString("Name", "");
    private Boolean nameok = true;
    Intent main;

    private String[] zs, zs2;

    DatabaseReference mDatenbank = FirebaseDatabase.getInstance().getReference();
    DatabaseReference Dmessages = mDatenbank.child("messages");
    DatabaseReference Duser = mDatenbank.child("user");
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senden);
        An = (EditText)findViewById(R.id.Adresse);
        Text = (EditText)findViewById(R.id.Nachricht);
        Senden = (Button)findViewById(R.id.Antworten);
        main = new Intent(this, MainActivity.class);
        editor = MainActivity.freundesliste.edit();
        start();


        Senden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senden2();
            }
        });
    }

    public void start(){
        if (!MainActivity.Anzeigeübergabe.equals("")){
            An.setText("An: " + MainActivity.Anzeigeübergabe);
        }else{
            nameok = false;
        }
    }

    public void senden2(){
        empfänger = An.getText().toString();
        inhalt = Text.getText().toString();
        zs = userlist.split(";");
        for (int i = 0; i < zs.length; i++) {
            zs2 = zs[i].split("-");
            if (empfänger.equals(zs2[0])) {
                endempfänger = empfänger;
                messages = messages + MainActivity.username.getString("Name", "") + "-" + empfänger + "-" + inhalt + ";";
                Dmessages.setValue(messages);
                //Zur Freundesliste hinzufügen
                editor.putString("Freunde", MainActivity.Anzeigeübergabe);
                editor.apply();
                //send PN
                Empfänger = endempfänger;
                sendNotification();
                startActivity(main);

            } else {
                nameok = false;
            }
        }
        if (!nameok){
            Toast.makeText(this, "Der Name existiert nicht.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendNotification() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic MzQzODFlMDQtMjkzZC00ZTUzLWFmMTctYTg3OWI3ODIwOTc0");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"dd9089dc-679e-464f-91c1-b5b744db669d\","
                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + Empfänger + "\"}],"
                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"" + message + "\"}"//message wird gesendet an empfänger
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        Duser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
