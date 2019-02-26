package yio.tro.shmatoosto.menu.scenes.gameplay;

import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;

public class SceneBilliardAimUI extends GameplaySceneYio{


    public BilliardAimUiElement billiardAimUiElement;


    @Override
    protected void initialize() {
        billiardAimUiElement = uiFactory.getBilliardAimUI()
                .setSize(1, 1 - GameView.TOP_BEZEL_HEIGHT);
    }


    public void applyTargetShot(double powerFraction, double angle) {
        if (billiardAimUiElement == null) return;

        billiardAimUiElement.applyTargetShot(powerFraction, angle);
    }


    @Override
    protected void onEndCreation() {
        super.onEndCreation();

        Scenes.gameOverlay.forceElementsToTop();
    }


    @Override
    public void onLevelCreationEnd() {
        super.onLevelCreationEnd();

        if (billiardAimUiElement != null) {
            billiardAimUiElement.resetAimAngle();
        }
    }
}
