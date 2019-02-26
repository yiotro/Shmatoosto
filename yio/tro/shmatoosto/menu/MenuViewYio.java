package yio.tro.shmatoosto.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.shmatoosto.*;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.Masking;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class MenuViewYio {

    public YioGdxGame yioGdxGame;
    MenuControllerYio menuControllerYio;
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public int w, h;


    public MenuViewYio(YioGdxGame yioGdxGame) {
        this.yioGdxGame = yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
        shapeRenderer = new ShapeRenderer();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        update();
    }


    public void update() {
        batch = yioGdxGame.batch;
        MenuRenders.updateRenderSystems(this);
    }


    public void renderAll(boolean onTopOfGameView) {
        batch.begin();

        // first layer
        for (InterfaceElement element : menuControllerYio.visibleElements) {
            if (!element.isVisible()) continue;
            if (!element.compareGvStatus(onTopOfGameView)) continue;
            element.getRenderSystem().renderFirstLayer(element);
        }

        // second layer
        for (InterfaceElement element : menuControllerYio.visibleElements) {
            if (!element.isVisible()) continue;
            if (!element.compareGvStatus(onTopOfGameView)) continue;
            element.getRenderSystem().renderSecondLayer(element);
        }

        // third layer
        for (InterfaceElement element : menuControllerYio.visibleElements) {
            if (!element.isVisible()) continue;
            if (!element.compareGvStatus(onTopOfGameView)) continue;
            element.getRenderSystem().renderThirdLayer(element);
        }

        renderDebug();

        GraphicsYio.setBatchAlpha(batch, 1);
        batch.end();
    }




    private void renderDebug() {

    }
}
