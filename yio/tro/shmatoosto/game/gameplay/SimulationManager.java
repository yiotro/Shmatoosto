package yio.tro.shmatoosto.game.gameplay;

import yio.tro.shmatoosto.game.GameMode;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.CollisionListener;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

public class SimulationManager implements CollisionListener{


    ObjectsLayer objectsLayer;
    ObjectPoolYio<SimResults> poolResults;
    private SimResults currentResults;
    private SnapshotManager snapshotManager;
    BColorYio currentColor;
    boolean forceStop;


    public SimulationManager(ObjectsLayer objectsLayer) {
        this.objectsLayer = objectsLayer;
        initPools();
    }


    private void initPools() {
        poolResults = new ObjectPoolYio<SimResults>() {
            @Override
            public SimResults makeNewObject() {
                return new SimResults(SimulationManager.this);
            }
        };
    }


    private void checkForSoccerSimulationFix() {
        if (objectsLayer.gameController.gameMode != GameMode.soccer) return;
        if (currentResults.scoreDelta != -1) return;

        currentResults.scoreDelta = 1 + (int) (objectsLayer.findSoccerBall().position.center.distanceTo(getCurrentTargetGoal()));
    }


    private RectangleYio getCurrentTargetGoal() {
        SoccerManager soccerManager = getSoccerManager();
        AbstractPlayerEntity currentEntity = soccerManager.getEntity(currentColor);

        if (currentEntity.getIndex() == 0) {
            return soccerManager.board.topGoalArea;
        } else {
            return soccerManager.board.bottomGoalArea;
        }
    }


    public void setSimulationParameters(Ball selectedBall, double aimAngle, double power) {
        for (Ball ball : objectsLayer.balls) {
            ball.potentialDelta.reset();
        }

        selectedBall.potentialDelta.relocateRadial(power, aimAngle);
    }


    public void setSimulationParameters(Ball ball1, double power1, double angle1, Ball ball2, double power2, double angle2) {
        for (Ball ball : objectsLayer.balls) {
            ball.potentialDelta.reset();
        }

        ball1.potentialDelta.relocateRadial(power1, angle1);
        ball2.potentialDelta.relocateRadial(power2, angle2);
    }


    public SimResults perform(Ball ball, double aimAngle, double power) {
        setSimulationParameters(ball, aimAngle, power);
        return perform();
    }


    public SimResults perform() {
        prepare();

        activateCertainBalls();

        while (!isReadyTopStopSimulation()) {
            objectsLayer.move();
        }

        checkForSoccerSimulationFix();
        finish();

        return currentResults;
    }


    private void activateCertainBalls() {
        for (Ball ball : objectsLayer.balls) {
            PointYio potentialDelta = ball.potentialDelta;
            if (potentialDelta.x == 0 && potentialDelta.y == 0) continue;

            ball.setSpeed(potentialDelta.getDistance(), potentialDelta.getAngle());
            ball.setFrozen(false);
        }
    }


    private void unfreezeBalls() {
        for (Ball ball : objectsLayer.balls) {
            ball.setFrozen(false);
        }
    }


    private void prepareFreezes() {
        objectsLayer.freezeAllBallExceptCue();
        objectsLayer.setNumberOfAllowedUnfreezes(999);
    }


    public boolean isReadyTopStopSimulation() {
        if (forceStop) return true;

        for (Ball ball : objectsLayer.balls) {
            if (ball.isFrozen()) continue;

            if (ball.getSpeedValue() > 0.15 * ball.collisionRadius) return false;
        }

        return true;
    }


    private void finish() {
        snapshotManager.recreateLastSnapshot();
        objectsLayer.setSimulationMode(false);
        unfreezeBalls();
    }


    private void prepare() {
        prepareFreezes();
        snapshotManager = objectsLayer.gameController.snapshotManager;
        snapshotManager.clear();
        snapshotManager.takeSnapshot();
        currentResults = poolResults.getNext();
        currentResults.scoreDelta = objectsLayer.gameController.gameplayManager.getDefaultScoreDelta();
        objectsLayer.setSimulationMode(true);
        objectsLayer.onApplyActionMode();
        forceStop = false;
    }


    public void disposeResults(SimResults results) {
        poolResults.addWithCheck(results);
    }


    public void onBallFellToHoleDuringSimulation(Ball ball) {
        if (ball.isCue()) {
            currentResults.scoreDelta -= 1;
        } else {
            currentResults.scoreDelta += 1;
        }
    }


    public void onBallFellOutsideDuringSimulation(Ball ball) {
        if (currentColor == null) return;

        if (ball.getColor() == currentColor) {
            currentResults.scoreDelta -= 1;
        } else {
            currentResults.scoreDelta += 1;
        }
    }


    public void onGoalRegisteredDuringSimulation(int goalIndex) {
        forceStop = true;

        SoccerManager soccerManager = getSoccerManager();
        AbstractPlayerEntity currentEntity = soccerManager.getEntity(currentColor);

        if (currentEntity.getIndex() == goalIndex) {
            currentResults.scoreDelta = -2; // auto goal
        } else {
            currentResults.scoreDelta = 0; // goal
        }
    }


    private SoccerManager getSoccerManager() {
        return (SoccerManager) objectsLayer.gameController.gameplayManager;
    }


    @Override
    public void onBallsCollided(Ball one, Ball two) {
        if (objectsLayer.inSimulationMode() && currentResults.scoreDelta < 1) {
            currentResults.collisionBeforeFirstScore++;
        }
    }


    @Override
    public void onPrecisionFixApplied(double previousAngle, double currentAngle) {

    }


    public void setCurrentColor(BColorYio currentColor) {
        this.currentColor = currentColor;
    }
}
