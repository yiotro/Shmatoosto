package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.SoccerAimUiElement;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class RenderSoccerAimUI extends RenderInterfaceElement{


    private TextureRegion blackBallTexture;
    private SoccerAimUiElement soccerAimUiElement;
    private Ball selectedBall;
    private double aimAngle;
    private double aimDistance;
    PointYio stickCenter;
    float stickLength, maxStickDelta;
    private TextureRegion stickTexture;
    private TextureRegion aimPointTexture;
    private PointYio selectedPoint;
    private TextureRegion specialAimPointTexture;
    private Ball firstBallToHit;


    public RenderSoccerAimUI() {
        stickLength = 0.3f * GraphicsYio.width;
        maxStickDelta = 0.12f * GraphicsYio.width;
        stickCenter = new PointYio();
    }


    @Override
    public void loadTextures() {
        blackBallTexture = GraphicsYio.loadTextureRegion("game/atlas/ball_black.png", false);
        stickTexture = GraphicsYio.loadTextureRegion("game/billiard/stick.png", true);
        aimPointTexture = GraphicsYio.loadTextureRegion("game/billiard/aim_point.png", false);
        specialAimPointTexture = GraphicsYio.loadTextureRegion("game/billiard/special_aim_point.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        updateReferences(element);

        renderSelectionEffect();
        renderAimLine();
        renderSpecialAimPoint();
        renderStick();
        renderHint();
    }


    private void renderHint() {
        if (!isStickVisible()) return;
        if (soccerAimUiElement.hasShotReadyOnHold) return;

        firstBallToHit = soccerAimUiElement.firstBallToHit;
        if (firstBallToHit == null) return;

        GraphicsYio.setBatchAlpha(batch, 0.2 * soccerAimUiElement.aimAlphaFactor.get());

        GraphicsYio.drawLine(
                batch,
                blackPixel,
                soccerAimUiElement.hintStart,
                soccerAimUiElement.hintEnd,
                GraphicsYio.borderThickness
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderSpecialAimPoint() {
        if (!isStickVisible()) return;

        GraphicsYio.setBatchAlpha(batch, soccerAimUiElement.aimAlphaFactor.get());

        GraphicsYio.drawFromCenter(
                batch,
                specialAimPointTexture,
                soccerAimUiElement.specialAimPoint.x,
                soccerAimUiElement.specialAimPoint.y,
                0.007f * GraphicsYio.width
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderStick() {
        if (!isStickVisible()) return;

        stickCenter.setBy(selectedPoint);
        stickCenter.relocateRadial(
                -selectedBall.collisionRadius - getStickDelta() - stickLength / 2,
                soccerAimUiElement.stickViewAngle
        );

        GraphicsYio.setBatchAlpha(batch, soccerAimUiElement.stickAlphaFactor.get());

        GraphicsYio.drawFromCenterRotated(
                batch,
                stickTexture,
                stickCenter.x,
                stickCenter.y,
                stickLength / 2,
                soccerAimUiElement.stickViewAngle - Math.PI / 2
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderAimLine() {
        if (!isStickVisible()) return;

        GraphicsYio.setBatchAlpha(batch, soccerAimUiElement.aimAlphaFactor.get());

        for (CircleYio circleYio : soccerAimUiElement.aimLine) {
            GraphicsYio.drawFromCenter(
                    batch,
                    blackBallTexture,
                    circleYio.center.x,
                    circleYio.center.y,
                    circleYio.radius
            );
        }

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private boolean isStickVisible() {
        return selectedBall != null;
    }


    private double getStickDelta() {
        return soccerAimUiElement.aimPower * maxStickDelta * (1 - soccerAimUiElement.shotFactor.get());
    }


    private void updateReferences(InterfaceElement element) {
        soccerAimUiElement = (SoccerAimUiElement) element;
        selectedBall = soccerAimUiElement.selectedBall;
        aimAngle = soccerAimUiElement.aimAngle;
        aimDistance = soccerAimUiElement.aimDistance;
        selectedPoint = soccerAimUiElement.selectedPoint;
    }


    private void renderSelectionEffect() {
        if (selectedBall == null) return;

        GraphicsYio.setBatchAlpha(batch, 0.25 * soccerAimUiElement.selectionAlphaFactor.get());

        GraphicsYio.drawFromCenter(
                batch,
                blackBallTexture,
                selectedPoint.x,
                selectedPoint.y,
                4 * soccerAimUiElement.selectionRadiusFactor.get() * selectedBall.collisionRadius
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
