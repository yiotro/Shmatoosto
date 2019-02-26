package yio.tro.shmatoosto.menu.elements.scrollable_list;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class SlyItem {

    ScrollableListYio parent;
    RectangleYio viewPosition, iconPosition;
    String name, description, key;
    FactorYio appearFactor, selectFactor;
    PointYio nameDelta, descriptionDelta, positionDelta;
    TextureRegion icon;
    boolean iconFlag;
    float animDelta, iconSize;


    public SlyItem(ScrollableListYio parent) {
        this.parent = parent;

        viewPosition = new RectangleYio();
        appearFactor = new FactorYio();
        selectFactor = new FactorYio();
        nameDelta = new PointYio();
        descriptionDelta = new PointYio();
        positionDelta = new PointYio();
        icon = null;
        iconPosition = new RectangleYio();
        iconFlag = false;

        animDelta = 0.3f * GraphicsYio.width;
        iconSize = (float) (0.7 * parent.itemHeight);
    }


    void move() {
        appearFactor.move();
        moveSelection();

        updateViewPosition();
        updateIconPosition();
    }


    private void updateIconPosition() {
        if (!hasIcon()) return;

        iconPosition.width = iconSize;
        iconPosition.height = iconSize;
        iconPosition.x = viewPosition.x + (float) ((parent.itemHeight - iconSize) / 2);
        iconPosition.y = viewPosition.y + (float) ((parent.itemHeight - iconSize) / 2);
    }


    private void updateViewPosition() {
        viewPosition.x = parent.getViewPosition().x + positionDelta.x + animDelta * (1 - appearFactor.get());
        viewPosition.y = parent.getViewPosition().y + positionDelta.y;
        viewPosition.height = (float) parent.itemHeight;
        viewPosition.width = parent.getPosition().width - 2 * animDelta * (1 - appearFactor.get());

        if (appearFactor.get() < 1) {
            viewPosition.y -= parent.itemHeight * (1 - appearFactor.get());
        }
    }


    void appear() {
        appearFactor.setDy(0);
        appearFactor.appear(3, 1);
    }


    void destroy() {
        appearFactor.setDy(0);
        appearFactor.destroy(1, 3);
    }


    void shortAppear() {
        appearFactor.setValues(0.9, 0);
        appearFactor.appear(3, 1);
    }


    private void moveSelection() {
        if (!isSelected()) return;

        if (parent.inEditMode && selectFactor.get() == 1) return;
        if (parent.touched) return;

        selectFactor.move();
    }


    boolean isTouched(PointYio touchPoint) {
        return InterfaceElement.isTouchInsideRectangle(touchPoint, viewPosition);
    }


    public void select() {
        selectFactor.setValues(1, 0);
        selectFactor.destroy(1, 3);
    }


    public void deselect() {
        selectFactor.setValues(0, 0);
        selectFactor.destroy(1, 1);
    }


    public void switchSelection() {
        if (isSelected()) {
            deselect();
        } else {
            select();
        }
    }


    public void indicateByFastDeselection() {
        selectFactor.setValues(0.3, 0);
        selectFactor.destroy(1, 2);
    }


    public boolean isVisible() {
        if (viewPosition.y + viewPosition.height < 0) return false;
        if (viewPosition.y > GraphicsYio.height) return false;

//        if (positionDelta.y + parent.position.y < -viewPosition.height) return false;
//        if (positionDelta.y + parent.position.y > ScrollableListYio.TOP_BEZEL * GraphicsYio.height) return false;

        return true;
    }


    public float getNameX() {
        if (hasIcon()) {
            return (float) (viewPosition.x + parent.itemHeight);
        }

        return viewPosition.x + nameDelta.x;
    }


    public float getNameY() {
        return viewPosition.y + nameDelta.y;
    }


    public RectangleYio getIconPosition() {
        return iconPosition;
    }


    public float getDescriptionX() {
        if (hasIcon()) {
            return (float) (viewPosition.x + parent.itemHeight);
        }

        return viewPosition.x + descriptionDelta.x;
    }


    public float getDescriptionY() {
        return viewPosition.y + descriptionDelta.y;
    }


    void updateDeltas() {
        double nameWidth = GraphicsYio.getTextWidth(parent.nameFont, name);
//        nameDelta.x = (float) (viewPosition.width / 2 - nameWidth / 2);
        nameDelta.x = 0.1f * GraphicsYio.width;
        nameDelta.y = 0.8f * viewPosition.height;

        double descriptionWidth = GraphicsYio.getTextWidth(parent.descFont, description);
//        descriptionDelta.x = (float) (viewPosition.width / 2 - descriptionWidth / 2);
        descriptionDelta.x = 0.1f * GraphicsYio.width;
        descriptionDelta.y = 0.45f * viewPosition.height;
    }


    public FactorYio getAppearFactor() {
        return appearFactor;
    }


    public FactorYio getSelectFactor() {
        return selectFactor;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public RectangleYio getViewPosition() {
        return viewPosition;
    }


    public boolean hasIcon() {
        return icon != null;
    }


    public TextureRegion getIcon() {
        return icon;
    }


    public void setIcon(TextureRegion icon) {
        this.icon = icon;
        iconFlag = true;
    }


    public void preventIconFromLoading() {
        iconFlag = true;
    }


    public boolean isSelected() {
        return selectFactor.get() > 0;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public void set(String name, String description, String key) {
        this.name = name;
        this.description = description;
        this.key = key;
        updateDeltas();
    }


}
