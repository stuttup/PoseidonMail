package com.tentelemed.poseidonmail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_SWITCH_SMTP = "switch_smtp";
    public static final String KEY_PREF_USERNAME = "pref_key_username";
    public static final String KEY_PREF_PASSWORD = "pref_key_password";
    public static final String KEY_PREF_HOST = "pref_key_host";
    public static final String KEY_PREF_PORT = "pref_key_host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
