package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.ResizableBorder;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class RenderResizableBorder extends RenderInterfaceElement {


    private TextureRegion redPixel;
    private ResizableBorder resizableBorder;
    private final float borderLineThickness;
    private float helpLineThickness;
    private TextureRegion cornerCircle;
    private float textOffset;
    private RectangleYio position;


    public RenderResizableBorder() {
        helpLineThickness = 0.002f * GraphicsYio.width;
        if (helpLineThickness < 1) helpLineThickness = 1;

        borderLineThickness = 0.01f * GraphicsYio.width;

        textOffset = 0.01f * GraphicsYio.width;
    }


    @Override
    public void loadTextures() {
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
        cornerCircle = GraphicsYio.loadTextureRegion("menu/slider/black_circle.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        resizableBorder = (ResizableBorder) element;
        position = resizableBorder.getPosition();

        renderCorners();

        checkToChangeAlpha();
        renderHelpLines();
        renderBorder();
        renderText();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderText() {
        BitmapFont font = Fonts.gameFont;

        renderTextForCornerA(font);
        renderTextForCornerC(font);

        renderMicroText();
    }


    private void renderMicroText() {
        BitmapFont microFont = Fonts.miniFont;

        String string1 = "" + Yio.roundUp(1 - (position.x + position.width) / GraphicsYio.width, 2);
        microFont.draw(batch, string1, position.x + position.width + textOffset, position.y + position.height - textOffset);

        String string2 = "" + Yio.roundUp(1 - (position.y + position.height) / GraphicsYio.height, 2);
        float textWidth = GraphicsYio.getTextWidth(microFont, string2);
        float textHeight = GraphicsYio.getTextHeight(microFont, string2);
        microFont.draw(batch, string2, position.x + position.width - textOffset - textWidth, position.y + position.height + textOffset + textHeight);
    }


    private void renderTextForCornerC(BitmapFont font) {
        double x = Yio.roundUp(position.width / GraphicsYio.width, 2);
        double y = Yio.roundUp(position.height / GraphicsYio.height, 2);
        String string = x + ", " + y;
        float textWidth = GraphicsYio.getTextWidth(font, string);
        font.draw(
                batch,
                string,
                position.x + position.width - textOffset - textWidth,
                position.y + position.height - textOffset);
    }


    private void renderTextForCornerA(BitmapFont font) {
        double x = Yio.roundUp(position.x / GraphicsYio.width, 2);
        double y = Yio.roundUp(position.y / GraphicsYio.height, 2);
        String string = x + ", " + y;
        float textHeight = GraphicsYio.getTextHeight(font, string);
        font.draw(batch, string, position.x + textOffset, position.y + textOffset + textHeight);
    }


    private void renderCorners() {
        GraphicsYio.setBatchAlpha(batch, 0.1);

        for (ResizableBorder.Corner corner : resizableBorder.getCorners()) {
            GraphicsYio.drawFromCenter(batch, cornerCircle, corner.position.x, corner.position.y, corner.touchDistance);
        }

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderHelpLines() {
        GraphicsYio.drawLine(
                batch, getHelpLinePixel(0, 1), position.x,
                0,
                position.x,
                GraphicsYio.height,
                helpLineThickness
        );

        GraphicsYio.drawLine(
                batch, getHelpLinePixel(2, 3), position.x + position.width,
                0,
                position.x + position.width,
                GraphicsYio.height,
                helpLineThickness
        );

        GraphicsYio.drawLine(
                batch, getHelpLinePixel(0, 3), 0,
                position.y,
                GraphicsYio.width,
                position.y,
                helpLineThickness
        );

        GraphicsYio.drawLine(
                batch, getHelpLinePixel(1, 2), 0,
                position.y + position.height,
                GraphicsYio.width,
                position.y + position.height,
                helpLineThickness
        );
    }


    private TextureRegion getHelpLinePixel(int corner1, int corner2) {
        if (resizableBorder.isCornerSelected(corner1)) {
            return redPixel;
        }

        if (resizableBorder.isCornerSelected(corner2)) {
            return redPixel;
        }

        return blackPixel;
    }


    private void checkToChangeAlpha() {
        FactorYio factor = resizableBorder.getFactor();
        if (factor.get() < 1) {
            GraphicsYio.setBatchAlpha(batch, factor.get());
        }
    }


    private void renderBorder() {
        GraphicsYio.renderBorder(batch, getBorderPixel(), position, borderLineThickness);
    }


    private TextureRegion getBorderPixel() {
        if (resizableBorder.getSelectMode() == ResizableBorder.SELECT_MAIN) {
            return redPixel;
        }

        return blackPixel;
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
