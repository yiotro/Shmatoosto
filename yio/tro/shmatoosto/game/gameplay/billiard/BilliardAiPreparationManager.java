package yio.tro.shmatoosto.game.gameplay.billiard;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.AiShotItem;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.object_pool.ObjectPoolYio;

import java.util.ArrayList;

public class BilliardAiPreparationManager {


    BilliardManager billiardManager;
    public RectangleYio innerRect;
    public ArrayList<BPointOfInterest> pointsOfInterest;
    public ArrayList<BPointOfInterest> inspectionList;
    public Ball inspectedBall;
    private PointYio tempPoint, sidePoint, tbPoint;
    public ArrayList<AiShotItem> shotItems;
    public ObjectPoolYio<AiShotItem> poolShotItems;
    private Ball tempBall;
    private ArrayList<Ball> tempList;


    public BilliardAiPreparationManager(BilliardManager billiardManager) {
        this.billiardManager = billiardManager;

        innerRect = new RectangleYio();
        pointsOfInterest = new ArrayList<>();
        inspectionList = new ArrayList<>();
        inspectedBall = null;
        tempPoint = new PointYio();
        tempList = new ArrayList<>();
        shotItems = new ArrayList<>();
        sidePoint = new PointYio();
        tbPoint = new PointYio();
        tempBall = new Ball(billiardManager.gameController.objectsLayer);

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


    public void updateShotItemsByInspectionList() {
        clearShotItems();
        addSomeShotItemsByInspectionList();
    }


    public void updateShotItemsByDoubleShots(boolean includeSingleShots) {
        clearShotItems();

        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return;

        tempBall.setRadius(cue.getTargetRadius());
        double jump = tempBall.collisionRadius / 2;

        for (BPointOfInterest bPointOfInterest : pointsOfInterest) {
            tempList.clear();
            tempBall.position.center.setBy(cue.position.center);
            float distance = cue.position.center.distanceTo(bPointOfInterest.position);
            double a = cue.position.center.angleTo(bPointOfInterest.position);
            int n = (int) (distance / jump);
            for (int k = 0; k < n; k++) {
                tempBall.position.center.relocateRadial(jump, a);
                getObjectsLayer().updateNearbyObjects(tempBall);
                addCollidedBallsToTempList();
                if (tempList.size() > 2) break;
            }

            if (tempList.size() == 2) {
                addDoubleShotByTempList(bPointOfInterest);
            }

            if (includeSingleShots && tempList.size() == 1) {
                addSingleShotByTempList(bPointOfInterest);
            }
        }
    }


    private void addSingleShotByTempList(BPointOfInterest bPointOfInterest) {
        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return;

        Ball targetBall = tempList.get(0);

        tempPoint.setBy(targetBall.position.center);
        tempPoint.relocateRadial(2 * targetBall.collisionRadius, bPointOfInterest.position.angleTo(tempPoint));

        double angle = cue.position.center.angleTo(tempPoint);
        double power = (0.6 + YioGdxGame.random.nextDouble() * 0.4) * cue.maxSpeed;

        addShotItem(angle, power, targetBall);
    }


    private void addDoubleShotByTempList(BPointOfInterest bPointOfInterest) {
        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return;

        Ball closeBall = tempList.get(0);
        Ball farBall = tempList.get(1);

        tempPoint.setBy(farBall.position.center);
        tempPoint.relocateRadial(1.9 * farBall.collisionRadius, bPointOfInterest.position.angleTo(tempPoint));

        tempBall.position.center.setBy(tempPoint);
        tempPoint.setBy(closeBall.position.center);
        tempPoint.relocateRadial(2 * closeBall.collisionRadius, tempBall.position.center.angleTo(tempPoint));

        double angle = cue.position.center.angleTo(tempPoint);
        double power = (0.6 + YioGdxGame.random.nextDouble() * 0.4) * cue.maxSpeed;

        addShotItem(angle, power, closeBall);
    }


    private void addCollidedBallsToTempList() {
        for (Ball nearbyBall : getObjectsLayer().nearbyBalls) {
            if (nearbyBall.isCue()) continue;
            if (tempList.contains(nearbyBall)) continue;
            if (!tempBall.isInCollisionWith(nearbyBall)) continue;

            tempList.add(nearbyBall);
        }
    }


    private void addSomeShotItemsByInspectionList() {
        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return;

        for (BPointOfInterest bPointOfInterest : inspectionList) {
            tempPoint.setBy(inspectedBall.position.center);
            tempPoint.relocateRadial(2 * inspectedBall.collisionRadius, bPointOfInterest.position.angleTo(tempPoint));

            double angle = cue.position.center.angleTo(tempPoint);
            double power = (0.6 + YioGdxGame.random.nextDouble() * 0.4) * cue.maxSpeed;

            addShotItem(angle, power, inspectedBall);
        }
    }


    private void addShotItem(double angle, double power, Ball inspectedBall) {
        AiShotItem next = poolShotItems.getNext();

        next.setAngle(angle);
        next.setPower(power);
        next.setTargetBall(inspectedBall);

        shotItems.add(next);
    }


    private void clearShotItems() {
        for (AiShotItem shotItem : shotItems) {
            poolShotItems.add(shotItem);
        }

        shotItems.clear();
    }


    public void setInspectedBall(Ball inspectedBall) {
        this.inspectedBall = inspectedBall;
    }


    public void setInspectedBallByAim() {
        // this method is slow and should not be used in AI
        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return;

        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        if (billiardAimUiElement == null) return;

        double aimAngle = billiardAimUiElement.aimAngle;
        tempPoint.setBy(cue.position.center);
        double jumpDelta = 0.01 * GraphicsYio.width;
        int c = 500;
        while (c > 0) {
            c--;
            tempPoint.relocateRadial(jumpDelta, aimAngle);
            Ball closestBall = findClosestBallToTempPoint();
            if (closestBall == null) return;
            if (closestBall == cue) continue;
            if (closestBall.position.center.distanceTo(tempPoint) > 2 * closestBall.collisionRadius) continue;

            setInspectedBall(closestBall);
            return;
        }
    }


    private Ball findClosestBallToTempPoint() {
        Ball result = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ball : getObjectsLayer().balls) {
            currentDistance = ball.position.center.distanceTo(tempPoint);
            if (result == null || currentDistance < minDistance) {
                result = ball;
                minDistance = currentDistance;
            }
        }

        return result;
    }


