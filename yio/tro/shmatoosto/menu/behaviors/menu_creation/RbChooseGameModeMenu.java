package yio.tro.shmatoosto.menu.behaviors.menu_creation;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class RbChooseGameModeMenu extends Reaction {

    @Override
    public void reaction() {
        Scenes.chooseGameMode.create();
    }
}
