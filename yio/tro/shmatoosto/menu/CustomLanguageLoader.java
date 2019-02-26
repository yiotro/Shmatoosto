package yio.tro.shmatoosto.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.shmatoosto.Fonts;

public class CustomLanguageLoader {


    public static final String prefs = "shmatoosto.language";
    private static boolean autoDetect;
    private static String langName;


    public static void loadLanguage() {
        System.out.println();
        System.out.println("CustomLanguageLoader.loadLanguage");
        Preferences preferences = Gdx.app.getPreferences(prefs);

        autoDetect = preferences.getBoolean("auto", true);

        if (!autoDetect) {
            langName = preferences.getString("lang_name");
            LanguagesManager.getInstance().setLanguage(langName);
        }
    }


    public static void setAndSaveLanguage(String langName) {
        Preferences preferences = Gdx.app.getPreferences(prefs);

        preferences.putBoolean("auto", false);
        preferences.putString("lang_name", langName);

        preferences.flush();

        Fonts.initFonts(); // load language and recreate fonts
    }


}
