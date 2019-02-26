package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class HumanBilliardEntity extends AbstractPlayerEntity{

    public HumanBilliardEntity(GameController gameController) {
        super(gameController);
    }


    @Override
    public void onApplyAimingMode() {
        Scenes.billiardAimUI.create();
    }


    @Override
    public boolean isHuman() {
        return true;
    }
}
