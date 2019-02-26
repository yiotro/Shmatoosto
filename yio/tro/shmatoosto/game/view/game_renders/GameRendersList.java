package yio.tro.shmatoosto.game.view.game_renders;

import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.game.view.game_renders.debug.RenderAiPreparation;
import yio.tro.shmatoosto.game.view.game_renders.debug.RenderCollisionLines;
import yio.tro.shmatoosto.game.view.game_renders.debug.RenderPosMap;

import java.util.ArrayList;

public class GameRendersList {

    private static GameRendersList instance;
    ArrayList<GameRender> gameRenders = new ArrayList<>();

    public RenderBalls renderBalls;
    public RenderPosMap renderPosMap;
    public RenderCollisionLines renderCollisionLines;
    public RenderBilliardBoard renderBilliardBoard;
    public RenderAiPreparation renderAiPreparation;
    public RenderSpecificDebugData renderSpecificDebugData;
    public RenderChapaevoBoard renderChapaevoBoard;
    public RenderObstacles renderObstacles;
    public RenderSoccerBoard renderSoccerBoard;
    // initialize them lower


    public GameRendersList() {
        //
    }


    private void createAllRenders() {
        renderBalls = new RenderBalls();
        renderPosMap = new RenderPosMap();
        renderCollisionLines = new RenderCollisionLines();
        renderBilliardBoard = new RenderBilliardBoard();
        renderAiPreparation = new RenderAiPreparation();
        renderSpecificDebugData = new RenderSpecificDebugData();
        renderChapaevoBoard = new RenderChapaevoBoard();
        renderObstacles = new RenderObstacles();
        renderSoccerBoard = new RenderSoccerBoard();
    }


    public static GameRendersList getInstance() {
        if (instance == null) {
            instance = new GameRendersList();
            instance.createAllRenders();
        }

        return instance;
    }


    public static void initialize() {
        instance = null;
    }


    public void updateGameRenders(GameView gameView) {
        for (GameRender gameRender : gameRenders) {
            gameRender.update(gameView);
        }
    }
}
