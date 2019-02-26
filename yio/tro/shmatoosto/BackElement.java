package yio.tro.shmatoosto;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.Storage3xTexture;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.Random;

public class BackElement {

    BackElementsController backElementsController;
    FactorYio alphaFactor, sizeFactor;
    TextureRegion texture;
    PointYio position;
    float speed, angle, aDelta;
    float radius, viewRadius, maxSpeed;
    public double viewAngle, deltaViewAngle;
    public static final float size = 0.05f * Gdx.graphics.getWidth();
    boolean visible;


    public BackElement(BackElementsController backElementsController) {
        this.backElementsController = backElementsController;
        alphaFactor = new FactorYio();
        sizeFactor = new FactorYio();
        position = new PointYio();
        Random random = new Random();
        int index = 1 + random.nextInt(6);
        maxSpeed = 0.025f * size;
        speed = 0;
        angle = 0;
        aDelta = 0;
        viewAngle = Yio.getRandomAngle();
        deltaViewAngle = 0.02 * (2 * YioGdxGame.random.nextDouble() - 1);
        texture = (new Storage3xTexture(backElementsController.atlasLoader, "bg" + index + ".png")).getNormal();
        visible = false;
    }


    public void resurrect(int x, int y) {
        visible = true;

        factorsOnAppear();

        position.x = x * size;
        position.y = y * size;
        radius = (0.7f + 0.3f * YioGdxGame.random.nextFloat()) * size;
        viewRadius = 0;

        giveRandomSpeed();
        updateRadius();
    }


    private void giveRandomSpeed() {
        angle = (float) Yio.getRandomAngle();
        speed = maxSpeed;

        aDelta = 0.01f * YioGdxGame.random.nextFloat();
        if (YioGdxGame.random.nextBoolean()) {
            aDelta *= -1;
        }
    }


    private void factorsOnAppear() {
        alphaFactor.setValues(1, 0);
        alphaFactor.destroy(1, 0.01);
        sizeFactor.setValues(0, 0);
        sizeFactor.appear(3, 1);
    }


    public void move() {
        if (!visible) return;

        moveBody();
        moveSpeed();
        moveViewAngle();

        moveFactors();
        checkToCancelVisibility();

        updateRadius();
    }


    private void moveViewAngle() {
        viewAngle += deltaViewAngle;
    }


    private void moveSpeed() {
        angle += aDelta;
    }


    private void moveBody() {
        position.relocateRadial(speed, angle);

        applyLimits();
    }


    private void applyLimits() {
        if (position.x > GraphicsYio.width) {
            position.x -= GraphicsYio.width;
        }

        if (position.x < 0) {
            position.x += GraphicsYio.width;
        }

        if (position.y > GraphicsYio.height) {
            position.y -= GraphicsYio.height;
        }

        if (position.y < 0) {
            position.y += GraphicsYio.height;
        }
    }


    private void checkToCancelVisibility() {
        if (alphaFactor.get() == 0) {
            visible = false;
        }
    }


    private void moveFactors() {
        alphaFactor.move();
        sizeFactor.move();
    }


    private void updateRadius() {
        viewRadius = sizeFactor.get() * radius;
    }
}
