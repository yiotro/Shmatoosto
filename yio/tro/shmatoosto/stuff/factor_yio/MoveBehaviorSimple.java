package yio.tro.shmatoosto.stuff.factor_yio;

class MoveBehaviorSimple extends MoveBehavior {

    public MoveBehaviorSimple() {
    }


    @Override
    void onAppear(FactorYio factor) {
        super.onAppear(factor);
        factor.speedMultiplier *= 20;
    }


    @Override
    void onDestroy(FactorYio factor) {
        super.onDestroy(factor);
        factor.speedMultiplier *= 20;
    }


    void strictBounds(FactorYio factor) {
        if (factor.dy > 0 && factor.value > 1) factor.value = 1;
        if (factor.dy < 0 && factor.value < 0) factor.value = 0;
    }


    @Override
    void move(FactorYio factor) {
        if (factor.dy == 0) factor.dy = factor.gravity;
        if (factor.needsToMove) {
            factor.value += factor.speedMultiplier * factor.dy;
        }
        strictBounds(factor);
    }
}
