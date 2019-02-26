package yio.tro.shmatoosto.menu.elements;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

import java.util.ArrayList;

public class UiGroupYio extends InterfaceElement<UiGroupYio>{

    ArrayList<InterfaceElement> children;
    boolean inProcessOfUpdatingMetrics;


    public UiGroupYio(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        children = new ArrayList<>();
        setSize(0, 0);
        inProcessOfUpdatingMetrics = false;
    }


    @Override
    protected UiGroupYio getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();
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
        return false;
    }


    @Override
    public boolean touchDrag() {
        return false;
    }


    @Override
    public boolean touchUp() {
        return false;
    }


    private void updateMetrics() {
        if (inProcessOfUpdatingMetrics) return;
        inProcessOfUpdatingMetrics = true;

        RectangleYio pos;

        float down = 0,
                up = 0,
                right = 0,
                left = 0;

        for (InterfaceElement child : children) {
            pos = child.position;
            if (pos.x < left) {
                left = pos.x;
            }
            if (pos.x + pos.width > right) {
                right = pos.x + pos.width;
            }
            if (pos.y < down) {
                down = pos.y;
            }
            if (pos.y + pos.height > up) {
                up = pos.y + pos.height;
            }
        }

        setSize((right - left) / GraphicsYio.width, (up - down) / GraphicsYio.height);

        if (left < 0 || down < 0) {
            relocate(left, down);
        }

        inProcessOfUpdatingMetrics = false;
    }


    @Override
    public void relocate(float horizontal, float vertical) {
        super.relocate(horizontal, vertical);

        for (InterfaceElement child : children) {
            child.relocate(-horizontal, -vertical);
        }
    }


    @Override
    protected void onChildPositionChanged(InterfaceElement child) {
        super.onChildPositionChanged(child);

        updateMetrics();
    }


    @Override
    protected void onChildAdded(InterfaceElement child) {
        super.onChildAdded(child);

        Yio.addToEndByIterator(children, child);
        updateMetrics();
    }


    public UiGroupYio packIntoParent(double offset) {
        parent.stretchVertically(this, offset);

        centerHorizontal();
        centerVertical();

        return getThis();
    }


    public ArrayList<InterfaceElement> getChildren() {
        return children;
    }


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderUiGroup;
    }
}
