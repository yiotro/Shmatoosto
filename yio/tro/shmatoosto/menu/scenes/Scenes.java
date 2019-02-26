package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.menu.scenes.gameplay.*;
import yio.tro.shmatoosto.menu.scenes.info.help.*;
import yio.tro.shmatoosto.menu.scenes.info.SceneAboutGame;
import yio.tro.shmatoosto.menu.scenes.info.SceneSpecialThanks;
import yio.tro.shmatoosto.menu.scenes.options.SceneLanguages;
import yio.tro.shmatoosto.menu.scenes.options.SceneSettingsMenu;

public class Scenes {

    public static SceneMainMenu mainMenu;
    public static SceneAboutGame aboutMenu;
    public static SceneChooseGameMode chooseGameMode;
    public static SceneGameOverlay gameOverlay;
    public static ScenePauseMenu pauseMenu;
    public static SceneNotification notification;
    public static SceneExceptionReport exceptionReport;
    public static SceneSettingsMenu settingsMenu;
    public static SceneHelpMenu helpMenu;
    public static SceneSpecialThanks specialThanks;
    public static SceneLanguages languages;
    public static SceneCatchedException catchedException;
    public static SceneLoadingScreen loadingScreen;
    public static SceneTestScreen testScreen;
    public static SceneBilliardMenu billiardMenu;
    public static SceneBilliardAimUI billiardAimUI;
    public static SceneDebugTests debugTests;
    public static SceneTestResults testResults;
    public static SceneSecretScreen secretScreen;
    public static SceneAfterGame afterGame;
    public static SceneChapaevoMenu chapaevoMenu;
    public static SceneSoccerMenu soccerMenu;
    public static SceneChapaevoAimUI chapaevoAimUI;
    public static SceneAiOnlyGameMenu aiOnlyGameMenu;
    public static SceneSoccerAimUI soccerAimUI;
    public static SceneSecondarySoccerAimUI secondarySoccerAimUI;


    public static void createAllScenes() {
        mainMenu = new SceneMainMenu();
        aboutMenu = new SceneAboutGame();
        chooseGameMode = new SceneChooseGameMode();
        gameOverlay = new SceneGameOverlay();
        pauseMenu = new ScenePauseMenu();
        notification = new SceneNotification();
        exceptionReport = new SceneExceptionReport();
        settingsMenu = new SceneSettingsMenu();
        helpMenu = new SceneHelpMenu();
        specialThanks = new SceneSpecialThanks();
        languages = new SceneLanguages();
        catchedException = new SceneCatchedException();
        loadingScreen = new SceneLoadingScreen();
        testScreen = new SceneTestScreen();
        billiardMenu = new SceneBilliardMenu();
        billiardAimUI = new SceneBilliardAimUI();
        debugTests = new SceneDebugTests();
        testResults = new SceneTestResults();
        secretScreen = new SceneSecretScreen();
        afterGame = new SceneAfterGame();
        chapaevoMenu = new SceneChapaevoMenu();
        soccerMenu = new SceneSoccerMenu();
        chapaevoAimUI = new SceneChapaevoAimUI();
        aiOnlyGameMenu = new SceneAiOnlyGameMenu();
        soccerAimUI = new SceneSoccerAimUI();
        secondarySoccerAimUI = new SceneSecondarySoccerAimUI();
    }
}
