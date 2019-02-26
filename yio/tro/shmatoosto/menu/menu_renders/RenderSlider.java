package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.slider.SliderYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderSlider extends RenderInterfaceElement {

    TextureRegion blackCircle, accentPixel, untouchableValue, untouchablePixel;
    float sliderLineHeight, sliderLineHeightHalved;
    private SliderYio sliderYio;
    private RectangleYio viewPosition;


    public RenderSlider() {
        sliderLineHeight = 0.007f * Gdx.graphics.getWidth();
        sliderLineHeightHalved = sliderLineHeight / 2;
    }


    @Override
    public void loadTextures() {
        blackCircle = GraphicsYio.loadTextureRegion("menu/slider/black_circle.png", true);
        accentPixel = GraphicsYio.loadTextureRegion("pixels/slider_accent.png", false);
        untouchableValue = GraphicsYio.loadTextureRegion("menu/slider/untouchable_slider_value.png", true);
        untouchablePixel = GraphicsYio.loadTextureRegion("pixels/blue.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        renderSlider((SliderYio) element);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    void renderSlider(SliderYio slider) {
        sliderYio = slider;
        viewPosition = slider.getViewPosition();

        checkToChangeBatchAlpha();

        renderBlackLine();
        renderAccent();
        renderSegments();
        renderValueCircle();
        renderText();

        // used only for debug
//        renderBorder();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderAccent() {
        if (!sliderYio.isAccentVisible()) return;
        batch.draw(getAccentPixel(
                sliderYio),
                viewPosition.x,
                viewPosition.y - sliderLineHeightHalved,
                sliderYio.runnerValue * viewPosition.width,
                sliderLineHeight);
    }


    private TextureRegion getAccentPixel(SliderYio sliderYio) {
        if (sliderYio.isTouchable()) {
            return accentPixel;
        } else {
            return untouchablePixel;
        }
    }


    private void renderBorder() {
        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.renderBorder(batch, getGameView().blackPixel, sliderYio.getTouchRectangle());
    }


    private void renderText() {
        if (sliderYio.getFactor().get() < 0.5) return;

        if (sliderYio.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(Fonts.gameFont, sliderYio.getFactor().get() * sliderYio.getFactor().get());
            GraphicsYio.setFontAlpha(Fonts.titleFont, sliderYio.getFactor().get() * sliderYio.getFactor().get());
        }

        Fonts.gameFont.draw(
                batch,
                sliderYio.getValueString(),
                viewPosition.x + viewPosition.width - sliderYio.textWidth,
                viewPosition.y + 0.04f * h);

        Fonts.titleFont.draw(batch, sliderYio.getName(), sliderYio.getNamePosition().x, sliderYio.getNamePosition().y);

        if (sliderYio.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(Fonts.gameFont, 1);
            GraphicsYio.setFontAlpha(Fonts.titleFont, 1);
        }
    }


    private void renderValueCircle() {
        GraphicsYio.drawFromCenter(
                batch,
                getValueCircle(sliderYio),
                sliderYio.getRunnerValueViewX(),
                viewPosition.y,
                sliderYio.circleSize);
    }


    private TextureRegion getValueCircle(SliderYio sliderYio) {
        if (sliderYio.isTouchable()) {
            return blackCircle;
        } else {
            return untouchableValue;
        }
    }


    private void checkToChangeBatchAlpha() {
        if (sliderYio.getFactor().get() == 1) return;
        batch.setColor(c.r, c.g, c.b, sliderYio.getFactor().get() * sliderYio.getFactor().get());
    }


    private void renderSegments() {
        if (!sliderYio.isSegmentsVisible()) {
            GraphicsYio.drawFromCenter(
                    batch,
                    blackCircle,
                    viewPosition.x,
                    viewPosition.y,
                    sliderYio.getSegmentCircleSize());
            GraphicsYio.drawFromCenter(
                    batch,
                    blackCircle,
                    viewPosition.x + viewPosition.width,
                    viewPosition.y,
                    sliderYio.getSegmentCircleSize());
        } else {
            for (int i = 0; i < sliderYio.numberOfSegments + 1; i++) {
                GraphicsYio.drawFromCenter(
                        batch,
                        blackCircle,
                        sliderYio.getSegmentLeftSidePos(i),
                        viewPosition.y,
                        sliderYio.getSegmentCircleSize());
            }
        }
    }


    private void renderBlackLine() {
        batch.draw(
                blackPixel,
                viewPosition.x,
                viewPosition.y - sliderLineHeightHalved,
                viewPosition.width,
                sliderLineHeight);
    }
}
