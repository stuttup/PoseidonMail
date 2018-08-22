package com.tentelemed.poseidonmail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String fileContents = "{\n" +
                "\"androidId\":\"75dc39513954b9dc\", \n" +
                "\"installId\" : \"8a42bb3f-ea1b-46a0-ada9-751ae4f5e758\", \n" +
                "\"timestamp\":\"1517482485\", \n" +
                "\"patientFirstName\":\"JOHNNY\", \n" +
                "\"patientLastName\":\"ZAZA\", \n" +
                "\"gender\":\"0\", \n" +
                "\"age\":\"29\", \n" +
                "\"bodyType\":\"2\", \n" +
                "\"complementaryInfo\":\"Just a test\", \n" +
                "\"measures\":\n" +
                "    [{\"timestamp\":\"144947928(........................................................................................)F0YT4NCiAgICAgICAgPC9FdmVudD4NCiAgICA8L0V2ZW50cz4NCjwvRUNHPg==\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"2\",\"source\":\"1\",\"value\":\"100\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"4\",\"source\":\"1\",\"value\":\"100\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"1\",\"source\":\"0\",\"value\":\"88\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"2\",\"source\":\"0\",\"value\":\"89\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"0\",\"source\":\"0\",\"value\":\"154\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"7\",\"source\":\"6\",\"value\":\"119\"},\n" +
                "    {\"timestamp\":\"1449479280286\",\"type\":\"3\",\"source\":\"2\",\"value\":\"31.5\"}]\n" +
                "}\n";

        writeFileOnInternalStorage(getApplicationContext(),"myfile", fileContents);



        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SWITCH_SMTP, false);
        final String _username = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
        final String _password = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
        String _hostname = sharedPref.getString(SettingsActivity.KEY_PREF_HOST, "smtp.gmail.com");
//        int _port = sharedPref.getInt(SettingsActivity.KEY_PREF_PORT, 465);
        Toast.makeText(this, switchPref ? "SMTP configured" : "SMTP not configured", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Mail to be send using credentials of " + _username, Toast.LENGTH_SHORT).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MailSender sender = new MailSender("", "");
                            sender.sendMail( "cni@tentelemed.com", getFileStreamPath("myfile"));
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }
                }).start();
                Snackbar.make(view, "Email sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void writeFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){
        File file = new File(mcoContext.getFilesDir(),"mydir");
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
