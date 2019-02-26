package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class DebugDataManager {


    ObjectsLayer objectsLayer;
    public PointYio firstPosition;
    PointYio tempPoint, tempDelta;
    public Ball firstHitBall;


    public DebugDataManager(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;

        firstPosition = new PointYio();
        tempPoint = new PointYio();
        tempDelta = new PointYio();

        reset();
    }


    public void reset() {
        firstPosition.set(0, 0);
        firstHitBall = null;
    }


    public void onEndCreation() {
        reset();
    }


    public void onApplyAimingMode() {
        if (!DebugFlags.showSpecificDebugData) return;

        //
    }


    public void onApplyActionMode() {
        if (!DebugFlags.showSpecificDebugData) return;

        firstHitBall = detectFirstHitBall();
        if (firstHitBall != null) {
            firstPosition.setBy(firstHitBall.position.center);
        }
    }


    public void move() {
        if (!DebugFlags.showSpecificDebugData) return;

        //
    }


    private Ball findClosestBall(PointYio position) {
        Ball closestBall = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ball : objectsLayer.balls) {
            currentDistance = position.distanceTo(ball.position.center);
            if (closestBall == null || currentDistance < minDistance) {
                closestBall = ball;
                minDistance = currentDistance;
            }
        }

        return closestBall;
    }


    private Ball detectFirstHitBall() {
        Ball cue = objectsLayer.findCue();
        tempPoint.setBy(cue.position.center);
        tempDelta.setBy(cue.delta);
        double movementAngle = cue.movementAngle;
        Ball result = null;

        int c = 500;
        while (c > 0) {
            c--;

            tempPoint.relocateRadial(cue.collisionRadius / 2, movementAngle);
            Ball closestBall = findClosestBall(tempPoint);
            if (closestBall == cue) continue;

            float distance = closestBall.position.center.distanceTo(tempPoint);
            if (distance > 2 * cue.collisionRadius) continue;

            result = closestBall;
            break;
        }

        return result;
    }


}
