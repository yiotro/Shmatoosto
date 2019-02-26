package yio.tro.shmatoosto.menu.elements.scrollable_list;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class ScrollableListYio extends InterfaceElement<ScrollableListYio> {

    public static final double TOP_BEZEL = 0.87;
    public static final int LONG_TAP_DELAY = 600;

    RectangleYio activeArea, deleteIcon;
    double itemHeight, itemOffset, scrollOffset, borderHeight;
    ArrayList<SlyItem> items;
    SlyItem firstItem, lastItem;
    double dy, minSpeedCut;
    boolean touched, canMoveItems, readyToReact, editable, inEditMode;
    boolean checkedLongTap;
    int clickItemIndex, reactCountDown;
    ListItemReaction itemBehavior;
    public BitmapFont nameFont, descFont;
    public RectangleYio shadowPosition;


    public ScrollableListYio(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        activeArea = new RectangleYio();
        deleteIcon = new RectangleYio();
        shadowPosition = new RectangleYio();

        touched = false;
        minSpeedCut = 0.007 * GraphicsYio.width;
        firstItem = null;
        lastItem = null;
        readyToReact = false;
        editable = false;
        inEditMode = false;
        nameFont = Fonts.gameFont;
        descFont = Fonts.miniFont;

        initMetrics();
        initListItems();
        initRepeats();
    }


    private void initRepeats() {

    }


    @Override
    protected ScrollableListYio getThis() {
        return this;
    }


    private void initListItems() {
        items = new ArrayList<>();
    }


    private void initMetrics() {
        itemHeight = 0.2 * GraphicsYio.width;
        itemOffset = 0.05f * GraphicsYio.width;
//        itemOffset = 0;
        scrollOffset = itemHeight;
    }


    public void addListItem(String name, String description, String key) {
        SlyItem slyItem = new SlyItem(this);

        slyItem.set(name, description, key);

        items.add(slyItem);
        updateItems();
    }


    public void clearItems() {
        items.clear();
    }


    public void updateItemReferences() {
        if (items.size() == 0) return;
        firstItem = items.get(0);
        lastItem = items.get(items.size() - 1);

        canMoveItems = (lastItem.positionDelta.y < 0);
    }


    private SlyItem getItemWithoutIcon() {
        for (SlyItem item : items) {
            if (item.iconFlag) continue;

            return item;
        }

        return null;
    }


    @Override
    public void move() {
        updateViewPosition();
        checkForLongTap();

        relocateAllItems(dy);
        limitItems();
        for (SlyItem slyItem : items) {
            slyItem.move();
        }
        moveDy();
        updateShadowPosition();
    }


    private void updateShadowPosition() {
        if (!isShadowEnabled()) return;

        SlyItem firstItem = items.get(0);
        SlyItem lastItem = items.get(items.size() - 1);

        shadowPosition.setBy(lastItem.viewPosition);
        shadowPosition.height = firstItem.viewPosition.y + firstItem.viewPosition.height - lastItem.viewPosition.y;
    }


    public boolean isShadowEnabled() {
        return items.size() > 0;
    }


    private void checkForLongTap() {
        if (!touched) return;
        if (checkedLongTap) return;
        if (System.currentTimeMillis() - touchDownTime <= LONG_TAP_DELAY) return;

        checkedLongTap = true;

        if (initialTouch.distanceTo(currentTouch) > 0.05f * GraphicsYio.width) return;
        onLongTap();
    }


    private void onLongTap() {
        if (inEditMode) {
            exitEditMode();
        } else {
            enterEditMode();
        }
    }


    private void enterEditMode() {
        if (inEditMode) return;

        inEditMode = true;

        for (SlyItem slyItem : items) {
            slyItem.indicateByFastDeselection();
            if (isTouchInsideRectangle(initialTouch, slyItem.getViewPosition())) {
                slyItem.select();
            }
        }
    }


    private void exitEditMode() {
        if (!inEditMode) return;

        inEditMode = false;

        for (SlyItem listItem : items) {
            listItem.indicateByFastDeselection();
        }
    }


    private void limitItems() {
        if (!canMoveItems) return;

        // soft correction
        if (!touched && dy == 0) {
            double firstItemSoftCorrection = (position.height - itemHeight) - firstItem.positionDelta.y;
            if (firstItemSoftCorrection > 0) {
                relocateAllItems(0.1 * firstItemSoftCorrection);
                return;
            }

            double lastItemSoftCorrection = 0 - lastItem.positionDelta.y;
            if (lastItemSoftCorrection < 0) {
                relocateAllItems(0.1 * lastItemSoftCorrection);
                return;
            }
        }

        // hard correction
        double firstItemHardCorrection = (position.height - scrollOffset - itemHeight) - firstItem.positionDelta.y;
        if (firstItemHardCorrection > 0) {
            relocateAllItems(firstItemHardCorrection);
            dy = 0;
            return;
        }

        double lastItemHardCorrection = (0 + scrollOffset) - lastItem.positionDelta.y;
        if (lastItemHardCorrection < 0) {
            relocateAllItems(lastItemHardCorrection);
            dy = 0;
        }
    }


    private void relocateAllItems(double delta) {
        if (!canMoveItems) return;
        for (SlyItem slyItem : items) {
            slyItem.positionDelta.y += delta;
        }
    }


    private void moveDy() {
//        if (touched) {
//            dy = 0;
//            return;
//        }

        dy *= 0.95;
        if (Math.abs(dy) < minSpeedCut) {
            dy *= 0.7;
            if (dy < minSpeedCut / 10) {
                dy = 0;
            }
        }
    }


    @Override
    protected void updateViewPosition() {
        viewPosition.x = position.x;
        viewPosition.y = position.y - (1 - appearFactor.get()) * position.height * 0.1f;
        viewPosition.width = position.width;
        viewPosition.height = position.height;
    }


    public ScrollableListYio setActiveArea(double x, double y, double width, double height) {
        activeArea.x = (float) (x * GraphicsYio.width);
        activeArea.y = (float) (y * GraphicsYio.height);
        activeArea.width = (float) (width * GraphicsYio.width);
        activeArea.height = (float) (height * GraphicsYio.height);
        return getThis();
    }


    @Override
    protected void onPositionChanged() {
        super.onPositionChanged();
        updateItems();
        updateDeleteIcon();
    }


    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();
        updateItems();
        updateDeleteIcon();
    }


    private void updateDeleteIcon() {
        float size = 0.09f * GraphicsYio.width;
        deleteIcon.width = size;
        deleteIcon.height = size;
        deleteIcon.x = position.x + position.width - deleteIcon.width;
        deleteIcon.y = position.y + position.height - deleteIcon.height;
    }


    private void updateItems() {
        updateItemPositions();
        updateItemReferences();
        updateBorderHeight();
    }


    private void updateItemPositions() {
        float currentY = (float) (position.height - itemHeight);
        for (SlyItem slyItem : items) {
            slyItem.viewPosition.width = position.width;
            slyItem.viewPosition.height = (float) itemHeight;
            slyItem.positionDelta.x = 0;
            slyItem.positionDelta.y = currentY;
            slyItem.updateDeltas();
            currentY -= itemHeight + itemOffset;
        }
    }


    private void updateBorderHeight() {
        if (firstItem == null) {
            borderHeight = 0;
            return;
        }

        borderHeight = firstItem.positionDelta.y + itemHeight - lastItem.positionDelta.y;

        if (borderHeight > position.height) {
            borderHeight = position.height;
        }
    }


    @Override
    public void onDestroy() {
        if (appearFactor.get() == 0) return;

        dy = 0;
        for (SlyItem slyItem : items) {
            slyItem.destroy();
        }
        exitEditMode();
    }


    @Override
    public void onAppear() {
        for (SlyItem listItem : items) {
            listItem.appear();
        }

        performInitialIconLoading();
    }


    private void performInitialIconLoading() {
        // nothing here
    }


    public boolean isInEditMode() {
        return inEditMode;
    }


    @Override
    public boolean checkToPerformAction() {
        if (!readyToReact) return false;

        if (reactCountDown > 0) {
            reactCountDown--;
        } else {
            readyToReact = false;
            itemBehavior.reaction(this, clickItemIndex);
            return true;
        }

        return false;
    }


    @Override
    public boolean touchDown() {
        if (!isTouchInsideRectangle(currentTouch, activeArea)) return false;
        touched = true;
        checkedLongTap = false;

        checkToSelectItems();

        lastTouch.setBy(initialTouch);
        currentTouch.setBy(initialTouch);

        return true;
    }


    private void checkToSelectItems() {
        if (inEditMode) return;

        for (SlyItem listItem : items) {
            if (listItem.isTouched(currentTouch)) {
                listItem.select();
            }
        }
    }


    @Override
    public boolean touchDrag() {
        if (!touched) return false;

        updateDyByTouch();

        lastTouch.setBy(currentTouch);
        return true;
    }


    private void updateDyByTouch() {
        dy = currentTouch.y - lastTouch.y;
    }


    @Override
    public boolean touchUp() {
        if (!touched) return false;

        if (satisfiesClickConditions()) {
            onClick();
        }

        touched = false;
        return true;
    }


    @Override
    public boolean onMouseWheelScrolled(int amount) {
        if (amount == 1) {
            dy = 0.06 * GraphicsYio.width;
        } else if (amount == -1) {
            dy = -0.06 * GraphicsYio.width;
        }
        limitItems();
        return true;
    }


    public RectangleYio getDeleteIcon() {
        return deleteIcon;
    }


    private void onClick() {
        if (inEditMode && isTouchInsideRectangle(initialTouch.x, initialTouch.y, deleteIcon)) {
            onDeleteButtonClicked();
            return;
        }

        for (SlyItem slyItem : items) {
            if (slyItem.isTouched(currentTouch)) {
                itemClicked(slyItem);
                return;
            }
        }
    }


    void onDeleteButtonClicked() {
        deleteSelectedItems();
        exitEditMode();
    }


    private void deleteSelectedItems() {
        // reactions
        for (int i = items.size() - 1; i >= 0; i--) {
            SlyItem slyItem = items.get(i);
            if (slyItem.isSelected()) {
                boolean deleted = itemBehavior.deleteReaction(this, i);
                if (!deleted) {
                    slyItem.deselect(); // do not remove from list
                }
            }
        }

        // remove items from list
        int firstIndex = items.size() + 1;
        for (int i = items.size() - 1; i >= 0; i--) {
            SlyItem slyItem = items.get(i);
            if (slyItem.isSelected()) {
                items.remove(i);
                firstIndex = i;
            }
        }

        for (int i = firstIndex; i < items.size(); i++) {
            SlyItem slyItem = items.get(i);
            slyItem.shortAppear();
        }

        updateItems();
    }


    private void itemClicked(SlyItem slyItem) {
        if (!inEditMode) {
            slyItem.select();

            SoundManager.playSound(SoundManager.button);
            clickItemIndex = items.indexOf(slyItem);
            readyToReact = true;
            reactCountDown = 2;
        } else {
            slyItem.switchSelection();
        }
    }


    private boolean satisfiesClickConditions() {
        if (System.currentTimeMillis() - touchDownTime > 300) return false;
        if (initialTouch.distanceTo(currentTouch) > 0.05f * GraphicsYio.width) return false;
        return true;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderScrollableListYio;
    }


    public ArrayList<SlyItem> getItems() {
        return items;
    }


    public SlyItem getListItem(int index) {
        return items.get(index);
    }


    public ScrollableListYio setItemBehavior(ListItemReaction itemBehavior) {
        this.itemBehavior = itemBehavior;
        return getThis();
    }


    public ScrollableListYio setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }
}
