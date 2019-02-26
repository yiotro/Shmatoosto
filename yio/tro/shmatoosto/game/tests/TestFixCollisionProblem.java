package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.MenuControllerYio;

import java.util.Random;

public class TestFixCollisionProblem extends AbstractTest{

    private final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;


    public TestFixCollisionProblem(GameController gameController) {
        super(gameController);
        yioGdxGame = gameController.yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
    }


    @Override
    protected void execute() {
        DebugFlags.firstShotHasFullStrength = true;
        int seed = 163420678;
        YioGdxGame.random = new Random(seed);

        launchGame();
    }


    protected void launchGame() {
        LoadingParameters loadingParameters = new LoadingParameters();

        loadingParameters.addParameter("balls", 20);
        loadingParameters.addParameter("difficulty", Difficulty.HARD);
        loadingParameters.addParameter("2_player", false);
        loadingParameters.addParameter("ai_only", false);
        loadingParameters.addParameter("obstacles", false);

        yioGdxGame.loadingManager.startInstantly(LoadingType.chapaevo, loadingParameters);
    }
}
