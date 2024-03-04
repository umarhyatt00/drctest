package com.yeel.drc.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class SharedPrefUtil {

    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    public SharedPrefUtil(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();

    }


    public void setEncryptedStringPreference(String key, String value) {
        if (editor != null) {
            String takkol=new String(ByteArraysTwo.PUBLIC_KEY_FOR_SHARED_PREF);
            String encryptedValue = dataOluppikkal(value, takkol);
           // String encryptedValue = dataOluppikkal(value, PUBLIC_KEY_FOR_SHARED_PREF);
            editor.putString(key, encryptedValue);
            editor.commit();
            editor.apply();
        }
    }

    public String getEncryptedStringPreference(String key, String defaultValue) {
        String thakkolOne=new String(ByteArraysTwo.PUBLIC_KEY_FOR_SHARED_PREF);
        String encryptedDefaultValue = dataOluppikkal(defaultValue, thakkolOne);
      //  String encryptedDefaultValue = dataOluppikkal(defaultValue, PUBLIC_KEY_FOR_SHARED_PREF);
        String encryptedValue = sharedPreferences.getString(key, encryptedDefaultValue);
        String thakkolTwo=new String(ByteArraysTwo.PRIVATE_KEY_FOR_SHARED_PREF);
        return dataEdukkal(encryptedValue, thakkolTwo);
        //return dataEdukkal(encryptedValue, PRIVATE_KEY_FOR_SHARED_PREF);
    }

    public void setStringPreference(String key, String value) {
        if (editor != null) {
            editor.putString(key, value);
            editor.commit();
            editor.apply();
        }
    }

    public String getStringPreference(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }


    public void setIntegerPreference(String key, int value) {
        if (editor != null) {
            editor.putInt(key, value);
            editor.commit();
            editor.apply();
        }
    }

    public int getIntegerPreference(String key) {
        return sharedPreferences.getInt(key, 0);
    }


    public void setBooleanPreference(String key, boolean value) {
        if (editor != null) {
            editor.putBoolean(key, value);
            editor.commit();
            editor.apply();
        }
    }

    public boolean getBooleanPreference(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }


    //encrypt a string using public key (RSA)
    public String dataOluppikkal(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {

        }

        return encryptedBase64.replaceAll("([\\r\\n])", "");
    }


    private String dataEdukkal(String encryptedStringTwo, String privateKey) {
        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedStringTwo, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedString;
    }


}
