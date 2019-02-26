package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.game_objects.Obstacle;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.gameplay.AiShotItem;
import yio.tro.shmatoosto.game.gameplay.SimResults;
import yio.tro.shmatoosto.game.gameplay.SimulationManager;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.menu.elements.gameplay.ChapaevoAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.Collections;

public class AiChapaevoEntity extends AbstractPlayerEntity {


    private ChapaevoManager chapaevoManager;
    private SimulationManager simulationManager;
    ArrayList<AiShotItem> shotItems;
    ObjectPoolYio<AiShotItem> poolShotItems;
    ArrayList<AiListener> listeners;
    PointYio tempPoint;
    private long startTime;
    PointYio geometricalCenterOfEnemy;
    ArrayList<Ball> ownedBalls, enemyBalls;


    public AiChapaevoEntity(GameController gameController) {
        super(gameController);

        shotItems = new ArrayList<>();
        tempPoint = new PointYio();
        listeners = new ArrayList<>();
        geometricalCenterOfEnemy = new PointYio();
        ownedBalls = new ArrayList<>();
        enemyBalls = new ArrayList<>();

        initPools();
    }


    private void initPools() {
        poolShotItems = new ObjectPoolYio<AiShotItem>() {
            @Override
            public AiShotItem makeNewObject() {
                return new AiShotItem();
            }
        };
    }


    @Override
    public void onApplyAimingMode() {
        startTime = System.currentTimeMillis();
        prepareReferences();
        if (!chapaevoManager.isThereAtLeastOneBallWithThisColor(getCurrentColor())) return;

        Scenes.chapaevoAimUI.create();
        Scenes.chapaevoAimUI.chapaevoAimUiElement.setFilterColor(getCurrentColor());

        updateShotItems();
        pickNiceShot();
    }


    private void notifyListeners(AiShotItem aiShotItem) {
        if (!DebugFlags.testingModeEnabled) return;

        for (AiListener listener : listeners) {
            listener.onScorePredicted(aiShotItem.scoreDelta);
            listener.onTimeTaken((int) (System.currentTimeMillis() - startTime));
        }
    }


    private int getMinimumAcceptedScoreDelta() {
        switch (GameRules.difficulty) {
            default:
            case Difficulty.EASY:
                return 1;
            case Difficulty.NORMAL:
                return 2;
            case Difficulty.HARD:
                return 5;
            case Difficulty.IMPOSSIBLE:
                return 10;
        }
    }


    private void pickNiceShot() {
        for (AiShotItem shotItem : shotItems) {
            fixShotItemAfterSimulation(shotItem);
            SimResults simResults = performSimulation(shotItem);
            shotItem.scoreDelta = simResults.scoreDelta;

            simResults.dispose();

            if (isShotItemGoodEnough(shotItem)) {
                applyShot(shotItem);
                return;
            }
        }

        AiShotItem bestShotItem = findShotItemWithBestScoreDelta();
        if (bestShotItem != null && bestShotItem.scoreDelta > 0) {
            applyShot(bestShotItem);
            return;
        }

        performRandomSafeShot();
    }


    private SimResults performSimulation(AiShotItem shotItem) {
        simulationManager.setCurrentColor(getCurrentColor());
        return simulationManager.perform(shotItem.selectedBall, shotItem.angle, shotItem.power * shotItem.selectedBall.maxSpeed);
    }


    private int getMaxScoreDeltaInList(ArrayList<AiShotItem> list) {
        if (list.size() == 0) {
            return -1;
        }

        int max = list.get(0).scoreDelta;

        for (AiShotItem aiShotItem : list) {
            if (aiShotItem.scoreDelta > max) {
                max = aiShotItem.scoreDelta;
            }
        }

        return max;
    }


    private void performRandomSafeShot() {
        clearShotItems();

        applyShot(findRandomSafeShot());
    }


