package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.LoadingScreenView;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class RenderLoadingScreenView extends RenderInterfaceElement{


    private LoadingScreenView loadingScreenView;
    private TextureRegion grayPixel;
    private TextureRegion progressPixel;
    private BitmapFont font;
    private TextureRegion background;


    @Override
    public void loadTextures() {
        background = GraphicsYio.loadTextureRegion("menu/loading/background.png", false);
        grayPixel = GraphicsYio.loadTextureRegion("menu/loading/gray.png", false);
        progressPixel = GraphicsYio.loadTextureRegion("menu/loading/progress.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {

    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {
        loadingScreenView = (LoadingScreenView) element;

//        if (loadingScreen.getProgress() > 0) {
//            System.out.println("RenderLoadingScreen.renderThirdLayer: " + loadingScreen.getProgress());
//        }

        GraphicsYio.setBatchAlpha(batch, loadingScreenView.getFactor().get());

        renderBlack();
        renderBackground();
        renderBand();
        renderProgress();

        GraphicsYio.setBatchAlpha(batch, 1);

        renderText();
    }


    private void renderBlack() {
        if (loadingScreenView.getFactor().isInDestroyState()) return;

        batch.draw(getGameView().blackPixel, 0, 0, GraphicsYio.width, GraphicsYio.height);
    }


    private void renderProgress() {
        if (!isProgressVisible()) return;

        GraphicsYio.drawByRectangle(
                batch,
                progressPixel,
                loadingScreenView.progressPosition
        );
    }


    private void renderBand() {
        if (!isProgressVisible()) return;

        GraphicsYio.drawByRectangle(
                batch,
                grayPixel,
                loadingScreenView.bandPosition
        );
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(
                batch,
                background,
                loadingScreenView.getViewPosition()
        );
    }


    private void renderText() {
        if (!isTextVisible()) return;

        font = loadingScreenView.font;
        GraphicsYio.setFontAlpha(font, loadingScreenView.getFactor().get() * loadingScreenView.getFactor().get());
//        font.setColor(Color.WHITE);

        font.draw(batch, loadingScreenView.text, loadingScreenView.textPosition.x, loadingScreenView.textPosition.y);

//        font.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(font, 1);
    }


    private boolean isTextVisible() {
        return loadingScreenView.getFactor().get() > 0.5;
    }


    private boolean isProgressVisible() {
        return loadingScreenView.getFactor().get() > 0.9;
    }
}
