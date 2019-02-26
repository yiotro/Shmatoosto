package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameResults;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.view.game_renders.AbstractRenderBoard;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;

public abstract class AbstractGameplayManager {

    public GameController gameController;


    public AbstractGameplayManager(GameController gameController) {
        this.gameController = gameController;
    }


    public abstract BackgroundYio getBackground();


    public abstract void onEndCreation();


    public abstract void move();


    public abstract boolean isReadyToFinishGame();


    public abstract void updateGameResults(GameResults gameResults);


    public abstract AbstractRenderBoard getBoardRender();


    public abstract void createPlayerEntities();


    public void onBallFellToHole(Ball ball) {

    }


    public void onMenuOverlayCreated() {

    }


    public abstract void onApplyAimingMode();


    protected ObjectsLayer getObjectsLayer() {
        return gameController.objectsLayer;
    }


    public boolean isReadyForAimingMode() {
        return true;
    }


    public int getDefaultScoreDelta() {
        return 0;
    }

}
