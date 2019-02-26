package yio.tro.shmatoosto.stuff;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.stuff.containers.tree.NodeYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class PointYio implements ReusableYio {

    public float x, y;


    public PointYio() {
        set(0, 0);
    }


    @Override
    public void reset() {
        set(0, 0);
    }


    public PointYio(double x, double y) {
        set(x, y);
    }


    public void set(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }


    public void setBy(PointYio src) {
        x = src.x;
        y = src.y;
    }


    public float distanceTo(PointYio pointYio) {
        return (float) Yio.distance(x, y, pointYio.x, pointYio.y);
    }


    public float distanceTo(RectangleYio rectangleYio) {
        return (float) rectangleYio.distanceTo(this);
    }


    public float angleTo(RectangleYio rectangleYio) {
        return (float) rectangleYio.angleFrom(this);
    }


    public double angleTo(PointYio pointYio) {
        return Yio.angle(x, y, pointYio.x, pointYio.y);
    }


    public float fastDistanceTo(PointYio pointYio) {
        return Math.abs(x - pointYio.x) + Math.abs(y - pointYio.y);
    }


    public void relocateRadial(double distance, double angle) {
        x += distance * Math.cos(angle);
        y += distance * Math.sin(angle);
    }


    public float getAngle() {
        return (float) Yio.angle(0, 0, x, y);
    }


    public float getDistance() {
        return (float) Yio.distance(0, 0, x, y);
    }


    public void rotate(double da) {
        double r = Yio.distance(0, 0, x, y);
        double a = Yio.angle(0, 0, x, y);
        a += da;
        x = (float) (r * Math.cos(a));
        y = (float) (r * Math.sin(a));
    }


    public boolean equals(PointYio p) {
        return x == p.x && y == p.y;
    }


    @Override
    public String toString() {
        return "[Point: " + Yio.roundUp(x, 2) + ", " + Yio.roundUp(y, 2) + "]";
    }


}
