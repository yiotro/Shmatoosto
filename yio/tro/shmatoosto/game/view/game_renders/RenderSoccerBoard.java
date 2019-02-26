package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerBoard;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderSoccerBoard extends AbstractRenderBoard{


    private SoccerManager soccerManager;
    private SoccerBoard board;
    private TextureRegion blackPixel;
    private RectangleYio pos;
    private TextureRegion accentPixel;
    private TextureRegion grassLight;
    private TextureRegion grassDark;
    private final float accentThickness;
    private TextureRegion centerCircle;
    private TextureRegion barbellTexture;
    private TextureRegion barbellPixel;
    private TextureRegion goalCorner;
    private TextureRegion accentCircle;
    private TextureRegion boardCorner;
    private TextureRegion redPixel;


    public RenderSoccerBoard() {
        accentThickness = 4 * GraphicsYio.borderThickness;
    }


    @Override
    public void renderBottomLayer() {
        updateReferences();

        renderGrass();
        renderGoalAreas();
        renderGoalEffect();
        renderBarbellLines();
        renderBorder();
        renderCircle();
        renderCenterLine();
        renderPlaygrounds();
        renderBarbells();
        renderGoalCorners();
        renderBoardCorners();
        renderAccentCircle();

        renderDebug();
    }


    private void renderBarbellLines() {
        GraphicsYio.renderBorder(
                batchMovable,
                barbellPixel,
                board.topGoalArea,
                accentThickness
        );

        GraphicsYio.renderBorder(
                batchMovable,
                barbellPixel,
                board.bottomGoalArea,
                accentThickness
        );
    }


    private void renderGoalEffect() {
        if (board.goalEffectFactor.get() == 0) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.4 * board.goalEffectFactor.get());

        GraphicsYio.drawByRectangle(
                batchMovable,
                redPixel,
                getGoalArea(board.goalIndex)
        );

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private RectangleYio getGoalArea(int goalIndex) {
        if (goalIndex == 0) {
            return board.bottomGoalArea;
        }

        return board.topGoalArea;
    }


    private void renderBoardCorners() {
        for (CircleYio corner : board.boardCorners) {
            GraphicsYio.drawByCircle(
                    batchMovable,
                    boardCorner,
                    corner
            );
        }
    }


    private void renderAccentCircle() {
        GraphicsYio.drawFromCenter(
                batchMovable,
                accentCircle,
                board.center.x,
                board.center.y,
                2 * accentThickness
        );
    }


    private void renderGoalCorners() {
        for (CircleYio corner : board.goalCorners) {
            GraphicsYio.drawByCircle(
                    batchMovable,
                    goalCorner,
                    corner
            );
        }
    }


    private void renderGoalAreas() {
        GraphicsYio.drawByRectangle(
                batchMovable,
                grassDark,
                board.topGoalArea
        );

        GraphicsYio.drawByRectangle(
                batchMovable,
                grassLight,
                board.bottomGoalArea
        );
    }


    private void renderBarbells() {
        for (CircleYio barbell : board.barbells) {
            GraphicsYio.drawByCircle(
                    batchMovable,
                    barbellTexture,
                    barbell
            );
        }
    }


    private void renderCircle() {
        GraphicsYio.drawFromCenter(
                batchMovable,
                centerCircle,
                board.center.x,
                board.center.y,
                board.circleRadius
        );
    }


    private void renderPlaygrounds() {
        GraphicsYio.renderBorder(
                batchMovable,
                accentPixel,
                board.bottomPlayground,
                accentThickness
        );

        GraphicsYio.renderBorder(
                batchMovable,
                accentPixel,
                board.topPlayground,
                accentThickness
        );
    }


    private void renderCenterLine() {
        GraphicsYio.drawLine(
                batchMovable,
                accentPixel,
                board.cLineLeft,
                board.cLineRight,
                accentThickness
        );
    }


    @Override
    protected void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
        accentPixel = GraphicsYio.loadTextureRegion("game/soccer/accent.png", false);
        grassLight = GraphicsYio.loadTextureRegion("game/soccer/grass_light.png", false);
        grassDark = GraphicsYio.loadTextureRegion("game/soccer/grass_dark.png", false);
        centerCircle = GraphicsYio.loadTextureRegion("game/soccer/soccer_circle.png", false);
        barbellTexture = GraphicsYio.loadTextureRegion("game/soccer/barbell.png", false);
        barbellPixel = GraphicsYio.loadTextureRegion("game/soccer/barbell_pixel.png", false);
        goalCorner = GraphicsYio.loadTextureRegion("game/soccer/goal_corner.png", false);
        accentCircle = GraphicsYio.loadTextureRegion("game/soccer/accent_circle.png", false);
        boardCorner = GraphicsYio.loadTextureRegion("game/soccer/soccer_board_corner.png", false);
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
    }


    @Override
    public void render() {

    }


    private void renderGrass() {
        GraphicsYio.drawByRectangle(
                batchMovable,
                grassLight,
                board.position
        );

        for (RectangleYio darkArea : board.darkAreas) {
            GraphicsYio.drawByRectangle(
                    batchMovable,
                    grassDark,
                    darkArea
            );
        }
    }


    private void renderBorder() {
        GraphicsYio.renderBorder(
                batchMovable,
                accentPixel,
                board.position,
                accentThickness
        );
    }


    private void renderDebug() {
//        GraphicsYio.renderBorder(
//                batchMovable,
//                blackPixel,
//                board.topDefenseArea
//        );

//        GraphicsYio.renderBorder(
//                batchMovable,
//                blackPixel,
//                board.bottomDefenseArea
//        );
    }


    private void updateReferences() {
        soccerManager = (SoccerManager) gameController.gameplayManager;
        board = soccerManager.board;
        pos = board.position;
    }


    @Override
    protected void disposeTextures() {
        blackPixel.getTexture().dispose();
        accentPixel.getTexture().dispose();
    }
}
