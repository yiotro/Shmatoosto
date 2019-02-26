package yio.tro.shmatoosto.game.loading.loading_processes;

import yio.tro.shmatoosto.game.GameMode;
import yio.tro.shmatoosto.game.loading.LoadingManager;

public class ProcessEmpty extends AbstractLoadingProcess{

    public ProcessEmpty(LoadingManager loadingManager) {
        super(loadingManager);
    }


    @Override
    public void prepare() {
        initGameMode(GameMode.billiard);
    }


    @Override
    public void applyGameRules() {

    }


    @Override
    public void loadTree(int step) {

    }


    @Override
    public void generateLevel() {

    }


    @Override
    public void onEndCreation() {

    }
}
