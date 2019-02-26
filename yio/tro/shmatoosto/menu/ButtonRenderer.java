package yio.tro.shmatoosto.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.StringBuilder;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.elements.BtItem;
import yio.tro.shmatoosto.stuff.FrameBufferYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.YioGdxGame;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static yio.tro.shmatoosto.Fonts.FONT_SIZE;

public class ButtonRenderer {

    private static ButtonRenderer instance = null;

    FrameBuffer frameBuffer;
    SpriteBatch batch;
    RectangleYio pos;
    TextureRegion
            bgYellow,
            bhBlue,
            bgGreen,
            bgGray,
            bgWhite,
            bgOrange;
    private static float horizontalOffset;
    ArrayList<String> text;
    ArrayList<Texture> texturesToDisposeInTheEnd;


    private ButtonRenderer() {
        batch = new SpriteBatch();
        texturesToDisposeInTheEnd = new ArrayList<>();
        bgYellow = loadButtonBackground("yellow");
        bhBlue = loadButtonBackground("blue");
        bgGreen = loadButtonBackground("green");
        bgGray = loadButtonBackground("gray");
        bgWhite = loadButtonBackground("white");
        bgOrange = loadButtonBackground("orange");

    }


    private TextureRegion loadButtonBackground(String name) {
        return GraphicsYio.loadTextureRegion("menu/button_backgrounds/" + name + ".png", true);
    }


    public static ButtonRenderer getInstance() {
        if (instance == null) {
            instance = new ButtonRenderer();
        }

        return instance;
    }


    public static void initialize() {
        instance = null;
    }


    TextureRegion getButtonBackground(ButtonYio buttonYio) {
        switch (buttonYio.getBackground()) {
            default:
            case gray:
                return bgGray;
            case yellow:
                return bgYellow;
            case blue:
                return bhBlue;
            case green:
                return bgGreen;
            case white:
                return bgWhite;
            case orange:
                return bgOrange;
        }
    }


    void beginRender(ButtonYio buttonYio, BitmapFont font, int FONT_SIZE) {
        resetHorizontalOffset(FONT_SIZE);
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frameBuffer.begin();
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // doesn't change anything for now
        Matrix4 matrix4 = new Matrix4();
        int orthoWidth = getExpectedOrthoWidth(buttonYio, font);
        int orthoHeight = (orthoWidth / Gdx.graphics.getWidth()) * Gdx.graphics.getHeight();
        matrix4.setToOrtho2D(0, 0, orthoWidth, orthoHeight);
        batch.setProjectionMatrix(matrix4);

        batch.begin();
        batch.draw(getButtonBackground(buttonYio), 0, 0, orthoWidth, orthoHeight);
        batch.end();

        pos = new RectangleYio(buttonYio.getPosition());

        updateInternalText(buttonYio);
    }


    private void updateInternalText(ButtonYio buttonYio) {
        text.clear();
        for (BtItem item : buttonYio.items) {
            text.add(item.string);
        }
    }


    private void resetHorizontalOffset(int FONT_SIZE) {
        horizontalOffset = (int) (0.3f * FONT_SIZE);
    }


    public void resetHorizontalOffset() {
        resetHorizontalOffset(FONT_SIZE);
    }


