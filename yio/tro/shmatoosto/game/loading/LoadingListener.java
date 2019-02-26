package yio.tro.shmatoosto.game.loading;

public interface LoadingListener {


    void onLevelCreationEnd();


    void makeExpensiveLoadingStep(int step);
}
