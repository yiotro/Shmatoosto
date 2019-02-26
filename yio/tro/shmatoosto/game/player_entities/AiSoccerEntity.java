package yio.tro.shmatoosto.game.player_entities;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.AiShotItem;
import yio.tro.shmatoosto.game.gameplay.SimResults;
import yio.tro.shmatoosto.game.gameplay.SimulationManager;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.menu.elements.gameplay.SoccerAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;
import java.util.Collections;

public class AiSoccerEntity extends AbstractPlayerEntity {


    private SimulationManager simulationManager;
    private SoccerManager soccerManager;
    private ObjectsLayer objectsLayer;
    SoccerAimUiElement currentAimUI;
    ArrayList<AiShotItem> shotItems;
    ObjectPoolYio<AiShotItem> poolShotItems;
    PointYio tempPoint;
    private long startTime;
    ArrayList<Ball> ownedBalls;
    AiShotItem enemyShotInfo;
    Ball tempBall;


    public AiSoccerEntity(GameController gameController) {
        super(gameController);

        currentAimUI = null;
        shotItems = new ArrayList<>();
        tempPoint = new PointYio();
        ownedBalls = new ArrayList<>();
        enemyShotInfo = new AiShotItem();
        tempBall = new Ball(gameController.objectsLayer);

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
        updateReferences();
        Scenes.soccerAimUI.create();
        Scenes.soccerAimUI.soccerAimUiElement.setFilterColor(getCurrentColor());

        performShot(Scenes.soccerAimUI.soccerAimUiElement);
    }


    public void performShot(SoccerAimUiElement soccerAimUiElement) {
        updateReferences();
        if (soccerManager.isReadyToFinishGame()) return;

        startTime = System.currentTimeMillis();
        setCurrentAimUI(soccerAimUiElement);

        decideWhichShotToPerform();
    }


    private void decideWhichShotToPerform() {
        Ball soccerBall = findSoccerBall();
        RectangleYio ownGoalArea = soccerManager.getGoalArea(getIndex());
        RectangleYio enemyGoalArea = soccerManager.getGoalArea(soccerManager.getEnemyEntityIndex(getIndex()));

        boolean isThereImmediateThreat = isPathToGoalAreaClear(soccerBall, ownGoalArea);
        boolean isThereAnEasyAttackAvailable = isPathToGoalAreaClear(soccerBall, enemyGoalArea);
        boolean isOwnBallCloserToSoccerBall = isOwnBallCloserToSoccerBall();
        boolean hasInfoAboutEnemyShot = hasInfoAboutEnemyShot();

        if (isOwnBallCloserToSoccerBall && isThereAnEasyAttackAvailable) {
            performAttackShot();
            return;
        }

        if (isThereImmediateThreat) {
            performBlockingShot();
            return;
        }

        if (isOwnBallCloserToSoccerBall && hasInfoAboutEnemyShot) {
            performAttackShot();
            return;
        }

        performStrategicShot();
    }


    private boolean hasInfoAboutEnemyShot() {
        SoccerAimUiElement anotherAimUI = getAnotherAimUI();
        return anotherAimUI != null && anotherAimUI.inHoldMode();
    }


    private boolean isOwnBallCloserToSoccerBall() {
        Ball soccerBall = findSoccerBall();
        Ball closestOwnBall = objectsLayer.findClosestBall(soccerBall.position.center, getCurrentColor());
        Ball closestEnemyBall = objectsLayer.findClosestBall(soccerBall.position.center, getEnemyColor());

        float ownDistance = closestOwnBall.position.center.distanceTo(soccerBall.position.center);
        float enemyDistance = closestEnemyBall.position.center.distanceTo(soccerBall.position.center);

        return ownDistance < enemyDistance;
    }


