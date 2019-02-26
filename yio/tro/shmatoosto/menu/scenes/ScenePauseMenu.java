package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

public class ScenePauseMenu extends SceneYio {


    public ButtonYio resumeButton;
    public ButtonYio restartButton;
    public ButtonYio mainMenuButton;


    @Override
    public void initialize() {
        setBackground(3);

        resumeButton = uiFactory.getButton()
                .setSize(0.8, 0.08)
                .setPosition(0.1, 0.55)
                .applyText("resume")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        yioGdxGame.gameView.appear();
                        gameController.createMenuOverlay();
                        yioGdxGame.setGamePaused(false);
                    }
                })
                .setAnimation(AnimationYio.center);

        restartButton = uiFactory.getButton()
                .setSize(0.8, 0.08)
                .centerHorizontal()
                .alignBottom(previousElement, 0.02)
                .applyText("restart")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        yioGdxGame.loadingManager.startInstantly(GameRules.initialLoadingType, GameRules.initialParameters);
                    }
                })
                .setAnimation(AnimationYio.center);

        mainMenuButton = uiFactory.getButton()
                .setSize(0.8, 0.08)
                .centerHorizontal()
                .alignBottom(previousElement, 0.02)
                .applyText("main_menu")
                .setReaction(Reaction.rbMainMenu)
                .setAnimation(AnimationYio.center)
                .tagAsBackButton();
    }
}
