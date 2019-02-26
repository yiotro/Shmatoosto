package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.stuff.PointYio;

import java.util.ArrayList;

public class BallCollisionPerformer {


    public static float BOUNCE_MULTIPLIER = 1.2f;
    ObjectsLayer objectsLayer;
    private float distance;
    Ball one, two;
    private double angle;
    double da;
    PointYio tempPoint, tf1, tf2;
    ArrayList<CollisionListener> listeners;
    private double a1;
    private double a2;
    boolean precisionFixApplied;
    float massRatio;


    public BallCollisionPerformer(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;
        one = null;
        two = null;
        tempPoint = new PointYio();
        listeners = new ArrayList<>();
        tf1 = new PointYio();
        tf2 = new PointYio();
    }


    public void checkToCollideTwoBalls(Ball one, Ball two) {
        if (one == two) return;

        this.one = one;
        this.two = two;
        if (!one.isInCollisionWith(two)) return;

        checkForCuePrecisionFix();

        updateDistance();
        updateAngle();

        checkToUnfreezeSecondBall();
        pushBallsAwayFromEachOther();
        swapSpeeds();

        notifyListeners();
        objectsLayer.onBallsCollided(one, two);
    }


    private void notifyListeners() {
        for (CollisionListener listener : listeners) {
            listener.onBallsCollided(one, two);
        }
    }


    private void checkToUnfreezeSecondBall() {
        if (!two.isFrozen()) return;
        if (!objectsLayer.canUnfreezeBall()) return;

        two.setFrozen(false);
    }


    private void checkForCuePrecisionFix() {
        precisionFixApplied = false;
        if (!one.isCue() && !two.isCue()) return;

        Ball cue, otherBall;
        if (one.isCue()) {
            cue = one;
            otherBall = two;
        } else {
            cue = two;
            otherBall = one;
        }

        if (otherBall.getSpeedValue() > 1) return;
        if (cue.getSpeedValue() < 0.1 * cue.maxSpeed) return;

        a1 = cue.position.center.angleTo(otherBall.position.center);

        cue.updateMovementAngleByLastPosition();
        if (isMovementAngleVertical(cue)) return;

        double k = Math.tan(cue.movementAngle);
        double d = cue.position.center.y - k * cue.position.center.x;
        double x0 = otherBall.position.center.x;
        double y0 = otherBall.position.center.y;
        double a = 1 + k * k;
        double b = -2 * x0 - 2 * y0 * k + 2 * k * d;
        double r = cue.collisionRadius + otherBall.collisionRadius;
        double c = x0 * x0 + y0 * y0 - 2 * y0 * d + d * d - r * r;
        double r1 = (-b) / (2 * a);
        double r2 = Math.sqrt(b * b - 4 * a * c) / (2 * a);
        tf1.x = (float) (r1 + r2);
        tf2.x = (float) (r1 - r2);
        tf1.y = (float) (k * tf1.x + d);
        tf2.y = (float) (k * tf2.x + d);
        double angleFromOneToTwo = tf1.angleTo(tf2);
        if (Yio.distanceBetweenAngles(angleFromOneToTwo, cue.movementAngle) < Math.PI / 2) {
            cue.position.center.setBy(tf1);
        } else  {
            cue.position.center.setBy(tf2);
        }

        a2 = cue.position.center.angleTo(otherBall.position.center);
        for (CollisionListener listener : listeners) {
            listener.onPrecisionFixApplied(a1, a2);
        }

        precisionFixApplied = true;
    }


    private boolean isMovementAngleVertical(Ball cue) {
        if (Yio.distanceBetweenAngles(cue.movementAngle, Math.PI / 2) < 0.001) return true;
        if (Yio.distanceBetweenAngles(cue.movementAngle, -Math.PI / 2) < 0.001) return true;

        return false;
    }


    private void swapSpeeds() {
        massRatio = (float) (one.mass / two.mass);

        one.delta.rotate(-da);
        two.delta.rotate(-da);

        one.delta.y *= BOUNCE_MULTIPLIER;
        two.delta.y *= BOUNCE_MULTIPLIER;

        if (one.solid || two.solid) {
            // special case, rare
            one.delta.y *= -0.5f;
            two.delta.y *= -0.5f;
        } else {
            float y1 = one.delta.y;
            one.delta.y = two.delta.y / massRatio;
            two.delta.y = massRatio * y1;
        }

        one.delta.rotate(da);
        two.delta.rotate(da);

        one.limitMaxSpeed();
        two.limitMaxSpeed();

        one.updateMovementAngle();
        two.updateMovementAngle();
    }


    private void updateAngle() {
        angle = one.position.center.angleTo(two.position.center);

        da = angle + Math.PI / 2;

        while (da < 0) {
            da += 2 * Math.PI;
        }

        while (da > 2 * Math.PI) {
            da -= 2 * Math.PI;
        }
    }


    private void pushBallsAwayFromEachOther() {
        if (precisionFixApplied) return;

        float dr = one.collisionRadius + two.collisionRadius - distance;
        dr += Math.max(1, 0.05f * dr); // to avoid strange bugs when balls magnet to each other

        if (!one.solid) {
            one.position.center.relocateRadial(dr / 2, angle + Math.PI);
        }
        if (!two.solid) {
            two.position.center.relocateRadial(dr / 2, angle);
        }
    }


    public void addListener(CollisionListener collisionListener) {
        listeners.add(collisionListener);
    }


    public void removeListener(CollisionListener collisionListener) {
        listeners.remove(collisionListener);
    }


    private void updateDistance() {
        distance = one.position.center.distanceTo(two.position.center);
    }
}
