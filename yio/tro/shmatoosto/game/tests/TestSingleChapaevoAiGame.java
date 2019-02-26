package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiChapaevoEntity;
import yio.tro.shmatoosto.game.player_entities.AiListener;
import yio.tro.shmatoosto.menu.MenuControllerYio;

import java.util.ArrayList;

public class TestSingleChapaevoAiGame extends AbstractTest implements AiListener{

    private final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;
    int fullTimeTaken, timeShotsCount;
    protected double averageTime;
    protected double realAverageScore;
    protected int currentWinnerIndex;
    int shotsNumber;


    public TestSingleChapaevoAiGame(GameController gameController) {
        super(gameController);
        yioGdxGame = gameController.yioGdxGame;
        menuControllerYio = yioGdxGame.menuControllerYio;
    }


    @Override
    protected void execute() {
        runSingleGame();
        showSingleGameResults();
    }


    protected void runSingleGame() {
        launchGame();
        prepareToTest();
        moveStuffForSomeTime();
        yioGdxGame.gameView.updateAnimationTexture();
        analyzeResults();
    }


    protected void moveStuffForSomeTime() {
        while (true) {
            gameController.move();
            menuControllerYio.move();

            if (isReadyToEndSingleGame()) break;
        }
    }


    private boolean isReadyToEndSingleGame() {
        ChapaevoManager chapaevoManager = (ChapaevoManager) gameController.gameplayManager;

        return chapaevoManager.isReadyToFinishGame() || shotsNumber > 50;
    }


    protected void prepareToTest() {
        addToAiAsListener();
        shotsNumber = 0;
        timeShotsCount = 0;
        fullTimeTaken = 0;
    }


    protected void launchGame() {
        LoadingParameters loadingParameters = new LoadingParameters();

        loadingParameters.addParameter("balls", 26);
        loadingParameters.addParameter("difficulty", Difficulty.HARD);
        loadingParameters.addParameter("2_player", false);
        loadingParameters.addParameter("ai_only", true);
        loadingParameters.addParameter("obstacles", false);

        yioGdxGame.loadingManager.startInstantly(LoadingType.chapaevo, loadingParameters);
    }


    protected void showSingleGameResults() {
        System.out.println();
        System.out.println("TestAiScore.showResults");
        System.out.println("averageTime = " + averageTime);
    }


    private double getRealScoreForEntity(int index) {
        AbstractPlayerEntity abstractPlayerEntity = gameController.playerEntities.get(index);
        ChapaevoManager chapaevoManager = (ChapaevoManager) gameController.gameplayManager;
        int entityScore = chapaevoManager.getEntityScore(abstractPlayerEntity);

        return (double) entityScore / (double) gameController.objectsLayer.getShotsNumberForEntity(index);
    }


    protected void analyzeResults() {
        averageTime = Yio.roundUp((double) fullTimeTaken / timeShotsCount, 2);
        realAverageScore = (getRealScoreForEntity(0) + getRealScoreForEntity(1)) / 2;

        ArrayList<Ball> balls = gameController.objectsLayer.balls;
        if (balls.size() == 0) {
            currentWinnerIndex = -1;
        } else {
            BColorYio color = balls.get(0).getColor();
            int entityIndex = gameController.playerEntities.get(0).getIndex();
            ChapaevoManager chapaevoManager = (ChapaevoManager) gameController.gameplayManager;
            BColorYio entityColor = chapaevoManager.getEntityColor(entityIndex);
            if (entityColor == color) {
                currentWinnerIndex = 0;
            } else {
                currentWinnerIndex = 1;
            }
        }
    }


    private void addToAiAsListener() {
        for (AbstractPlayerEntity playerEntity : gameController.playerEntities) {
            if (playerEntity instanceof AiChapaevoEntity) {
                ((AiChapaevoEntity) playerEntity).addListener(this);
            }
        }
    }


    @Override
    public void onScorePredicted(double score) {
        shotsNumber++;
    }


    @Override
    public void onTimeTaken(int millis) {
        fullTimeTaken += millis;
        timeShotsCount++;
    }


    @Override
    protected void end() {
        super.end();
        for (AbstractPlayerEntity playerEntity : gameController.playerEntities) {
            if (playerEntity instanceof AiChapaevoEntity) {
                ((AiChapaevoEntity) playerEntity).removeListener(this);
            }
        }
    }
}
