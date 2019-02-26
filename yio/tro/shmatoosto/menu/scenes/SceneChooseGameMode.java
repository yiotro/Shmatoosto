package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

public class SceneChooseGameMode extends SceneYio {


    public ButtonYio billiardButton;
    public ButtonYio chapaevoButton;
    public ButtonYio soccerButton;


    @Override
    public void initialize() {
        setBackground(1);

        billiardButton = uiFactory.getButton()
                .setSize(0.8, 0.08)
                .setPosition(0.1, 0.52)
                .applyText(languagesManager.getString("billiard"), BackgroundYio.yellow)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.billiardMenu.create();
                    }
                })
                .setAnimation(AnimationYio.center);

        soccerButton = uiFactory.getButton()
                .clone(previousElement)
                .alignBottom(previousElement, 0.02)
                .applyText("soccer", BackgroundYio.orange)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.soccerMenu.create();
                    }
                });

        chapaevoButton = uiFactory.getButton()
                .clone(previousElement)
                .alignBottom(previousElement, 0.02)
                .applyText("chapaevo", BackgroundYio.green)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.chapaevoMenu.create();
                    }
                });

        spawnBackButton(Reaction.rbMainMenu);
    }



}
