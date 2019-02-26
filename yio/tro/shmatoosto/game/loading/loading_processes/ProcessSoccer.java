package yio.tro.shmatoosto.game.loading.loading_processes;

import yio.tro.shmatoosto.game.GameMode;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.loading.LoadingManager;

public class ProcessSoccer extends AbstractLoadingProcess{

    public ProcessSoccer(LoadingManager loadingManager) {
        super(loadingManager);
    }


    @Override
    public void prepare() {
        initGameMode(GameMode.soccer);
    }


    @Override
    public void applyGameRules() {
        if (Boolean.valueOf(loadingParameters.getParameter("ai_only").toString())) {
            GameRules.aiOnly = true;
        }

        if (Boolean.valueOf(loadingParameters.getParameter("2_player").toString())) {
            GameRules.twoPlayerMode = true;
        }

        GameRules.difficulty = Integer.valueOf(loadingParameters.getParameter("difficulty").toString());
        GameRules.goalsLimit = Integer.valueOf(loadingParameters.getParameter("goals_limit").toString());
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
