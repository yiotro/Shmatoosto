package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapObjectYio;

public class CollisionLine extends PosMapObjectYio{

    public PointYio one, two;
    public double angle;
    public float length;
    public int collisionId;


    public CollisionLine() {
        one = new PointYio();
        two = new PointYio();
        angle = 0;
        length = 0;
        collisionId = 0;
    }


    @Override
    protected void updatePosMapPosition() {
        posMapPosition.set(
                (one.x + two.x) / 2,
                (one.y + two.y) / 2
        );
    }


    public void set(double x1, double y1, double x2, double y2) {
        one.set(x1, y1);
        two.set(x2, y2);
        length = one.distanceTo(two);
        angle = one.angleTo(two);
    }


    public void setCollisionId(int collisionId) {
        this.collisionId = collisionId;
    }
}
