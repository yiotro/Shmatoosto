package yio.tro.shmatoosto.menu.elements.gameplay;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiSoccerEntity;
import yio.tro.shmatoosto.game.player_entities.HumanSoccerEntity;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.ArrayList;

public class SoccerAimUiElement extends InterfaceElement<SoccerAimUiElement> {


    public double aimAngle;
    public BColorYio filterColor;
    public Ball selectedBall;
    PointYio tempPoint;
    double targetAngle, atmStartAngle;
    boolean autoTargetMode;
    double targetPower;
    public double referenceAngle;
    int referenceUpdateCountDown;
    public FactorYio selectionRadiusFactor, selectionAlphaFactor;
    public boolean referenceUpdated;
    public double aimDistance, aimPower;
    public double maxPower;
    public FactorYio stickAlphaFactor, aimAlphaFactor;
    public FactorYio atmAngleFactor, atmPowerFactor;
    boolean touched;
    public ArrayList<CircleYio> aimLine;
    float alDelta; // aim line delta distance between circles
    boolean readyToShowAim, readyToLaunchBall;
    public FactorYio shotFactor;
    public PointYio selectedPoint;
    public double stickViewAngle;
    boolean holdMode;
    public boolean hasShotReadyOnHold;
    boolean holdReadyToReleaseFlag;
    public PointYio specialAimPoint;
    public Ball firstBallToHit;
    public PointYio hintStart, hintEnd;


    public SoccerAimUiElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        resetAimAngle();
        filterColor = null;
        resetSelectedBall();
        tempPoint = new PointYio();
        targetAngle = 0;
        autoTargetMode = false;
        targetPower = 0;
        referenceAngle = aimAngle;
        referenceUpdateCountDown = 0;
        aimDistance = 0;
        referenceUpdated = false;
        aimPower = 0;
        maxPower = 0.4f * GraphicsYio.width;
        touched = false;
        readyToShowAim = false;
        selectionRadiusFactor = new FactorYio();
        selectionAlphaFactor = new FactorYio();
        stickAlphaFactor = new FactorYio();
        aimAlphaFactor = new FactorYio();
        shotFactor = new FactorYio();
        stickViewAngle = aimAngle;
        selectedPoint = new PointYio();
        readyToLaunchBall = false;
        atmAngleFactor = new FactorYio();
        atmPowerFactor = new FactorYio();
        holdMode = false;
        hasShotReadyOnHold = false;
        holdReadyToReleaseFlag = false;
        specialAimPoint = new PointYio();
        firstBallToHit = null;
        hintStart = new PointYio();
        hintEnd = new PointYio();

