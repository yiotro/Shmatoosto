package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.game_objects.Obstacle;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderObstacles extends GameRender{


    private TextureRegion grayPixel;
    RectangleYio tempRect;


    public RenderObstacles() {
        tempRect = new RectangleYio();
    }


    @Override
    protected void loadTextures() {
        grayPixel = GraphicsYio.loadTextureRegion("pixels/gray.png", false);
    }


    @Override
    public void render() {
        for (Obstacle obstacle : gameController.objectsLayer.obstacles) {
            GraphicsYio.drawByRectangle(
                    batchMovable,
                    grayPixel,
                    obstacle.position
            );
        }
    }


    public void renderShadows() {
        for (Obstacle obstacle : gameController.objectsLayer.obstacles) {
            tempRect.setBy(obstacle.position);
            tempRect.y += 0.0125f * GraphicsYio.width;
            tempRect.increase(-0.01f * GraphicsYio.width);

            MenuRenders.renderShadow.renderShadow(
                    batchMovable,
                    tempRect,
                    1,
                    0.03f * GraphicsYio.width
            );
        }
    }


    @Override
    protected void disposeTextures() {
        grayPixel.getTexture().dispose();
    }
}
