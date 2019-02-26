package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SceneExceptionReport extends SceneYio {


    private ButtonYio label;
    public ButtonYio okButton;


    public void createReport(Exception exception) {
//        yioGdxGame.setGamePaused(true);
//        yioGdxGame.gameView.destroy();

        create();
        updateLabel(exception);
    }


    private void updateLabel(Exception exception) {
        int lineNumber = 30;

        ArrayList<String> text = new ArrayList<String>();
        String title = "Error : " + exception.toString();
        text.add(title);
        text.add(" ");

        String temp;
        for (int i = 0; i < exception.getStackTrace().length; i++) {
            temp = exception.getStackTrace()[i].toString();
            StringBuilder builder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(temp, ".");
            while (tokenizer.hasMoreTokens()) {
                builder.append(tokenizer.nextToken()).append(". ");
            }
            text.add(builder.toString());
        }

        label.clearText();
        label.applyText(text);
        label.updateTextDeltas();
    }


    @Override
    public void initialize() {
        setBackground(4); // black

        label = uiFactory.getButton()
                .setSize(1, 1)
                .centerHorizontal()
                .centerVertical()
                .setBorder(false)
                .setTouchable(false)
                .setFont(Fonts.miniFont)
                .setAnimation(AnimationYio.center);

        okButton = uiFactory.getButton()
                .setParent(label)
                .setSize(0.3, 0.05)
                .setTouchOffset(0.05)
                .alignRight()
                .applyText("Ok")
                .setReaction(Reaction.rbExit)
                .tagAsBackButton();
    }
}