    private AiShotItem findRandomSafeShot() {
        if (!chapaevoManager.isThereAtLeastOneBallWithThisColor(getEnemyColor())) return null;

        while (true) {
            AiShotItem randomWeakShot = findRandomWeakShot();
            SimResults simResults = performSimulation(randomWeakShot);
            fixShotItemAfterSimulation(randomWeakShot);
            randomWeakShot.scoreDelta = simResults.scoreDelta;

            simResults.dispose();
            removeShotItem(randomWeakShot); // push it back to pool

            if (randomWeakShot.scoreDelta >= 0) {
                return randomWeakShot;
            }
        }
    }


    private AiShotItem findRandomWeakShot() {
        AiShotItem next = poolShotItems.getNext();

        next.setSelectedBall(findRandomBall(getCurrentColor()));
        next.setAngle(Yio.getRandomAngle());
        next.setPower(0.1 + 0.25 * YioGdxGame.random.nextDouble());

        return next;
    }


    private void fixShotItemAfterSimulation(AiShotItem aiShotItem) {
        aiShotItem.setSelectedBall(getObjectsLayer().findClosestBall(aiShotItem.selectedPoint));
    }


    private AiShotItem findShotItemWithBestScoreDelta() {
        AiShotItem bestItem = null;

        for (AiShotItem shotItem : shotItems) {
            if (bestItem == null || shotItem.scoreDelta > bestItem.scoreDelta) {
                bestItem = shotItem;
            }
        }

        return bestItem;
    }


    private boolean isShotItemGoodEnough(AiShotItem aiShotItem) {
        return aiShotItem.scoreDelta >= getMinimumAcceptedScoreDelta();
    }


    private void updateShotItems() {
        clearShotItems();

        addShotItemsInUsualWay();

        int m = 3 * getObjectsLayer().obstacles.size();
        for (int k = 0; k < m; k++) {
            addShotItemByObstacle(findRandomBall(getCurrentColor()), findRandomVerticalObstacle());
        }
    }


    private void addShotItemsInUsualWay() {
        updateLists();
        updateGeometricalEnemyCenter();
        updateDistancesToGeometricalEnemyCenter();
        Collections.sort(ownedBalls);

        for (int i = 0; i < Math.min(3, ownedBalls.size()); i++) {
            for (Ball enemyBall : enemyBalls) {
                for (int k = 0; k < 3; k++) {
                    addShotItemByBallPair(ownedBalls.get(i), enemyBall);
                }
            }
        }
    }


    private void updateLists() {
        ownedBalls.clear();
        enemyBalls.clear();

        BColorYio currentColor = getCurrentColor();
        for (Ball ball : getObjectsLayer().balls) {
            if (ball.getColor() == currentColor) {
                ownedBalls.add(ball);
            } else {
                enemyBalls.add(ball);
            }
        }
    }


    private Ball findBestBallToSelect() {
        if (!chapaevoManager.isThereAtLeastOneBallWithThisColor(getCurrentColor())) return null;

        Ball bestBall = null;
        Ball currentBall;
        for (int i = 0; i < 5; i++) {
            currentBall = findRandomBall(getCurrentColor());
            if (currentBall == null) break;

            if (bestBall == null || currentBall.flagDistance < bestBall.flagDistance) {
                bestBall = currentBall;
            }
        }

        return bestBall;
    }


    private void updateDistancesToGeometricalEnemyCenter() {
        for (Ball ownedBall : ownedBalls) {
            ownedBall.flagDistance = ownedBall.position.center.fastDistanceTo(geometricalCenterOfEnemy);
        }
    }


    private void updateGeometricalEnemyCenter() {
        geometricalCenterOfEnemy.reset();

        for (Ball enemyBall : enemyBalls) {
            geometricalCenterOfEnemy.x += enemyBall.position.center.x;
            geometricalCenterOfEnemy.y += enemyBall.position.center.y;
        }

        geometricalCenterOfEnemy.x /= enemyBalls.size();
        geometricalCenterOfEnemy.y /= enemyBalls.size();
    }


    private void addShotItemByObstacle(Ball selectedBall, Obstacle obstacle) {
        if (selectedBall == null) return;
        if (obstacle == null) return;

        tempPoint.x = obstacle.position.x + YioGdxGame.random.nextFloat() * obstacle.position.width;
        tempPoint.y = obstacle.position.y + YioGdxGame.random.nextFloat() * obstacle.position.height;

        addShotItem(selectedBall, 0.8 + 0.2 * YioGdxGame.random.nextDouble(), selectedBall.position.center.angleTo(tempPoint));
    }


