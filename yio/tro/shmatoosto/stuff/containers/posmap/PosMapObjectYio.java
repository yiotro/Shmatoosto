package yio.tro.shmatoosto.stuff.containers.posmap;

import yio.tro.shmatoosto.stuff.PointYio;

public abstract class PosMapObjectYio {

    protected PointYio posMapPosition;
    PmSectorIndex lastIndexPoint, indexPoint;


    public PosMapObjectYio() {
        lastIndexPoint = new PmSectorIndex();
        indexPoint = new PmSectorIndex();
        posMapPosition = new PointYio();
    }


    protected abstract void updatePosMapPosition();
}
