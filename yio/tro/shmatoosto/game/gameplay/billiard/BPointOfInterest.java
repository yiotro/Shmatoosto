package yio.tro.shmatoosto.game.gameplay.billiard;

import yio.tro.shmatoosto.stuff.PointYio;

public class BPointOfInterest {


    public PointYio position;
    public double angleFromCenter;


    public BPointOfInterest() {
        position = new PointYio();
        angleFromCenter = 0;
    }


    public void setAngleFromCenter(double angleFromCenter) {
        this.angleFromCenter = angleFromCenter;
    }
}
