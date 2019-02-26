package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;

public abstract class AbstractPlayerEntity {


    GameController gameController;


    public AbstractPlayerEntity(GameController gameController) {
        this.gameController = gameController;
    }


    public abstract void onApplyAimingMode();


    protected Ball findCue() {
        for (Ball ball : gameController.objectsLayer.balls) {
            if (!ball.isCue()) continue;

            return ball;
        }

        return null;
    }


    public abstract boolean isHuman();


    public int getIndex() {
        return gameController.playerEntities.indexOf(this);
    }
}
