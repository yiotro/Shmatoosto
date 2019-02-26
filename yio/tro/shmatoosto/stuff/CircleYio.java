package yio.tro.shmatoosto.stuff;

import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class CircleYio implements ReusableYio{

    public PointYio center;
    public float radius;
    public double angle;


    public CircleYio() {
        center = new PointYio();
        reset();
    }


    public CircleYio(double x, double y, double r) {
        this();
        set(x, y, r);
    }


    public CircleYio set(double x, double y, double r) {
        center.set(x, y);
        setRadius(r);
        return this;
    }


    public CircleYio setRadius(double radius) {
        this.radius = (float) radius;
        return this;
    }


    public CircleYio setAngle(double angle) {
        this.angle = angle;
        return this;
    }


    @Override
    public void reset() {
        radius = 0;
        angle = 0;
    }
}
