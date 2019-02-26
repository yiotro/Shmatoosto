package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;

import java.util.ArrayList;

public class SceneTestResults extends SceneYio{

    ArrayList<String> text;
    private ButtonYio label;


    public SceneTestResults() {
        text = new ArrayList<>();
    }


    @Override
    protected void initialize() {
        setBackground(2);

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.debugTests.create();
            }
        });

        label = uiFactory.getButton()
                .setSize(0.8, 0.5)
                .centerHorizontal()
                .centerVertical()
                .applyText(text)
                .setTouchable(false);
    }


    @Override
    protected void onAppear() {
        super.onAppear();
        yioGdxGame.beginBackgroundChange(backgroundIndex);
        yioGdxGame.beginBackgroundChange(backgroundIndex);
        label.applyText(text);
        label.updateTextDeltas();
    }


    public void clearText() {
        text.clear();
    }


    public void addTextLine(String s) {
        text.add(s);
    }


    public void setText(ArrayList<String> src) {
        text.clear();
        text.addAll(src);

        finishText();
    }


    public void finishText() {
        while (text.size() < 10) {
            text.add(" ");
        }
    }
}
