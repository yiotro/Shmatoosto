package yio.tro.shmatoosto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import yio.tro.shmatoosto.game.debug.DebugFlags;

public class SoundManager {

    public static Sound button;
    public static Sound backButton;
    public static Sound collBallsQuiet;
    public static Sound collBallsNormal;
    public static Sound collBallsLoud;
    public static Sound collWallQuiet;
    public static Sound collWallLoud;
    public static Sound score;
    public static Sound fall;
    public static Sound testEnded;
    public static SoundThreadYio soundThreadYio;


    public static void loadSounds() {
        soundThreadYio = new SoundThreadYio();
        soundThreadYio.start();
        soundThreadYio.setPriority(1);

        button = loadSound("button");
        backButton = loadSound("back");

        collBallsQuiet = loadSound("coll_balls_quiet");
        collBallsNormal = loadSound("coll_balls_normal");
        collBallsLoud = loadSound("coll_balls_loud");
        collWallQuiet = loadSound("coll_wall_quiet");
        collWallLoud = loadSound("coll_wall_loud");
        score = loadSound("score");
        fall = loadSound("fall1");
        testEnded = loadSound("test_ended");
    }


    private static Sound loadSound(String name) {
        return Gdx.audio.newSound(Gdx.files.internal("sound/" + name + ".ogg"));
    }


    public static void playSound(Sound sound) {
        soundThreadYio.playSound(sound);
    }


    public static void playSoundDirectly(Sound sound) {
        if (!SettingsManager.getInstance().soundEnabled) return;
        if (DebugFlags.testingModeEnabled) return;

        sound.play();
    }
}