    public void updateInspectionList() {
        inspectionList.clear();

        BPointOfInterest closestPointOfInterest = findClosestPointOfInterest(inspectedBall.position.center);
        inspectionList.add(closestPointOfInterest);

        BPointOfInterest poiByAimAngle = findPoiByAimAngle();
        if (poiByAimAngle != null && poiByAimAngle != closestPointOfInterest) {
            inspectionList.add(poiByAimAngle);
        }
    }


    public BPointOfInterest findPoiByAimAngle() {
        Ball cue = getObjectsLayer().findCue();
        if (cue == null) return null;

        double inspectionAngle = cue.position.center.angleTo(inspectedBall.position.center);

        BPointOfInterest result = null;
        double minAngleDistance = 0;
        double currentAngleDistance;

        for (BPointOfInterest bPointOfInterest : pointsOfInterest) {
            currentAngleDistance = Yio.distanceBetweenAngles(inspectionAngle, inspectedBall.position.center.angleTo(bPointOfInterest.position));
            if (result == null || currentAngleDistance < minAngleDistance) {
                result = bPointOfInterest;
                minAngleDistance = currentAngleDistance;
            }
        }

        return result;
    }


    public BPointOfInterest findClosestPointOfInterest(PointYio pointYio) {
        BPointOfInterest result = null;
        double minDistance = 0;
        double currentDistance;

        for (BPointOfInterest bPointOfInterest : pointsOfInterest) {
            currentDistance = bPointOfInterest.position.distanceTo(pointYio);
            if (result == null || currentDistance < minDistance) {
                result = bPointOfInterest;
                minDistance = currentDistance;
            }
        }

        return result;
    }


    public void onEndCreation() {
        initInnerRect();
        initPointsOfInterest();
    }


    private void initPointsOfInterest() {
        addPointOfInterest(innerRect.x, innerRect.y);
        addPointOfInterest(innerRect.x, innerRect.y + innerRect.height / 2);
        addPointOfInterest(innerRect.x, innerRect.y + innerRect.height);
        addPointOfInterest(innerRect.x + innerRect.width, innerRect.y);
        addPointOfInterest(innerRect.x + innerRect.width, innerRect.y + innerRect.height / 2);
        addPointOfInterest(innerRect.x + innerRect.width, innerRect.y + innerRect.height);
    }


    private void addPointOfInterest(float x, float y) {
        RectangleYio position = billiardManager.board.position;
        tempPoint.set(position.x + position.width / 2, position.y + position.height / 2);
        BPointOfInterest bPointOfInterest = new BPointOfInterest();
        bPointOfInterest.position.set(x, y);
        bPointOfInterest.setAngleFromCenter(tempPoint.angleTo(bPointOfInterest.position));
        pointsOfInterest.add(bPointOfInterest);
    }


    private void initInnerRect() {
        innerRect.setBy(billiardManager.board.position);
        compressInnerRect(billiardManager.board.cRadius);
        compressInnerRect(getObjectsLayer().findCue().collisionRadius);
    }


    private ObjectsLayer getObjectsLayer() {
        return getGameController().objectsLayer;
    }


    private GameController getGameController() {
        return billiardManager.gameController;
    }


    public PointYio getSidePoint() {
        return sidePoint;
    }


    private void compressInnerRect(float delta) {
        innerRect.x += delta;
        innerRect.y += delta;
        innerRect.width -= 2 * delta;
        innerRect.height -= 2 * delta;
    }
}
