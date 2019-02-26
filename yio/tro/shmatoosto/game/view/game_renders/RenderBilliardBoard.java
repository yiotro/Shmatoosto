package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.gameplay.SimResults;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardBoard;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.gameplay.billiard.Hole;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.Storage3xTexture;

public class RenderBilliardBoard extends AbstractRenderBoard {


    private BilliardManager billiardManager;
    private BilliardBoard board;
    private Storage3xTexture bbSide;
    private Storage3xTexture bbMiddle;
    private Storage3xTexture bbCorner;
    private Storage3xTexture bbCornerHole;
    private Storage3xTexture bbMiddleHole;
    private Storage3xTexture cornerShadow;
    private Storage3xTexture middleShadow;
    private Storage3xTexture sideShadow;
    float decRadius;
    private Storage3xTexture decCircle;
    private TextureRegion darkGrayBackground;
    private final PointYio temp;


    public RenderBilliardBoard() {
        decRadius = 0.008f * GraphicsYio.width;
        temp = new PointYio();
    }


    @Override
    protected void loadTextures() {
        bbSide = loadBoardTexture("bb_side");
        bbMiddle = loadBoardTexture("bb_middle");
        bbCorner = loadBoardTexture("bb_corner");
        bbCornerHole = load3xTexture("bb_corner_hole");
        bbMiddleHole = load3xTexture("bb_middle_hole");
        sideShadow = loadBoardTexture("bb_side_shadow");
        middleShadow = loadBoardTexture("bb_middle_shadow");
        cornerShadow = loadBoardTexture("bb_corner_shadow");
        decCircle = loadBoardTexture("dec_circle");
        darkGrayBackground = GraphicsYio.loadTextureRegion("game/background/dark_gray.png", false);
    }


    @Override
    public void renderBottomLayer() {
        updateReferences();

        renderHoles();
        renderShadows();
    }


    private void renderShadows() {
        for (CircleYio corner : board.corners) {
            GraphicsYio.drawByCircle(batchMovable, cornerShadow.getTextureRegion(), corner);
        }

        for (CircleYio middleHole : board.middleHoles) {
            GraphicsYio.drawByCircle(batchMovable, middleShadow.getTextureRegion(), middleHole);
        }

        for (SimResults.BoardLine boardLine : board.boardLines) {
            GraphicsYio.drawLine(
                    batchMovable,
                    sideShadow.getTextureRegion(),
                    boardLine.one,
                    boardLine.two,
                    2 * board.cRadius
            );
        }
    }


    private void renderHoles() {
        for (CircleYio corner : board.corners) {
            GraphicsYio.drawByCircle(batchMovable, bbCornerHole.getTextureRegion(), corner);
        }

        for (CircleYio middleHole : board.middleHoles) {
            GraphicsYio.drawByCircle(batchMovable, bbMiddleHole.getTextureRegion(), middleHole);
        }
    }


    private void updateReferences() {
        billiardManager = (BilliardManager) gameController.gameplayManager;
        board = billiardManager.board;
    }


    @Override
    public void render() {
        updateReferences();

        renderSides();
        renderMiddleHoles();
        renderCorners();
        renderDecorativeCircles();
        renderTopBezel();
    }


    private void renderTopBezel() {
        GraphicsYio.drawByRectangle(batchMovable, darkGrayBackground, board.topBezelPosition);
    }


    private void renderDecorativeCircles() {
        for (PointYio decorativeCircle : board.decorativeCircles) {
            GraphicsYio.drawFromCenter(batchMovable, decCircle.getTextureRegion(), decorativeCircle.x, decorativeCircle.y, decRadius);
        }
    }


    private void renderCorners() {
        for (CircleYio corner : board.corners) {
            GraphicsYio.drawByCircle(batchMovable, bbCorner.getTextureRegion(), corner);
        }
    }


    private void renderMiddleHoles() {
        for (CircleYio middleHole : board.middleHoles) {
            GraphicsYio.drawByCircle(batchMovable, bbMiddle.getTextureRegion(), middleHole);
        }
    }


    private void renderSides() {
        for (SimResults.BoardLine boardLine : board.boardLines) {
            GraphicsYio.drawLine(
                    batchMovable,
                    bbSide.getTextureRegion(),
                    boardLine.one,
                    boardLine.two,
                    2 * board.cRadius
            );
        }
    }


    public void renderDebug() {
        GraphicsYio.renderBorder(batchMovable, gameView.blackPixel, board.position, 3 * GraphicsYio.borderThickness);

        for (SimResults.BoardLine boardLine : board.boardLines) {
            GraphicsYio.drawLine(batchMovable, gameView.whitePixel, boardLine.one, boardLine.two, 3 * GraphicsYio.borderThickness);
        }

        for (CircleYio corner : board.corners) {
            GraphicsYio.drawFromCenter(batchMovable, gameView.whitePixel, corner.center.x, corner.center.y, 3 * GraphicsYio.borderThickness);
        }

        for (CircleYio middleHole : board.middleHoles) {
            GraphicsYio.drawFromCenter(batchMovable, gameView.whitePixel, middleHole.center.x, middleHole.center.y, 3 * GraphicsYio.borderThickness);
        }

        BilliardManager billiardManager = (BilliardManager) gameController.gameplayManager;
        for (Hole hole : billiardManager.holes) {
            temp.setBy(hole.position.center);
            GraphicsYio.drawFromCenter(batchMovable, gameView.whitePixel, temp.x, temp.y, 3 * GraphicsYio.borderThickness);
            temp.relocateRadial(hole.position.radius, hole.position.angle);
            GraphicsYio.drawLine(batchMovable, gameView.whitePixel, hole.position.center, temp, GraphicsYio.borderThickness);
        }
    }


    @Override
    protected void disposeTextures() {

    }
}
