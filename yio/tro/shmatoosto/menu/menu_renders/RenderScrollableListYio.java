package yio.tro.shmatoosto.menu.menu_renders;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.elements.scrollable_list.SlyItem;
import yio.tro.shmatoosto.menu.elements.scrollable_list.ScrollableListYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class RenderScrollableListYio extends RenderInterfaceElement {

    TextureRegion blackPixel, listItemBackground;
    TextureRegion deleteIconTexture;
    ShapeRenderer shapeRenderer;
    private RectangleYio shadowPosition;
    private ScrollableListYio scrollableListYio;
    private TextureRegion grayPixel;


    public RenderScrollableListYio() {
        shadowPosition = new RectangleYio();
    }


    @Override
    public void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
        listItemBackground = GraphicsYio.loadTextureRegion("pixels/save_slot_bg.png", false);
        grayPixel = GraphicsYio.loadTextureRegion("pixels/gray.png", false);

        deleteIconTexture = GraphicsYio.loadTextureRegion("menu/delete_icon.png", true);
    }


    @Override
    public void renderFirstLayer(InterfaceElement element) {
        scrollableListYio = (ScrollableListYio) element;

        renderShadow();
    }


    private void renderShadow() {
//        showItemShadows(scrollableListYio);

        if (scrollableListYio.isShadowEnabled()) {
            for (SlyItem slyItem : scrollableListYio.getItems()) {
                renderShadow(slyItem.getViewPosition(), scrollableListYio.getFactor().get());
            }
        }
    }


    @Override
    public void renderSecondLayer(InterfaceElement element) {
        scrollableListYio = (ScrollableListYio) element;
        shapeRenderer = menuViewYio.shapeRenderer;

        for (SlyItem slyItem : scrollableListYio.getItems()) {
            renderListItem(slyItem);
        }
        GraphicsYio.setBatchAlpha(batch, 1);
        GraphicsYio.setFontAlpha(scrollableListYio.nameFont, 1);
        GraphicsYio.setFontAlpha(scrollableListYio.descFont, 1);

        renderEditModeStuff();
    }


    private void renderEditModeStuff() {
        if (!scrollableListYio.isInEditMode()) return;

        GraphicsYio.drawByRectangle(batch, deleteIconTexture, scrollableListYio.getDeleteIcon());
    }


    private void renderListItem(SlyItem slyItem) {
        if (!slyItem.isVisible()) return;

        renderSingleItemBackground(slyItem);
        renderSingleItemText(slyItem);
        renderSingleItemIcon(slyItem);
        renderSingleItemSelection(slyItem);
    }


    private void renderSingleItemIcon(SlyItem slyItem) {
        if (!slyItem.hasIcon()) return;

        GraphicsYio.drawByRectangle(
                batch,
                slyItem.getIcon(),
                slyItem.getIconPosition()
        );

        GraphicsYio.renderBorder(
                batch,
                grayPixel,
                slyItem.getIconPosition()
        );
    }


    private void renderSingleItemBackground(SlyItem slyItem) {
        GraphicsYio.setBatchAlpha(batch, scrollableListYio.getFactor().get());
        MenuRenders.renderRoundShape.renderRoundShape(slyItem.getViewPosition(), BackgroundYio.white);
//        GraphicsYio.drawByRectangle(batch, listItemBackground, slyItem.getViewPosition());
    }


    private void renderSingleItemText(SlyItem slyItem) {
        if (!needToShowFullSelector(scrollableListYio)) return;

        GraphicsYio.setFontAlpha(scrollableListYio.nameFont, scrollableListYio.getFactor().get());
        scrollableListYio.nameFont.draw(batch, slyItem.getName(), slyItem.getNameX(), slyItem.getNameY());

        GraphicsYio.setFontAlpha(scrollableListYio.descFont, scrollableListYio.getFactor().get());
        scrollableListYio.descFont.draw(batch, slyItem.getDescription(), slyItem.getDescriptionX(), slyItem.getDescriptionY());
    }


    private void renderSingleItemSelection(SlyItem slyItem) {
        if (!slyItem.isSelected()) return;

        GraphicsYio.setBatchAlpha(batch, slyItem.getSelectFactor().get() * 0.4);
        MenuRenders.renderRoundShape.renderRoundShape(slyItem.getViewPosition(), BackgroundYio.blue);
//        GraphicsYio.drawByRectangle(batch, blackPixel, slyItem.getViewPosition());
        GraphicsYio.setBatchAlpha(batch, 1);
    }


    private void showItemShadows(ScrollableListYio scrollableListYio) {
        for (SlyItem slyItem : scrollableListYio.getItems()) {
            if (!slyItem.isVisible()) continue;

            shadowPosition.setBy(slyItem.getViewPosition());

//            renderShadow(shadowPosition, scrollableListYio.getFactor().get());
        }
    }


    private boolean needToShowFullSelector(ScrollableListYio scrollableListYio) {
        return scrollableListYio.getFactor().get() > 0.5;
    }


    @Override
    public void renderThirdLayer(InterfaceElement element) {

    }
}
