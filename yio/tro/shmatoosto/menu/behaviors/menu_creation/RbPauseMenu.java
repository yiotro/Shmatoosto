package yio.tro.shmatoosto.menu.behaviors.menu_creation;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class RbPauseMenu extends Reaction {

    @Override
    public void reaction() {
        yioGdxGame.setGamePaused(true);
        gameController.onEscapedToPauseMenu();
        menuControllerYio.destroyGameView();

        Scenes.pauseMenu.create();
    }
}
