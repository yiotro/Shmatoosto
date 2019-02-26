package yio.tro.shmatoosto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class OneTimeInfo {

    private static OneTimeInfo instance;

    public boolean syncComplete;
    public boolean aboutSandboxTasks;


    public static OneTimeInfo getInstance() {
        if (instance == null) {
            instance = new OneTimeInfo();
        }

        return instance;
    }


    public static void initialize() {
        instance = null;
    }


    private Preferences getPreferences() {
        return Gdx.app.getPreferences("shmatoosto.oneTimeInfo");
    }


    public void load() {
        Preferences preferences = getPreferences();
        syncComplete = preferences.getBoolean("sync_complete", false);
        aboutSandboxTasks = preferences.getBoolean("about_sandbox_tasks", false);
    }


    public void save() {
        Preferences preferences = getPreferences();
        preferences.putBoolean("sync_complete", syncComplete);
        preferences.putBoolean("about_sandbox_tasks", aboutSandboxTasks);
        preferences.flush();
    }
}
