package yio.tro.shmatoosto.menu.elements.gameplay;

import yio.tro.shmatoosto.SettingsManager;
import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.SimResults;
import yio.tro.shmatoosto.game.gameplay.SimulationManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.ArrayList;

public class BilliardAimUiElement extends InterfaceElement<BilliardAimUiElement> {

    public static final double MAX_AIM_ASSIST = 0.01;
    public double aimAngle;
    public ArrayList<CircleYio> aimLine;
    float alDelta; // aim line delta distance between circles
    PointYio tempPoint;
    public ShotStrengthBar shotStrengthBar;
    boolean ssbTouched;
    double targetAngle, taStartAngle;
    FactorYio taFactor, tpFactor;
    boolean autoTargetMode;
    double targetPower;
    public FactorYio lineAlphaFactor;
    public double referenceAngle;
    int referenceUpdateCountDown;
    public boolean referenceUpdated;
    boolean touched;


    public BilliardAimUiElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        resetAimAngle();
        tempPoint = new PointYio();
        shotStrengthBar = new ShotStrengthBar(this);
        taFactor = new FactorYio();
        autoTargetMode = false;
        tpFactor = new FactorYio();
        lineAlphaFactor = new FactorYio();
        referenceAngle = aimAngle;
        referenceUpdateCountDown = 0;
        referenceUpdated = false;
        touched = false;
        initLine();
    }


    public void resetAimAngle() {
        aimAngle = Math.PI / 2;
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


    @Override
    protected BilliardAimUiElement getThis() {
        return this;
    }


    @Override
    public void move() {
        moveLineAlphaFactor();
        updateViewPosition();
        updateAimLine();
        shotStrengthBar.move();
        moveAutoTarget();
        moveReferenceAngle();
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


    private void moveLineAlphaFactor() {
        lineAlphaFactor.move();

        if (lineAlphaFactor.get() == 0 && appearFactor.get() == 1) {
            lineAlphaFactor.appear(3, 0.5);
        }
    }


    private void moveAutoTarget() {
        if (!autoTargetMode) return;

        if (taFactor.move()) {
            aimAngle = taStartAngle + taFactor.get() * (targetAngle - taStartAngle);
            if (taFactor.get() == 1) {
                tpFactor.appear(3, 1);
            }
            return;
        }


        if (tpFactor.move()) {
            shotStrengthBar.setCurrentPower(tpFactor.get() * targetPower);
            return;
        }

        makeShot();
        autoTargetMode = false;
    }


    public void applyTargetShot(double powerFraction, double angle) {
        autoTargetMode = true;
        taStartAngle = aimAngle;
        targetPower = powerFraction;
        targetAngle = angle;
        prepareTaStartAngle();
        taFactor.reset();
        tpFactor.reset();
        taFactor.appear(6, 1);
    }


    public void forceFirstStageOfAutoShot() {
        taFactor.setValues(0.99, 1);
    }


    private void prepareTaStartAngle() {
        while (taStartAngle < targetAngle - Math.PI) {
            taStartAngle += 2 * Math.PI;
        }

        while (taStartAngle > targetAngle + Math.PI) {
            taStartAngle -= 2 * Math.PI;
        }
    }


    private void updateAimLine() {
        GameController gameController = getGameController();
        if (gameController.actionMode) return;

        Ball cue = gameController.objectsLayer.findCue();
        if (cue == null) return;

        tempPoint.setBy(cue.position.center);
        for (CircleYio circleYio : aimLine) {
            tempPoint.relocateRadial(alDelta, aimAngle);
            circleYio.center.setBy(tempPoint);
        }
    }


    @Override
    public void onDestroy() {
        lineAlphaFactor.destroy(1, 4);
    }


    public void resetFactors() {
        appearFactor.reset();
        lineAlphaFactor.reset();
    }


    @Override
    public void onAppear() {
        updateShotStrengthBarPosition();
        shotStrengthBar.setCurrentPower(0);
        autoTargetMode = false;
        shotStrengthBar.setShootMode(false);
    }


    private void updateShotStrengthBarPosition() {
        float rightOffset = 0.008f * GraphicsYio.width;
        float ssbWidth = 0.05f * GraphicsYio.width;
        float ssbHeight = 0.25f * position.height;
        float ssbY = position.y + (position.height / 2 - ssbHeight) / 2;

        shotStrengthBar.position.set(
                position.x + position.width - rightOffset - ssbWidth,
                ssbY,
                ssbWidth,
                ssbHeight
        );
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    void updateAimAngleByCurrentTouch() {
        Ball cue = getGameController().objectsLayer.findCue();
        if (cue == null) return;

        aimAngle = cue.position.center.angleTo(currentTouch);
        applyReadyToUpdateReferenceAngle();
    }


    private void applyReadyToUpdateReferenceAngle() {
        referenceUpdateCountDown = 3;
        referenceUpdated = false;
    }


    @Override
    public boolean touchDown() {
        if (!getGameController().isHumanTurn()) return false;

        touched = true;
        ssbTouched = shotStrengthBar.isTouched(currentTouch);

        if (ssbTouched) {
            shotStrengthBar.updateCurrentPower(currentTouch);
        } else {
            updateAimAngleByCurrentTouch();
        }

        return true;
    }


    @Override
    public boolean touchDrag() {
        if (!getGameController().isHumanTurn()) return false;
        if (!touched) return false;

        if (ssbTouched) {
            shotStrengthBar.updateCurrentPower(currentTouch);
        } else {
            updateAimAngleByCurrentTouch();
        }

        return true;
    }


    @Override
    public boolean touchUp() {
        if (!getGameController().isHumanTurn()) return false;
        if (!touched) return false;

        touched = false;

        if (ssbTouched) {
            ssbTouched = false;
            makeShot();
        } else {
            if (Yio.distanceBetweenAngles(aimAngle, referenceAngle) < 0.1) {
                aimAngle = referenceAngle;
            }
        }

        return true;
    }


    private void makeShot() {
        if (shotStrengthBar.currentPower < 0.05) return;

        Ball cue = getGameController().objectsLayer.findCue();
        if (cue == null) return;

        cue.setSpeed(shotStrengthBar.currentPower * cue.maxSpeed, aimAngle);
        checkForAimAssist();
        getGameController().applyActionMode();

        shotStrengthBar.setCurrentPower(0);
        shotStrengthBar.setShootMode(true);

        destroy();
    }


    public void checkForAimAssist() {
        if (!getGameController().getCurrentPlayerEntity().isHuman()) return;
        if (!SettingsManager.getInstance().aimAssistEnabled) return;

        applyAimAssist(getObjectsLayer().findCue());
    }


    private ObjectsLayer getObjectsLayer() {
        return menuControllerYio.yioGdxGame.gameController.objectsLayer;
    }


    private void applyAimAssist(Ball cue) {
        SimulationManager simulationManager = getObjectsLayer().simulationManager;
        double a = cue.movementAngle;
        double speedValue = cue.getSpeedValue();

        SimResults results;
        results = simulationManager.perform(cue, a, speedValue);
        cue = getObjectsLayer().findCue();
        if (areResultsNice(results)) {
            results.dispose();
            return;
        }

        results.dispose();

        int n = 2;
        for (int i = 0; i < n; i++) {
            double delta = (i + 1) * (MAX_AIM_ASSIST / n);

            results = simulationManager.perform(cue, a + delta, speedValue);
            cue = getObjectsLayer().findCue();
            if (areResultsNice(results)) {
                cue.setSpeed(speedValue, a + delta);
                results.dispose();
                return;
            }

            results = simulationManager.perform(cue, a - delta, speedValue);
            cue = getObjectsLayer().findCue();
            if (areResultsNice(results)) {
                cue.setSpeed(speedValue, a - delta);
                results.dispose();
                return;
            }
        }

        cue.setSpeed(speedValue, a); // return values back to normal
    }


    private boolean areResultsNice(SimResults results) {
        return results.scoreDelta >= 1;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderBilliardAimUI;
    }
}
