package yio.tro.shmatoosto.game.gameplay.soccer;

import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameResults;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiSoccerEntity;
import yio.tro.shmatoosto.game.player_entities.HumanSoccerEntity;
import yio.tro.shmatoosto.game.view.game_renders.AbstractRenderBoard;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class SoccerManager extends AbstractGameplayManager{

    public SoccerBoard board;
    public float defaultBallRadius;
    public RectangleYio topSpawnPlace, bottomSpawnPlace;
    BColorYio colorPair[];
    boolean goalRegistered;
    ArrayList<Ball> tempList;
    public int firstScore, secondScore;
    PointYio tempPredictionPoint;


    public SoccerManager(GameController gameController) {
        super(gameController);

        defaultBallRadius = 0.035f * GraphicsYio.width;
        board = new SoccerBoard(this);
        topSpawnPlace = new RectangleYio();
        bottomSpawnPlace = new RectangleYio();
        tempList = new ArrayList<>();
        tempPredictionPoint = new PointYio();
    }


    @Override
    public BackgroundYio getBackground() {
        return BackgroundYio.soccer;
    }


    @Override
    public void onEndCreation() {
        updateSpawnPlaces();
        updateColorPair();
        spawnBalls();

        for (int k = 0; k < 100; k++) {
            getObjectsLayer().move();
        }

        board.initCollisionLines();
        goalRegistered = false;
        firstScore = 0;
        secondScore = 0;
    }


    private void spawnBalls() {
        int n = (int) GameRules.initialParameters.getParameter("balls");
        spawnSomeBallsWithParameters(n / 2, getEntityColor(0), bottomSpawnPlace);
        spawnSomeBallsWithParameters(n / 2, getEntityColor(1), topSpawnPlace);
        spawnSoccerBall();
        spawnHiddenBalls();
    }


    private void spawnHiddenBalls() {
        for (CircleYio barbell : board.barbells) {
            Ball ball = getObjectsLayer().addBall();
            ball.setColor(BColorYio.white); // matters only for debug
            ball.setSolid(true);
            ball.setHidden(true);
            ball.setPosition(barbell.center.x, barbell.center.y);
            ball.setRadius(board.br);
        }
    }


    private void spawnSoccerBall() {
        Ball ball = getObjectsLayer().addBall();
        ball.setColor(BColorYio.soccer);
        ball.setPosition(board.center.x, board.center.y);
        ball.setRadius(0.9 * defaultBallRadius);
        ball.setBoardFriction(getBoardFrictionForSoccer());
        ball.setCollisionId(0);
        ball.delta.reset();
    }


    private void spawnSomeBallsWithParameters(int number, BColorYio color, RectangleYio spawnPlace) {
        for (int i = 0; i < number; i++) {
            Ball ball = getObjectsLayer().addBall();
            ball.setColor(color);
            ball.setPosition(
                    spawnPlace.x + defaultBallRadius + YioGdxGame.random.nextDouble() * (spawnPlace.width - 2 * defaultBallRadius),
                    spawnPlace.y + defaultBallRadius + YioGdxGame.random.nextDouble() * (spawnPlace.height - 2 * defaultBallRadius)
            );
            ball.setRadius(defaultBallRadius);
            ball.setBoardFriction(getBoardFrictionForSoccer());
            ball.setCollisionId(1);
            ball.delta.reset();
        }
    }


    private float getBoardFrictionForSoccer() {
        return 2.0f * Ball.DEFAULT_BOARD_FRICTION;
    }


    private void updateSpawnPlaces() {
        bottomSpawnPlace.setBy(board.position);
        bottomSpawnPlace.height = 0.25f * board.position.height;

        topSpawnPlace.setBy(bottomSpawnPlace);
        topSpawnPlace.y = board.position.y + board.position.height - topSpawnPlace.height;
    }


    public BColorYio getEntityColor(AbstractPlayerEntity playerEntity) {
        return getEntityColor(playerEntity.getIndex());
    }


    public PointYio predictBallPositionAfterShot(Ball ball, double power, double angle) {
        // this method may work incorrectly if method with same name in SoccerAimUiElement was modified
        if (ball == null) return null;

        double speed = convertAimPowerToSpeed(ball, power);
        double x = 0;
        while (speed > 0.7 * GraphicsYio.borderThickness) {
            x += speed;
            speed *= 1 - ball.getBoardFriction();
        }

        tempPredictionPoint.setBy(ball.position.center);
        tempPredictionPoint.relocateRadial(x, angle);

        return tempPredictionPoint;
    }


    public double convertAimPowerToSpeed(Ball ball, double aimPower) {
        if (gameController.isHumanTurn()) {
            aimPower *= aimPower;

            if (aimPower < 0.1) {
                aimPower = 0.1;
            }
        }

        return aimPower * ball.maxSpeed;
    }


    public RectangleYio getDefenseArea(int entityIndex) {
        if (entityIndex == 0) {
            return board.bottomDefenseArea;
        }

        return board.topDefenseArea;
    }


    public RectangleYio getGoalArea(int entityIndex) {
        if (entityIndex == 0) {
            return board.bottomGoalArea;
        }

        return board.topGoalArea;
    }


    public AbstractPlayerEntity getEntity(BColorYio bColorYio) {
        for (AbstractPlayerEntity playerEntity : gameController.playerEntities) {
            if (getEntityColor(playerEntity) == bColorYio) {
                return playerEntity;
            }
        }

        return null;
    }


    public void updateColorPair() {
        colorPair = getRandomColorPair();

        if (YioGdxGame.random.nextBoolean()) {
            BColorYio tempColor = colorPair[0];
            colorPair[0] = colorPair[1];
            colorPair[1] = tempColor;
        }
    }


    private BColorYio[] getRandomColorPair() {
        switch (YioGdxGame.random.nextInt(4)) {
            default:
                return null;
            case 0:
                return new BColorYio[]{BColorYio.cyan, BColorYio.red};
            case 1:
                return new BColorYio[]{BColorYio.blue, BColorYio.yellow};
            case 2:
                return new BColorYio[]{BColorYio.blue, BColorYio.red};
            case 3:
                return new BColorYio[]{BColorYio.cyan, BColorYio.yellow};
        }
    }


    public BColorYio getEntityColor(int entityIndex) {
        switch (entityIndex) {
            default: return null; // error
            case 0: return colorPair[0];
            case 1: return colorPair[1];
        }
    }


    public int getEnemyEntityIndex(int entityIndex) {
        if (entityIndex == 0) return 1;
        if (entityIndex == 1) return 0;

        return -1;
    }


    @Override
    public void move() {
        limitBallsByScreen();
        checkForGoal();
        checkToSlowDownSoccerBall();
        board.move();
        checkToProcessGoal();
    }


    private void checkToSlowDownSoccerBall() {
        if (!goalRegistered) return;
        if (inSimulationMode()) return;

        Ball soccerBall = getObjectsLayer().findSoccerBall();
        if (soccerBall == null) return;

        soccerBall.setBoardFriction(50 * Ball.DEFAULT_BOARD_FRICTION);
    }


    private void checkForGoal() {
        if (goalRegistered) return;

        Ball soccerBall = getObjectsLayer().findSoccerBall();
        if (soccerBall == null) return;

        if (board.topGoalArea.isPointInside(soccerBall.position.center)) {
            onGoalRegistered(1);
        }

        if (board.bottomGoalArea.isPointInside(soccerBall.position.center)) {
            onGoalRegistered(0);
        }
    }


    private boolean inSimulationMode() {
        return getObjectsLayer().inSimulationMode();
    }


    public void updateScoreView() {
        Scenes.gameOverlay.updateScore(0, firstScore);
        Scenes.gameOverlay.updateScore(1, secondScore);
    }


    private void checkToProcessGoal() {
        if (!goalRegistered) return;
        if (inSimulationMode()) return;
        if (board.goalEffectFactor.get() > 0) return;
        goalRegistered = false;

        returnBallsToDefaultLocations();

        if (board.goalIndex == 0) {
            secondScore++;
        } else {
            firstScore++;
        }

        updateScoreView();
    }


    private void onGoalRegistered(int goalIndex) {
        if (inSimulationMode()) {
            getObjectsLayer().simulationManager.onGoalRegisteredDuringSimulation(goalIndex);
            return;
        }

        goalRegistered = true;
        board.onGoalDetected(goalIndex);
        clearFieldFromUsualBalls(true);
        SoundManager.playSound(SoundManager.fall);
    }


    private void returnBallsToDefaultLocations() {
        if (!gameController.actionMode && !inSimulationMode()) return;

        clearFieldFromUsualBalls(false);
        spawnBalls();
    }


    private void clearFieldFromUsualBalls(boolean ignoreSoccerBall) {
        tempList.clear();
        for (Ball ball : getObjectsLayer().balls) {
            if (ignoreSoccerBall && ball.isSoccerBall()) continue;
            tempList.add(ball);
        }

        for (Ball ball : tempList) {
            if (ball.hidden) continue;
            getObjectsLayer().addFallBallAnimation(ball);
        }

        if (ignoreSoccerBall) {
            getObjectsLayer().clearBallsFiltered(BColorYio.soccer);
        } else {
            getObjectsLayer().clearBalls();
        }
    }


    private void limitBallsByScreen() {
        for (Ball ball : getObjectsLayer().balls) {
            ball.limitByScreen();
        }
    }


    @Override
    public boolean isReadyToFinishGame() {
        if (GameRules.goalsLimit == -1) return false;

        if (firstScore >= GameRules.goalsLimit) return true;
        if (secondScore >= GameRules.goalsLimit) return true;

        return false;
    }


    @Override
    public void updateGameResults(GameResults gameResults) {
        gameResults.setLoadingType(LoadingType.soccer);
        gameResults.setFirstScore(firstScore);
        gameResults.setSecondScore(secondScore);
        gameResults.setEntityOne(gameController.playerEntities.get(0));
        gameResults.setEntityTwo(gameController.playerEntities.get(1));

        if (firstScore > secondScore) {
            gameResults.setWinnerIndex(1);
        } else if (firstScore < secondScore) {
            gameResults.setWinnerIndex(2);
        } else {
            gameResults.setWinnerIndex(-1);
        }
    }


    @Override
    public AbstractRenderBoard getBoardRender() {
        return GameRendersList.getInstance().renderSoccerBoard;
    }


    @Override
    public void createPlayerEntities() {
        ArrayList<AbstractPlayerEntity> playerEntities = gameController.playerEntities;

        if (GameRules.aiOnly) {
            playerEntities.add(new AiSoccerEntity(gameController));
            playerEntities.add(new AiSoccerEntity(gameController));
            return;
        }

        if (GameRules.twoPlayerMode) {
            playerEntities.add(new HumanSoccerEntity(gameController));
            playerEntities.add(new HumanSoccerEntity(gameController));
            return;
        }

        playerEntities.add(new HumanSoccerEntity(gameController));
        playerEntities.add(new AiSoccerEntity(gameController));
    }


    @Override
    public boolean isReadyForAimingMode() {
        return board.goalEffectFactor.get() == 0 && !goalRegistered;
    }


    @Override
    public void onApplyAimingMode() {
        goalRegistered = false;
    }


    @Override
    public int getDefaultScoreDelta() {
        return -1;
    }
}
