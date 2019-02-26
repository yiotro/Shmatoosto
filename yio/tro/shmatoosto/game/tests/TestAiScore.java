package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiBilliardEntity;
import yio.tro.shmatoosto.game.player_entities.AiListener;
import yio.tro.shmatoosto.menu.MenuControllerYio;

public class TestAiScore extends AbstractTest implements AiListener{


    private final YioGdxGame yioGdxGame;
    private final MenuControllerYio menuControllerYio;
    double predictedSum;
    int predictionsNumber;
    int fullTimeTaken, timeShotsCount;
    protected double mediumPredictedScore;
    protected double averageTime;
    protected double realAverageScore;

    public TestAiScore(GameController gameController) {
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
        return gameController.objectsLayer.balls.size() < 2 || predictionsNumber > 50;
    }


    protected void prepareToTest() {
        addToAiAsListener();
        predictedSum = 0;
        predictionsNumber = 0;
        timeShotsCount = 0;
        fullTimeTaken = 0;
    }


    protected void launchGame() {
        LoadingParameters loadingParameters = new LoadingParameters();

        loadingParameters.addParameter("balls", 20);
        loadingParameters.addParameter("difficulty", Difficulty.HARD);
        loadingParameters.addParameter("2_player", false);
        loadingParameters.addParameter("ai_only", true);
        loadingParameters.addParameter("infinite_game", false);

        yioGdxGame.loadingManager.startInstantly(LoadingType.billiard, loadingParameters);
    }


    protected void showSingleGameResults() {
        System.out.println();
        System.out.println("TestAiScore.showResults");
        System.out.println("mediumPredictedScore = " + mediumPredictedScore);
        System.out.println("averageTime = " + averageTime);
    }


    private double getRealScoreForEntity(int index) {
        AbstractPlayerEntity abstractPlayerEntity = gameController.playerEntities.get(index);
        BilliardManager billiardManager = (BilliardManager) gameController.gameplayManager;
        int entityScore = billiardManager.getEntityScore(abstractPlayerEntity);

        return (double) entityScore / (double) gameController.objectsLayer.getShotsNumberForEntity(index);
    }


    protected void analyzeResults() {
        mediumPredictedScore = Yio.roundUp(predictedSum / predictionsNumber, 2);
        averageTime = Yio.roundUp((double) fullTimeTaken / timeShotsCount, 2);
        realAverageScore = (getRealScoreForEntity(0) + getRealScoreForEntity(1)) / 2;
    }


    private void addToAiAsListener() {
        for (AbstractPlayerEntity playerEntity : gameController.playerEntities) {
            if (playerEntity instanceof AiBilliardEntity) {
                ((AiBilliardEntity) playerEntity).addListener(this);
            }
        }
    }


    @Override
    public void onScorePredicted(double score) {
        predictedSum += score;
        predictionsNumber++;
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
            if (playerEntity instanceof AiBilliardEntity) {
                ((AiBilliardEntity) playerEntity).removeListener(this);
            }
        }
    }
}
