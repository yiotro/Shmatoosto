package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoBoard;
import yio.tro.shmatoosto.game.gameplay.chapaevo.ChapaevoManager;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderChapaevoBoard extends AbstractRenderBoard{


    private ChapaevoManager chapaevoManager;
    private ChapaevoBoard board;
    private TextureRegion blackPixel;
    private TextureRegion redPixel;
    private RectangleYio shadowPosition;
    private RectangleYio pos;


    public RenderChapaevoBoard() {
        shadowPosition = new RectangleYio();
    }


    @Override
    protected void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
    }


    @Override
    public void renderBottomLayer() {
        updateReferences();

        renderShadow();
        renderMainPart();
        renderCenterLine();
        renderDebug();
    }


    private void renderCenterLine() {
        GraphicsYio.setBatchAlpha(batchMovable, 0.1);

        GraphicsYio.drawLine(
                batchMovable,
                blackPixel,
                pos.x + 0.1f * GraphicsYio.width,
                pos.y + pos.height / 2,
                pos.x + pos.width - 0.1f * GraphicsYio.width,
                pos.y + pos.height / 2,
                2 * GraphicsYio.borderThickness
        );

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private void renderShadow() {
        shadowPosition.setBy(pos);
        shadowPosition.y += 0.0125f * GraphicsYio.width;
        shadowPosition.increase(0.01f * GraphicsYio.width);

        MenuRenders.renderShadow.renderShadow(
                batchMovable,
                shadowPosition,
                1,
                0.1f * GraphicsYio.width
        );
    }


    private void renderMainPart() {
        MenuRenders.renderRoundShape.renderRoundShape(
                batchMovable,
                pos,
                BackgroundYio.white,
                chapaevoManager.defaultBallRadius
        );
    }


    private void renderDebug() {
//        GraphicsYio.renderBorder(
//                batchMovable,
//                blackPixel,
//                chapaevoManager.topSpawnPlace
//        );

//        GraphicsYio.renderBorder(
//                batchMovable,
//                blackPixel,
//                chapaevoManager.bottomSpawnPlace
//        );
    }


    private void updateReferences() {
        chapaevoManager = (ChapaevoManager) gameController.gameplayManager;
        board = chapaevoManager.board;
        pos = board.position;
    }


    @Override
    public void render() {

    }


    @Override
    protected void disposeTextures() {

    }
}
