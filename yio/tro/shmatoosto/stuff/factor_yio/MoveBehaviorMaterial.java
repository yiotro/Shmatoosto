package yio.tro.shmatoosto.stuff.factor_yio;

public class MoveBehaviorMaterial extends MoveBehavior {

    @Override
    void move(FactorYio factor) {
        if (factor.inAppearState) {
            moveSpawn(factor);
        } else {
            moveDestroy(factor);
        }
    }


    private void moveDestroy(FactorYio fy) {
        if (fy.value > 0.99) fy.value = 0.99;
        if (fy.value < 0.01) fy.value = 0;

        if (fy.value > 0.5) {
            fy.value -= 0.05 * fy.speedMultiplier * (1 - fy.value);
        } else {
            fy.value -= 0.05 * fy.speedMultiplier * fy.value;
        }
    }


    private void moveSpawn(FactorYio fy) {
        if (fy.value < 0.01) fy.value = 0.01;
        if (fy.value > 0.99) fy.value = 1;

        if (fy.value < 0.5) {
            fy.value += 0.05 * fy.speedMultiplier * fy.value;
        } else {
            fy.value += 0.05 * fy.speedMultiplier * (1 - fy.value);
        }
    }


    @Override
    void onAppear(FactorYio factor) {
        factor.speedMultiplier *= 15;
    }


    @Override
    void onDestroy(FactorYio factor) {
        factor.speedMultiplier *= 15;
    }
}
