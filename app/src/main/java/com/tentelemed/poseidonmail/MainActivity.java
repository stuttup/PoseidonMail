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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

//    private String sharedPrefFile = "com.tentelemed.poseidonmailsharedprefs";
    private String _username;
    private String _password;
    private String _hostname;
    private String _port;
    private Boolean _smtp;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        _smtp = sharedPref.getBoolean(SettingsActivity.KEY_PREF_SWITCH_SMTP, false);
        _username = sharedPref.getString(SettingsActivity.KEY_PREF_USERNAME, "");
        _password = sharedPref.getString(SettingsActivity.KEY_PREF_PASSWORD, "");
        _hostname = sharedPref.getString(SettingsActivity.KEY_PREF_HOST, "smtp.gmail.com");
        _port = sharedPref.getString(SettingsActivity.KEY_PREF_PORT, "465");
        Toast.makeText(this, _smtp ? "SMTP configured" : "SMTP not configured", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Mail to be send using credentials of " + _username, Toast.LENGTH_SHORT).show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        createZip("myzip");
                        try {
                            MailSender sender = new MailSender(_hostname, _port, _username, _password);
                            sender.sendMail( "", getFileStreamPath("myzip"));
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    protected void onPause(){
        super.onPause();

        SharedPreferences.Editor preferencesEditor = sharedPref.edit();
        preferencesEditor.putBoolean(SettingsActivity.KEY_PREF_SWITCH_SMTP, _smtp);
        preferencesEditor.putString(SettingsActivity.KEY_PREF_HOST, _hostname);
        preferencesEditor.putString(SettingsActivity.KEY_PREF_PORT, _port);
        preferencesEditor.putString(SettingsActivity.KEY_PREF_USERNAME, _username);
        preferencesEditor.putString(SettingsActivity.KEY_PREF_PASSWORD, _password);
        preferencesEditor.apply();
    }*/

    public  void createZip(String zipFileName)
    {
        byte[] buffer = new byte[1024];
        FileOutputStream fos;
        FileOutputStream originFile;
        FileInputStream in;
        int res_id = getResources().getIdentifier("poseidon", "raw", getPackageName());

        try {
            fos = openFileOutput(zipFileName, Context.MODE_PRIVATE);
            originFile = openFileOutput("poseidon", Context.MODE_PRIVATE);
            InputStream _data = getResources().openRawResource(res_id);
            int _chunk;
            while ((_chunk = _data.read(buffer)) > 0) {
                fos.write(buffer, 0, _chunk);
                originFile.write(buffer, 0, _chunk);
            }
            File filesDir = getFilesDir();
            File poseidon = new File(filesDir, "poseidon");

            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze= new ZipEntry("poseidon");
            zos.putNextEntry(ze);

            in = new FileInputStream(poseidon);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
                Log.d("MainActivity", "writing new chunk");
            }
            in.close();

            zos.closeEntry();
            //close it
            zos.close();
            fos.close();
            Log.d("MainActivity", "Zipping done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
