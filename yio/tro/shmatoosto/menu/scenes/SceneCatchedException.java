package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

public class SceneCatchedException extends SceneYio{

    Exception exception;
    private ButtonYio label;
    private ButtonYio okButton;


    @Override
    protected void initialize() {
        setBackground(1);

        label = uiFactory.getButton()
                .setSize(0.95, 0.3)
                .centerHorizontal()
                .centerVertical()
                .setFont(Fonts.miniFont)
                .applyManyLines("catched_exception", 2)
                .setAnimation(AnimationYio.center)
                .setTouchable(false);

        okButton = uiFactory.getButton()
                .setParent(label)
                .setSize(0.3, 0.05)
                .setTouchOffset(0.05)
                .alignRight()
                .applyText("show_more")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.exceptionReport.createReport(exception);
                    }
                })
                .tagAsBackButton();
    }


    public void setException(Exception exception) {
        this.exception = exception;
    }
}
