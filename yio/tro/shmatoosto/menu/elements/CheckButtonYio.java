package yio.tro.shmatoosto.menu.elements;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class CheckButtonYio extends InterfaceElement<CheckButtonYio> {

    RectangleYio activeSquare;
    float internalOffset, textHeight;
    boolean checked, touched;
    double defaultHeight;
    public FactorYio activeFactor, selectionFactor;
    Reaction reaction;
    String text;
    PointYio textPosition, textOffset;


    public CheckButtonYio(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        activeSquare = new RectangleYio();
        activeFactor = new FactorYio();
        selectionFactor = new FactorYio();
        textPosition = new PointYio();
        textOffset = new PointYio();
        reaction = null;

        // defaults
        internalOffset = 0;
        defaultHeight = 0.07;
        setChecked(false);
        setName("[Check button]");
        setAnimation(AnimationYio.none);
    }


    @Override
    protected CheckButtonYio getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();

        activeFactor.move();

        if (!touched) {
            selectionFactor.move();
        }
    }


    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();

        internalOffset = 0.3f * position.height;
    }


    @Override
    protected void onApplyParent() {
        super.onApplyParent();

        updateActiveSquare();
        updateTextPosition();
    }


    private void updateTextPosition() {
        textPosition.x = viewPosition.x + textOffset.x;
        textPosition.y = viewPosition.y + viewPosition.height - textOffset.y;
    }


    @Override
    public CheckButtonYio setParent(InterfaceElement parent) {
        setSize(parent.position.width / GraphicsYio.width, defaultHeight);
        return super.setParent(parent);
    }


    public CheckButtonYio setHeight(double height) {
        return setSize(position.width / GraphicsYio.width, height);
    }


    public CheckButtonYio setInternalOffset(double offset) {
        internalOffset = (float) (offset * GraphicsYio.width);
        updateActiveSquare();
        return this;
    }


    private void updateActiveSquare() {
        activeSquare.height = viewPosition.height - 2 * internalOffset;
        activeSquare.width = activeSquare.height;
        activeSquare.x = viewPosition.x + viewPosition.width - internalOffset - activeSquare.width;
        activeSquare.y = viewPosition.y + internalOffset;
    }


    public RectangleYio getActiveSquare() {
        return activeSquare;
    }


    public PointYio getTextPosition() {
        return textPosition;
    }


    @Override
    public void onDestroy() {
        selectionFactor.destroy(3, 2);
    }


    @Override
    public void onAppear() {

    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean touchDown() {
        if (isTouchInsideRectangle(currentTouch, viewPosition)) {
            touched = true;
            select();
            return true;
        }

        return false;
    }


    public void press() {
        setChecked(!checked);
    }


    private void select() {
        selectionFactor.setValues(1, 0);
        selectionFactor.destroy(1, 3);
    }


    @Override
    public boolean touchDrag() {
        return false;
    }


    @Override
    public boolean touchUp() {
        if (touched) {
            touched = false;
            if (isClicked()) {
                onClick();
            }
            return true;
        }

        return false;
    }


    private void onClick() {
        press();

        if (reaction != null) {
            reaction.performReactActions(menuControllerYio);
        }
    }


    public CheckButtonYio setReaction(Reaction reaction) {
        this.reaction = reaction;
        return this;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderCheckButton;
    }


    public boolean isChecked() {
        return checked;
    }


    public CheckButtonYio setName(String key) {
        this.text = LanguagesManager.getInstance().getString(key);
        updateTextMetrics();

        return this;
    }


    private void updateTextMetrics() {
        textHeight = GraphicsYio.getTextHeight(Fonts.gameFont, text);

        textOffset.x = internalOffset;
        textOffset.y = (position.height - textHeight) / 2;
    }


    public String getText() {
        return text;
    }


    public CheckButtonYio setChecked(boolean checked) {
        if (this.checked == checked) return this;

        this.checked = checked;

        if (!checked) { // uncheck
            activeFactor.setValues(1, 0);
            activeFactor.destroy(1, 3);
        } else { // check
            activeFactor.setValues(0, 0);
            activeFactor.appear(1, 2);
        }

        return this;
    }


    @Override
    public String toString() {
        return "[CheckButton: " +
                text +
                "]";
    }
}
