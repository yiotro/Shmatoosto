package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class HumanSoccerEntity extends AbstractPlayerEntity{

    public HumanSoccerEntity(GameController gameController) {
        super(gameController);
    }


    @Override
    public void onApplyAimingMode() {
        Scenes.soccerAimUI.create();
        Scenes.soccerAimUI.soccerAimUiElement.setFilterColor(getCurrentColor());

        selectCurrentBalls();
    }


    public void selectCurrentBalls() {
        gameController.objectsLayer.selectBalls(getCurrentColor());
    }


    public BColorYio getCurrentColor() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        SoccerManager soccerManager = (SoccerManager) gameplayManager;

        return soccerManager.getEntityColor(this);
    }


    @Override
    public boolean isHuman() {
        return true;
    }
}
