package yio.tro.shmatoosto.menu.menu_renders;

import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.TestRoundRectElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderTestRoundRectElement extends RenderInterfaceElement{


    private TestRoundRectElement trrElement;
    private RectangleYio pos;
    private float f;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        trrElement = (TestRoundRectElement) element;
        pos = trrElement.getViewPosition();
        f = trrElement.getFactor().get();

        GraphicsYio.setBatchAlpha(batch, 0.1 * f);

//        renderBorder();

        renderShadow();

        GraphicsYio.setBatchAlpha(batch, f);

        renderShape();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderShape() {
        MenuRenders.renderRoundShape.renderRoundShape(pos, BackgroundYio.green);
    }


    private void renderShadow() {
        MenuRenders.renderShadow.renderShadow(pos, f);
    }


    private void renderBorder() {
        GraphicsYio.renderBorder(batch, blackPixel, pos);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
