package yio.tro.shmatoosto.menu.elements;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

import java.util.ArrayList;

public class ResizableBorder extends InterfaceElement<ResizableBorder> {

    public static final int SELECT_NONE = 0;
    public static final int SELECT_MAIN = 1;
    public static final int SELECT_CORNER = 2;

    int selectMode;
    PointYio delta;
    ArrayList<Corner> corners;


    public ResizableBorder(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        delta = new PointYio();
        initCorners();
        setSelectMode(SELECT_NONE);
    }


    private void initCorners() {
        corners = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Corner corner = new Corner();
            corner.position = new PointYio();
            corner.touchDistance = 0.05 * GraphicsYio.width;
            corner.selected = false;
            corners.add(corner);
        }
    }


    @Override
    protected ResizableBorder getThis() {
        return this;
    }


    @Override
    public void move() {

    }


    @Override
    protected void onPositionChanged() {
        super.onPositionChanged();
        updateCorners();
    }


    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();
        updateCorners();
    }


    private void updateCorners() {
        corners.get(0).position.set(position.x, position.y);
        corners.get(1).position.set(position.x, position.y + position.height);
        corners.get(2).position.set(position.x + position.width, position.y + position.height);
        corners.get(3).position.set(position.x + position.width, position.y);
    }


    @Override
    public void onDestroy() {

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
        for (Corner corner : corners) {
            if (currentTouch.distanceTo(corner.position) < corner.touchDistance) {
                setSelectMode(SELECT_CORNER);
                corner.selected = true;
                return true;
            }
        }

        if (isTouchInsideRectangle(currentTouch, viewPosition)) {
            setSelectMode(SELECT_MAIN);
            return true;
        }

        return false;
    }


    @Override
    public boolean touchDrag() {
        delta.x = currentTouch.x - lastTouch.x;
        delta.y = currentTouch.y - lastTouch.y;

        if (selectMode == SELECT_MAIN) {
            dragMain();
        }

        if (selectMode == SELECT_CORNER) {
            dragCorner();
        }

        return false;
    }


    private void dragCorner() {
        for (Corner corner : corners) {
            if (corner.selected) {
                corner.position.x += delta.x;
                corner.position.y += delta.y;
                break;
            }
        }

        applyCorner();
        updateCorners();
    }


    private void applyCorner() {
        Corner cornerA = corners.get(0);
        if (cornerA.selected) {
            delta.set(
                    cornerA.position.x - position.x,
                    cornerA.position.y - position.y);

            position.x += delta.x;
            position.y += delta.y;
            position.width -= delta.x;
            position.height -= delta.y;
            return;
        }

        Corner cornerB = corners.get(1);
        if (cornerB.selected) {
            delta.set(
                    cornerB.position.x - position.x,
                    cornerB.position.y - (position.y + position.height));

            position.x += delta.x;
            position.width -= delta.x;
            position.height += delta.y;
            return;
        }

        Corner cornerC = corners.get(2);
        if (cornerC.selected) {
            delta.set(
                    cornerC.position.x - (position.x + position.width),
                    cornerC.position.y - (position.y + position.height));

            position.width += delta.x;
            position.height += delta.y;
            return;
        }

        Corner cornerD = corners.get(3);
        if (cornerD.selected) {
            delta.set(
                    cornerD.position.x - (position.x + position.width),
                    cornerD.position.y - position.y);

            position.y += delta.y;
            position.width += delta.x;
            position.height -= delta.y;
            return;
        }
    }


    public boolean isCornerSelected(int index) {
        return corners.get(index).selected;
    }


    private void dragMain() {
        relocate(delta.x, delta.y);
    }


    @Override
    public boolean touchUp() {
        setSelectMode(SELECT_NONE);

        roundUpPosition();

        return false;
    }


    private void roundUpPosition() {
        position.x /= GraphicsYio.width;
        position.y /= GraphicsYio.height;
        position.width /= GraphicsYio.width;
        position.height /= GraphicsYio.height;

        position.x = (float) Yio.roundUp(position.x, 2);
        position.y = (float) Yio.roundUp(position.y, 2);
        position.width = (float) Yio.roundUp(position.width, 2);
        position.height = (float) Yio.roundUp(position.height, 2);

        position.x *= GraphicsYio.width;
        position.y *= GraphicsYio.height;
        position.width *= GraphicsYio.width;
        position.height *= GraphicsYio.height;

        onPositionChanged();
    }


    public int getSelectMode() {
        return selectMode;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderResizableBorder;
    }


    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;

        if (selectMode != SELECT_CORNER) {
            for (Corner corner : corners) {
                corner.selected = false;
            }
        }
    }


    public ArrayList<Corner> getCorners() {
        return corners;
    }


    public class Corner {
        public PointYio position;
        boolean selected;
        public double touchDistance;
    }
}
