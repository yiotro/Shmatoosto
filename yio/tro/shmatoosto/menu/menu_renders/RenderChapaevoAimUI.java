package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.game_objects.Ball;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.ChapaevoAimUiElement;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class RenderChapaevoAimUI extends RenderInterfaceElement{


    private TextureRegion blackBallTexture;
    private ChapaevoAimUiElement chapaevoAimUiElement;
    private Ball selectedBall;
    private double aimAngle;
    private double aimDistance;
    PointYio stickCenter;
    float stickLength, maxStickDelta;
    private TextureRegion stickTexture;
    private TextureRegion aimPointTexture;
    private PointYio selectedPoint;
    private TextureRegion specialAimPointTexture;


    public RenderChapaevoAimUI() {
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
    }


    private void renderSpecialAimPoint() {
        if (!isStickVisible()) return;

        GraphicsYio.setBatchAlpha(batch, chapaevoAimUiElement.aimAlphaFactor.get());

        GraphicsYio.drawFromCenter(
                batch,
                specialAimPointTexture,
                chapaevoAimUiElement.specialAimPoint.x,
                chapaevoAimUiElement.specialAimPoint.y,
                0.007f * GraphicsYio.width
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderStick() {
        if (!isStickVisible()) return;

        stickCenter.setBy(selectedPoint);
        stickCenter.relocateRadial(
                -selectedBall.collisionRadius - getStickDelta() - stickLength / 2,
                chapaevoAimUiElement.stickViewAngle
        );

        GraphicsYio.setBatchAlpha(batch, chapaevoAimUiElement.stickAlphaFactor.get());

        GraphicsYio.drawFromCenterRotated(
                batch,
                stickTexture,
                stickCenter.x,
                stickCenter.y,
                stickLength / 2,
                chapaevoAimUiElement.stickViewAngle - Math.PI / 2
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderAimLine() {
        if (!isStickVisible()) return;

        GraphicsYio.setBatchAlpha(batch, chapaevoAimUiElement.aimAlphaFactor.get());

        for (CircleYio circleYio : chapaevoAimUiElement.aimLine) {
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
        return chapaevoAimUiElement.aimPower * maxStickDelta * (1 - chapaevoAimUiElement.shotFactor.get());
    }


    private void updateReferences(InterfaceElement element) {
        chapaevoAimUiElement = (ChapaevoAimUiElement) element;
        selectedBall = chapaevoAimUiElement.selectedBall;
        aimAngle = chapaevoAimUiElement.aimAngle;
        aimDistance = chapaevoAimUiElement.aimDistance;
        selectedPoint = chapaevoAimUiElement.selectedPoint;
    }


    private void renderSelectionEffect() {
        if (selectedBall == null) return;

        GraphicsYio.setBatchAlpha(batch, 0.25 * chapaevoAimUiElement.selectionAlphaFactor.get());

        GraphicsYio.drawFromCenter(
                batch,
                blackBallTexture,
                selectedPoint.x,
                selectedPoint.y,
                4 * chapaevoAimUiElement.selectionRadiusFactor.get() * selectedBall.collisionRadius
        );

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
