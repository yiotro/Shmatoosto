package yio.tro.shmatoosto.menu.scenes.info;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CircleButtonYio;
import yio.tro.shmatoosto.menu.scenes.SceneYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public abstract class AbstractInfoScene extends SceneYio {


    protected ButtonYio infoPanel;
    protected CircleButtonYio backButton;
    protected RectangleYio infoLabelPosition;


    protected void initInfoLabelPosition() {
        infoLabelPosition = new RectangleYio(0.05, 0.1, 0.9, 0.7);
    }


    protected void createInfoMenu(String key, Reaction backButtonBehavior, int lines, int background_id) {
        setBackground(background_id);

        backButton = spawnBackButton(backButtonBehavior);

        initInfoLabelPosition();
        infoPanel = uiFactory.getButton()
                .setPosition(infoLabelPosition.x, infoLabelPosition.y, infoLabelPosition.width, infoLabelPosition.height)
                .setTouchable(false)
                .setAnimation(AnimationYio.center)
                .setFont(Fonts.miniFont)
                .clearText()
                .applyFixedAmountOfLines(key, lines);
    }
}
