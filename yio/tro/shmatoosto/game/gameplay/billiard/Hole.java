package yio.tro.shmatoosto.game.gameplay.billiard;

import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapObjectYio;

public class Hole extends PosMapObjectYio {

    public CircleYio position;


    public Hole() {
        position = new CircleYio();
    }


    @Override
    protected void updatePosMapPosition() {
        posMapPosition.setBy(position.center);
    }
}
