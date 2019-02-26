package yio.tro.shmatoosto.menu.elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class NotificationElement extends InterfaceElement<NotificationElement> {

    public static final int AUTO_HIDE_DELAY = 1500;

    boolean autoHide;
    public PointYio textPosition, textDelta;
    public String message;
    public BitmapFont font;
    private long timeToHide;
    float textOffset;


    public NotificationElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        autoHide = false;
        message = "";
        timeToHide = 0;
        font = Fonts.gameFont;
        textOffset = 0.03f * GraphicsYio.width;
        textPosition = new PointYio();
        textDelta = new PointYio();

        setAnimation(AnimationYio.up);
    }


    @Override
    protected NotificationElement getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();
        updateTextPosition();

        checkToDie();
    }


    @Override
    protected void updateViewPosition() {
        if (!factorMoved) return;

        viewPosition.setBy(position);

        viewPosition.y += (1 - appearFactor.get()) * 1.5f * position.height;
    }


    private void checkToDie() {
        if (autoHide && System.currentTimeMillis() > timeToHide) {
            destroy();
        }
    }


    private void updateTextPosition() {
        if (!factorMoved) return;

        textPosition.x = viewPosition.x + textDelta.x;
        textPosition.y = viewPosition.y + textDelta.y;
    }


    @Override
    public void onDestroy() {
        appearFactor.destroy(1, 3);

        autoHide = false;
    }


    @Override
    public void onAppear() {
        appearFactor.appear(3, 1);
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


    public void enableAutoHide() {
        autoHide = true;

        timeToHide = System.currentTimeMillis() + AUTO_HIDE_DELAY;
    }


    public void setMessage(String message) {
        this.message = LanguagesManager.getInstance().getString(message);

        updateTextDelta();
    }


    private void updateTextDelta() {
        textDelta.x = textOffset;
        float textHeight = GraphicsYio.getTextHeight(font, message);
        textDelta.y = position.height / 2 + textHeight / 2;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderNotificationElement;
    }
}
