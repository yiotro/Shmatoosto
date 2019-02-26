package yio.tro.shmatoosto.menu.behaviors;

import yio.tro.shmatoosto.menu.scenes.Scenes;

public class RbLanguageMenu extends Reaction {

    @Override
    protected void reaction() {
        Scenes.languages.create();
    }
}
