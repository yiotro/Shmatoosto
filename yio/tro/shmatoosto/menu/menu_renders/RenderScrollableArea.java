package yio.tro.shmatoosto.menu.menu_renders;

import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.ScrollableAreaYio;

public class RenderScrollableArea extends RenderInterfaceElement {

    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        ScrollableAreaYio scrollableAreaYio = (ScrollableAreaYio) element;

//        renderDebug(scrollableAreaYio);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
