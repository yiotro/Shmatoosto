package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.scenes.SceneYio;

public abstract class GameplaySceneYio extends SceneYio {


    protected ButtonYio defaultLabel;
    protected ButtonYio closeButton;


    @Override
    protected void beginCreation() {
        menuControllerYio.setCurrentScene(this);
        menuControllerYio.checkToRemoveInvisibleElements();
    }


    @Override
    protected void endInitialization() {
        super.endInitialization();

        for (InterfaceElement element : getLocalElementsList()) {
            element.setOnTopOfGameView(true);
        }
    }


    protected void initDefaultLabel(double width, double height) {
        initDefaultCloseButton();

        defaultLabel = uiFactory.getButton()
                .setSize(width, height)
                .setPosition(0, 0.08)
                .centerHorizontal()
                .setAnimation(AnimationYio.down_short);
    }


    protected void initDownsideLabel(double height) {
        initDefaultCloseButton();

        defaultLabel = uiFactory.getButton()
                .setSize(1.02, height + 0.01)
                .setPosition(0, -0.01)
                .centerHorizontal()
                .setAnimation(AnimationYio.down);
    }


    protected void initDefaultCloseButton() {
        closeButton = uiFactory.getButton()
                .setSize(1, 1)
                .setRenderable(false)
                .setAppearParameters(3, 15)
                .setDebugName("default close button")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        destroy();
                    }
                });
    }

}
