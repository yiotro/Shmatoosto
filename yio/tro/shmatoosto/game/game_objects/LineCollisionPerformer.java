package yio.tro.shmatoosto.game.game_objects;

import yio.tro.shmatoosto.stuff.PointYio;

public class LineCollisionPerformer {

    ObjectsLayer objectsLayer;
    Ball ball;
    CollisionLine line;
    PointYio tempPoint, collPoint;
    private float distance;
    private double dr;
    private double angle;
    private double da;


    public LineCollisionPerformer(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;
        tempPoint = new PointYio();
        collPoint = new PointYio();
        ball = null;
        line = null;
    }


    private void updateReferences(Ball ball, CollisionLine line) {
        this.ball = ball;
        this.line = line;
    }


    public void checkToCollideBallVsLine(Ball ball, CollisionLine line) {
        if (line.collisionId != 0 && ball.collisionId != line.collisionId) return;
        if (ball.solid) return;

        double collisionFactor = getCollisionFactor(ball, line);
        if (collisionFactor < 0 || collisionFactor > 1) return;

        collPoint.setBy(line.one);
        collPoint.relocateRadial(collisionFactor * line.length, line.angle);

        collideBallVsPoint();
        objectsLayer.onBallVsWallCollision(ball);
    }


    public void collideBallVsPoint() {
        distance = ball.position.center.distanceTo(collPoint);
        updateAngle();
        dr = ball.collisionRadius - distance;

        pushBallAwayFromCollPoint();
        changeBallSpeed();
    }


    private void updateAngle() {
        angle = ball.position.center.angleTo(collPoint);

        da = angle + Math.PI / 2;

        while (da < 0) {
            da += 2 * Math.PI;
        }

        while (da > 2 * Math.PI) {
            da -= 2 * Math.PI;
        }
    }


    private void changeBallSpeed() {
        ball.delta.rotate(-da);

        ball.delta.y *= -(1 - Ball.WALL_FRICTION);

        ball.delta.rotate(da);
        ball.updateMovementAngle();
    }


    private void pushBallAwayFromCollPoint() {
        ball.position.center.relocateRadial(dr + 1, Math.PI + angle);
    }


    private double getCollisionFactor(Ball ball, CollisionLine line) {
        updateReferences(ball, line);
        prepareTempBallPoint();
        if (Math.abs(tempPoint.y) > ball.collisionRadius) return -1;
        if (tempPoint.x > 0 && tempPoint.x < line.length) {
            return tempPoint.x / line.length;
        }
        if (ball.position.center.distanceTo(line.one) < ball.collisionRadius) {
            return 0;
        }
        if (ball.position.center.distanceTo(line.two) < ball.collisionRadius) {
            return 1;
        }
        return -1;
    }


    private void prepareTempBallPoint() {
        tempPoint.setBy(ball.position.center);
        tempPoint.x -= line.one.x;
        tempPoint.y -= line.one.y;
        tempPoint.rotate(-line.angle);
    }


    public boolean areBallAndLineInCollision(Ball ball, CollisionLine line) {
        double collisionFactor = getCollisionFactor(ball, line);
        return collisionFactor >= 0 && collisionFactor <= 1;
    }
}
