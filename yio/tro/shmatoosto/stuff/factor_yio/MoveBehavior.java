package yio.tro.shmatoosto.stuff.factor_yio;

public abstract class MoveBehavior {

    static final MoveBehavior moveBehaviorSimple = new MoveBehaviorSimple();
    static final MoveBehavior moveBehaviorLighty = new MoveBehaviorLighty();
    static final MoveBehavior moveBehaviorMaterial = new MoveBehaviorMaterial();
    static final MoveBehavior moveBehaviorApproach = new MoveBehaviorApproach();
    static final MoveBehavior moveBehaviorStay = new MoveBehaviorStay();
    static final MoveBehavior moveBehaviorInertia = new MoveBehaviorInertia();
    static final MoveBehavior moveBehaviorGravity = new MoveBehaviorGravity();


    public MoveBehavior() {
    }


    void updateNeedsToMove(FactorYio factor) {
        if (factor.inAppearState && factor.value == 1) {
            factor.needsToMove = false;
            return;
        }
        if (factor.inDestroyState && factor.value == 0) {
            factor.needsToMove = false;
        }
    }


    void strictBounds(FactorYio factor) {
        if (factor.inAppearState && factor.value > 1) factor.value = 1;
        if (factor.inDestroyState && factor.value < 0) factor.value = 0;
    }


    void stopMoving(FactorYio factor) {
        factor.dy = 0;
        factor.gravity = 0;
        factor.needsToMove = false;
    }


    abstract void move(FactorYio factor);


    void onAppear(FactorYio factor) {

    }


    void onDestroy(FactorYio factor) {

    }
}
