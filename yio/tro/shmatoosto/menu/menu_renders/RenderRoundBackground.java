package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderRoundBackground extends RenderInterfaceElement{


    private TextureRegion yellowBackground;
    private TextureRegion yellowCorner;
    private TextureRegion blueBackground;
    private TextureRegion blueCorner;
    private TextureRegion grayBackground;
    private TextureRegion grayCorner;
    private TextureRegion whiteBackground;
    private TextureRegion whiteCorner;
    private TextureRegion orangeBackground;
    private TextureRegion orangeCorner;
    private TextureRegion greenBackground;
    private TextureRegion greenCorner;
    TextureRegion currentBackground;
    TextureRegion currentCorner;
    float cornerRadius;
    RectangleYio pos1, pos2, pos3;
    private RectangleYio position;
    PointYio corners[];
    private TextureRegion blackBackground;
    private TextureRegion blackCorner;
    SpriteBatch currentBatch;


    public RenderRoundBackground() {
        pos1 = new RectangleYio();
        pos2 = new RectangleYio();
        pos3 = new RectangleYio();
        currentBackground = null;

        corners = new PointYio[4];
        for (int i = 0; i < corners.length; i++) {
            corners[i] = new PointYio();
        }
    }


    @Override
    public void loadTextures() {
        yellowBackground = loadMainBackground("yellow");
        yellowCorner = loadCornerTexture("yellow");
        blueBackground = loadMainBackground("blue");
        blueCorner = loadCornerTexture("blue");
        grayBackground = loadMainBackground("gray");
        grayCorner = loadCornerTexture("gray");
        whiteBackground = loadMainBackground("white");
        whiteCorner = loadCornerTexture("white");
        orangeBackground = loadMainBackground("orange");
        orangeCorner = loadCornerTexture("orange");
        greenBackground = loadMainBackground("green");
        greenCorner = loadCornerTexture("green");
        blackBackground = loadMainBackground("black");
        blackCorner = loadCornerTexture("black");
    }


    TextureRegion getCornerTexture(BackgroundYio backgroundYio) {
        switch (backgroundYio) {
            default:
            case white: return whiteCorner;
            case blue: return blueCorner;
            case green: return greenCorner;
            case gray: return grayCorner;
            case orange: return orangeCorner;
            case yellow: return yellowCorner;
            case black: return blackCorner;
        }
    }


    public TextureRegion getBackgroundTexture(BackgroundYio backgroundYio) {
        switch (backgroundYio) {
            default:
            case white: return whiteBackground;
            case blue: return blueBackground;
            case green: return greenBackground;
            case gray: return grayBackground;
            case orange: return orangeBackground;
            case yellow: return yellowBackground;
            case black: return blackBackground;
        }
    }


    private TextureRegion loadMainBackground(String name) {
        return GraphicsYio.loadTextureRegion("menu/button_backgrounds/" + name + ".png", false);
    }


    private TextureRegion loadCornerTexture(String name) {
        return GraphicsYio.loadTextureRegion("menu/button_backgrounds/" + name + "_corner.png", true);
    }


    public void renderRoundShape(SpriteBatch argBatch, RectangleYio position, BackgroundYio backgroundYio, float cornerRadius) {
        currentBatch = argBatch;
        currentBackground = getBackgroundTexture(backgroundYio);
        currentCorner = getCornerTexture(backgroundYio);
        this.cornerRadius = cornerRadius;
        this.position = position;

        updatePos1();
        updatePos2();
        updatePos3();
        updateCorners();

        GraphicsYio.drawByRectangle(currentBatch, currentBackground, pos1);
        GraphicsYio.drawByRectangle(currentBatch, currentBackground, pos2);
        GraphicsYio.drawByRectangle(currentBatch, currentBackground, pos3);
        renderCorners();
    }


    public void renderRoundShape(RectangleYio position, BackgroundYio backgroundYio, float cornerRadius) {
        renderRoundShape(batch, position, backgroundYio, cornerRadius);
    }


    private void renderCorners() {
        for (int i = 0; i < corners.length; i++) {
            GraphicsYio.drawFromCenterRotated(
                    currentBatch,
                    currentCorner,
                    corners[i].x,
                    corners[i].y,
                    cornerRadius / 2,
                    - (Math.PI / 2) * i
            );
        }
    }


    private void updateCorners() {
        corners[0].set(position.x + cornerRadius / 2, position.y + cornerRadius / 2);
        corners[1].set(position.x + cornerRadius / 2, position.y + position.height - cornerRadius / 2);
        corners[2].set(position.x + position.width - cornerRadius / 2, position.y + position.height - cornerRadius / 2);
        corners[3].set(position.x + position.width - cornerRadius / 2, position.y + cornerRadius / 2);
    }


    private void updatePos3() {
        pos3.x = position.x + cornerRadius;
        pos3.y = position.y + position.height - cornerRadius;
        pos3.width = position.width - 2 * cornerRadius;
        pos3.height = cornerRadius;
    }


    private void updatePos2() {
        pos2.x = position.x;
        pos2.y = position.y + cornerRadius;
        pos2.width = position.width;
        pos2.height = position.height - 2 * cornerRadius;
    }


    private void updatePos1() {
        pos1.x = position.x + cornerRadius;
        pos1.y = position.y;
        pos1.width = position.width - 2 * cornerRadius;
        pos1.height = cornerRadius;
    }


    public void setCurrentBatch(SpriteBatch currentBatch) {
        this.currentBatch = currentBatch;
    }


    public void renderRoundShape(RectangleYio position, BackgroundYio backgroundYio) {
        renderRoundShape(position, backgroundYio, 0.02f * GraphicsYio.width);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {

    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
