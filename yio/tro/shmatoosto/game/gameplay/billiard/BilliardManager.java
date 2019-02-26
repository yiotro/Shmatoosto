package yio.tro.shmatoosto.game.gameplay.billiard;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameResults;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.game_objects.ObjectsLayer;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.game.player_entities.AiBilliardEntity;
import yio.tro.shmatoosto.game.player_entities.HumanBilliardEntity;
import yio.tro.shmatoosto.game.view.game_renders.AbstractRenderBoard;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class BilliardManager extends AbstractGameplayManager {

    public BilliardBoard board;
    public ArrayList<Hole> holes;
    private float defaultBallRadius;
    public int firstScore, secondScore;
    PointYio tempPoint;
    int aimAppliesNumber;
    public BilliardAiPreparationManager aiPreparationManager;


    public BilliardManager(GameController gameController) {
        super(gameController);
        board = new BilliardBoard(this);
        holes = new ArrayList<>();
        defaultBallRadius = 0.04f * GraphicsYio.width;
        tempPoint = new PointYio();
        aiPreparationManager = new BilliardAiPreparationManager(this);
    }


    @Override
    public BackgroundYio getBackground() {
        return BackgroundYio.green;
    }


    @Override
    public void onEndCreation() {
//        spawnSomeRandomBalls();
        spawnBalls();

        board.initCollisionLines();
        initHoles();
        firstScore = 0;
        secondScore = 0;
        aimAppliesNumber = 0;
        aiPreparationManager.onEndCreation();
    }


    @Override
    public void onBallFellToHole(Ball ball) {
        if (isTooEarlyToStartCountingScores()) return; // ignore scoring at first shot

        AbstractPlayerEntity currentPlayerEntity = gameController.getCurrentPlayerEntity();
        int index = currentPlayerEntity.getIndex();
        int delta = 1;
        if (ball.isCue()) {
            delta = -1;
        }

        if (index == 0) {
            firstScore += delta;
        }

        if (index == 1) {
            secondScore += delta;
        }

        updateScoreView();
    }


    public boolean isTooEarlyToStartCountingScores() {
        return aimAppliesNumber < 2;
    }


    @Override
    public void onApplyAimingMode() {
        checkToSpawnCue();
        checkToSpawnFallenBalls();
        aimAppliesNumber++;
    }


    private void checkToSpawnCue() {
        if (getObjectsLayer().isCuePresentOnBoard()) return;

        PointYio placeForNewCue = findPlaceForNewBall();
        Ball ball = getObjectsLayer().addBall();
        ball.setColor(BColorYio.white);
        ball.setPosition(placeForNewCue.x, placeForNewCue.y);
        ball.setRadius(defaultBallRadius);
        ball.delta.reset();
    }


    private void checkToSpawnFallenBalls() {
        if (!GameRules.infiniteMode) return;

        ObjectsLayer objectsLayer = getObjectsLayer();
        while (objectsLayer.balls.size() < objectsLayer.initialBallsNumber) {
            PointYio placeForNewBall = findPlaceForNewBall();
            Ball ball = objectsLayer.addBall();
            ball.setColor(getRandomColor());
            ball.setPosition(placeForNewBall.x, placeForNewBall.y);
            ball.setRadius(defaultBallRadius);
            ball.delta.reset();
        }
    }


    private PointYio findPlaceForNewBall() {
        int c = 250;
        tempPoint.reset();
        while (c > 0) {
            c--;
            tempPoint.x = (float) (board.position.x + (0.1 + 0.8 * YioGdxGame.random.nextDouble()) * board.position.width);
            tempPoint.y = (float) (board.position.y + (0.1 + 0.8 * YioGdxGame.random.nextDouble()) * board.position.height);
            if (isPlaceValidForNewBall(tempPoint)) break;
        }
        return tempPoint;
    }


    private boolean isPlaceValidForNewBall(PointYio place) {
        for (Ball ball : getObjectsLayer().balls) {
            if (ball.position.center.distanceTo(place) < ball.collisionRadius * 2) {
                return false;
            }
        }

        return true;
    }


    public void updateScoreView() {
        Scenes.gameOverlay.updateScore(0, firstScore);
        Scenes.gameOverlay.updateScore(1, secondScore);
    }


    public int getEntityScore(AbstractPlayerEntity abstractPlayerEntity) {
        int index = abstractPlayerEntity.getIndex();

        if (index == 0) {
            return firstScore;
        }

        if (index == 1) {
            return secondScore;
        }

        return 0;
    }


    private void initHoles() {
        Ball sampleBall = new Ball(getObjectsLayer());
        sampleBall.setRadius(defaultBallRadius);
        float holeRadius = 1.1f * sampleBall.collisionRadius;
        PointYio p = new PointYio();

        for (CircleYio middleHole : board.middleHoles) {
            p.setBy(middleHole.center);
            p.relocateRadial(0.4 * middleHole.radius, middleHole.angle);
            Hole hole = addHole();
            hole.position.set(p.x, p.y, holeRadius).setAngle(middleHole.angle);
            getObjectsLayer().posMap.updateObjectPos(hole);
        }

        for (CircleYio corner : board.corners) {
            p.setBy(corner.center);
            p.relocateRadial(0.2 * corner.radius, corner.angle + Math.PI / 4);
            Hole hole = addHole();
            hole.position.set(p.x, p.y, holeRadius).setAngle(corner.angle + Math.PI / 4);
            getObjectsLayer().posMap.updateObjectPos(hole);
        }
    }


    private Hole addHole() {
        Hole hole = new Hole();
        holes.add(hole);
        getObjectsLayer().posMap.addObject(hole);
        return hole;
    }


    private void spawnBalls() {
        int n = (int) GameRules.initialParameters.getParameter("balls");

        // prepare
        RectangleYio pos = board.position;
        for (int i = 0; i < n; i++) {
            Ball ball = getObjectsLayer().addBall();
            ball.setColor(getRandomColor());
            ball.setPosition(
                    pos.x + pos.width / 2,
                    pos.y + 0.75 * pos.height
            );
            ball.position.center.relocateRadial(0.05f * GraphicsYio.width, Yio.getRandomAngle());
            ball.setRadius(defaultBallRadius);
            ball.delta.reset();
        }

        for (int k = 0; k < 100; k++) {
            getObjectsLayer().move();
        }

        spawnCue();
    }


    private void spawnCue() {
        Ball ball = getObjectsLayer().addBall();
        ball.setColor(BColorYio.white);
        RectangleYio pos = board.position;
        ball.setPosition(
                pos.x + pos.width / 2,
                pos.y + 0.2 * pos.height
        );
        ball.setRadius(defaultBallRadius);
        ball.delta.reset();
    }


    private void spawnSomeRandomBalls() {
        int n = (int) GameRules.initialParameters.getParameter("balls");

        for (int i = 0; i < n; i++) {
            Ball ball = getObjectsLayer().addBall();
            ball.setColor(getRandomColor());
            ball.setPosition(
                    (0.2 + 0.6 * YioGdxGame.random.nextDouble()) * GraphicsYio.width,
                    (0.2 + 0.6 * YioGdxGame.random.nextDouble()) * GraphicsYio.height
            );
            ball.setRadius(defaultBallRadius);
            ball.setSpeed(YioGdxGame.random.nextDouble() * ball.maxSpeed, Yio.getRandomAngle());
        }
    }


    BColorYio getRandomColor() {
        switch (YioGdxGame.random.nextInt(5)) {
            default:
            case 0:
                return BColorYio.blue;
            case 1:
                return BColorYio.yellow;
            case 2:
                return BColorYio.purple;
            case 3:
                return BColorYio.red;
            case 4:
                return BColorYio.cyan;
        }
    }


    @Override
    public void move() {

    }


    @Override
    public boolean isReadyToFinishGame() {
        ArrayList<Ball> balls = getObjectsLayer().balls;
        if (balls.size() != 1) return false;

        Ball firstBall = balls.get(0);
        if (!firstBall.isCue()) return false;

        return true;
    }


    @Override
    public void updateGameResults(GameResults gameResults) {
        gameResults.setLoadingType(LoadingType.billiard);
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
        return GameRendersList.getInstance().renderBilliardBoard;
    }


    @Override
    public void createPlayerEntities() {
        ArrayList<AbstractPlayerEntity> playerEntities = gameController.playerEntities;

        if (GameRules.aiOnly) {
            playerEntities.add(new AiBilliardEntity(gameController));
            playerEntities.add(new AiBilliardEntity(gameController));
            return;
        }

        if (GameRules.twoPlayerMode) {
            playerEntities.add(new HumanBilliardEntity(gameController));
            playerEntities.add(new HumanBilliardEntity(gameController));
            return;
        }

        playerEntities.add(new HumanBilliardEntity(gameController));
        playerEntities.add(new AiBilliardEntity(gameController));
    }
}
