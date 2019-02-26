package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.NotificationElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class RenderNotificationElement extends RenderInterfaceElement {

    private TextureRegion backgroundTexture;
    private NotificationElement notificationElement;
    private BitmapFont font;


    @Override
    public void loadTextures() {
        backgroundTexture = GraphicsYio.loadTextureRegion("menu/button_backgrounds/green.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        notificationElement = (NotificationElement) element;
        font = notificationElement.font;

        renderShadow(notificationElement.getViewPosition(), 1);
        renderBackground();
        renderMessage();
    }


    private void renderMessage() {
        Color color = font.getColor();
        font.setColor(Color.BLACK);
        GraphicsYio.setFontAlpha(font, notificationElement.getFactor().get());

        font.draw(
                batch,
                notificationElement.message,
                notificationElement.textPosition.x,
                notificationElement.textPosition.y
        );

        GraphicsYio.setFontAlpha(font, 1);
        font.setColor(color);
    }


    private void renderBackground() {

        GraphicsYio.drawByRectangle(
                batch,
                backgroundTexture,
                notificationElement.getViewPosition()
        );
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
