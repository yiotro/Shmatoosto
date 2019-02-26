package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.menu.MenuViewYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public abstract class RenderInterfaceElement {

    protected MenuViewYio menuViewYio;
    protected SpriteBatch batch;
    protected Color c;
    public int w, h;
    protected TextureRegion blackPixel;


    public RenderInterfaceElement() {
        MenuRenders.list.listIterator().add(this);
    }


    void update(MenuViewYio menuViewYio) {
        this.menuViewYio = menuViewYio;
        batch = menuViewYio.batch;
        c = batch.getColor();
        w = menuViewYio.w;
        h = menuViewYio.h;
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
        loadTextures();
    }


    public abstract void loadTextures();


    public abstract void renderFirstLayer(InterfaceElement element);


    public abstract void renderSecondLayer(InterfaceElement element);


    public abstract void renderThirdLayer(InterfaceElement element);


    protected void renderShadow(RectangleYio rectangle, float factor) {
//        batch.setColor(c.r, c.g, c.b, 0.5f * factor);

        MenuRenders.renderShadow.renderShadow(rectangle, factor);

//        batch.setColor(c.r, c.g, c.b, 1);
    }


    protected void renderDebug(InterfaceElement interfaceElement) {
        GraphicsYio.renderBorder(batch, blackPixel, interfaceElement.getViewPosition(), 0.01f * GraphicsYio.width);
    }


    public GameView getGameView() {
        return menuViewYio.yioGdxGame.gameView;
    }
}
