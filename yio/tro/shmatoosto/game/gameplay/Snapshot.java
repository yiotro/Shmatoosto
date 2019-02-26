package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

import java.util.ArrayList;

public class Snapshot implements ReusableYio{

    GameController gameController;
    public ArrayList<Ball> balls;
    ObjectPoolYio<Ball> poolBalls;


    public Snapshot(GameController gameController) {
        this.gameController = gameController;

        balls = new ArrayList<>();
        initPools();
    }


    private void initPools() {
        poolBalls = new ObjectPoolYio<Ball>() {
            @Override
            public Ball makeNewObject() {
                return new Ball(gameController.objectsLayer);
            }
        };
    }


    public void recreate() {
        gameController.objectsLayer.clearBalls();
        for (Ball ball : balls) {
            Ball addedBall = gameController.objectsLayer.addBall();
            addedBall.copyFrom(ball);
            gameController.objectsLayer.posMap.updateObjectPos(addedBall);
        }
    }


    public void syncWithCurrentBoardState() {
        clearBalls();
        for (Ball src : gameController.objectsLayer.balls) {
            Ball next = poolBalls.getNext();
            next.copyFrom(src);
            balls.add(next);
        }
    }


    @Override
    public void reset() {
        clearBalls();
    }


    private void clearBalls() {
        for (Ball ball : balls) {
            poolBalls.add(ball);
        }

        balls.clear();
    }
}
