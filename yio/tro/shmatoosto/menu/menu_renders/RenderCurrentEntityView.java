package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.gameplay.CurrentEntityViewElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderCurrentEntityView extends RenderInterfaceElement{


    private CurrentEntityViewElement currentEntityView;
    private BitmapFont font;
    private RectangleYio viewPosition;


    @Override
    public void loadTextures() {

    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {

    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        currentEntityView = (CurrentEntityViewElement) element;
        font = Fonts.gameFont;
        viewPosition = currentEntityView.getViewPosition();

        GraphicsYio.setFontAlpha(font, currentEntityView.textAlphaFactor.get());

        font.draw(
                batch,
                currentEntityView.viewString,
                viewPosition.x,
                viewPosition.y + viewPosition.height
        );

        GraphicsYio.setFontAlpha(font, 1);
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
