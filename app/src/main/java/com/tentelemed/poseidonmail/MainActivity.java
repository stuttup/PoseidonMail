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

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createZip("myzip");


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
                            MailSender sender = new MailSender(_username, _password);
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


    public  void createZip(String zipFileName)
    {
        byte[] buffer = new byte[1024];
        String fileContents = "Hello world!";
        FileOutputStream fos;
        FileInputStream in;

        try {
            fos = openFileOutput(zipFileName, Context.MODE_PRIVATE);
            fos.write(fileContents.getBytes());
            fos.close();

            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze= new ZipEntry("poseidon");
            zos.putNextEntry(ze);
            in = (FileInputStream) getResources().openRawResource(R.raw.poseidon);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

            Log.i("MainActivity", "Zipping done");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
