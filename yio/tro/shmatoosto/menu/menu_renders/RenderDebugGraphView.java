package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.elements.DebugGraphView;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;

import java.util.ArrayList;

public class RenderDebugGraphView extends RenderInterfaceElement{


    private TextureRegion background;
    private DebugGraphView debugGraphView;


    @Override
    public void loadTextures() {
        background = GraphicsYio.loadTextureRegion("pixels/white.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {
        renderShadow(element.getViewPosition(), element.getFactor().get());
    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        debugGraphView = (DebugGraphView) element;

        GraphicsYio.setBatchAlpha(batch, debugGraphView.getFactor().get());

        renderBackground();
        renderGraph();
        renderName();

        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void renderName() {
        if (!debugGraphView.hasName()) return;
        if (!debugGraphView.isNameActive()) return;

        GraphicsYio.setFontAlpha(Fonts.gameFont, debugGraphView.getFactor().get());

        Fonts.gameFont.draw(
                batch,
                debugGraphView.getName(),
                debugGraphView.getNamePosition().x,
                debugGraphView.getNamePosition().y
        );

        GraphicsYio.setFontAlpha(Fonts.gameFont, 1);
    }


    private void renderGraph() {
        ArrayList<PointYio> views = debugGraphView.getViews();

        for (int i = 0; i < views.size() - 1; i++) {
            PointYio current = views.get(i);
            PointYio next = views.get(i + 1);

            GraphicsYio.drawLine(batch, blackPixel, current, next, 0.01 * GraphicsYio.width);
        }
    }


    private void renderBackground() {
        GraphicsYio.drawByRectangle(batch, background, debugGraphView.getViewPosition());
        GraphicsYio.renderBorder(batch, blackPixel, debugGraphView.getViewPosition());
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
