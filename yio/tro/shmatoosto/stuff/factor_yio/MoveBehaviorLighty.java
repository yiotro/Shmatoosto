package yio.tro.shmatoosto.stuff.factor_yio;

/**
 * Created by ivan on 23.04.2015.
 */
public class MoveBehaviorLighty extends MoveBehavior {


    public static final int CURVE = 10;


    public MoveBehaviorLighty() {
    }


    @Override
    void move(FactorYio factor) {
        factor.value += factor.speedMultiplier * factor.dy;
        factor.dy += factor.gravity;

        strictBounds(factor);
    }


    @Override
    void onAppear(FactorYio factor) {
        super.onAppear(factor);

        factor.speedMultiplier /= CURVE;
        factor.gravity *= CURVE;
    }
}
