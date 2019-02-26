package yio.tro.shmatoosto.menu.elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

public class BtItem {


    public String string;
    public PointYio position;
    public PointYio delta;
    public float textWidth, textHeight;


    public BtItem() {
        string = null;
        position = new PointYio();
        delta = new PointYio();
    }


    public void setString(String string, BitmapFont font) {
        this.string = string;
        textWidth = GraphicsYio.getTextWidth(font, string);
        textHeight = GraphicsYio.getTextHeight(font, string);
    }
}
