package yio.tro.shmatoosto.menu.behaviors.menu_creation;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class RbMainMenu extends Reaction {

    @Override
    public void reaction() {
        yioGdxGame.setGamePaused(true);
        Scenes.mainMenu.create();
    }
}
