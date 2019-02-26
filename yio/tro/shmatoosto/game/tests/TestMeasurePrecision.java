package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.Difficulty;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.CollisionListener;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

import java.util.ArrayList;
import java.util.Collections;

public class TestMeasurePrecision extends AbstractTest implements CollisionListener{

    public static final boolean MANUAL_SITUATION_ENABLED = false;
    public static final boolean LOG_ENABLED = false;
    public static final boolean DEMO_MODE = false;
    int quantity;
    private final YioGdxGame yioGdxGame;
    PointYio tempPoint;
    boolean collisionHappened;
    int shotCountDown;
    double aimAngle;
    double expectedAngle, resultAngle;
    ArrayList<Double> diffs, fixes;
    private int gamesCounter;


    public TestMeasurePrecision(GameController gameController) {
        super(gameController);
        yioGdxGame = gameController.yioGdxGame;
        quantity = -1;
        tempPoint = new PointYio();
        diffs = new ArrayList<>();
        fixes = new ArrayList<>();
    }


    @Override
    protected void prepare() {
        super.prepare();
        Scenes.billiardAimUI.create();
        diffs.clear();
        fixes.clear();
    }


    @Override
    protected void execute() {
        gamesCounter = quantity;

        while (gamesCounter > 0) {
            gamesCounter--;
            runSingleGame();
        }
    }


    @Override
    protected void end() {
        super.end();
        Collections.sort(diffs);
        showResultsInConsole();
        yioGdxGame.gameView.updateAnimationTexture();
        createResultsScene();

        if (DEMO_MODE) {
            Scenes.billiardAimUI.destroy();
        }
    }


    private void createResultsScene() {
        if (DEMO_MODE) return;

        gameController.yioGdxGame.setGamePaused(true);
        gameController.yioGdxGame.gameView.destroy();

        Scenes.testResults.clearText();
        Scenes.testResults.addTextLine("Precision test finished.");
        Scenes.testResults.addTextLine("Average: " + getAverage());
        Scenes.testResults.addTextLine("Median: " + getMedian());
        Scenes.testResults.addTextLine("Min: " + getMin());
        Scenes.testResults.addTextLine("Max: " + getMax());
        Scenes.testResults.addTextLine("Fix: " + getFixResult());
        Scenes.testResults.finishText();
        Scenes.testResults.create();
    }


    private void showResultsInConsole() {
        System.out.println();
        System.out.println("TestMeasurePrecision.showResultsInConsole");
        System.out.println("getAverage() = " + getAverage());
        System.out.println("getMin() = " + getMin());
        System.out.println("getMax() = " + getMax());
        System.out.println("getFixResult() = " + getFixResult());
        System.out.println("getMedian() = " + getMedian());
    }


    private double getMedian() {
        if (diffs.size() == 0) return 0;

        int index = diffs.size() / 2;
        double a = diffs.get(index);

        if (a < 0.001) {
            a = 0;
        }

        return Yio.roundUp(a, 4);
    }


    private double getMax() {
        if (diffs.size() == 0) return 0;

        double max = diffs.get(0);

        for (double d : diffs) {
            if (d > max) {
                max = d;
            }
        }

        return Yio.roundUp(max, 4);
    }


    private double getMin() {
        if (diffs.size() == 0) return 0;

        double min = diffs.get(0);

        for (double d : diffs) {
            if (d < min) {
                min = d;
            }
        }

        if (min < 0.001) return 0;

        return Yio.roundUp(min, 4);
    }


    private double getAverage() {
        double sum = 0;

        for (double d : diffs) {
            sum += d;
        }

        return Yio.roundUp(sum / diffs.size(), 4);
    }


    protected void runSingleGame() {
        launchGame();
        prepareForSingleGame();
        moveStuffForSomeTime();
        analyzeSingleGame();
    }


    private void analyzeSingleGame() {
        if (!collisionHappened && !DEMO_MODE) {
            gamesCounter++; // one more game please
            return;
        }

        double diff = Yio.distanceBetweenAngles(expectedAngle, resultAngle);
        diffs.add(diff);

        if (LOG_ENABLED && diff > 2) {
            System.out.println("diff = " + diff);
            System.out.println("Anomaly detected");
        }
    }


    @Override
    public void onPrecisionFixApplied(double previousAngle, double currentAngle) {
        double da = Yio.distanceBetweenAngles(previousAngle, currentAngle);
        if (da < 0.0001) {
            da = 0;
        }
        fixes.add(da);
    }


    double getFixResult() {
        double sum = 0;

        for (double f : fixes) {
            sum += f;
        }

        sum /= fixes.size();
        if (sum < 0.001) {
            sum = 0;
        }
        return Yio.roundUp(sum, 4);
    }


