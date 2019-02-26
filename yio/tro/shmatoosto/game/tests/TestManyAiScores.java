package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.menu.scenes.Scenes;

import java.util.ArrayList;

public class TestManyAiScores extends TestAiScore{


    public static final int DEFAULT_QUANTITY = 50;
    ArrayList<Double> averagePredictedScores;
    ArrayList<Double> averageTimes;
    ArrayList<Double> realAverageScores;
    private double predictedScore;
    private double time;
    private double realScore;
    int quantity;


    public TestManyAiScores(GameController gameController) {
        super(gameController);

        averagePredictedScores = new ArrayList<>();
        averageTimes = new ArrayList<>();
        realAverageScores = new ArrayList<>();
        quantity = DEFAULT_QUANTITY;
    }


    @Override
    protected void execute() {
        System.out.println("Number of games: " + quantity);

        averagePredictedScores.clear();
        averageTimes.clear();
        realAverageScores.clear();

        for (int i = 0; i < quantity; i++) {
            runSingleGame();
            averagePredictedScores.add(mediumPredictedScore);
            averageTimes.add(averageTime);
            realAverageScores.add(realAverageScore);
        }

        updateResults();
        onTestEnded();

        gameController.yioGdxGame.setGamePaused(true);
        gameController.yioGdxGame.gameView.destroy();

        Scenes.testResults.clearText();
        Scenes.testResults.addTextLine("Test AI many scores:");
        Scenes.testResults.addTextLine("Prediction: " + predictedScore);
        Scenes.testResults.addTextLine("Real score: " + realScore);
        Scenes.testResults.addTextLine("Time: " + time);
        Scenes.testResults.finishText();
        Scenes.testResults.create();
    }


    private void onTestEnded() {
        System.out.println();
        System.out.println("TestManyAiScores.onTestEnded");
        System.out.println("prediction = " + predictedScore);
        System.out.println("realScore = " + realScore);
        System.out.println("time = " + time);
    }


    private void updateResults() {
        updatePredictedScore();
        updateTime();
        updateRealScore();
    }


    private void updateRealScore() {
        realScore = 0;
        for (double s : realAverageScores) {
            realScore += s;
        }
        realScore /= realAverageScores.size();
        realScore = Yio.roundUp(realScore, 2);
    }


    private void updateTime() {
        time = 0;
        for (double t : averageTimes) {
            time += t;
        }
        time /= averageTimes.size();
        time = Yio.roundUp(time, 2);
    }


    private void updatePredictedScore() {
        predictedScore = 0;
        for (double s : averagePredictedScores) {
            predictedScore += s;
        }
        predictedScore /= averagePredictedScores.size();
        predictedScore = Yio.roundUp(predictedScore, 2);
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