    private boolean isPathToPointClear(Ball ball, PointYio pointYio) {
        tempBall.setRadius(ball.position.radius);
        tempBall.updateCollisionRadius();
        tempBall.position.center.setBy(ball.position.center);

        float distance = tempBall.position.center.distanceTo(pointYio);
        double angle = tempBall.position.center.angleTo(pointYio);
        float step = tempBall.collisionRadius;
        int n = (int) (distance / step) - 1;
        if (n > 50) return false; // if step is too small

        for (int i = 0; i < n; i++) {
            tempBall.position.center.relocateRadial(step, angle);
            gameController.objectsLayer.updateNearbyObjects(tempBall);
            for (Ball nearbyBall : gameController.objectsLayer.nearbyBalls) {
                if (nearbyBall == ball) continue;
                if (nearbyBall.hidden) continue;
                if (!tempBall.isInCollisionWith(nearbyBall)) continue;

                return false;
            }
        }

        return true;
    }


    private boolean isPathToGoalAreaClear(Ball ball, RectangleYio goalArea) {
        tempPoint.y = goalArea.y + goalArea.height / 2;

        tempPoint.x = goalArea.x;
        if (isPathToPointClear(ball, tempPoint)) return true;

        tempPoint.x = goalArea.x + goalArea.width / 2;
        if (isPathToPointClear(ball, tempPoint)) return true;

        tempPoint.x = goalArea.x + goalArea.width;
        if (isPathToPointClear(ball, tempPoint)) return true;

        return false;
    }


    private void performBlockingShot() {
        updateEnemyShotInfo();
        updateShotItemsForBlockingShot();
        pickNiceShot();
    }


    private void updateShotItemsForBlockingShot() {
        clearShotItems();
        addShotItemsForBlockingShot();
    }


    private void addShotItemsForBlockingShot() {
        Ball soccerBall = findSoccerBall();
        if (soccerBall == null) return;

        Ball closestOwnBall = objectsLayer.findClosestBall(soccerBall.position.center, getCurrentColor());
        if (closestOwnBall == null) return;

        RectangleYio goalArea = soccerManager.getGoalArea(getIndex());
        float a = soccerBall.position.center.angleTo(goalArea);

        tempPoint.setBy(soccerBall.position.center);
        for (int i = 0; i < 20; i++) {
            addShotItem(closestOwnBall, 1, closestOwnBall.position.center.angleTo(tempPoint));
            tempPoint.relocateRadial(soccerBall.collisionRadius, a);
        }
    }


    private void updateEnemyShotInfo() {
        SoccerAimUiElement anotherAimUI = getAnotherAimUI();
        if (anotherAimUI != null && anotherAimUI.inHoldMode()) {
            anotherAimUI.fixAfterSimulation();
            enemyShotInfo.power = anotherAimUI.aimPower;
            enemyShotInfo.angle = anotherAimUI.aimAngle;
            enemyShotInfo.setSelectedBall(anotherAimUI.selectedBall);
        } else {
            RectangleYio goalArea = soccerManager.getGoalArea(getIndex());
            Ball soccerBall = findSoccerBall();
            double a = soccerBall.position.center.angleTo(goalArea);
            if (a == 0) {
                a = Yio.getRandomAngle();
            }
            Ball ballWithClosestAngleToTarget = findBallWithClosestAngleToTarget(getEnemyColor(), soccerBall, a);
            enemyShotInfo.setSelectedBall(ballWithClosestAngleToTarget);
            enemyShotInfo.angle = ballWithClosestAngleToTarget.position.center.angleTo(soccerBall.position.center);
            enemyShotInfo.power = 1;
        }
    }


    private Ball findBallWithClosestAngleToTarget(BColorYio filterColor, Ball target, double neededAngle) {
        Ball bestBall = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ball : objectsLayer.balls) {
            if (ball.getColor() != filterColor) continue;

            currentDistance = Yio.distanceBetweenAngles(ball.position.center.angleTo(target.position.center), neededAngle);
            if (bestBall == null || currentDistance < minDistance) {
                bestBall = ball;
                minDistance = currentDistance;
            }
        }

