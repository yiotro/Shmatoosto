package yio.tro.shmatoosto.menu.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.ButtonRenderer;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.scenes.SceneYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;

import java.util.ArrayList;

public class ButtonYio extends InterfaceElement<ButtonYio> {

    public static final String NO_NAME = "NoName";
    public boolean shadowEnabled, onlyShadow, textCentered;
    public boolean renderable;
    protected boolean currentlyTouched, needToPerformAction, borderEnabled;
    boolean silent, returningBackButton, forcedShadow, ignoreResumePause;
    boolean lockSelection;
    int touchDelay;
    protected long lastTimeTouched;
    long timeToPerformAction;
    float verticalTouchOffset, horizontalTouchOffset;
    public BitmapFont font;

    public ArrayList<BtItem> items;
    String debugName;
    public TextureRegion textureRegion, selectionTexture;
    public FactorYio selectionFactor;
    Reaction reaction;
    private BackgroundYio background;
    private boolean silentReactionMode;
    String texturePath;


    public ButtonYio(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);
        borderEnabled = true;
        textCentered = true;
        renderable = true;
        silent = false;
        returningBackButton = false;
        debugName = NO_NAME;
        touchDelay = 1000;
        selectionFactor = new FactorYio();
        items = new ArrayList<>();
        shadowEnabled = true;
        forcedShadow = false;
        ignoreResumePause = false;
        textureRegion = null;
        selectionTexture = null;
        texturePath = null;
        reaction = Reaction.rbNothing;
        background = BackgroundYio.gray;
        lockSelection = false;
        silentReactionMode = false;
        font = Fonts.buttonFont;
    }


    public ButtonYio setFont(BitmapFont font) {
        this.font = font;
        return this;
    }


    @Override
    protected ButtonYio getThis() {
        return this;
    }


    @Override
    public void move() {
        if (!currentlyTouched && !lockSelection) {
            selectionFactor.move();
        }

        updateViewPosition();
        moveItems();
    }


    @Override
    protected void onApplyParent() {
        super.onApplyParent();
        moveItems();
    }


    private void moveItems() {
        for (BtItem item : items) {
            item.position.x = viewPosition.x + item.delta.x;
            item.position.y = viewPosition.y + item.delta.y;
        }
    }


    @Override
    public boolean checkToPerformAction() {
        if (needToPerformAction && System.currentTimeMillis() > timeToPerformAction) {
            needToPerformAction = false;
            if (touchable) { // touchable state can be changed after touch down and before reaction
                reaction.performReactActions(menuControllerYio);
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean touchDown() {
        if (!touchable) return false;
        if (appearFactor.get() < 0.5) return false;

        if (isTouched(currentTouch)) {
            select();
            return true;
        }

        return false;
    }


    @Override
    public boolean touchDrag() {
        return false;
    }


    @Override
    public boolean touchUp() {
        if (currentlyTouched) {
            currentlyTouched = false;
            if (isClicked()) {
                onClick();
            }
            return true;
        }

        return false;
    }


    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();

        if (position.height <= 0.06 * GraphicsYio.height) {
            setFont(Fonts.miniFont);
        }
    }


    @Override
    public void pressArtificially() {
        select();
        onClick();
    }


    void onClick() {
        if (reaction == null) return;
        if (appearFactor.isInDestroyState()) return;

        lastTimeTouched = System.currentTimeMillis();
        currentlyTouched = false;
        playSound();

        menuControllerYio.yioGdxGame.render();
        needToPerformAction = true;
        timeToPerformAction = System.currentTimeMillis() + 100;
    }


    private void select() {
        if (appearFactor.isInDestroyState()) return;

        currentlyTouched = true;

        if (renderable) {
            resetSelectionFactorToTop();
        }
    }


    public void resetSelectionFactorToTop() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 0.5);
    }


    private void playSound() {
        if (silent) return;
        if (silentReactionMode) return;

        // special sound for buttons that return back
        if (returningBackButton) {
            SoundManager.playSound(SoundManager.backButton);
            return;
        }

        SoundManager.playSound(SoundManager.button);
    }


    public TextureRegion getTextureRegion() {
        return textureRegion;
    }


    public ButtonYio setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;

        setShadow(false);
        setBorder(false);

        return getThis();
    }


    public ButtonYio loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setDebugNameByPath(path);
        texturePath = path;

        return setTextureRegion(new TextureRegion(texture));
    }


    public ButtonYio cropTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        float realRatio = position.width / position.height;
        float dstWidth = texture.getHeight() * realRatio;
        if (dstWidth > texture.getWidth()) {
            dstWidth = texture.getWidth();
        }
        float dstX = (texture.getWidth() - dstWidth) / 2;

        TextureRegion textureRegion;
        textureRegion = new TextureRegion(texture, (int) dstX, 0, (int) dstWidth, texture.getHeight());

        return setTextureRegion(textureRegion);
    }


    public ButtonYio setSelectionTexture(TextureRegion selectionTexture) {
        this.selectionTexture = selectionTexture;
        return getThis();
    }


    private void setDebugNameByPath(String path) {
        int indexOfSlash = path.lastIndexOf("/");
        int indexOfDot = path.lastIndexOf(".");
        setDebugName(path.substring(indexOfSlash + 1, indexOfDot));
    }


    @Override
    public void onDestroy() {

    }


    @Override
    public void onAppear() {
        appearFactor.setValues(0, 0.001);
        selectionFactor.setValues(0, 0);
        currentlyTouched = false;
    }


    public ButtonYio clearText() {
        items.clear();
        return getThis();
    }


    public ButtonYio setTextLine(String line) {
        clearText();
        addTextLine(line);
        setDebugName(line);

        return getThis();
    }


    public ButtonYio setBorder(boolean hasBorder) {
        this.borderEnabled = hasBorder;
        return getThis();
    }


    public boolean hasBorder() {
        return borderEnabled;
    }


    private void addItem(String value) {
        BtItem btItem = new BtItem();
        btItem.setString(value, font);
        items.add(btItem);
    }


    public ButtonYio addTextLine(String textLine) {
        addItem(textLine);
        if (items.size() == 1) {
            setDebugName(textLine);
        }

        return getThis();
    }


    public ButtonYio addManyLines(ArrayList<String> lines) {
        for (String line : lines) {
            addTextLine(line);
        }

        return getThis();
    }


    public boolean isInSilentReactionMode() {
        return silentReactionMode;
    }


    public ButtonYio setSilentReactionMode(boolean silentReactionMode) {
        this.silentReactionMode = silentReactionMode;

        return getThis();
    }


    public ButtonYio setReaction(Reaction reaction) {
        this.reaction = reaction;

        return getThis();
    }


    public Reaction getReaction() {
        return reaction;
    }


    public boolean isRenderable() {
        return renderable;
    }


    public ButtonYio setRenderable(boolean renderable) {
        this.renderable = renderable;

        if (!renderable) {
            setBorder(false);
            setShadow(false);
            setSilent(true);
        }

        return getThis();
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderButton;
    }


    public ButtonYio setTouchOffset(double touchOffset) {
        horizontalTouchOffset = (float) (touchOffset * GraphicsYio.width);
        verticalTouchOffset = (float) (touchOffset * GraphicsYio.width);

        return getThis();
    }


    public float getVerticalTouchOffset() {
        return verticalTouchOffset;
    }


    public float getHorizontalTouchOffset() {
        return horizontalTouchOffset;
    }


    public ButtonYio setOnlyShadow(boolean onlyShadow) {
        this.onlyShadow = onlyShadow;
        return getThis();
    }


    public boolean isSelected() {
        return selectionFactor.get() > 0;
    }


    public ButtonYio applyText(String key) {
        return applyText(key, autoBackground());
    }


    public ButtonYio applyText(ArrayList<String> lines) {
        clearText();
        ArrayList<String> parsed = ButtonRenderer.getInstance().parseText(lines, font, 0.92f * position.width, false);
        addManyLines(parsed);
        updateTextDeltas();
        return render();
    }


    public ButtonYio applyManyLines(String key) {
        return applyManyLines(key, 0);
    }


    public ButtonYio applyManyLines(String key, int emptyLines) {
        String string = LanguagesManager.getInstance().getString(key);
        ArrayList<String> strings = SceneYio.convertStringToArray(string);

        for (int i = 0; i < emptyLines; i++) {
            strings.add(" ");
        }

        return applyText(strings);
    }


    public ButtonYio applyFixedAmountOfLines(String key, int lines) {
        String string = LanguagesManager.getInstance().getString(key);
        ArrayList<String> list = SceneYio.convertStringToArray(string);
        ArrayList<String> parsed = ButtonRenderer.getInstance().parseText(list, font, 0.92f * position.width, false);
        int addedEmptyLines = lines - parsed.size();

        return applyManyLines(key, addedEmptyLines);
    }


    public ButtonYio applyText(String key, BackgroundYio background) {
        String string = LanguagesManager.getInstance().getString(key);
        this.background = background;
        setTextLine(string);
        checkToFillWithBlankLines();
        render();
        updateTextDeltas();

        return getThis();
    }


    private BackgroundYio autoBackground() {
        if (position.height >= 0.12 * GraphicsYio.height) {
            return BackgroundYio.gray;
        }

        switch (countButtonThatAreUpper() % 3) {
            default:
            case 0:
                return BackgroundYio.yellow;
            case 1:
                return BackgroundYio.blue;
            case 2:
                return BackgroundYio.green;
        }
    }


    private void checkToFillWithBlankLines() {
        // this helps to fix rendering problem
        // many blank lines force button renderer to render bigger texture
        // which results in less stretching

        if (background != BackgroundYio.gray) return;
        if (items.size() > 1) return;
        float averageLineHeight = 0.05f * GraphicsYio.height;
        int dst = (int) (position.height / averageLineHeight);

        while (items.size() < dst) {
            addTextLine(" ");
        }
    }


    public void updateTextDeltas() {
        if (items.size() == 1) {
            BtItem btItem = items.get(0);
            btItem.delta.x = position.width / 2 - btItem.textWidth / 2;
            btItem.delta.y = position.height / 2 + btItem.textHeight / 2;
            return;
        }

        float curY = position.height - 0.05f * GraphicsYio.width;
        float curX = 0.05f * GraphicsYio.width;
        for (BtItem item : items) {
            item.delta.x = curX;
            item.delta.y = curY;

            curY -= 1.35f * font.getLineHeight();
        }
    }


    private int countButtonThatAreUpper() {
        int count = 0;

        float y = position.y + getParentCompensationY(false);
        for (InterfaceElement element : sceneOwner.getLocalElementsList()) {
            if (element == this) continue;
            if (!(element instanceof ButtonYio)) continue;
//            if (element.position.y + element.getParentCompensationY(false) <= y) continue;

            count++;
        }

        return count;
    }


    public ButtonYio applyText(String key, int emptyLines) {
        String string = LanguagesManager.getInstance().getString(key);
        setTextLine(string);
        for (int i = 0; i < emptyLines; i++) {
            addTextLine(" ");
        }
        render();
        updateTextDeltas();

        return getThis();
    }


    public ButtonYio render() {
//        ButtonRenderer.getInstance().renderButton(this);
        return getThis();
    }


    public ButtonYio setShadow(boolean hasShadow) {
        this.shadowEnabled = hasShadow;
        return getThis();
    }


    @Override
    public ButtonYio setParent(InterfaceElement parent) {
        setShadow(false);
        super.setParent(parent);
        return getThis();
    }


    @Override
    public ButtonYio clone(InterfaceElement src) {
        super.clone(src);

        ButtonYio srcButton = (ButtonYio) src;
        setBorder(srcButton.borderEnabled);
        setShadow(srcButton.shadowEnabled);
        setForcedShadow(srcButton.forcedShadow);

        horizontalTouchOffset = ((ButtonYio) src).horizontalTouchOffset;
        verticalTouchOffset = ((ButtonYio) src).verticalTouchOffset;

        setAnimation(src.animType);
        setSelectionTexture(((ButtonYio) src).selectionTexture);

        return getThis();
    }


    public ButtonYio setTextCentered(boolean textCentered) {
        this.textCentered = textCentered;
        return getThis();
    }


    @Override
    public void forceDestroyToEnd() {
        appearFactor.setValues(0, 0);
    }


    public void forceAppearance() {
        appearFactor.setValues(1, 0);
    }


    public ButtonYio setSilent(boolean silent) {
        this.silent = silent;
        return getThis();
    }


    public ButtonYio tagAsBackButton() {
        returningBackButton = true;
        return getThis();
    }


    public ButtonYio setForcedShadow(boolean forcedShadow) {
        this.forcedShadow = forcedShadow;
        return getThis();
    }


    public BackgroundYio getBackground() {
        return background;
    }


    public boolean isReturningBackButton() {
        return returningBackButton;
    }


    @Override
    public boolean isTouched(PointYio touchPoint) {
        return isTouchInsideRectangle(touchPoint.x, touchPoint.y, viewPosition, horizontalTouchOffset, verticalTouchOffset);
    }


    public void setLockSelection(boolean lockSelection) {
        this.lockSelection = lockSelection;
    }


    @Override
    public void onAppPause() {
        if (ignoreResumePause) return;

        if (textureRegion != null) {
            textureRegion.getTexture().dispose();
        }
    }


    public void reloadTexture() {
        resetTextureRegion();
        boolean shadow = shadowEnabled;
        loadTexture(texturePath);
        setShadow(shadow);
    }


    private void resetTextureRegion() {
        textureRegion = null;
    }


    public ButtonYio setIgnoreResumePause(boolean ignoreResumePause) {
        this.ignoreResumePause = ignoreResumePause;
        return getThis();
    }


    @Override
    public void onAppResume() {
        if (ignoreResumePause) return;

        if (hasText()) {
            // do nothing
        } else {
            reloadTexture();
        }
    }


    private boolean hasText() {
        return texturePath == null;
    }


    public ButtonYio setDebugName(String debugName) {
        if (this.debugName.equals(NO_NAME)) {
            this.debugName = debugName;
        }
        return this;
    }


    public boolean isForcedShadow() {
        return forcedShadow;
    }


    @Override
    public String toString() {
        return "Button(" + id + ") '" + debugName + "'";
    }
}
