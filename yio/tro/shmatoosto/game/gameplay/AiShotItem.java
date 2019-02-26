package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class AiShotItem implements ReusableYio {

    public double angle;
    public double funScore;
    public double power;
    public int scoreDelta;
    public Ball targetBall;
    public Ball selectedBall;
    public PointYio selectedPoint;
    public int collisionBeforeFirstScore;


    public AiShotItem() {
        selectedPoint = new PointYio();
        reset();
    }


    @Override
    public void reset() {
        angle = 0;
        funScore = 0;
        scoreDelta = 0;
        power = 0;
        targetBall = null;
        selectedBall = null;
        selectedPoint.reset();
        collisionBeforeFirstScore = 0;
    }


    public void setPower(double power) {
        this.power = power;
    }


    public void setAngle(double angle) {
        this.angle = angle;
    }


    public void setFunScore(double funScore) {
        this.funScore = funScore;
    }


    public void setScoreDelta(int scoreDelta) {
        this.scoreDelta = scoreDelta;
    }


    public void setTargetBall(Ball targetBall) {
        this.targetBall = targetBall;
    }


    public void setCollisionBeforeFirstScore(int collisionBeforeFirstScore) {
        this.collisionBeforeFirstScore = collisionBeforeFirstScore;
    }


    public void setSelectedBall(Ball selectedBall) {
        this.selectedBall = selectedBall;
        selectedPoint.setBy(selectedBall.position.center);
    }


    @Override
    public String toString() {
        return "[AiShotItem: " +
                scoreDelta +
                ", power = " + Yio.roundUp(power, 2) +
                ", angle = " + Yio.roundUp(angle / Math.PI, 2) + " * Math.PI" +
                "]";
    }
}
