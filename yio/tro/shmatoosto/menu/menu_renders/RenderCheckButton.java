package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.elements.CheckButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class RenderCheckButton extends RenderInterfaceElement {

    TextureRegion activeTexture;
    private BitmapFont font;
    private CheckButtonYio checkButton;


    @Override
    public void loadTextures() {
        activeTexture = GraphicsYio.loadTextureRegion("menu/check_button/chk_active.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        checkButton = (CheckButtonYio) element;
        font = Fonts.gameFont;

        if (checkButton.getFactor().get() < 0.4) return;

        renderSelection();
        renderActiveSquare();
        renderText();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderText() {
        if (checkButton.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(font, checkButton.getFactor().get());
        }

        font.draw(batch, checkButton.getText(), checkButton.getTextPosition().x, checkButton.getTextPosition().y);

        if (checkButton.getFactor().get() < 1) {
            GraphicsYio.setFontAlpha(font, 1);
        }
    }


    private void renderActiveSquare() {
        if (checkButton.getFactor().get() < 1) {
            GraphicsYio.setBatchAlpha(batch, checkButton.getFactor().get());
        }
        GraphicsYio.renderBorder(batch, blackPixel, checkButton.getActiveSquare());

        if (checkButton.activeFactor.get() > 0) {
            GraphicsYio.setBatchAlpha(batch, Math.min(checkButton.activeFactor.get(), checkButton.getFactor().get()));
            GraphicsYio.drawByRectangle(batch, activeTexture, checkButton.getActiveSquare());
        }
    }


    private void renderSelection() {
        if (checkButton.selectionFactor.get() > 0) {
            GraphicsYio.setBatchAlpha(batch, 0.2 * checkButton.selectionFactor.get());
            GraphicsYio.drawByRectangle(batch, blackPixel, checkButton.getViewPosition());
            GraphicsYio.setBatchAlpha(batch, 1);
        }
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
