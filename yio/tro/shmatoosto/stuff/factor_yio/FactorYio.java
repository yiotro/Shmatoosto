package yio.tro.shmatoosto.stuff.factor_yio;

import yio.tro.shmatoosto.Yio;

public class FactorYio {
	
    boolean inAppearState, inDestroyState;
    double value, gravity, dy, speedMultiplier;
    private int moveBehaviorIndex; // used as save data
    MoveBehavior moveBehavior;
    boolean needsToMove;


    public FactorYio() {
        // empty constructor
        moveBehavior = MoveBehavior.moveBehaviorLighty;
        inAppearState = false;
        inDestroyState = false;
    }


    public boolean move() {
        if (!hasToMove()) return false;
        moveBehavior.move(this);
        moveBehavior.updateNeedsToMove(this);
        return true;
    }


    public void appear(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehavior(moveMode);
        prepareForMovement(speed);

        gravity = 0.01;
        inAppearState = true;
        moveBehavior.onAppear(this);
    }


    public void destroy(int moveMode, double speed) {
        // speed == 1 is default
        setMoveBehavior(moveMode);
        prepareForMovement(speed);

        gravity = -0.01;
        inDestroyState = true;
        moveBehavior.onDestroy(this);
    }


    public static MoveBehavior getMoveBehaviorByIndex(int index) {
        switch (index) {
            default:
                System.out.println("Wrong move behavior");
            case 0: return MoveBehavior.moveBehaviorSimple;
            case 1: return MoveBehavior.moveBehaviorLighty;
            case 2: return MoveBehavior.moveBehaviorMaterial;
            case 3: return MoveBehavior.moveBehaviorApproach;
            case 5: return MoveBehavior.moveBehaviorStay;
            case 6: return MoveBehavior.moveBehaviorInertia;
            case 7: return MoveBehavior.moveBehaviorGravity;
        }
    }


    private void prepareForMovement(double speed) {
        setDy(0);
        speedMultiplier = 0.3 * speed;
        inDestroyState = false;
        inAppearState = false;
        needsToMove = true;
    }


    private void setMoveBehavior(int mbIndex) {
        moveBehaviorIndex = mbIndex;
        moveBehavior = getMoveBehaviorByIndex(mbIndex);
    }


    public void copyFrom(FactorYio src) {
        inAppearState = src.inAppearState;
        inDestroyState = src.inDestroyState;
        value = src.value;
        gravity = src.gravity;
        dy = src.dy;
        speedMultiplier = src.speedMultiplier;
        setMoveBehavior(src.moveBehaviorIndex);
        needsToMove = src.needsToMove;
    }


    public void setValues(double f, double dy) {
        this.value = f;
        this.dy = dy;
    }


    public void setDy(double dy) {
        this.dy = dy;
    }


    public double getDy() {
        return dy;
    }


    public double getGravity() {
        return gravity;
    }


    public void stop() {
        appear(5, 1); // 5 - move behavior 'stop'
    }


    public boolean hasToMove() {
        return needsToMove;
    }


    public void setGravity(double gravity) {
        this.gravity = gravity;
    }


    public float get() {
        return (float) value;
    }


    public boolean isInAppearState() {
        return inAppearState;
    }


    public boolean isInDestroyState() {
        return inDestroyState;
    }



    public void reset() {
        setValues(0, 0);
        stop();
    }


    @Override
    public String toString() {
        return "Factor: " +
                "state(" + inAppearState + ", " + inDestroyState + "), " +
                "value(" + Yio.roundUp(value, 2) + "), " +
                "dy(" + Yio.roundUp(dy, 2) + "), " +
                "gravity(" + Yio.roundUp(gravity, 2) + ")";
    }
}