        initLine();
    }


    private void initLine() {
        alDelta = 0.05f * GraphicsYio.width;
        aimLine = new ArrayList<>();
        double maxLength = 1.1 * Yio.distance(0, 0, GraphicsYio.width, GraphicsYio.height);
        int n = (int) (maxLength / alDelta) + 1;
        for (int i = 0; i < n; i++) {
            CircleYio circle = new CircleYio();
            circle.setRadius(0.007f * GraphicsYio.width);
            aimLine.add(circle);
        }
    }


    public void applyAutoTarget(Ball selectedBall, double power, double angle) {
        // power from 0 to 1

        resetFactorsOnTouchDown();
        setSelectedBall(selectedBall);
        autoTargetMode = true;
        targetAngle = angle;
        targetPower = power;

        prepareAimAngleForAutoTarget();
        atmStartAngle = aimAngle;

        atmAngleFactor.reset();
        atmAngleFactor.appear(6, 1);

        atmPowerFactor.reset();
        atmPowerFactor.appear(6, 0.85);

        applyAimAlphaFactor();
    }


    public void fixSelectedBallReferenceAfterSimulation() {
        selectedBall = getGameController().objectsLayer.findClosestBall(selectedPoint);
    }


    private void prepareAimAngleForAutoTarget() {
        while (aimAngle < targetAngle - Math.PI) {
            aimAngle += 2 * Math.PI;
        }

        while (aimAngle > targetAngle + Math.PI) {
            aimAngle -= 2 * Math.PI;
        }
    }


    private void updateAimLine() {
        GameController gameController = getGameController();
        if (gameController.actionMode) return;

        if (selectedBall == null) return;

        tempPoint.setBy(selectedPoint);
        for (CircleYio circleYio : aimLine) {
            tempPoint.relocateRadial(alDelta, aimAngle);
            circleYio.center.setBy(tempPoint);
        }
    }


    @Override
    protected SoccerAimUiElement getThis() {
        return this;
    }


    @Override
    public void move() {
        moveSelectionEffect();
        moveReferenceAngle();
        stickAlphaFactor.move();
        aimAlphaFactor.move();
        updateAimLine();
        shotFactor.move();
        checkToLaunchBall();
        moveStickViewAngle();
        moveHoldMode();
        updateSpecialAimPoint();
    }


    private void updateSpecialAimPoint() {
        if (selectedBall == null) return;

        double speed = convertAimPowerToSpeed(aimPower);
        double x = 0;
        while (speed > 0.7 * GraphicsYio.borderThickness) {
            x += speed;
            speed *= 1 - selectedBall.getBoardFriction();
        }

        specialAimPoint.setBy(selectedPoint);
        specialAimPoint.relocateRadial(x, aimAngle);
        specialAimPoint.setBy(findClosestAimLinePoint(specialAimPoint).center);
    }


    private CircleYio findClosestAimLinePoint(PointYio src) {
        CircleYio closestPoint = null;
        double minDistance = 0;
        double currentDistance;

        for (CircleYio circleYio : aimLine) {
            currentDistance = circleYio.center.distanceTo(src);
            if (closestPoint == null || currentDistance < minDistance) {
                closestPoint = circleYio;
                minDistance = currentDistance;
            }
        }

        return closestPoint;
    }


    private void moveHoldMode() {
        if (!holdMode) return;

        if (hasShotReadyOnHold && holdReadyToReleaseFlag) {
            makeShot();
        }
    }


    private void moveAutoTarget() {
        if (!autoTargetMode) return;

        atmAngleFactor.move();
        atmPowerFactor.move();

        aimAngle = atmStartAngle + atmAngleFactor.get() * (targetAngle - atmStartAngle);
        aimPower = atmPowerFactor.get() * targetPower;

        if (atmPowerFactor.get() == 1 && atmAngleFactor.get() == 1) {
            autoTargetMode = false;
            makeShot();
        }
    }


    private void moveStickViewAngle() {
        stickViewAngle += 0.3 * (aimAngle - stickViewAngle);
    }


    public double convertAimPowerToSpeed(double aimPower) {
        if (getGameController().isHumanTurn()) {
            aimPower *= aimPower;

            if (aimPower < 0.1) {
                aimPower = 0.1;
            }
        }

        return aimPower * selectedBall.maxSpeed;
    }


    public void fixAfterSimulation() {
        setSelectedBall(getGameController().objectsLayer.findClosestBall(selectedPoint));
    }


    private void checkToLaunchBall() {
        if (!readyToLaunchBall) return;
        if (shotFactor.get() < 1) return;

        readyToLaunchBall = false;

        selectedBall.setSpeed(convertAimPowerToSpeed(aimPower), aimAngle);
        getGameController().applyActionMode();

        for (Ball ball : getGameController().objectsLayer.balls) {
            if (ball.getSpeedValue() > 0) {
                ball.setFrozen(false);
            }
        }

        destroy();
    }


    private void moveSelectionEffect() {
        selectionRadiusFactor.move();
        selectionAlphaFactor.move();
    }


    private void moveReferenceAngle() {
        if (referenceUpdated) return;

        if (referenceUpdateCountDown > 0) {
            referenceUpdateCountDown--;

            if (referenceUpdateCountDown == 0) {
                referenceUpdated = true;
                referenceAngle = aimAngle;
            }
        }
    }


    public void applyTargetShot(double powerFraction, double angle) {
        autoTargetMode = true;
        targetPower = powerFraction;
        targetAngle = angle;
    }


    public void resetAimAngle() {
        setAimAngle(Math.PI / 2);
    }


    public void setAimAngle(double aimAngle) {
        this.aimAngle = aimAngle;
        prepareStickViewAngle();
        updateFirstBallToHit();
    }


    private void updateHintPoints() {
        if (firstBallToHit == null) return;

        hintStart.setBy(firstBallToHit.position.center);
        hintEnd.setBy(hintStart);

        double a = tempPoint.angleTo(hintStart);
        hintEnd.relocateRadial(4 * selectedBall.collisionRadius, a);
    }


    private void prepareStickViewAngle() {
        while (stickViewAngle > aimAngle + Math.PI) {
            stickViewAngle -= 2 * Math.PI;
        }

        while (stickViewAngle < aimAngle - Math.PI) {
            stickViewAngle += 2 * Math.PI;
        }
    }


    @Override
    public void onDestroy() {
        hideAim();
    }


    private void hideAim() {
        aimAlphaFactor.destroy(3, 5);
        stickAlphaFactor.destroy(3, 5);
    }


    @Override
    public void onAppear() {
        autoTargetMode = false;
        firstBallToHit = null;
    }


    void updateFirstBallToHit() {
        firstBallToHit = null;
        if (selectedBall == null) return;

        tempPoint.setBy(selectedBall.position.center);
        double step = selectedBall.collisionRadius / 5;

        int n = (int) ((GraphicsYio.width + GraphicsYio.height) / step) + 1;
        if (n > 1000) {
            n = 1000;
        }

        for (int i = 0; i < n; i++) {
            if (firstBallToHit != null) break;

            tempPoint.relocateRadial(step, aimAngle);
            getGameController().objectsLayer.updateNearbyObjects(tempPoint);

            for (Ball nearbyBall : getGameController().objectsLayer.nearbyBalls) {
                if (nearbyBall == selectedBall) continue;
                if (nearbyBall.hidden) continue;
                if (tempPoint.distanceTo(nearbyBall.position.center) > selectedBall.collisionRadius + nearbyBall.collisionRadius) continue;

                if (firstBallToHit == null) {
                    firstBallToHit = nearbyBall;
                } else {
                    double d1 = firstBallToHit.position.center.distanceTo(selectedBall.position.center);
                    double d2 = nearbyBall.position.center.distanceTo(selectedBall.position.center);
                    if (d2 < d1) {
                        firstBallToHit = nearbyBall;
                    }
                }
            }
        }

        updateHintPoints();
    }


    void updateAimByCurrentTouch() {
        if (selectedBall == null) return;

        setAimAngle(selectedBall.position.center.angleTo(currentTouch));
        applyReadyToUpdateReferenceAngle();

        boolean bigEnough = isAimDistanceBigEnough();
        aimDistance = selectedBall.position.center.distanceTo(currentTouch);
        if (bigEnough != isAimDistanceBigEnough()) {
            if (isAimDistanceBigEnough()) {
                aimAlphaFactor.appear(1, 2);
            } else {
                aimAlphaFactor.destroy(1, 2);
            }
        }

        updateAimPower();
    }


    private void updateAimPower() {
        if (selectedBall == null) {
            aimPower = 0;
            return;
        }

        aimPower = aimDistance - selectedBall.collisionRadius;
        aimPower /= maxPower;

        if (aimPower < 0) {
            aimPower = 0;
        }

        if (aimPower > 1) {
            aimPower = 1;
        }
    }


    public boolean isAimDistanceBigEnough() {
        if (selectedBall == null) return false;

        return aimDistance > 2 * selectedBall.collisionRadius;
    }


    private void applyReadyToUpdateReferenceAngle() {
        referenceUpdateCountDown = 3;
        referenceUpdated = false;
    }


    @Override
    public boolean checkToPerformAction() {
        moveAutoTarget();
        return false;
    }


    @Override
    public boolean touchDown() {
        if (isInAiMode()) return false;
        if (hasShotReadyOnHold) return false;

        touched = true;

        resetSelectedBall();
        checkToSelectBall();
        resetFactorsOnTouchDown();
        aimDistance = 0;
        getGameController().objectsLayer.deselectAllBalls();

        if (selectedBall != null) {
            readyToShowAim = true;
        }

        return true;
    }


    private boolean isInAiMode() {
        if (GameRules.twoPlayerMode) return false;

        if (getGameController().isHumanTurn() && holdMode) return false;
        if (!getGameController().isHumanTurn() && !holdMode) return false;

        return true;
    }


    private void resetFactorsOnTouchDown() {
        stickAlphaFactor.reset();
        aimAlphaFactor.reset();
        shotFactor.reset();
    }


    private void checkToSelectBall() {
        Ball closestBall = null;
        double minDistance = 0;
        double currentDistance;

        for (Ball ball : getGameController().objectsLayer.balls) {
            if (ball.getColor() != filterColor) continue;

            currentDistance = currentTouch.distanceTo(ball.position.center);
            if (closestBall == null || currentDistance < minDistance) {
                closestBall = ball;
                minDistance = currentDistance;
            }
        }

        if (closestBall == null) return;
        if (closestBall.position.center.distanceTo(currentTouch) > 4 * closestBall.collisionRadius) return;

        setSelectedBall(closestBall);
        applySelectionEffect();
    }


    private void applyAimAlphaFactor() {
        stickAlphaFactor.reset();
        stickAlphaFactor.appear(1, 0.8);

        aimAlphaFactor.reset();
        aimAlphaFactor.appear(1, 0.8);
    }


    private void applySelectionEffect() {
        selectionRadiusFactor.reset();
        selectionRadiusFactor.appear(3, 3);

        selectionAlphaFactor.setValues(1, 0);
        selectionAlphaFactor.destroy(1, 0.7);
    }


    @Override
    public boolean touchDrag() {
        if (isInAiMode()) return false;
        if (!touched) return false;

        updateAimByCurrentTouch();

        if (readyToShowAim) {
            readyToShowAim = false;
            applyAimAlphaFactor();
        }

        return true;
    }


    @Override
    public boolean touchUp() {
        if (isInAiMode()) return false;
        if (!touched) return false;

        touched = false;

        if (Yio.distanceBetweenAngles(aimAngle, referenceAngle) < 0.15) {
            setAimAngle(referenceAngle);
        }

        if (isAimDistanceBigEnough()) {
            makeShot();
        } else {
            hideAim();

            AbstractPlayerEntity aimingPlayerEntity = getGameController().getCurrentPlayerEntity();

            if (Scenes.secondarySoccerAimUI.soccerAimUiElement == this) {
                SoccerManager soccerManager = getSoccerManager();
                aimingPlayerEntity = getGameController().playerEntities.get(soccerManager.getEnemyEntityIndex(aimingPlayerEntity.getIndex()));
            }

            if (aimingPlayerEntity instanceof HumanSoccerEntity) {
                ((HumanSoccerEntity) aimingPlayerEntity).selectCurrentBalls();
            }
        }

        return true;
    }


    private void resetSelectedBall() {
        selectedBall = null;
    }


    private void makeShot() {
        aimAlphaFactor.destroy(1, 2);

        if (holdMode && !hasShotReadyOnHold) {
            hasShotReadyOnHold = true;
            holdReadyToReleaseFlag = false;

            Scenes.secondarySoccerAimUI.create();
            SoccerManager soccerManager = getSoccerManager();
            int enemyEntityIndex = soccerManager.getEnemyEntityIndex(getGameController().currentEntityIndex);
            BColorYio enemyColor = soccerManager.getEntityColor(enemyEntityIndex);
            Scenes.secondarySoccerAimUI.soccerAimUiElement.setFilterColor(enemyColor);

            AbstractPlayerEntity enemyEntity = getGameController().playerEntities.get(enemyEntityIndex);
            if (enemyEntity.isHuman()) {
                getGameController().objectsLayer.selectBalls(enemyColor);
            } else {
                AiSoccerEntity aiSoccerEntity = (AiSoccerEntity) enemyEntity;
                aiSoccerEntity.performShot(Scenes.secondarySoccerAimUI.soccerAimUiElement);
            }

            return;
        }

        hasShotReadyOnHold = false;
        holdReadyToReleaseFlag = false;

        shotFactor.reset();
        shotFactor.appear(0, 4);

        readyToLaunchBall = true;
        fixAfterSimulation();
        notifyAnotherSoccerAimUIAboutShot();
    }


    private SoccerManager getSoccerManager() {
        return (SoccerManager) getGameController().gameplayManager;
    }


    private void notifyAnotherSoccerAimUIAboutShot() {
        for (InterfaceElement interfaceElement : menuControllerYio.getInterfaceElements()) {
            if (interfaceElement == this) continue;

            if (interfaceElement instanceof SoccerAimUiElement) {
                SoccerAimUiElement soccerAimUiElement = (SoccerAimUiElement) interfaceElement;
                if (!soccerAimUiElement.holdMode) continue;

                soccerAimUiElement.notifyAboutHoldReadyToRelease();
            }
        }
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderSoccerAimUI;
    }


    public void setFilterColor(BColorYio filterColor) {
        this.filterColor = filterColor;
    }


    public SoccerAimUiElement setHoldMode(boolean holdMode) {
        this.holdMode = holdMode;
        return this;
    }


    public boolean inHoldMode() {
        return holdMode;
    }


    public void notifyAboutHoldReadyToRelease() {
        holdReadyToReleaseFlag = true;
    }


    public void setSelectedBall(Ball selectedBall) {
        this.selectedBall = selectedBall;
        selectedPoint.setBy(selectedBall.position.center);
    }
}
