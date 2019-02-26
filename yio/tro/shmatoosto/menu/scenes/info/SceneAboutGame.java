package yio.tro.shmatoosto.menu.scenes.info;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SceneAboutGame extends AbstractInfoScene {


    private ButtonYio helpButton;
    private ButtonYio specialThanksButton;


    @Override
    protected void initInfoLabelPosition() {
        super.initInfoLabelPosition();
        infoLabelPosition.y = 0.05f;
        infoLabelPosition.height = 0.8f;
    }


    @Override
    public void initialize() {
        createInfoMenu("about_game", Reaction.rbMainMenu, 16, 1);

//        helpButton = uiFactory.getButton()
//                .setPosition(0.55, 0.9, 0.4, 0.07)
//                .renderText("help")
//                .setReaction(Reaction.rbHelpMenu)
//                .setAnimation(AnimationYio.up);

//        specialThanksButton = uiFactory.getButton()
//                .setSize(0.45, 0.05)
//                .setParent(infoPanel)
//                .centerHorizontal()
//                .alignBottom(0.01)
//                .setFont(Fonts.miniFont)
//                .applyText("special_thanks_title")
//                .setReaction(new Reaction() {
//                    @Override
//                    protected void reaction() {
//                        Scenes.specialThanks.create();
//                    }
//                });
    }
}
