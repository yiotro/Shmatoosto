package yio.tro.shmatoosto.game.view.game_renders.debug;

import yio.tro.shmatoosto.game.game_objects.CollisionLine;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.stuff.GraphicsYio;

import java.util.ArrayList;

public class RenderCollisionLines extends GameRender {


    private ArrayList<CollisionLine> collisionLines;


    @Override
    protected void loadTextures() {

    }


    @Override
    public void render() {
        collisionLines = gameController.objectsLayer.collisionLines;

        for (CollisionLine collisionLine : collisionLines) {
            GraphicsYio.drawLine(batchMovable, gameView.blackPixel, collisionLine.one, collisionLine.two, 2 * GraphicsYio.borderThickness);
        }
    }


    @Override
    protected void disposeTextures() {

    }
}
