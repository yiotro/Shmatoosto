package yio.tro.shmatoosto.stuff.factor_yio;

public class MoveBehaviorGravity extends MoveBehavior{

    @Override
    void onAppear(FactorYio factor) {
        super.onAppear(factor);

        factor.gravity = 0.01 * (factor.speedMultiplier / 0.3);
    }


    @Override
    void updateNeedsToMove(FactorYio factor) {
        if (factor.dy < 0 && factor.value == 0) {
            factor.needsToMove = false;
        }
    }


    @Override
    void move(FactorYio factor) {
        factor.dy -= factor.gravity;

        factor.value += factor.dy;

        if (factor.value < 0) {
            factor.value = 0;
        }
    }
}
