package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.menu.elements.gameplay.SoccerAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SceneSoccerAimUI extends GameplaySceneYio{


    public SoccerAimUiElement soccerAimUiElement;


    @Override
    protected void initialize() {
        soccerAimUiElement = uiFactory.getSoccerAimUiElement()
                .setSize(1, 1 - GameView.TOP_BEZEL_HEIGHT)
                .setAppearParameters(1, 50)
                .setHoldMode(true);
    }


    @Override
    protected void onEndCreation() {
        super.onEndCreation();

        Scenes.gameOverlay.forceElementsToTop();
    }


    @Override
    public void onLevelCreationEnd() {
        super.onLevelCreationEnd();

        if (soccerAimUiElement != null) {
            soccerAimUiElement.resetAimAngle();
        }
    }
}
