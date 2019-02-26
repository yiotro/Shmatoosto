package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.BtItem;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;

public class RenderButton extends RenderInterfaceElement {

    float x1, y1; // local variables for rendering
    // yes, this is strange, but this class is really old

    TextureRegion buttonPixel, blackPixel;
    private ButtonYio buttonYio;
    private RectangleYio viewPos;
    private RectangleYio touchArea;
    private float f;


    public RenderButton() {
        touchArea = new RectangleYio();
    }


    @Override
    public void loadTextures() {
        buttonPixel = GraphicsYio.loadTextureRegion("pixels/button.png", false);
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {
        buttonYio = (ButtonYio) element;
        viewPos = buttonYio.getViewPosition();
        f = buttonYio.getFactor().get();

        if (!buttonYio.isForcedShadow()) {
            renderButtonShadow();
        }
    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        buttonYio = (ButtonYio) element;
        if (!buttonYio.isRenderable()) return;
        f = buttonYio.getFactor().get();

        viewPos = buttonYio.getViewPosition();

        if (buttonYio.isForcedShadow()) {
            renderButtonShadow();
        }

        renderSingleButton();

        if (buttonYio.isVisible() && buttonYio.hasBorder()) {
//            renderBorder();
        }
    }


    public void renderBorder() {
        if (buttonYio.getFactor().get() < 1) {
            batch.setColor(c.r, c.g, c.b, buttonYio.getFactor().get() * f);
        }
        GraphicsYio.renderBorder(batch, blackPixel, viewPos);
        batch.setColor(c.r, c.g, c.b, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }


    public void renderSingleButton() {
        if (buttonYio.onlyShadow) return;
        if (!buttonYio.isVisible()) return;

        updateBatchAlpha();

        if (buttonYio.getTextureRegion() != null) {
            GraphicsYio.drawByRectangle(batch, buttonYio.getTextureRegion(), viewPos);
        } else {
            MenuRenders.renderRoundShape.renderRoundShape(viewPos, buttonYio.getBackground());
        }

//        renderTouchArea();

        if (buttonYio.isSelected()) {
            renderButtonSelection();
        }

        renderButtonItems();
    }


    private void renderButtonItems() {
        if (f < 0.5) return;

        GraphicsYio.setFontAlpha(buttonYio.font, (f - 0.5) * 2);

        for (BtItem item : buttonYio.items) {
            buttonYio.font.draw(batch, item.string, item.position.x, item.position.y);
        }

        GraphicsYio.setFontAlpha(buttonYio.font, 1);
    }


    private void renderTouchArea() {
        updateTouchArea();

        GraphicsYio.renderBorder(batch, blackPixel, touchArea);
    }


    private void updateTouchArea() {
        RectangleYio pos = buttonYio.getViewPosition();
        touchArea.x = pos.x - buttonYio.getHorizontalTouchOffset();
        touchArea.width = pos.width + 2 * buttonYio.getHorizontalTouchOffset();
        touchArea.y = pos.y - buttonYio.getVerticalTouchOffset();
        touchArea.height = pos.height + 2 * buttonYio.getVerticalTouchOffset();
    }


    private void updateBatchAlpha() {
        if (f <= 1) {
            batch.setColor(c.r, c.g, c.b, f);
        } else {
            batch.setColor(c.r, c.g, c.b, 1);
        }
    }


    public void renderButtonSelection() {
        if (buttonYio.isInSilentReactionMode()) return;

        batch.setColor(c.r, c.g, c.b, Math.min(0.25f * buttonYio.selectionFactor.get(), f * f));

        if (buttonYio.selectionTexture == null) {
//            GraphicsYio.drawByRectangle(batch, buttonPixel, viewPos);
            MenuRenders.renderRoundShape.renderRoundShape(viewPos, BackgroundYio.black);
        } else {
            GraphicsYio.drawFromCenter(
                    batch,
                    buttonYio.selectionTexture,
                    viewPos.x + viewPos.width / 2,
                    viewPos.y + viewPos.height / 2,
                    viewPos.width
            );
        }

        batch.setColor(c.r, c.g, c.b, 1);
    }


    public void renderButtonShadow() {
        if (!buttonYio.isVisible()) return;
        if (!buttonYio.shadowEnabled) return;
        if (f < 0.5) return;

        MenuRenders.renderShadow.renderShadow(viewPos, 2 * (f - 0.5f));
    }

}
