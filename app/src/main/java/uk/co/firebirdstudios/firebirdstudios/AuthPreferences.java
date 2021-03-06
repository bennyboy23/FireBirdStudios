package uk.co.firebirdstudios.firebirdstudios;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LOCALE = "locale";
    private SharedPreferences preferences;
    /*the following code has been taken from
    http://blog.tomtasche.at/2013/05/google-oauth-on-android-using.html an
    and has been modified for this app
    this is java class handles all requests to and from the sharedpreferences
     */
    public AuthPreferences(Context context) {
        preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void setUser(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.commit();
    }

    public void setToken(String password) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, password);
        editor.commit();
    }
    public void notLoggedIn(){
        Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn",false);
        editor.commit();
    }
    public void loggedIn(){
        Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn",true);
        editor.commit();
    }
    public boolean isLoggedIn(){return preferences.getBoolean("isLoggedIn",true);}
    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void clearUser() {
        Editor editor = preferences.edit();
        editor.remove(KEY_TOKEN);
        editor.commit();
    }

    public void setLocale(String locale) {
        Editor editor = preferences.edit();
        editor.putString(KEY_LOCALE, locale);
        editor.commit();
    }

    public String getLocale() {
        return preferences.getString(KEY_LOCALE, "en");
    }

    public void setEquipment(String equipment) {

        Editor editor = preferences.edit();
        editor.putString("equipment", equipment);
        editor.commit();

    }
    public String getEquipment(){return preferences.getString("equipment","");}
}