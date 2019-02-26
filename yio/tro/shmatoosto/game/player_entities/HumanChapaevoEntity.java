package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class HumanChapaevoEntity extends AbstractPlayerEntity{

    public HumanChapaevoEntity(GameController gameController) {
        super(gameController);
    }


    @Override
    public void onApplyAimingMode() {
        Scenes.chapaevoAimUI.create();
        Scenes.chapaevoAimUI.chapaevoAimUiElement.setFilterColor(getCurrentColor());

        selectCurrentBalls();
    }


    public void selectCurrentBalls() {
        gameController.objectsLayer.selectBalls(getCurrentColor());
    }


    public BColorYio getCurrentColor() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        ChapaevoManager chapaevoManager = (ChapaevoManager) gameplayManager;

        return chapaevoManager.getEntityColor(this);
    }


    @Override
    public boolean isHuman() {
        return true;
    }


    @Override
    public String toString() {
        return "[HumanChapaevoEntity: " +
                getCurrentColor() +
                "]";
    }
}
