package yio.tro.shmatoosto.game;

import com.badlogic.gdx.Gdx;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.*;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.menu.ClickDetector;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RepeatYio;

import java.util.ArrayList;

public class GameController {

    public YioGdxGame yioGdxGame;

    public int w, h;
    public int currentTouchCount;

    long currentTime;

    public PointYio touchDownPos, touchPoint;
    public DebugActionsController debugActionsController;
    public GameMode gameMode;
    public ObjectsLayer objectsLayer;
    ClickDetector clickDetector;
    public AbstractGameplayManager gameplayManager;
    public boolean actionMode;
    public int currentEntityIndex;
    public ArrayList<AbstractPlayerEntity> playerEntities;
    RepeatYio<GameController> repeatCheckAimingMode;
    public SnapshotManager snapshotManager;
    LowPerformanceDetector lowPerformanceDetector;
    GameResults gameResults;


    public GameController(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        touchDownPos = new PointYio();
        touchPoint = new PointYio();
        debugActionsController = new DebugActionsController(this);
        clickDetector = new ClickDetector();
        playerEntities = new ArrayList<>();
        gameMode = null;
        gameplayManager = null;
        snapshotManager = new SnapshotManager(this);
        gameResults = new GameResults();
        lowPerformanceDetector = new LowPerformanceDetector(this);

        initRepeats();
    }


    private void initRepeats() {
        repeatCheckAimingMode = new RepeatYio<GameController>(this, 6) {
            @Override
            public void performAction() {
                parent.checkToApplyAimingMode();
            }
        };
    }


    public boolean isHumanTurn() {
        AbstractPlayerEntity currentPlayerEntity = getCurrentPlayerEntity();
        return currentPlayerEntity != null && currentPlayerEntity.isHuman();
    }


    public AbstractPlayerEntity getCurrentPlayerEntity() {
        if (currentEntityIndex == -1) return null;

        return playerEntities.get(currentEntityIndex);
    }


    public void move() {
        currentTime = System.currentTimeMillis();

        objectsLayer.move();
        repeatCheckAimingMode.move();
        lowPerformanceDetector.move();
    }


    public void moveGameplayManager() {
        if (gameplayManager == null) return;

        gameplayManager.move();
    }


    public void defaultValues() {
        GameRules.defaultValues();
        currentTouchCount = 0;
        touchDownPos.set(0, 0);
        currentEntityIndex = YioGdxGame.random.nextInt(2) - 1; // -1 or 0

        if (DebugFlags.firstEntityAlwaysMakesFirstShot) {
            currentEntityIndex = -1;
        }

        actionMode = false;
    }


    public void applyActionMode() {
        actionMode = true;
        onApplyActionMode();
    }


    private void onApplyActionMode() {
        objectsLayer.onApplyActionMode();
    }


    public void applyAimingMode() {
        actionMode = false;
        switchCurrentEntityIndex();
        onApplyAimingMode();
    }


    public void switchCurrentEntityIndex() {
        currentEntityIndex++;
        if (currentEntityIndex > playerEntities.size() - 1) {
            currentEntityIndex = 0;
        }

        Scenes.gameOverlay.updateActiveScoreView(currentEntityIndex);
    }


    private void checkToApplyAimingMode() {
        if (!isReadyForAimingMode()) return;
        if (!actionMode) return;

        applyAimingMode();
    }


    public boolean isReadyForAimingMode() {
        return gameplayManager.isReadyForAimingMode() && objectsLayer.isReadyForAimingMode();

    }


    private void onApplyAimingMode() {
        stopAllBalls();
        gameplayManager.onApplyAimingMode();
        getCurrentPlayerEntity().onApplyAimingMode();
        objectsLayer.onApplyAimingMode();
        checkToFinishGame();
    }


    private void checkToFinishGame() {
        if (!gameplayManager.isReadyToFinishGame()) return;

        finishGame();
    }


    private void finishGame() {
        gameplayManager.updateGameResults(gameResults);

        yioGdxGame.setGamePaused(true);
        yioGdxGame.menuControllerYio.destroyGameView();

        Scenes.afterGame.create();
        Scenes.afterGame.updateLabel(gameResults);
    }


    private void stopAllBalls() {
        for (Ball ball : objectsLayer.balls) {
            ball.delta.reset();
        }
    }


    public void createMenuOverlay() {
        Scenes.gameOverlay.create();

        if (!actionMode) {
            getCurrentPlayerEntity().onApplyAimingMode();
        }

        gameplayManager.onMenuOverlayCreated();
    }


    public void createGameObjects() {
        if (objectsLayer != null) {
            objectsLayer.onDestroy();
        }
        objectsLayer = new ObjectsLayer(this);
        objectsLayer.createGameObjects();
    }


    public void debugActions() {
        debugActionsController.debugActions();
    }


    public YioGdxGame getYioGdxGame() {
        return yioGdxGame;
    }


    public void touchDown(int screenX, int screenY, int pointer, int button) {
        currentTouchCount++;
        updateTouchPoints(screenX, screenY);
        touchDownPos.setBy(touchPoint);
        clickDetector.touchDown(touchPoint);

        if (objectsLayer.touchDown(touchPoint)) return;
    }


    boolean touchedAsClick() {
        return clickDetector.isClicked();
    }


    public void updateTouchPoints(int screenX, int screenY) {
        touchPoint.x = screenX;
        touchPoint.y = screenY;
    }


    public void touchUp(int screenX, int screenY, int pointer, int button) {
        currentTouchCount--;
        if (currentTouchCount < 0) {
            currentTouchCount = 0;
            return;
        }

        updateTouchPoints(screenX, screenY);
        clickDetector.touchUp(touchPoint);
        if (objectsLayer.touchUp(touchPoint)) return;

        if (currentTouchCount == 0) {
            if (touchedAsClick()) {
                onClick();
            }
        }
    }


    private void onClick() {
        System.out.println("GameController.onClick");
    }


    public void touchDragged(int screenX, int screenY, int pointer) {
        updateTouchPoints(screenX, screenY);
        clickDetector.touchDrag(touchPoint);
        if (objectsLayer.touchDrag(touchPoint)) return;
    }


    public void onEndCreation() {
        gameplayManager.onEndCreation();
        objectsLayer.onEndCreation();

        playerEntities.clear();
        gameplayManager.createPlayerEntities();
        snapshotManager.clear();

        applyActionMode();
        objectsLayer.resetShotCounters();
    }


    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;

        switch (gameMode) {
            case soccer:
                gameplayManager = new SoccerManager(this);
                break;
            case billiard:
                gameplayManager = new BilliardManager(this);
                break;
            case chapaevo:
                gameplayManager = new ChapaevoManager(this);
                break;
        }
    }


    public void onEscapedToPauseMenu() {

    }


    public void onPause() {

    }


    public void onResume() {

    }


}
