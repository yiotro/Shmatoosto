package yio.tro.shmatoosto.game.game_objects;

import com.badlogic.gdx.audio.Sound;
import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.TouchableYio;
import yio.tro.shmatoosto.game.gameplay.*;
import yio.tro.shmatoosto.game.gameplay.billiard.Hole;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.RepeatYio;
import yio.tro.shmatoosto.stuff.containers.posmap.PmSectorIndex;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapLooper;
import yio.tro.shmatoosto.stuff.containers.posmap.PosMapYio;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class ObjectsLayer implements TouchableYio {

    public GameController gameController;
    public ArrayList<Ball> balls;
    public PosMapYio posMap;
    private final float sectorSize;
    PosMapLooper looper, looperLines;
    PmSectorIndex sectorIndex;
    public ArrayList<Ball> nearbyBalls;
    ArrayList<CollisionLine> nearbyLines;
    public BallCollisionPerformer ballCollisionPerformer;
    public ArrayList<CollisionLine> collisionLines;
    public LineCollisionPerformer lineCollisionPerformer;
    ArrayList<Hole> nearbyHoles;
    ArrayList<Ball> readyToBeEatenList;
    public ArrayList<FallBallAnimation> fallBallAnimations;
    ObjectPoolYio<FallBallAnimation> poolFallAnimations;
    RepeatYio<ObjectsLayer> repeatRemoveFallAnimations;
    boolean simulationMode;
    public SimulationManager simulationManager;
    public DebugDataManager debugDataManager;
    int numberOfAllowedUnfreezes;
    public int firstEntityShotsNumber, secondEntityShotsNumber;
    int collisionSoundCountDown;
    public int initialBallsNumber;
    public ArrayList<Obstacle> obstacles;


    public ObjectsLayer(GameController gameController) {
        this.gameController = gameController;
        balls = new ArrayList<>();

        RectangleYio screenRect = new RectangleYio(0, 0, GraphicsYio.width, GraphicsYio.height);
        sectorSize = 0.2f * GraphicsYio.width;
        posMap = new PosMapYio(screenRect, sectorSize);
        sectorIndex = new PmSectorIndex();
        nearbyBalls = new ArrayList<>();
        ballCollisionPerformer = new BallCollisionPerformer(this);
        collisionLines = new ArrayList<>();
        lineCollisionPerformer = new LineCollisionPerformer(this);
        nearbyLines = new ArrayList<>();
        nearbyHoles = new ArrayList<>();
        readyToBeEatenList = new ArrayList<>();
        fallBallAnimations = new ArrayList<>();
        simulationManager = new SimulationManager(this);
        ballCollisionPerformer.addListener(simulationManager);
        debugDataManager = new DebugDataManager(this);
        numberOfAllowedUnfreezes = 0;
        collisionSoundCountDown = 0;
        initialBallsNumber = 0;
        obstacles = new ArrayList<>();

        initLooper();
        initRepeats();
        initPools();
    }


    private void initRepeats() {
        repeatRemoveFallAnimations = new RepeatYio<ObjectsLayer>(this, 30) {
            @Override
            public void performAction() {
                parent.checkToRemoveSomeFallBallAnimations();
            }
        };
    }


    private void initPools() {
        poolFallAnimations = new ObjectPoolYio<FallBallAnimation>() {
            @Override
            public FallBallAnimation makeNewObject() {
                return new FallBallAnimation();
            }
        };
    }


    void clearFallBallAnimations() {
        for (FallBallAnimation fallBallAnimation : fallBallAnimations) {
            poolFallAnimations.add(fallBallAnimation);
        }

        fallBallAnimations.clear();
    }


    void checkToRemoveSomeFallBallAnimations() {
        if (fallBallAnimations.size() == 0) return;

        for (int i = fallBallAnimations.size() - 1; i >= 0; i--) {
            FallBallAnimation fallBallAnimation = fallBallAnimations.get(i);
            if (fallBallAnimation.factorYio.get() < 1) continue;

            removeFallBallAnimation(fallBallAnimation);
        }
    }


    void removeFallBallAnimation(FallBallAnimation fallBallAnimation) {
        poolFallAnimations.add(fallBallAnimation);
        fallBallAnimations.remove(fallBallAnimation);
    }


    private void initLooper() {
        looper = new PosMapLooper(this, 1) {
            @Override
            public void performAction(ArrayList posMapObjects) {
                applyPosMapObjects(posMapObjects);
            }
        };

        looperLines = new PosMapLooper(this, 0, 1) {
            @Override
            public void performAction(ArrayList posMapObjects) {
                for (Object posMapObject : posMapObjects) {
                    if (posMapObject instanceof CollisionLine) {
                        CollisionLine line = (CollisionLine) posMapObject;
                        if (nearbyLines.contains(line)) continue;
                        nearbyLines.add(line);
                    }
                }
            }
        };
    }


    private void applyPosMapObjects(ArrayList posMapObjects) {
        for (Object posMapObject : posMapObjects) {
            if (posMapObject instanceof Ball) {
                Ball nearbyBall = (Ball) posMapObject;
                nearbyBalls.add(nearbyBall);
            }
            if (posMapObject instanceof Hole) {
                Hole hole = (Hole) posMapObject;
                if (nearbyHoles.contains(hole)) continue;
                nearbyHoles.add(hole);
            }
        }
    }


    public void updateNearbyObjects(Ball srcBall) {
        updateNearbyObjects(srcBall.position.center);
    }


    public void updateNearbyObjects(PointYio srcPoint) {
        nearbyBalls.clear();
        nearbyLines.clear();
        nearbyHoles.clear();
        posMap.transformCoorToIndex(srcPoint, sectorIndex);
        looper.forNearbySectors(posMap, sectorIndex);
        looperLines.forNearbySectors(posMap, sectorIndex);
    }


    public void move() {
        moveBalls();
        performCollisions();
        eatBallsByHoles();
        moveFallBallAnimations();
        moveSoundCountDown();
        debugDataManager.move();

        gameController.moveGameplayManager();
    }


    private void moveSoundCountDown() {
        if (collisionSoundCountDown > 0) {
            collisionSoundCountDown--;
        }
    }


    public boolean isReadyForAimingMode() {
        for (Ball ball : balls) {
            if (ball.getSpeedValue() > 0.01) return false;
        }

        for (FallBallAnimation fallBallAnimation : fallBallAnimations) {
            if (fallBallAnimation.factorYio.get() < 1) {
                return false;
            }
        }

        return true;
    }


    private void moveFallBallAnimations() {
        if (simulationMode) return;

        for (FallBallAnimation fallBallAnimation : fallBallAnimations) {
            fallBallAnimation.move();
        }

        repeatRemoveFallAnimations.move();
    }


    private void eatBallsByHoles() {
        if (readyToBeEatenList.size() == 0) return;

        for (Ball ball : readyToBeEatenList) {
            removeBall(ball);
            addFallBallAnimation(ball);
            onBallFellToHole(ball);
        }

        readyToBeEatenList.clear();
    }


    public void removeBall(Ball ball) {
        balls.remove(ball);
        posMap.removeObject(ball);
    }


    public void clearBalls() {
        for (Ball ball : balls) {
            posMap.removeObject(ball);
        }

        balls.clear();
    }


    public void clearBallsFiltered(BColorYio ignoreColor) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            if (ball.getColor() == ignoreColor) continue;

            posMap.removeObject(ball);
            balls.remove(i);
        }
    }


    public BColorYio getSelectedBallsColor() {
        for (Ball ball : balls) {
            if (ball.isSelected()) {
                return ball.getColor();
            }
        }

        return null;
    }


    private void onBallFellToHole(Ball ball) {
        if (simulationMode) {
            simulationManager.onBallFellToHoleDuringSimulation(ball);
            return;
        }

        gameController.gameplayManager.onBallFellToHole(ball);
        checkForFallSound();
    }


    public Ball findClosestBall(PointYio point) {
        return findClosestBall(point, null);
    }


    public Ball findClosestBall(PointYio point, BColorYio filterColor) {
        Ball closestBall = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ball : balls) {
            if (filterColor != null && ball.getColor() != filterColor) continue;

            currentDistance = ball.position.center.distanceTo(point);
            if (closestBall == null || currentDistance < minDistance) {
                closestBall = ball;
                minDistance = currentDistance;
            }
        }

        return closestBall;
    }


    private void checkForFallSound() {
        if (simulationMode) return;
        if (!gameController.actionMode) return;

        SoundManager.playSound(SoundManager.fall);
        collisionSoundCountDown = 3;
    }


    public boolean isCuePresentOnBoard() {
        return findCue() != null;
    }


    public void addFallBallAnimation(Ball ball) {
        if (simulationMode) return;

        FallBallAnimation next = poolFallAnimations.getNext();

        switch (gameController.gameMode) {
            case billiard:
                next.setBallInBilliardMode(ball);
                break;
            case chapaevo:
                next.setBallInChapaevoMode(ball);
                break;
            case soccer:
                next.setBallInSoccerMode(ball);
                break;
        }

        fallBallAnimations.add(next);
    }


    public void onApplyAimingMode() {
        debugDataManager.onApplyAimingMode();
    }


    public void onApplyActionMode() {
        // should be careful because this method is called in simulation manager

        debugDataManager.onApplyActionMode();

        if (!simulationMode) {
            if (gameController.currentEntityIndex == 0) {
                firstEntityShotsNumber++;
            } else {
                secondEntityShotsNumber++;
            }
        }

        if (GameRules.frozenMode) {
            freezeAllBallExceptCue();
            numberOfAllowedUnfreezes = 999;
        }
    }


    public int getShotsNumberForEntity(int index) {
        if (index == 0) {
            return firstEntityShotsNumber;
        }

        if (index == 1) {
            return secondEntityShotsNumber;
        }

        return 0;
    }


    public void freezeAllBallExceptCue() {
        for (Ball ball : balls) {
            if (ball.isCue()) continue;
            if (ball.isSoccerBall()) continue;

            ball.setFrozen(true);
        }
    }


    public void performCollisions() {
        for (Ball ball : balls) {
            if (ball.isFrozen()) continue;

            updateNearbyObjects(ball);
            collideWithNearbyObjects(ball);
            checkForHoles(ball);
        }
    }


    public void onBallsCollided(Ball one, Ball two) {
        if (simulationMode) return;
        if (collisionSoundCountDown > 0) return;
        if (!gameController.actionMode) return;

        double f = (one.getSpeedValue() + two.getSpeedValue()) / (one.maxSpeed + two.maxSpeed);
        SoundManager.playSound(getBallsCollSound(f));

        collisionSoundCountDown = 3;
    }


    private Sound getBallsCollSound(double f) {
        if (f < 0.3) {
            return SoundManager.collBallsQuiet;
        }

        if (f < 0.6) {
            return SoundManager.collBallsNormal;
        }

        return SoundManager.collBallsLoud;
    }


    public void onBallVsWallCollision(Ball ball) {
        if (simulationMode) return;
        if (collisionSoundCountDown > 0) return;
        if (!gameController.actionMode) return;

        double f = ball.getSpeedValue() / ball.maxSpeed;

        SoundManager.playSound(getBallWallSound(f));
        collisionSoundCountDown = 3;
    }


    private Sound getBallWallSound(double f) {
        if (f < 0.5) {
            return SoundManager.collWallQuiet;
        }

        return SoundManager.collWallLoud;
    }


    private void checkForHoles(Ball ball) {
        for (Hole nearbyHole : nearbyHoles) {
            if (nearbyHole.position.center.distanceTo(ball.position.center) > nearbyHole.position.radius + ball.collisionRadius) continue;
            if (readyToBeEatenList.contains(ball)) continue;

            readyToBeEatenList.add(ball);
            ball.hole = nearbyHole;
        }
    }


    private void collideWithNearbyObjects(Ball ball) {
        for (Ball nearbyBall : nearbyBalls) {
            if (!isCollisionAllowed(ball, nearbyBall)) continue;

            ballCollisionPerformer.checkToCollideTwoBalls(ball, nearbyBall);
        }

        for (CollisionLine nearbyLine : nearbyLines) {
            lineCollisionPerformer.checkToCollideBallVsLine(ball, nearbyLine);
        }
    }


    private boolean isCollisionAllowed(Ball ball, Ball nearbyBall) {
        // this method is used to cut excessive collisions

        if (nearbyBall.isFrozen()) return true;

        if (nearbyBall.position.center.x < ball.position.center.x) return true;

        return nearbyBall.position.center.x == ball.position.center.x && nearbyBall.position.center.y < ball.position.center.y;
    }


    private void moveBalls() {
        for (Ball ball : balls) {
            ball.moveSelection();

            if (ball.isFrozen()) continue;

            ball.move();
            posMap.updateObjectPos(ball);
        }
    }


    public void deselectAllBalls() {
        for (Ball ball : balls) {
            ball.deselect();
        }
    }


    public void selectBalls(BColorYio filterColor) {
        for (Ball ball : balls) {
            if (ball.getColor() != filterColor) continue;

            ball.select();
        }
    }


    public CollisionLine addCollisionLine(SimResults.BoardLine boardLine) {
        return addCollisionLine(boardLine.one.x, boardLine.one.y, boardLine.two.x, boardLine.two.y);
    }


    public CollisionLine addCollisionLine(double x1, double y1, double x2, double y2) {
        CollisionLine collisionLine = new CollisionLine();
        collisionLine.set(x1, y1, x2, y2);
        collisionLines.add(collisionLine);
        return collisionLine;
    }


    public Obstacle addObstacle() {
        Obstacle o = new Obstacle(this);
        obstacles.add(o);
        return o;
    }


    public void removeObstacle(Obstacle obstacle) {
        obstacles.remove(obstacle);
    }


    public Ball addBall() {
        Ball ball = new Ball(this);
        balls.add(ball);
        posMap.addObject(ball);
        return ball;
    }


    public void createGameObjects() {

    }


    public void onEndCreation() {
        updatePosMapWithCollisionLines();
        readyToBeEatenList.clear();
        clearFallBallAnimations();
        simulationMode = false;
        updateInitialBallsNumber();
        debugDataManager.onEndCreation();
    }


    private void updateInitialBallsNumber() {
        initialBallsNumber = balls.size();
    }


    public void resetShotCounters() {
        firstEntityShotsNumber = 0;
        secondEntityShotsNumber = 0;
    }


    public void setSimulationMode(boolean simulationMode) {
        this.simulationMode = simulationMode;
    }


    public boolean inSimulationMode() {
        return simulationMode;
    }


    public Ball findCue() {
        for (Ball ball : balls) {
            if (!ball.isCue()) continue;

            return ball;
        }

        return null;
    }


    public Ball findSoccerBall() {
        for (Ball ball : balls) {
            if (!ball.isSoccerBall()) continue;

            return ball;
        }

        return null;
    }


    private void updatePosMapWithCollisionLines() {
        Ball ball = new Ball(this);
        ball.setRadius(posMap.sectorSize);
        ball.updateCollisionRadius();

        for (int i = 0; i < posMap.width; i++) {
            for (int j = 0; j < posMap.height; j++) {
                ball.position.center.x = posMap.mapPos.x + i * posMap.sectorSize + posMap.sectorSize / 2;
                ball.position.center.y = posMap.mapPos.y + j * posMap.sectorSize + posMap.sectorSize / 2;
                for (CollisionLine collisionLine : collisionLines) {
                    if (lineCollisionPerformer.areBallAndLineInCollision(ball, collisionLine)) {
                        posMap.getSector(i, j).add(collisionLine);
                    }
                }
            }
        }
    }


    public boolean canUnfreezeBall() {
        return numberOfAllowedUnfreezes > 0;
    }


    public void onBallUnfrozen() {
        numberOfAllowedUnfreezes--;
    }


    public void setNumberOfAllowedUnfreezes(int numberOfAllowedUnfreezes) {
        this.numberOfAllowedUnfreezes = numberOfAllowedUnfreezes;
    }


    @Override
    public boolean touchDown(PointYio touchPoint) {
        return false;
    }


    @Override
    public boolean touchDrag(PointYio touchPoint) {
        return false;
    }


    @Override
    public boolean touchUp(PointYio touchPoint) {
        return false;
    }


    public void onDestroy() {

    }

}
