package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CircleButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.CurrentEntityViewElement;
import yio.tro.shmatoosto.menu.elements.gameplay.ScoreViewElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class SceneGameOverlay extends GameplaySceneYio {

    public ButtonYio pauseMenuButton;
    public ScoreViewElement scoreViewElement;
    public CurrentEntityViewElement currentEntityViewElement;


    @Override
    protected void beginCreation() {
        menuControllerYio.setCurrentScene(this);
        destroyAllVisibleElements();
        menuControllerYio.checkToRemoveInvisibleElements();
    }


    @Override
    public void initialize() {
        pauseMenuButton = uiFactory.getButton()
                .setSize(GraphicsYio.convertToWidth(0.05))
                .alignRight()
                .alignTop()
                .setTouchOffset(0.05)
                .loadTexture("menu/pause_menu_icon.png")
                .setReaction(Reaction.rbPauseMenu)
                .setAnimation(AnimationYio.up)
                .setKey("pause_menu")
                .setSelectionTexture(GraphicsYio.loadTextureRegion("menu/selection.png", true))
                .tagAsBackButton();

        scoreViewElement = uiFactory.getScoreViewElement()
                .setSize(0.5)
                .centerHorizontal()
                .alignTop()
                .setAppearParameters(6, 0.8)
                .setAnimation(AnimationYio.up);

        currentEntityViewElement = uiFactory.getCurrentEntityViewElement()
                .setSize(0.4, 0.05)
                .alignLeft(0.03)
                .alignTop(0.01)
                .setAppearParameters(6, 0.8)
                .setAnimation(AnimationYio.up);
    }


    public void updateScore(int entityIndex, int score) {
        if (scoreViewElement == null) return;

        scoreViewElement.updateScore(entityIndex, score);
    }


    public void updateActiveScoreView(int index) {
        if (scoreViewElement == null) return;

        scoreViewElement.updateActiveView(index);
    }


    @Override
    public void onLevelCreationEnd() {
        super.onLevelCreationEnd();

        if (scoreViewElement != null) {
            scoreViewElement.onGameStarted();
        }
    }
}
