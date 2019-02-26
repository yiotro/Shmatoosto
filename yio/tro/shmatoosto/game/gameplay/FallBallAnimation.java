package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.billiard.Hole;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class FallBallAnimation implements ReusableYio{


    public Ball ball;
    PointYio start, end;
    public FactorYio factorYio, alphaFactor;
    float f;


    public FallBallAnimation() {
        start = new PointYio();
        end = new PointYio();
        factorYio = new FactorYio();
        alphaFactor = new FactorYio();
    }


    @Override
    public void reset() {
        ball = null;

        factorYio.reset();
        factorYio.appear(0, 1);

        alphaFactor.setValues(1, 0);
        alphaFactor.destroy(0, 1);
    }


    public void move() {
        factorYio.move();
        alphaFactor.move();
        updateBallPosition();
    }


    private void updateBallPosition() {
        f = factorYio.get();
        ball.position.center.set(
                start.x + factorYio.get() * (end.x - start.x),
                start.y + factorYio.get() * (end.y - start.y)
        );
    }


    public void setBallInBilliardMode(Ball ball) {
        this.ball = ball;
        start.setBy(ball.position.center);
        end.setBy(ball.hole.position.center);
        end.relocateRadial(2 * ball.hole.position.radius, start.angleTo(end));
    }


    public void setBallInChapaevoMode(Ball ball) {
        this.ball = ball;
        start.setBy(ball.position.center);
        end.setBy(ball.position.center);
        end.relocateRadial(2 * ball.collisionRadius, ball.delta.getAngle());
    }


    public void setBallInSoccerMode(Ball ball) {
        this.ball = ball;
        start.setBy(ball.position.center);
        end.setBy(start);
    }
}
