package yio.tro.shmatoosto.menu.scenes.info.help;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.scenes.SceneYio;

public class SceneHelpMenu extends SceneYio {


    @Override
    protected void initialize() {
        setBackground(1);

        spawnBackButton(Reaction.rbMainMenu);
    }
}
