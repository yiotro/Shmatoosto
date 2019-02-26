package yio.tro.shmatoosto.menu.elements;

import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;

public class TestRoundRectElement extends InterfaceElement<TestRoundRectElement> {

    public TestRoundRectElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);
    }


    @Override
    protected TestRoundRectElement getThis() {
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


    @Override
    public RenderInterfaceElement getRenderSystem() {
        return MenuRenders.renderTestRoundRectElement;
    }
}
