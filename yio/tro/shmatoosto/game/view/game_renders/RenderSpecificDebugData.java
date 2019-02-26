package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.AbstractGameplayManager;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardAiPreparationManager;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.gameplay.DebugDataManager;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class RenderSpecificDebugData extends GameRender{


    private TextureRegion redPixel;
    private TextureRegion greenPixel;
    private DebugDataManager debugDataManager;
    private TextureRegion bluePixel;
    PointYio tempPoint;


    public RenderSpecificDebugData() {
        tempPoint = new PointYio();
    }


    @Override
    protected void loadTextures() {
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
        greenPixel = GraphicsYio.loadTextureRegion("pixels/green.png", false);
        bluePixel = GraphicsYio.loadTextureRegion("pixels/blue.png", false);
    }


    @Override
    public void render() {
        if (!DebugFlags.showSpecificDebugData) return;
        debugDataManager = gameController.objectsLayer.debugDataManager;

//        showFirstHitBall();
//        showFirstCollisionPlace();
//        showSidePoint();
        showReferenceAimAngle();
    }


    private void showReferenceAimAngle() {
        if (gameController.yioGdxGame.isGamePaused()) return;

        BilliardAimUiElement billiardAimUiElement = Scenes.billiardAimUI.billiardAimUiElement;
        if (billiardAimUiElement == null) return;

        double referenceAngle = billiardAimUiElement.referenceAngle;

        Ball cue = gameController.objectsLayer.findCue();
        if (cue == null) return;

        tempPoint.setBy(cue.position.center);
        tempPoint.relocateRadial(2 * GraphicsYio.height, referenceAngle);

        GraphicsYio.drawLine(
                batchMovable,
                redPixel,
                cue.position.center,
                tempPoint,
                2 * GraphicsYio.borderThickness
        );
    }


    private void showSidePoint() {
        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        BilliardManager billiardManager = (BilliardManager) gameplayManager;
        BilliardAiPreparationManager aiPreparationManager = billiardManager.aiPreparationManager;

        PointYio sidePoint = aiPreparationManager.getSidePoint();

        GraphicsYio.setBatchAlpha(batchMovable, 0.3);

        GraphicsYio.drawFromCenter(
                batchMovable,
                gameView.blackPixel,
                sidePoint.x,
                sidePoint.y,
                2 * GraphicsYio.borderThickness
        );

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private void showFirstCollisionPlace() {
        Ball firstHitBall = debugDataManager.firstHitBall;
        if (firstHitBall == null) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.3);

        GraphicsYio.drawFromCenter(
                batchMovable,
                gameView.blackPixel,
                debugDataManager.firstPosition.x,
                debugDataManager.firstPosition.y,
                2 * GraphicsYio.borderThickness
        );

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private void showFirstHitBall() {
        Ball firstHitBall = debugDataManager.firstHitBall;
        if (firstHitBall == null) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.1);

        GraphicsYio.drawFromCenter(
                batchMovable,
                gameView.blackPixel,
                firstHitBall.position.center.x,
                firstHitBall.position.center.y,
                firstHitBall.collisionRadius
        );

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    @Override
    protected void disposeTextures() {
        redPixel.getTexture().dispose();
        greenPixel.getTexture().dispose();
        bluePixel.getTexture().dispose();
    }
}
