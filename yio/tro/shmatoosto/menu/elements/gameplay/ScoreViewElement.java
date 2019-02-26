package yio.tro.shmatoosto.menu.elements.gameplay;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;

import java.util.ArrayList;

public class ScoreViewElement extends InterfaceElement<ScoreViewElement> {


    public ArrayList<SvView> svViews;
    float vWidth, vHeight, centerOffset, topOffset;


    public ScoreViewElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        svViews = new ArrayList<>();
        initMetrics();
        initViews();
    }


    private void initMetrics() {
        centerOffset = 0.03f * GraphicsYio.width;
        vHeight = 0.035f * GraphicsYio.height;
        vWidth = 0.1f * GraphicsYio.width;
        topOffset = (float) ((GameView.TOP_BEZEL_HEIGHT * GraphicsYio.height - vHeight) / 2);
    }


    private void initViews() {
        SvView view1 = new SvView();
        view1.position.set(
                GraphicsYio.width / 2 - centerOffset - vWidth,
                GraphicsYio.height - topOffset - vHeight,
                vWidth,
                vHeight
        );
        svViews.add(view1);

        SvView view2 = new SvView();
        view2.position.set(
                GraphicsYio.width / 2 + centerOffset,
                GraphicsYio.height - topOffset - vHeight,
                vWidth,
                vHeight
        );
        svViews.add(view2);

        for (SvView svView : svViews) {
            svView.setFont(Fonts.miniFont);
        }
    }


    public void onGameStarted() {
        for (SvView svView : svViews) {
            svView.setTextValue(0);
        }
    }


    public void updateScore(int entityIndex, int score) {
        svViews.get(entityIndex).setTextValue(score);
    }


    @Override
    protected ScoreViewElement getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();
        moveViews();
    }


    private void moveViews() {
        for (SvView svView : svViews) {
            svView.move();
        }
    }


    @Override
    public void onDestroy() {
        for (SvView svView : svViews) {
            svView.destroy();
        }
    }


    @Override
    public void onAppear() {
        for (SvView svView : svViews) {
            svView.appear();
        }
    }


    public void updateActiveView(int index) {
        for (SvView svView : svViews) {
            svView.active = false;
        }

        svViews.get(index).active = true;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean touchDown() {
        return false;
    }


    @Override
    public boolean touchDrag() {
        return false;
    }


    @Override
    public boolean touchUp() {
        return false;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderScoreView;
    }
}
