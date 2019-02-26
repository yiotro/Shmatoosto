package yio.tro.shmatoosto.game;

import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;

public class GameRules {

    public static LoadingParameters initialParameters;
    public static LoadingType initialLoadingType;
    public static boolean aiOnly;
    public static boolean twoPlayerMode;
    public static boolean frozenMode; // should be enabled
    public static boolean infiniteMode;
    public static boolean obstaclesEnabled;
    public static int goalsLimit;
    public static int difficulty;


    public static void bootInit() {
        initialParameters = null;
        initialLoadingType = null;
        defaultValues();
    }


    static void defaultValues() {
        aiOnly = false;
        twoPlayerMode = false;
        frozenMode = true;
        difficulty = Difficulty.EASY;
        infiniteMode = false;
        goalsLimit = -1;
    }


}
