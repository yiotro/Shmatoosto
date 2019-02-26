package yio.tro.shmatoosto.game.view.game_renders.debug;

import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapObjectYio;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapYio;

import java.util.ArrayList;

public class RenderPosMap extends GameRender {

    RectangleYio temp;


    public RenderPosMap() {
        temp = new RectangleYio();
    }


    @Override
    protected void loadTextures() {

    }


    @Override
    public void render() {
        if (!DebugFlags.showPosMap) return;

        PosMapYio posMap = getPosMap();

        GraphicsYio.setBatchAlpha(batchMovable, 0.1);

        for (int i = 0; i < posMap.width; i++) {
            for (int j = 0; j < posMap.height; j++) {
                ArrayList<PosMapObjectYio> sector = posMap.getSector(i, j);
                if (!isSectorVisible(sector)) continue;

                temp.x = posMap.mapPos.x + i * posMap.sectorSize;
                temp.y = posMap.mapPos.x + j * posMap.sectorSize;
                temp.width = posMap.sectorSize;
                temp.height = posMap.sectorSize;

                GraphicsYio.drawByRectangle(batchMovable, gameView.blackPixel, temp);
            }
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private PosMapYio getPosMap() {
        return gameController.objectsLayer.posMap;
    }


    private boolean isSectorVisible(ArrayList<PosMapObjectYio> sector) {
        for (PosMapObjectYio posMapObjectYio : sector) {
            if (posMapObjectYio instanceof Ball) {
                return true;
            }
        }

        return false;
    }


    @Override
    protected void disposeTextures() {

    }
}
