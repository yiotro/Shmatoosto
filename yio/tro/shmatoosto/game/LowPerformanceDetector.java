package yio.tro.shmatoosto.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import yio.tro.shmatoosto.SettingsManager;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.RepeatYio;

public class LowPerformanceDetector {


    GameController gameController;
    public static final int TARGET_FPS = 60;
    public static final int ALLOWED_DELTA = 4;
    int measures[];
    int currentIndex;
    RepeatYio<LowPerformanceDetector> repeatUpdate;
    boolean alreadyAppliedOnce;


    public LowPerformanceDetector(GameController gameController) {
        this.gameController = gameController;

        currentIndex = 0;

        measures = new int[4];
        for (int i = 0; i < measures.length; i++) {
            measures[i] = TARGET_FPS;
        }

        repeatUpdate = new RepeatYio<LowPerformanceDetector>(this, 60) {
            @Override
            public void performAction() {
                parent.updateMeasures();
            }
        };

        alreadyAppliedOnce = getPrefs().getBoolean("applied", false);
    }


    private Preferences getPrefs() {
        return Gdx.app.getPreferences("yio.tro.shmat.low_performance");
    }


    private void updateMeasures() {
        if (SettingsManager.getInstance().lowGraphics) return; // already enabled
        if (alreadyAppliedOnce) return;

        measures[currentIndex] = Math.min(Gdx.graphics.getFramesPerSecond(), TARGET_FPS);

        currentIndex++;
        if (currentIndex >= measures.length) {
            currentIndex = 0;
        }

        if (isLowPerformanceDetected()) {
            enableLowGraphics();
        }
    }


    private void enableLowGraphics() {
        alreadyAppliedOnce = true;
        Preferences prefs = getPrefs();
        prefs.putBoolean("applied", true);
        prefs.flush();

        SettingsManager.getInstance().lowGraphics = true;
        SettingsManager.getInstance().saveValues();

        Scenes.notification.show("low_graphics_applied");
    }


    private boolean isLowPerformanceDetected() {
        for (int i = 0; i < measures.length; i++) {
            if (Math.abs(measures[i] - TARGET_FPS) <= ALLOWED_DELTA) {
                return false;
            }
        }

        return true;
    }


    public void move() {
        repeatUpdate.move();
    }
}
