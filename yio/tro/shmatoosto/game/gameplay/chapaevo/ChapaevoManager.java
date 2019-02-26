package yio.tro.shmatoosto.game.gameplay.chapaevo;

import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameResults;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.game_objects.Obstacle;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiChapaevoEntity;
import yio.tro.shmatoosto.game.player_entities.HumanChapaevoEntity;
import yio.tro.shmatoosto.game.view.game_renders.AbstractRenderBoard;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class ChapaevoManager extends AbstractGameplayManager{

    public ChapaevoBoard board;
    public float defaultBallRadius;
    public RectangleYio topSpawnPlace, bottomSpawnPlace;
    ArrayList<Ball> currentFallenBallsList;
    public int firstScore, secondScore; // used only for statistics
    BColorYio colorPair[];
    public boolean firstShotMade;


    public ChapaevoManager(GameController gameController) {
        super(gameController);

        defaultBallRadius = 0.04f * GraphicsYio.width;
        board = new ChapaevoBoard(this);
        topSpawnPlace = new RectangleYio();
        bottomSpawnPlace = new RectangleYio();
        currentFallenBallsList = new ArrayList<>();
    }


    @Override
    public BackgroundYio getBackground() {
        return BackgroundYio.chapaevo;
    }


    @Override
    public void onEndCreation() {
        firstScore = 0;
        secondScore = 0;
        firstShotMade = false;

        spawnBalls();
        checkToAddObstacles();
    }


    private void checkToAddObstacles() {
        if (!GameRules.obstaclesEnabled) return;

        spawnCentralObstacles();
        spawnSideObstacles();

        for (Obstacle o : getObjectsLayer().obstacles) {
            o.applyCollisionLines();
        }
    }


    private void spawnSideObstacles() {
        RectangleYio bp = board.position;
        float oh = 0.4f * bp.height;
        float ow = defaultBallRadius;

        getObjectsLayer().addObstacle().position.set(
                bp.x - ow / 2, bp.y + bp.height / 2 - oh / 2,
                ow, oh
        );

        getObjectsLayer().addObstacle().position.set(
                bp.x + bp.width - ow / 2, bp.y + bp.height / 2 - oh / 2,
                ow, oh
        );
    }


    private void spawnCentralObstacles() {
        int n = YioGdxGame.random.nextInt(2) + 2;

        double h = defaultBallRadius;
        for (int i = 0; i < n; i++) {
            Obstacle obstacle = getObjectsLayer().addObstacle();

            double w = (0.1 + 0.25 * YioGdxGame.random.nextDouble()) * GraphicsYio.width;
            double cx = board.position.x + (0.25 + 0.5 * YioGdxGame.random.nextDouble()) * board.position.width;
            double cy = board.position.y + board.position.height / 2 - h / 2;

            obstacle.position.set(cx, cy, w, h);
        }

        removeObstacleCollisions();
        limitObstaclesByBoard();
    }


    private void limitObstaclesByBoard() {
        for (Obstacle obstacle : getObjectsLayer().obstacles) {
            obstacle.limitBy(board.position);
        }
    }


    private void removeObstacleCollisions() {
        ArrayList<Obstacle> obstacles = getObjectsLayer().obstacles;
        ArrayList<Obstacle> removalList = new ArrayList<>();

        for (int i = obstacles.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                if (!obstacles.get(i).isInCollisionWith(obstacles.get(j))) continue;
                if (removalList.contains(obstacles.get(j))) continue;

                removalList.add(obstacles.get(j));
            }
        }

        for (Obstacle obstacle : removalList) {
            getObjectsLayer().removeObstacle(obstacle);
            obstacles.remove(obstacle);
        }

        removalList.clear();
    }


    private void spawnBalls() {
        updateSpawnPlaces();
        updateColorPair();

        int n = (int) GameRules.initialParameters.getParameter("balls");
        spawnSomeBallsWithParameters(n / 2, getEntityColor(0), bottomSpawnPlace);
        spawnSomeBallsWithParameters(n / 2, getEntityColor(1), topSpawnPlace);

        for (int k = 0; k < 100; k++) {
            getObjectsLayer().move();
        }
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
            ball.setBoardFriction(1.5f * Ball.DEFAULT_BOARD_FRICTION);
            ball.delta.reset();
        }
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


    public void updateColorPair() {
        colorPair = getRandomColorPair();

        if (YioGdxGame.random.nextBoolean()) {
            BColorYio tempColor = colorPair[0];
            colorPair[0] = colorPair[1];
            colorPair[1] = tempColor;
        }
    }


    private BColorYio[] getRandomColorPair() {
        switch (YioGdxGame.random.nextInt(6)) {
            default:
                return null;
            case 0:
                return new BColorYio[]{BColorYio.cyan, BColorYio.red};
            case 1:
                return new BColorYio[]{BColorYio.blue, BColorYio.yellow};
            case 2:
                return new BColorYio[]{BColorYio.purple, BColorYio.green};
            case 3:
                return new BColorYio[]{BColorYio.blue, BColorYio.red};
            case 4:
                return new BColorYio[]{BColorYio.red, BColorYio.green};
            case 5:
                return new BColorYio[]{BColorYio.blue, BColorYio.green};
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
        checkForOutsideBalls();
        checkToProcessFallenBallsList();
    }


    private void checkToProcessFallenBallsList() {
        if (!gameController.actionMode && !getObjectsLayer().inSimulationMode()) return;
        if (currentFallenBallsList.size() == 0) return;

        for (Ball ball : currentFallenBallsList) {
            getObjectsLayer().removeBall(ball);
            getObjectsLayer().addFallBallAnimation(ball);
            onBallFellOutside(ball);
        }

        currentFallenBallsList.clear();
    }


    private void onBallFellOutside(Ball ball) {
        if (getObjectsLayer().inSimulationMode()) {
            getObjectsLayer().simulationManager.onBallFellOutsideDuringSimulation(ball);
            return;
        }

        SoundManager.playSound(SoundManager.fall);

        if (ball.getColor() != getEntityColor(gameController.getCurrentPlayerEntity())) {
            increaseCurrentScore();
        } else {
            decreaseCurrentScore();
        }
    }


    private void decreaseCurrentScore() {
        if (gameController.currentEntityIndex == 0) {
            firstScore--;
        } else {
            secondScore--;
        }
    }


    private void increaseCurrentScore() {
        if (gameController.currentEntityIndex == 0) {
            firstScore++;
        } else {
            secondScore++;
        }
    }


    private void checkForOutsideBalls() {
        if (!gameController.actionMode && !getObjectsLayer().inSimulationMode()) return;

        currentFallenBallsList.clear();
        for (Ball ball : getObjectsLayer().balls) {
            if (ball.isFrozen()) continue;
            if (isBallInsideBoard(ball)) continue;
            if (currentFallenBallsList.contains(ball)) continue;

            currentFallenBallsList.add(ball);
        }
    }


    public boolean isBallInsideBoard(Ball ball) {
        return InterfaceElement.isTouchInsideRectangle(ball.position.center, board.position);
    }


    @Override
    public boolean isReadyToFinishGame() {
        if (!isThereAtLeastOneBallWithThisColor(getEntityColor(0))) return true;
        if (!isThereAtLeastOneBallWithThisColor(getEntityColor(1))) return true;

        return false;
    }


    public boolean isThereAtLeastOneBallWithThisColor(BColorYio filterColor) {
        for (Ball ball : getObjectsLayer().balls) {
            if (ball.getColor() != filterColor) continue;

            return true;
        }

        return false;
    }


    @Override
    public void updateGameResults(GameResults gameResults) {
        gameResults.setLoadingType(LoadingType.chapaevo);
        gameResults.setEntityOne(gameController.playerEntities.get(0));
        gameResults.setEntityTwo(gameController.playerEntities.get(1));

        if (isThereAtLeastOneBallWithThisColor(getEntityColor(0))) {
            gameResults.setWinnerIndex(1);
        } else if (isThereAtLeastOneBallWithThisColor(getEntityColor(1))) {
            gameResults.setWinnerIndex(2);
        } else {
            gameResults.setWinnerIndex(-1);
        }
    }


    @Override
    public void onMenuOverlayCreated() {
        Scenes.gameOverlay.scoreViewElement.destroy();
    }


    @Override
    public AbstractRenderBoard getBoardRender() {
        return GameRendersList.getInstance().renderChapaevoBoard;
    }


    @Override
    public void createPlayerEntities() {
        ArrayList<AbstractPlayerEntity> playerEntities = gameController.playerEntities;

        if (GameRules.aiOnly) {
            playerEntities.add(new AiChapaevoEntity(gameController));
            playerEntities.add(new AiChapaevoEntity(gameController));
            return;
        }

        if (GameRules.twoPlayerMode) {
            playerEntities.add(new HumanChapaevoEntity(gameController));
            playerEntities.add(new HumanChapaevoEntity(gameController));
            return;
        }

        playerEntities.add(new HumanChapaevoEntity(gameController));
        playerEntities.add(new AiChapaevoEntity(gameController));
    }


    @Override
    public void onApplyAimingMode() {

    }


    public int getEntityScore(AbstractPlayerEntity abstractPlayerEntity) {
        switch (abstractPlayerEntity.getIndex()) {
            default:
                return -1;
            case 0:
                return firstScore;
            case 1:
                return secondScore;
        }
    }
}
