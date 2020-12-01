package com.topmngr.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Locale;

/**
 * Created by PROFYAN on 16.04.2017.
 */
public class Settings {

    public static final Settings instance = new Settings();

    public ObjectMap<String,String> languages;
    private Preferences preferences;
    public boolean all_set = false;
   // public String language = Locale.getDefault().getLanguage(); // TODO сделать английскую локализацию
    public float soundVolume =0.3f;
    public float musicVolume = 0.4f;

    private Settings(){
        languages = new ObjectMap<String, String>();
        languages.put("en","ENGLISH");
        languages.put("ru","RUSSIAN");
        Gdx.app.debug("Settings","Locale language = "+ Locale.getDefault().getLanguage());
    }
    public void loadSettings(){
        preferences = Gdx.app.getPreferences("settings");

        all_set = preferences.getBoolean("all_set");
        soundVolume = preferences.getFloat("soundVolume");
        musicVolume = preferences.getFloat("musicVolume");
        //language = preferences.getString("language");
        if(!all_set) {
            soundVolume = 0.8f;
            musicVolume = 0.8f;
            //language = Locale.getDefault().getLanguage();
        }

        Gdx.app.debug("Settings","soundVolume = "+ soundVolume);
        Gdx.app.debug("Settings","musicVolume = "+musicVolume);
        //Gdx.app.debug("Settings","language = "+ language);
        Gdx.app.debug("Settings","all_set = "+ all_set);
        //Locale.setDefault(new Locale(language));
    }
    public void saveSettings(){
        preferences.putFloat("soundVolume", soundVolume);
        preferences.putFloat("musicVolume",musicVolume);
        preferences.putBoolean("all_set", all_set);
        //preferences.putString("language", language);
        preferences.flush();
    }
}
