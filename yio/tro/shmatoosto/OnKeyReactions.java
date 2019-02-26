package yio.tro.shmatoosto;

import com.badlogic.gdx.Input;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.gameplay.Snapshot;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CircleButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class OnKeyReactions {

    YioGdxGame yioGdxGame;
    MenuControllerYio menuControllerYio;
    GameController gameController;
    private Snapshot snapshot;


    public OnKeyReactions(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        gameController = yioGdxGame.gameController;
        snapshot = null;
    }


    public void keyDown(int keycode) {
        for (InterfaceElement element : menuControllerYio.getInterfaceElements()) {
            if (!element.isVisible()) continue;

            if (element.onKeyDown(keycode)) {
                return; // key press was stolen by ui element
            }
        }

        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            onBackPressed();
            return;
        }

        if (keycode == Input.Keys.ENTER) {
            onEnterPressed();
            return;
        }

        if (keycode == Input.Keys.SPACE) {
            onSpacePressed();
            return;
        }

        checkOtherStuff(keycode);

        checkForDebugCreationStuff(keycode);

        return;
    }


    private void checkForDebugCreationStuff(int keycode) {

    }


    private void onSpacePressed() {
        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        if (billiardAimUiElement == null) return;

        billiardAimUiElement.applyTargetShot(1, billiardAimUiElement.aimAngle);
        billiardAimUiElement.forceFirstStageOfAutoShot();
    }


    private void checkOtherStuff(int keycode) {
        switch (keycode) {
            case Input.Keys.D:
                gameController.debugActions(); // debug
                break;
            case Input.Keys.T:
                yioGdxGame.setGamePaused(true);
                yioGdxGame.gameView.destroy();
                Scenes.testScreen.create();
                break;
            case Input.Keys.L:
                yioGdxGame.setGamePaused(true);
                yioGdxGame.gameView.destroy();
                LoadingParameters loadingParameters = new LoadingParameters();
                loadingParameters.addParameter("balls", 15);
                loadingParameters.addParameter("ai_only", false);
                loadingParameters.addParameter("2_player", false);
                loadingParameters.addParameter("difficulty", Difficulty.HARD);
                loadingParameters.addParameter("infinite_game", false);
                yioGdxGame.loadingManager.startInstantly(LoadingType.billiard, loadingParameters);
                break;
            case Input.Keys.NUM_0:
                yioGdxGame.setGamePaused(true);
                yioGdxGame.gameView.destroy();
                Scenes.debugTests.create();
                break;
            case Input.Keys.C:
                yioGdxGame.setGamePaused(true);
                yioGdxGame.gameView.destroy();
                Scenes.secretScreen.create();
                break;
            case Input.Keys.U:
                snapshot = gameController.snapshotManager.takeSnapshot();
                Scenes.notification.show("Snapshot taken");
                break;
            case Input.Keys.I:
                if (snapshot != null) {
                    snapshot.recreate();
                } else {
                    Scenes.notification.show("Snapshot is null");
                }
                break;
            case Input.Keys.F:
                switchFrozenMode();
                break;
            case Input.Keys.S:
                switchSlowMo();
                break;
            case Input.Keys.A:
                gameController.debugActionsController.doSlightlyAdjustAimByAi();
                break;
        }
    }


    private void switchSlowMo() {
        yioGdxGame.sloMo = !yioGdxGame.sloMo;
    }


    private void switchFrozenMode() {
        GameRules.frozenMode = !GameRules.frozenMode;

        if (GameRules.frozenMode) {
            Scenes.notification.show("Frozen mode enabled");
        } else {
            Scenes.notification.show("Frozen mode disabled");
        }
    }


    private void onEnterPressed() {
        pressElementIfVisible(Scenes.mainMenu.playButton);
        pressElementIfVisible(Scenes.chooseGameMode.soccerButton);
        pressElementIfVisible(Scenes.pauseMenu.resumeButton);
        pressElementIfVisible(Scenes.exceptionReport.okButton);
        pressElementIfVisible(Scenes.billiardMenu.startButton);
        pressElementIfVisible(Scenes.chapaevoMenu.startButton);
        pressElementIfVisible(Scenes.soccerMenu.startButton);
    }


    private void onBackPressed() {
        for (int i = menuControllerYio.getInterfaceElements().size() - 1; i >= 0; i--) {
            InterfaceElement interfaceElement = menuControllerYio.getInterfaceElements().get(i);
            if (!interfaceElement.isVisible()) continue;
            if (interfaceElement.getFactor().get() < 0.95) continue;

            if ((interfaceElement instanceof ButtonYio)) {
                ButtonYio buttonYio = (ButtonYio) interfaceElement;
                if (!buttonYio.isReturningBackButton()) continue;

                buttonYio.pressArtificially();
                break;
            }

            if ((interfaceElement instanceof CircleButtonYio)) {
                CircleButtonYio buttonYio = (CircleButtonYio) interfaceElement;
                if (!buttonYio.isReturningBackButton()) continue;

                buttonYio.pressArtificially();
                break;
            }
        }
    }


    boolean pressElementIfVisible(InterfaceElement element) {
        if (element == null) return false;
        if (!element.isVisible()) return false;
        if (element.getFactor().get() < 0.95) return false;
        element.pressArtificially();
        return true;
    }
}
