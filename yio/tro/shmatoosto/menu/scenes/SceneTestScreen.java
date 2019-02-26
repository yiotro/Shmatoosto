package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;

public class SceneTestScreen extends SceneYio{

    @Override
    protected void initialize() {
        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.mainMenu.create();
            }
        });

        uiFactory.getButton()
                .setSize(0.8, 0.06)
                .alignTop(0.2)
                .centerHorizontal()
                .applyText("Button", BackgroundYio.green)
                .setAnimation(AnimationYio.down_short)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        System.out.println("SceneTestScreen.reaction");
                    }
                });

        uiFactory.getTestRoundRectElement()
                .setSize(0.8, 0.3)
                .alignBottom(previousElement, 0.1)
                .centerHorizontal()
                .setAnimation(AnimationYio.down_short);
    }
}