        return bestBall;
    }


    private void performStrategicShot() {
        int ownDefensiveBalls = countOwnDefensiveBalls();
//        int enemyDefensiveBalls = countEnemyDefensiveBalls();

        if (ownDefensiveBalls < 2) {
            tryToImproveOwnDefense();
            return;
        }

        tryToDestroyEnemyDefense();
    }


    private void tryToDestroyEnemyDefense() {
        Ball enemyDefensiveBall = getRandomEnemyDefensiveBall();
        Ball ownRandomNonDefensiveBall = findOwnRandomNonDefensiveBall();
        tempPoint.setBy(enemyDefensiveBall.position.center);
        tempPoint.relocateRadial(YioGdxGame.random.nextDouble() * 1.2 * enemyDefensiveBall.collisionRadius, Yio.getRandomAngle());
        applyShot(ownRandomNonDefensiveBall, 1, ownRandomNonDefensiveBall.position.center.angleTo(tempPoint));
    }


    private Ball getRandomEnemyDefensiveBall() {
        int enemyEntityIndex = soccerManager.getEnemyEntityIndex(getIndex());
        RectangleYio defenseArea = soccerManager.getDefenseArea(enemyEntityIndex);
        BColorYio enemyColor = getEnemyColor();

        int c = 25;
        Ball randomBall = null;
        while (c > 0) {
            c--;
            randomBall = getRandomBall(enemyColor);
            if (defenseArea.isPointInside(randomBall.position.center)) {
                return randomBall;
            }
        }

        return randomBall;
    }


    private void tryToImproveOwnDefense() {
        Ball ownClosestNonDefensiveBall = findOwnClosestNonDefensiveBall();
        RectangleYio defenseArea = soccerManager.getDefenseArea(getIndex());

        int a = 5;

        double power = 0;
        double angle = 0;
        boolean shotFound = false;

        while (a > 0 && !shotFound) {
            a--;
            tempPoint.x = defenseArea.x + YioGdxGame.random.nextFloat() * defenseArea.width;
            tempPoint.y = defenseArea.y + YioGdxGame.random.nextFloat() * defenseArea.height;
            angle = ownClosestNonDefensiveBall.position.center.angleTo(tempPoint);

            for (int i = 0; i < 15; i++) {
                power = 0.15 + i * 0.05;
                PointYio predictionPoint = soccerManager.predictBallPositionAfterShot(ownClosestNonDefensiveBall, power, angle);

                if (defenseArea.isPointInside(predictionPoint)) {
                    shotFound = true;
                    break;
                }
            }
        }

        applyShot(ownClosestNonDefensiveBall, power, angle);
    }


    private Ball findOwnClosestNonDefensiveBall() {
        updateLists();
        RectangleYio defenseArea = soccerManager.getDefenseArea(getIndex());

        Ball closestBall = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ownedBall : ownedBalls) {
            if (defenseArea.isPointInside(ownedBall.position.center)) continue;

            currentDistance = ownedBall.position.center.distanceTo(defenseArea);
            if (closestBall == null || currentDistance < minDistance) {
                closestBall = ownedBall;
                minDistance = currentDistance;
            }
        }

        return closestBall;
    }


    private Ball findOwnRandomNonDefensiveBall() {
        RectangleYio defenseArea = soccerManager.getDefenseArea(getIndex());

        int c = 25;
        Ball randomBall = null;
        while (c > 0) {
            c--;

            randomBall = getRandomBall(getCurrentColor());
            if (!defenseArea.isPointInside(randomBall.position.center)) {
                return randomBall;
            }
        }

        return randomBall;
    }


    private int countEnemyDefensiveBalls() {
        int c = 0;

        int enemyEntityIndex = soccerManager.getEnemyEntityIndex(getIndex());
        RectangleYio defenseArea = soccerManager.getDefenseArea(enemyEntityIndex);
        BColorYio enemyColor = getEnemyColor();
        for (Ball ball : objectsLayer.balls) {
            if (ball.getColor() != enemyColor) continue;
            if (!defenseArea.isPointInside(ball.position.center)) continue;

            c++;
        }

        return c;
    }


    private int countOwnDefensiveBalls() {
        int c = 0;

        RectangleYio defenseArea = soccerManager.getDefenseArea(getIndex());
        for (Ball ball : objectsLayer.balls) {
            if (ball.isSoccerBall()) continue;
            if (ball.hidden) continue;
            if (!defenseArea.isPointInside(ball.position.center)) continue;

            c++;
        }

        return c;
    }


    private void performAttackShot() {
        updateShotItemsForAttack();
        pickNiceShot();
    }


    private void pickNiceShot() {
        if (shotItems.size() == 0) return;

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

        removeBadShotItems();

        AiShotItem bestShotItem = findShotItemWithBestScoreDelta();
        if (bestShotItem != null) {
            applyShot(bestShotItem);
        }
    }


    private void showShotItemsInConsole() {
        System.out.println();
        System.out.println("AiSoccerEntity.showShotItemsInConsole");
        for (AiShotItem shotItem : shotItems) {
            System.out.println("- " + shotItem);
        }
    }


    private boolean isShotItemGoodEnough(AiShotItem aiShotItem) {
        return aiShotItem.scoreDelta == 0; // goal
    }


    private AiShotItem findShotItemWithBestScoreDelta() {
        AiShotItem bestItem = null;

        for (AiShotItem shotItem : shotItems) {
            if (bestItem == null || shotItem.scoreDelta < bestItem.scoreDelta) {
                bestItem = shotItem;
            }
        }

        return bestItem;
    }


    private SimResults performSimulation(AiShotItem shotItem) {
        simulationManager.setCurrentColor(getCurrentColor());
        simulationManager.setSimulationParameters(shotItem.selectedBall, shotItem.angle, shotItem.power * shotItem.selectedBall.maxSpeed);

        SoccerAimUiElement anotherAimUI = getAnotherAimUI();
        if (anotherAimUI != null && anotherAimUI.inHoldMode()) {
            anotherAimUI.fixAfterSimulation();
            simulationManager.setSimulationParameters(
                    shotItem.selectedBall, shotItem.power * shotItem.selectedBall.maxSpeed, shotItem.angle,
                    anotherAimUI.selectedBall, anotherAimUI.aimPower, anotherAimUI.aimAngle
            );
        }

        return simulationManager.perform();
    }


    private SoccerAimUiElement getAnotherAimUI() {
        if (currentAimUI == Scenes.soccerAimUI.soccerAimUiElement) {
            return Scenes.secondarySoccerAimUI.soccerAimUiElement;
        } else {
            return Scenes.soccerAimUI.soccerAimUiElement;
        }
    }


    private void updateShotItemsForAttack() {
        clearShotItems();
        addShotItemsBySoccerBall();
        addAttackGoalAreaShotItems();
    }


    private void addAttackGoalAreaShotItems() {
        Ball soccerBall = findSoccerBall();
        if (soccerBall == null) return;

        RectangleYio goalArea = soccerManager.getGoalArea(soccerManager.getEnemyEntityIndex(getIndex()));
        Ball randomOwnBall = getRandomBall(getCurrentColor());

        for (int i = 0; i < 20; i++) {
            tempPoint.x = goalArea.x + YioGdxGame.random.nextFloat() * goalArea.width;
            tempPoint.y = goalArea.y + goalArea.height / 2;
            double a = tempPoint.angleTo(soccerBall.position.center);

            tempPoint.setBy(soccerBall.position.center);
            tempPoint.relocateRadial(soccerBall.collisionRadius + randomOwnBall.collisionRadius, a);

            Ball ballWithClosestAngle = findBallWithClosestAngleToTarget(getCurrentColor(), soccerBall, Math.PI + a);
            addShotItem(ballWithClosestAngle, 1, ballWithClosestAngle.position.center.angleTo(tempPoint));
        }
    }


    private void removeBadShotItems() {
        for (int i = shotItems.size() - 1; i >= 0; i--) {
            AiShotItem aiShotItem = shotItems.get(i);
            if (aiShotItem.scoreDelta >= 0) continue;
            if (shotItems.size() == 1) break;

            shotItems.remove(i);
        }
    }


    private void addShotItemsBySoccerBall() {
        Ball soccerBall = findSoccerBall();
        if (soccerBall == null) return;

        updateLists();
        updateDistancesToSoccerBall();
        Collections.sort(ownedBalls);

        for (int i = 0; i < Math.min(2, ownedBalls.size()); i++) {
            for (int k = 0; k < getNumberOfAttackShotItemsForEachBall(); k++) {
                addShotItemByBallPair(ownedBalls.get(i), soccerBall);
            }
        }
    }


    private int getNumberOfAttackShotItemsForEachBall() {
        switch (GameRules.difficulty) {
            default:
            case 0:
                return 10;
            case 1:
                return 15;
            case 2:
                return 50;
        }
    }


    private void addShotItemByBallPair(Ball selectedBall, Ball targetBall) {
        if (selectedBall == null) return;
        if (targetBall == null) return;

        tempPoint.setBy(targetBall.position.center);
        tempPoint.relocateRadial(YioGdxGame.random.nextDouble() * getAttackShotDelta() * targetBall.collisionRadius, Yio.getRandomAngle());

        addShotItem(selectedBall, 0.7 + 0.3 * YioGdxGame.random.nextDouble(), selectedBall.position.center.angleTo(tempPoint));
    }


    private double getAttackShotDelta() {
        switch (GameRules.difficulty) {
            default:
            case 0:
                return 1.0;
            case 1:
                return 1.3;
            case 2:
                return 4;
        }
    }


    private void addShotItem(Ball selectedBall, double power, double angle) {
        AiShotItem next = poolShotItems.getNext();

        next.setSelectedBall(selectedBall);
        next.setPower(power);
        next.setAngle(angle);

        shotItems.add(next);
    }


    private void updateDistancesToSoccerBall() {
        Ball soccerBall = findSoccerBall();
        if (soccerBall == null) return;

        for (Ball ownedBall : ownedBalls) {
            ownedBall.flagDistance = ownedBall.position.center.fastDistanceTo(soccerBall.position.center);
        }
    }


    private Ball findSoccerBall() {
        return objectsLayer.findSoccerBall();
    }


    private void updateLists() {
        ownedBalls.clear();

        BColorYio currentColor = getCurrentColor();
        for (Ball ball : objectsLayer.balls) {
            if (ball.getColor() == currentColor) {
                ownedBalls.add(ball);
            }
        }
    }


    private void clearShotItems() {
        for (AiShotItem shotItem : shotItems) {
            poolShotItems.add(shotItem);
        }

        shotItems.clear();
    }


    private void applyShot(AiShotItem aiShotItem) {
        if (aiShotItem == null) return;

        fixShotItemAfterSimulation(aiShotItem);
        applyShot(aiShotItem.selectedBall, aiShotItem.power, aiShotItem.angle);
    }


    private void fixShotItemAfterSimulation(AiShotItem aiShotItem) {
        aiShotItem.setSelectedBall(objectsLayer.findClosestBall(aiShotItem.selectedPoint));
    }


    private void applyShot(Ball ball, double power, double angle) {
        angle = getSlightlyChangedAngle(angle);
        currentAimUI.applyAutoTarget(ball, power, angle);
    }


    private double getSlightlyChangedAngle(double srcAngle) {
        switch (GameRules.difficulty) {
            default:
                return srcAngle;
            case 0:
                return srcAngle + 0.02 * (2 * YioGdxGame.random.nextDouble() - 1);
        }
    }


    public void setCurrentAimUI(SoccerAimUiElement currentAimUI) {
        this.currentAimUI = currentAimUI;
    }


    private void updateReferences() {
        objectsLayer = gameController.objectsLayer;
        simulationManager = objectsLayer.simulationManager;
        soccerManager = (SoccerManager) gameController.gameplayManager;
    }


    private Ball getRandomBall(BColorYio filterColor) {
        ArrayList<Ball> balls = objectsLayer.balls;

        while (true) {
            Ball randomBall = balls.get(YioGdxGame.random.nextInt(balls.size()));

            if (randomBall.getColor() == filterColor) {
                return randomBall;
            }
        }
    }


    public BColorYio getEnemyColor() {
        return soccerManager.getEntityColor(soccerManager.getEnemyEntityIndex(getIndex()));
    }


    public BColorYio getCurrentColor() {
        return soccerManager.getEntityColor(this);
    }


    @Override
    public boolean isHuman() {
        return false;
    }
}