    private Obstacle findRandomVerticalObstacle() {
        if (!isThereAtLeastOneVerticalObstacle()) return null;

        while (true) {
            int index = YioGdxGame.random.nextInt(getObjectsLayer().obstacles.size());
            Obstacle obstacle = getObjectsLayer().obstacles.get(index);

            if (obstacle.isVertical()) {
                return obstacle;
            }
        }
    }


    private boolean isThereAtLeastOneVerticalObstacle() {
        for (Obstacle obstacle : getObjectsLayer().obstacles) {
            if (obstacle.isVertical()) {
                return true;
            }
        }

        return false;
    }


    private void clearShotItems() {
        for (AiShotItem shotItem : shotItems) {
            poolShotItems.add(shotItem);
        }

        shotItems.clear();
    }


    private void removeShotItem(AiShotItem aiShotItem) {
        poolShotItems.add(aiShotItem);
        shotItems.remove(aiShotItem);
    }


    private Ball findRandomBall(BColorYio filterColor) {
        if (!chapaevoManager.isThereAtLeastOneBallWithThisColor(filterColor)) return null;

        ArrayList<Ball> balls = getObjectsLayer().balls;
        while (true) {
            int index = YioGdxGame.random.nextInt(balls.size());
            Ball ball = balls.get(index);
            if (ball.getColor() == filterColor) {
                return ball;
            }
        }
    }


    private void addShotItemByBallPair(Ball selectedBall, Ball targetBall) {
        if (selectedBall == null) return;
        if (targetBall == null) return;

        tempPoint.setBy(targetBall.position.center);
        tempPoint.relocateRadial(YioGdxGame.random.nextDouble() * targetBall.collisionRadius, Yio.getRandomAngle());

        addShotItem(selectedBall, 0.8 + 0.2 * YioGdxGame.random.nextDouble(), selectedBall.position.center.angleTo(tempPoint));
    }


    private void addShotItem(Ball selectedBall, double power, double angle) {
        AiShotItem next = poolShotItems.getNext();

        next.setSelectedBall(selectedBall);
        next.setPower(power);
        next.setAngle(angle);

        shotItems.add(next);
    }


    private void prepareReferences() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        chapaevoManager = (ChapaevoManager) gameplayManager;
        simulationManager = gameController.objectsLayer.simulationManager;
    }


    private void applyShot(AiShotItem aiShotItem) {
        if (aiShotItem == null) return;

        fixShotItemAfterSimulation(aiShotItem);
        applyShot(aiShotItem.selectedBall, aiShotItem.power, aiShotItem.angle);
        notifyListeners(aiShotItem);
    }


    private void applyShot(Ball ball, double power, double angle) {
        ChapaevoAimUiElement chapaevoAimUiElement = Scenes.chapaevoAimUI.chapaevoAimUiElement;
        chapaevoAimUiElement.applyAutoTarget(ball, power, angle);
    }


    public Ball getBallWithCurrentColor() {
        BColorYio currentColor = getCurrentColor();

        for (Ball ball : getObjectsLayer().balls) {
            if (ball.getColor() != currentColor) continue;

            return ball;
        }

        return null;
    }


    private ObjectsLayer getObjectsLayer() {
        return gameController.objectsLayer;
    }


    public BColorYio getEnemyColor() {
        return chapaevoManager.getEntityColor(chapaevoManager.getEnemyEntityIndex(getIndex()));
    }


    public BColorYio getCurrentColor() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        ChapaevoManager chapaevoManager = (ChapaevoManager) gameplayManager;

        return chapaevoManager.getEntityColor(this);
    }


    public void addListener(AiListener aiListener) {
        listeners.add(aiListener);
    }


    public void removeListener(AiListener aiListener) {
        listeners.remove(aiListener);
    }


    @Override
    public boolean isHuman() {
        return false;
    }


    @Override
    public String toString() {
        return "[AiChapaevoEntity: " +
                getCurrentColor() +
                "]";
    }
}
