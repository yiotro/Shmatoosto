package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.BilliardAimUiElement;
import yio.tro.shmatoosto.menu.elements.gameplay.ShotStrengthBar;
import yio.tro.shmatoosto.stuff.CircleYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.Masking;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderBilliardAimUI extends RenderInterfaceElement{


    private BilliardAimUiElement billiardAimUiElement;
    private float f;
    private TextureRegion aimPointTexture;
    private TextureRegion whitePixel;
    private ShotStrengthBar shotStrengthBar;
    private TextureRegion redPixel;
    private TextureRegion stickTexture;


    @Override
    public void loadTextures() {
        aimPointTexture = GraphicsYio.loadTextureRegion("game/billiard/aim_point.png", false);
        whitePixel = GraphicsYio.loadTextureRegion("pixels/white.png", false);
        redPixel = GraphicsYio.loadTextureRegion("pixels/red.png", false);
        stickTexture = GraphicsYio.loadTextureRegion("game/billiard/stick.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        billiardAimUiElement = (BilliardAimUiElement) element;
        f = billiardAimUiElement.getFactor().get();

        resetAlpha();
        renderInternals();
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void resetAlpha() {
        GraphicsYio.setBatchAlpha(batch, f);
    }


    private void renderInternals() {
        if (isMaskingNecessary()) {
            batch.end();
            Masking.begin();
            menuViewYio.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float cx = w / 2;
            float cy = h / 2;
            float fw = getGameView().appearFactor.get() * cx;
            float fh = getGameView().appearFactor.get() * cy;
            menuViewYio.shapeRenderer.rect(cx - fw, cy - fh, 2 * fw, 2 * fh);
            menuViewYio.shapeRenderer.end();
            batch.begin();
            Masking.continueAfterBatchBegin();
            renderAimLine();
            Masking.end(batch);
        } else {
            renderAimLine();
        }

        renderShotStrengthBar();
    }


    private void renderShotStrengthBar() {
        shotStrengthBar = billiardAimUiElement.shotStrengthBar;

        RectangleYio pos = shotStrengthBar.viewPosition;
        GraphicsYio.drawLine(
                batch,
                blackPixel,
                pos.x, pos.y + pos.height,
                pos.x + pos.width, pos.y + pos.height,
                2 * GraphicsYio.borderThickness
        );

        GraphicsYio.drawByCircle(batch, stickTexture, shotStrengthBar.viewStickCircle);
    }


    private boolean isMaskingNecessary() {
        return f > 0 && getGameView().isInMotion();
    }


    private void renderAimLine() {
        GraphicsYio.setBatchAlpha(batch, billiardAimUiElement.lineAlphaFactor.get());

        for (CircleYio circleYio : billiardAimUiElement.aimLine) {
            GraphicsYio.drawFromCenter(
                    batch,
                    aimPointTexture,
                    circleYio.center.x,
                    circleYio.center.y,
                    circleYio.radius
            );
        }

        resetAlpha();
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
