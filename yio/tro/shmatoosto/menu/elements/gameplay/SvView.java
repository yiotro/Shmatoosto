package yio.tro.shmatoosto.menu.elements.gameplay;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class SvView {


    public RectangleYio position, viewPosition;
    public FactorYio appearFactor;
    public boolean active;
    public PointYio textPosition;
    public BitmapFont font;
    double textWidth, textHeight;
    public String textValue;


    public SvView() {
        position = new RectangleYio();
        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        textPosition = new PointYio();
        textValue = "-";
        active = false;
    }


    public void move() {
        appearFactor.move();
        updateViewPosition();
        updateTextPosition();
    }


    private void updateTextPosition() {
        textPosition.x = (float) (viewPosition.x + viewPosition.width / 2 - textWidth / 2);
        textPosition.y = (float) (viewPosition.y + viewPosition.height / 2 + textHeight / 2);
    }


    public void setTextValue(int score) {
        setTextValue("" + score);
    }


    public void setTextValue(String textValue) {
        this.textValue = textValue;
        textWidth = GraphicsYio.getTextWidth(font, textValue);
        textHeight = GraphicsYio.getTextHeight(font, textValue);
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }


    private void updateViewPosition() {
        viewPosition.setBy(position);
        viewPosition.y += 1.5f * position.height * (1 - appearFactor.get());
    }


    public void appear() {
        appearFactor.reset();
        appearFactor.appear(6, 2);
    }


    public void destroy() {
        appearFactor.destroy(1, 3);
    }
}