    protected void prepareForSingleGame() {
        while (true) {
            relocateBalls();

            if (areBallPositionsValid()) {
                break;
            }
        }

        updateExpectedAngle();
        checkToApplyManualSituation();
        pointAimAtBall();
        showPreparedPositionInConsole();

        gameController.objectsLayer.ballCollisionPerformer.addListener(this);
        collisionHappened = false;
        shotCountDown = 5;
        resultAngle = 0;
    }


    private void checkToApplyManualSituation() {
        if (!MANUAL_SITUATION_ENABLED) return;

//        cue.position.center = [Point: 318.51, 178.52]
//        otherBall.position.center = [Point: 350.85, 443.67]
//        expectedAngle = 2.064081099523629
//        diff = 2.892970587335518
//        Anomaly detected

        Ball cue = gameController.objectsLayer.findCue();
        Ball otherBall = findOtherBall();

        cue.position.center.set(318.51, 178.52);
        otherBall.position.center.set(350.85, 443.67);
        expectedAngle = 2.064081099523629;

        for (Ball ball : getBalls()) {
            gameController.objectsLayer.posMap.updateObjectPos(ball);
        }
    }


    private void showPreparedPositionInConsole() {
        if (!LOG_ENABLED) return;

        Ball cue = gameController.objectsLayer.findCue();
        Ball otherBall = findOtherBall();
        System.out.println();
        System.out.println("cue.position.center = " + cue.position.center);
        System.out.println("otherBall.position.center = " + otherBall.position.center);
        System.out.println("expectedAngle = " + expectedAngle);
    }


    private void updateExpectedAngle() {
        Ball cue = gameController.objectsLayer.findCue();
        Ball otherBall = findOtherBall();

        double a = cue.position.center.angleTo(otherBall.position.center);
        a += (2 * YioGdxGame.random.nextDouble() - 1) * 0.35 * Math.PI;

        expectedAngle = a;
    }


    @Override
    public void onBallsCollided(Ball one, Ball two) {
        Ball cue, otherBall;
        if (one.isCue()) {
            cue = one;
            otherBall = two;
        } else {
            cue = two;
            otherBall = one;
        }

        resultAngle = Yio.angle(0, 0, otherBall.delta.x, otherBall.delta.y);
        collisionHappened = true;
    }


    private void pointAimAtBall() {
        Ball cue = gameController.objectsLayer.findCue();
        Ball otherBall = findOtherBall();

        tempPoint.setBy(otherBall.position.center);
        tempPoint.relocateRadial(otherBall.collisionRadius + cue.collisionRadius, expectedAngle + Math.PI);

        aimAngle = cue.position.center.angleTo(tempPoint);

        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        billiardAimUiElement.aimAngle = aimAngle;
    }


    private Ball findOtherBall() {
        for (Ball ball : getBalls()) {
            if (ball.isCue()) continue;

            return ball;
        }

        return null;
    }


    private boolean areBallPositionsValid() {
        for (int i = 0; i < getBalls().size(); i++) {
            for (int j = i + 1; j < getBalls().size(); j++) {
                Ball one = getBalls().get(i);
                Ball two = getBalls().get(j);
                if (one.position.center.distanceTo(two.position.center) < 2 * (one.collisionRadius + two.collisionRadius)) {
                    return false;
                }
            }
        }

        return true;
    }


    private ArrayList<Ball> getBalls() {
        return gameController.objectsLayer.balls;
    }


    private void relocateBalls() {
        for (Ball ball : getBalls()) {
            ball.setPosition(
                    (0.15 + 0.7 * YioGdxGame.random.nextDouble()) * GraphicsYio.width,
                    (0.15 + 0.6 * YioGdxGame.random.nextDouble()) * GraphicsYio.height
            );
            gameController.objectsLayer.posMap.updateObjectPos(ball);
        }
    }


    protected void launchGame() {
        LoadingParameters loadingParameters = new LoadingParameters();

        loadingParameters.addParameter("balls", 1);
        loadingParameters.addParameter("difficulty", Difficulty.HARD);
        loadingParameters.addParameter("2_player", true);
        loadingParameters.addParameter("ai_only", false);
        loadingParameters.addParameter("infinite_game", false);

        yioGdxGame.loadingManager.startInstantly(LoadingType.billiard, loadingParameters);
    }


    protected void moveStuffForSomeTime() {
        int c = 0;
        while (c < 200) {
            c++;
            gameController.move();
            yioGdxGame.menuControllerYio.move();

            if (shotCountDown > 0) {
                shotCountDown--;
                if (shotCountDown == 0) {
                    makeShot();
                }
            }

            if (readyToEndSingleGame()) {
                break;
            }
        }
    }


    private void makeShot() {
        Ball cue = gameController.objectsLayer.findCue();
        cue.setSpeed(cue.maxSpeed, aimAngle);
        gameController.applyActionMode();
    }


    private boolean readyToEndSingleGame() {
        if (DEMO_MODE && shotCountDown == 0) return true;

        return collisionHappened;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
