package com.crypho.plugins;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class SharedPreferencesHandler {
    private SharedPreferences prefs;

    public SharedPreferencesHandler(String prefsName, Context ctx) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            prefs = EncryptedSharedPreferences.create(
                    prefsName + "_SS",
                    masterKeyAlias,
                    ctx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SharedPreferenceHandler", "Error creating EncryptedSharedPreferences", e);
            prefs = null;
        }
    }

    SharedPreferences getPrefs() {
        return prefs;
    }

    void store(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_SS_" + key, value);
        editor.apply();
    }

    String fetch(String key) {
        return prefs.getString("_SS_" + key, null);
    }

    void remove(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("_SS_" + key);
        editor.apply();
    }

    Set<String> keys() {
        Set<String> res = new HashSet<>();
        Iterator<String> iter = prefs.getAll().keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (key.startsWith("_SS_") && !key.startsWith("_SS_MIGRATED_")) {
                res.add(key.replaceFirst("^_SS_", ""));
            }
        }
        return res;
    }

    void clear() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
