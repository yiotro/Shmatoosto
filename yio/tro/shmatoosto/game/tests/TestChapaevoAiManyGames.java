package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.menu.scenes.Scenes;

import java.util.ArrayList;

public class TestChapaevoAiManyGames extends TestSingleChapaevoAiGame{

    public static final int DEFAULT_QUANTITY = 50;
    ArrayList<Double> averageTimes;
    ArrayList<Double> realAverageScores;
    ArrayList<Integer> winners;
    private double time;
    private double realScore;
    private double averageWinner;
    int quantity;


    public TestChapaevoAiManyGames(GameController gameController) {
        super(gameController);

        averageTimes = new ArrayList<>();
        realAverageScores = new ArrayList<>();
        winners = new ArrayList<>();
        quantity = DEFAULT_QUANTITY;
    }


    @Override
    protected void execute() {
        System.out.println("Number of games: " + quantity);

        DebugFlags.firstEntityAlwaysMakesFirstShot = true;
        averageTimes.clear();
        realAverageScores.clear();

        for (int i = 0; i < quantity; i++) {
            runSingleGame();
            averageTimes.add(averageTime);
            realAverageScores.add(realAverageScore);
            winners.add(currentWinnerIndex);
        }

        updateResults();
        onTestEnded();

        DebugFlags.firstEntityAlwaysMakesFirstShot = false;
        gameController.yioGdxGame.setGamePaused(true);
        gameController.yioGdxGame.gameView.destroy();

        Scenes.testResults.clearText();
        Scenes.testResults.addTextLine("Test AI many scores:");
        Scenes.testResults.addTextLine("Real score: " + realScore);
        Scenes.testResults.addTextLine("Average winner: " + averageWinner);
        Scenes.testResults.addTextLine("Time: " + time);
        Scenes.testResults.finishText();
        Scenes.testResults.create();
    }


    private void onTestEnded() {
        System.out.println();
        System.out.println("TestManyAiScores.onTestEnded");
        System.out.println("realScore = " + realScore);
        System.out.println("time = " + time);
        System.out.println("averageWinner = " + averageWinner);
    }


    private void updateResults() {
        updateTime();
        updateRealScore();
        updateAverageWinner();
    }


    private void updateAverageWinner() {
        int n = 0;
        averageWinner = 0;

        for (int winnerIndex : winners) {
            if (winnerIndex == -1) continue;

            n++;
            averageWinner += winnerIndex;
        }

        averageWinner /= n;
        averageWinner = Yio.roundUp(averageWinner, 2);
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


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