    void endRender(ButtonYio buttonYio) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float f = ((FrameBufferYio) frameBuffer).f;
        buttonYio.textureRegion = new TextureRegion(texture, (int) (pos.width * f), (int) (pos.height * f));
        frameBuffer.end();
        frameBuffer.dispose();
        texturesToDisposeInTheEnd.add(texture);
    }


    public ArrayList<String> parseText(ArrayList<String> src, BitmapFont font) {
        return parseText(src, font, 0.99 * Gdx.graphics.getWidth(), false);
    }


    public ArrayList<String> parseText(ArrayList<String> src, BitmapFont font, double maxWidth, boolean resetOffset) {
        if (resetOffset) {
            resetHorizontalOffset();
        }

        ArrayList<String> result = new ArrayList<>();
        double currentX, currentWidth;
        StringBuilder builder = new StringBuilder();
        for (String srcLine : src) {
            currentX = horizontalOffset;
            String[] split = srcLine.split(" ");
            for (String token : split) {
                currentWidth = GraphicsYio.getTextWidth(font, token + " ");
                if (currentX + currentWidth > maxWidth) {
                    result.add(builder.toString());
                    builder = new StringBuilder();
                    currentX = 0;
                }
                builder.append(token).append(" ");
                currentX += currentWidth;
            }
            result.add(builder.toString());
            builder = new StringBuilder();
        }
        return result;
    }


    private int getExpectedOrthoWidth(ButtonYio buttonYio, BitmapFont font) {
//        float longestLineLength = 0, currentLineLength;
//        for (String line : buttonLighty.text) {
//            currentLineLength = font.getBounds(line).width;
//            if (currentLineLength > longestLineLength) longestLineLength = currentLineLength;
//        }
//        longestLineLength += 0.1 * buttonLighty.position.width;
//        if (longestLineLength < Gdx.graphics.getWidth()) longestLineLength = Gdx.graphics.getWidth();
//        return (int)(longestLineLength);
//        if (buttonLighty.position.height < 0.2 * Gdx.graphics.getHeight())
        return Gdx.graphics.getWidth();
//        else return (int)(1.2 * Gdx.graphics.getWidth());
    }


    public void renderButton(ButtonYio buttonYio, BitmapFont font, int FONT_SIZE) {
        beginRender(buttonYio, font, FONT_SIZE);

        float ratio = pos.width / pos.height;
        int lineHeight = (int) (1.2f * FONT_SIZE);
        int verticalOffset = (int) (0.38f * FONT_SIZE);
        int lineNumber = 0;
        float longestLineLength = 0, currentLineLength;

        //if button has single line of text then center it
        if (text.size() == 1 && buttonYio.textCentered) {
            float textWidth = getTextWidth(text.get(0), font);
            horizontalOffset = (int) (0.5 * (1.35f * FONT_SIZE * ratio - textWidth));
            if (horizontalOffset < 0) {
                resetHorizontalOffset(FONT_SIZE);
            }
        }

        batch.begin();
        font.setColor(0, 0, 0, 1);
//        wrapText(buttonYio, font);
        for (String line : text) {
            font.draw(batch, line, horizontalOffset, verticalOffset + lineNumber * lineHeight);
            currentLineLength = getTextWidth(line, font);
            if (currentLineLength > longestLineLength) longestLineLength = currentLineLength;
            lineNumber++;
        }
        batch.end();
        pos.height = text.size() * lineHeight + verticalOffset / 2;
        pos.width = pos.height * ratio;
        if (longestLineLength > pos.width - 0.3f * (float) lineHeight) {
            pos.width = longestLineLength + 2 * horizontalOffset;
        }
        endRender(buttonYio);
    }


    private int getTextWidth(String text, BitmapFont font) {
        return (int) GraphicsYio.getTextWidth(font, text);
    }


    private boolean hasTooLongWord(ButtonYio buttonYio, BitmapFont font) {
        updateInternalText(buttonYio);
        for (String s : text) {
            StringTokenizer tokenizer = new StringTokenizer(s, " ");
            while (tokenizer.hasMoreTokens()) {
                if (getTextWidth(tokenizer.nextToken() + " ", font) > 0.9 * getExpectedOrthoWidth(buttonYio, font))
                    return true;
            }
        }
        return false;
    }


    private void wrapText(ButtonYio buttonYio, BitmapFont font) {
        if (hasTooLongWord(buttonYio, font)) return;

        updateInternalText(buttonYio);
        if (text.size() > 3) {
            sayText(text);
        }
        ArrayList<String> tempList = new ArrayList<>();
        int lineWidth = getExpectedOrthoWidth(buttonYio, font) - (int) (0.1f * Gdx.graphics.getWidth());
        int currentWidth;

        for (int i = text.size() - 1; i >= 0; i--) {
            StringTokenizer tokenizer = new StringTokenizer(text.get(i), " ");
            StringBuffer stringBuffer = new StringBuffer();
            currentWidth = 0;

            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
                int wordWidth = getTextWidth(word + " ", font);

                if (currentWidth + wordWidth < lineWidth) { // add to current line
                    stringBuffer.append(word + " ");
                    currentWidth += wordWidth;
                } else { // next line
                    tempList.add(stringBuffer.toString());
                    stringBuffer = new StringBuffer();
                    stringBuffer.append(word + " ");
                }
            }

            if (stringBuffer.length() > 0) tempList.add(stringBuffer.toString());

            if (tempList.size() > 1) {
                text.remove(i);
                tempList.addAll(i, tempList);
            }
        }

        if (text.size() > 3) sayText(text);
    }


    private void sayText(ArrayList<String> text) {
        for (String s : text) {
            YioGdxGame.say("# " + s);
        }
        YioGdxGame.say("----");
    }


    public void disposeAllTextures() {
        for (Texture texture : texturesToDisposeInTheEnd) {
            texture.dispose();
        }

        texturesToDisposeInTheEnd.clear();
    }


    public void killInstance() {
        // this actually fixes stupid android bug
        // it's important and should not be deleted.

        // So here is the bug:
        // 1. Open app, go through menus, close app from main menu
        // 2. Open it again. Some buttons (with text) are now gray

        // It happens because android fucks up textures if they are in static objects
        // So I invalidate static instance of button renderer
        // to get them recreated after restart

        instance = null;
    }


    public void renderButton(ButtonYio buttonYio) {
        renderButton(buttonYio, Fonts.buttonFont, FONT_SIZE);
    }
}
