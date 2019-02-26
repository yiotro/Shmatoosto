package yio.tro.shmatoosto.game.view.game_renders.debug;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.game.gameplay.*;
import yio.tro.shmatoosto.game.gameplay.billiard.BPointOfInterest;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardAiPreparationManager;
import yio.tro.shmatoosto.game.gameplay.billiard.BilliardManager;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class RenderAiPreparation extends GameRender{


    private BilliardManager billiardManager;
    private BilliardAiPreparationManager aiPreparationManager;
    private TextureRegion redPixel;
    private Ball inspectedBall;
    PointYio tempPoint;


    public RenderAiPreparation() {
        tempPoint = new PointYio();
    }


    @Override
    protected void loadTextures() {
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
    }


    @Override
    public void render() {
        if (!DebugFlags.showAiPreparation) return;

        AbstractGameplayManager gameplayManager = gameController.gameplayManager;
        if (!(gameplayManager instanceof BilliardManager)) return;

        billiardManager = (BilliardManager) gameplayManager;
        aiPreparationManager = billiardManager.aiPreparationManager;

        renderInternals();
    }


    private void renderInternals() {
        renderInnerRect();
        renderPointsOfInterest();
        if (gameController.isHumanTurn() && !gameController.actionMode) {
            renderInspectedBall();
            renderInspectionList();
            renderShotItems();
        }

        GraphicsYio.setBatchAlpha(batchMovable, 1);
    }


    private void renderShotItems() {
        Ball cue = gameController.objectsLayer.findCue();
        if (cue == null) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.25);
        for (AiShotItem shotItem : aiPreparationManager.shotItems) {
            tempPoint.setBy(cue.position.center);
            tempPoint.relocateRadial(2 * GraphicsYio.height, shotItem.angle);
            GraphicsYio.drawLine(
                    batchMovable,
                    redPixel,
                    cue.position.center,
                    tempPoint,
                    2 * GraphicsYio.borderThickness
            );
        }
    }


    private void renderInspectionList() {
        inspectedBall = aiPreparationManager.inspectedBall;
        if (inspectedBall == null) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.1);

        for (BPointOfInterest bPointOfInterest : aiPreparationManager.inspectionList) {
            GraphicsYio.drawLine(
                    batchMovable,
                    gameView.blackPixel,
                    inspectedBall.position.center,
                    bPointOfInterest.position,
                    GraphicsYio.borderThickness
            );
        }
    }


    private void renderInspectedBall() {
        inspectedBall = aiPreparationManager.inspectedBall;
        if (inspectedBall == null) return;

        GraphicsYio.setBatchAlpha(batchMovable, 0.1);
        GraphicsYio.drawFromCenter(
                batchMovable,
                gameView.blackPixel,
                inspectedBall.position.center.x,
                inspectedBall.position.center.y,
                inspectedBall.position.radius
        );
    }


    private void renderPointsOfInterest() {
        GraphicsYio.setBatchAlpha(batchMovable, 0.5);
        for (BPointOfInterest bPointOfInterest : aiPreparationManager.pointsOfInterest) {
            GraphicsYio.drawFromCenter(
                    batchMovable,
                    redPixel,
                    bPointOfInterest.position.x,
                    bPointOfInterest.position.y,
                    4 * GraphicsYio.borderThickness
            );
        }
    }


    private void renderInnerRect() {
        GraphicsYio.setBatchAlpha(batchMovable, 0.1);
        GraphicsYio.renderBorder(batchMovable, gameView.blackPixel, aiPreparationManager.innerRect);
    }


    @Override
    protected void disposeTextures() {
        redPixel.getTexture().dispose();
    }
}
