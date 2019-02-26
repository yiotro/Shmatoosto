package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.ScoreViewElement;
import yio.tro.shmatoosto.menu.elements.gameplay.SvView;
import yio.tro.shmatoosto.stuff.GraphicsYio;

import java.util.ArrayList;

public class RenderScoreView extends RenderInterfaceElement{


    private ScoreViewElement scoreViewElement;
    private TextureRegion defaultBackgroundTexture;
    private TextureRegion activeBackgroundTexture;
    private ArrayList<SvView> svViews;
    private float f;


    @Override
    public void loadTextures() {
        defaultBackgroundTexture = GraphicsYio.loadTextureRegion("game/score/def.png", false);
        activeBackgroundTexture = GraphicsYio.loadTextureRegion("game/score/active.png", false);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        scoreViewElement = (ScoreViewElement) element;
        svViews = scoreViewElement.svViews;
        if (svViews.size() == 0) return;
        f = scoreViewElement.getFactor().get();

        GraphicsYio.setBatchAlpha(batch, 0.2 * f);
        GraphicsYio.setFontAlpha(svViews.get(0).font, f * f);

        for (SvView svView : svViews) {
            GraphicsYio.drawByRectangle(batch, getSvViewBackground(svView), svView.viewPosition);
            svView.font.draw(batch, svView.textValue, svView.textPosition.x, svView.textPosition.y);
        }

        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.setFontAlpha(svViews.get(0).font, 1);
    }


    private TextureRegion getSvViewBackground(SvView svView) {
        if (svView.active) {
            return activeBackgroundTexture;
        }

        return defaultBackgroundTexture;
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
