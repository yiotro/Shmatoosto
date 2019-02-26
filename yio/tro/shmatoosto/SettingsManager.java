package yio.tro.shmatoosto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SettingsManager {

    private static SettingsManager instance;
    public boolean soundEnabled;
    public boolean fullScreenMode;
    public boolean requestRestartApp;
    public boolean aimAssistEnabled;
    public boolean lowGraphics;


    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }

        return instance;
    }


    public static void initialize() {
        instance = null;
    }


    public void saveValues() {
        Preferences prefs = Gdx.app.getPreferences("shmatoosto.settings");

        prefs.putBoolean("sound", soundEnabled);
        prefs.putBoolean("full_screen", fullScreenMode);
        prefs.putBoolean("aim_assist", aimAssistEnabled);
        prefs.putBoolean("low_graphics", lowGraphics);

        prefs.flush();
    }


    public void loadValues() {
        Preferences prefs = Gdx.app.getPreferences("shmatoosto.settings");

        soundEnabled = prefs.getBoolean("sound", true);
        fullScreenMode = prefs.getBoolean("full_screen", true);
        aimAssistEnabled = prefs.getBoolean("aim_assist", false);
        lowGraphics = prefs.getBoolean("low_graphics", false);
    }

}
