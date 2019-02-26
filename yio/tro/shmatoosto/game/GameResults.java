package yio.tro.shmatoosto.game;

import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.stuff.object_pool.ReusableYio;

public class GameResults implements ReusableYio{


    public LoadingType loadingType;
    public int firstScore;
    public int secondScore;
    public int winnerIndex;
    public AbstractPlayerEntity entityOne;
    public AbstractPlayerEntity entityTwo;


    @Override
    public void reset() {
        loadingType = null;
        firstScore = -1;
        secondScore = -1;
        winnerIndex = -1; // no winner
        entityOne = null;
        entityTwo = null;
    }


    public void setLoadingType(LoadingType loadingType) {
        this.loadingType = loadingType;
    }


    public void setFirstScore(int firstScore) {
        this.firstScore = firstScore;
    }


    public void setSecondScore(int secondScore) {
        this.secondScore = secondScore;
    }


    public void setWinnerIndex(int winnerIndex) {
        this.winnerIndex = winnerIndex;
    }


    public void setEntityOne(AbstractPlayerEntity entityOne) {
        this.entityOne = entityOne;
    }


    public void setEntityTwo(AbstractPlayerEntity entityTwo) {
        this.entityTwo = entityTwo;
    }


    @Override
    public String toString() {
        return "[GameResults:" +
                " type=" + loadingType +
                " firstScore=" + firstScore +
                " secondScore=" + secondScore +
                " winnerIndex=" + winnerIndex +
                "]";
    }
}
