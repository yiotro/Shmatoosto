package yio.tro.shmatoosto.stuff.factor_yio;

public class MoveBehaviorStay extends MoveBehavior{

    @Override
    void updateNeedsToMove(FactorYio factor) {
        if (factor.needsToMove) {
            factor.needsToMove = false;
        }
    }


    @Override
    void move(FactorYio factor) {
        // nothing
    }
}
