package yio.tro.shmatoosto.stuff.factor_yio;

public class MoveBehaviorApproach extends MoveBehavior {

    public MoveBehaviorApproach() {
    }


    @Override
    void onAppear(FactorYio factor) {
        super.onAppear(factor);
        factor.speedMultiplier /= 0.3;
    }


    @Override
    void move(FactorYio factor) {
        if (!factor.needsToMove) return;

        if (factor.inAppearState) {
            factor.value += Math.max(factor.speedMultiplier * 0.15 * (1 - factor.value), 0.01);
            if (factor.value > 0.99) {
                factor.value = 1;
            }
        } else {
            factor.value += Math.min(factor.speedMultiplier * 0.15 * (0 - factor.value), -0.01);
            if (factor.value < 0.01) {
                factor.value = 0;
            }
        }
    }
}
