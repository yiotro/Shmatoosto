package yio.tro.shmatoosto.game.loading.loading_processes;

import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameMode;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.loading.LoadingManager;
import yio.tro.shmatoosto.game.loading.LoadingParameters;

public abstract class AbstractLoadingProcess {


    YioGdxGame yioGdxGame;
    GameController gameController;
    LoadingManager loadingManager;
    LoadingParameters loadingParameters;


    public AbstractLoadingProcess(LoadingManager loadingManager) {
        this.loadingManager = loadingManager;
        yioGdxGame = loadingManager.yioGdxGame;
        gameController = yioGdxGame.gameController;

        loadingParameters = null;
    }


    public abstract void prepare();


    public abstract void applyGameRules();


    public abstract void loadTree(int step);


    public abstract void generateLevel();


    public abstract void onEndCreation();


    public void setLoadingParameters(LoadingParameters loadingParameters) {
        this.loadingParameters = loadingParameters;
        GameRules.initialParameters = loadingParameters;
    }


    public void initGameMode(GameMode gameMode) {
        loadingManager.initGameMode(gameMode);
    }
}
