package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.menu.elements.gameplay.ChapaevoAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SceneChapaevoAimUI extends GameplaySceneYio{


    public ChapaevoAimUiElement chapaevoAimUiElement;


    @Override
    protected void initialize() {
        chapaevoAimUiElement = uiFactory.getChapaevoAimUiElement()
                .setSize(1, 1 - GameView.TOP_BEZEL_HEIGHT);
    }


    @Override
    protected void onEndCreation() {
        super.onEndCreation();

        Scenes.gameOverlay.forceElementsToTop();
    }


    @Override
    public void onLevelCreationEnd() {
        super.onLevelCreationEnd();

        if (chapaevoAimUiElement != null) {
            chapaevoAimUiElement.resetAimAngle();
        }
    }

}
