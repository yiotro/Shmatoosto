package yio.tro.shmatoosto.menu.behaviors.menu_creation;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class RbHelpMenu extends Reaction {

    @Override
    protected void reaction() {
        Scenes.helpMenu.create();
    }
}
