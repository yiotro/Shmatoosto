package yio.tro.shmatoosto.menu.elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.game.loading.LoadingParameters;
import yio.tro.shmatoosto.game.loading.LoadingType;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.PointYio;
import yio.tro.shmatoosto.stuff.RectangleYio;

public class LoadingScreenView extends InterfaceElement<LoadingScreenView> {

    LoadingType loadingType;
    LoadingParameters loadingParameters;
    boolean readyToLaunchLoading;
    public RectangleYio bandPosition, progressPosition;
    float bandHeight;
    double progress;
    public String text;
    public PointYio textPosition;
    public BitmapFont font;


    public LoadingScreenView(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        loadingType = LoadingType.billiard;
        loadingParameters = null;
        readyToLaunchLoading = false;
        progress = 0;
        bandPosition = new RectangleYio();
        progressPosition = new RectangleYio();
        bandHeight = 4 * GraphicsYio.borderThickness;
        text = LanguagesManager.getInstance().getString("loading");
        textPosition = new PointYio();
        font = Fonts.gameFont;
    }


    public void setLoadingType(LoadingType loadingType) {
        this.loadingType = loadingType;
    }


    public void setLoadingParameters(LoadingParameters loadingParameters) {
        this.loadingParameters = loadingParameters;
    }


    @Override
    protected LoadingScreenView getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();
        updateProgressPosition();
        checkToGoAboveGameView();
    }


    private void checkToGoAboveGameView() {
        if (!factorMoved) return;
        if (appearFactor.get() != 1) return;

        setOnTopOfGameView(true);
    }


    @Override
    protected void updateViewPosition() {
        if (!factorMoved) return;

        float f = 0.5f + 0.5f * appearFactor.get();
        if (appearFactor.isInAppearState() && appearFactor.get() < 1) {
            viewPosition.width = f * position.width;
            viewPosition.height = f * position.height;
            viewPosition.x = (position.width - viewPosition.width) / 2;
            viewPosition.y = (position.height - viewPosition.height) / 2;
            return;
        }

        viewPosition.setBy(position);
    }


    private void updateProgressPosition() {
        progressPosition.setBy(bandPosition);
        progressPosition.width = (float) (progress * bandPosition.width);
    }


    private boolean checkToLaunchLoading() {
        if (!readyToLaunchLoading) return false;
        if (getFactor().get() < 1) return false;

        launchLoading();
        return true;
    }


    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();
        updateMetrics();
    }


    @Override
    protected void onPositionChanged() {
        super.onPositionChanged();
        updateMetrics();
    }


    private void updateMetrics() {
        updateBandPosition();
        updateTextPosition();
    }


    private void updateTextPosition() {
        float textWidth = GraphicsYio.getTextWidth(font, text);

        textPosition.x = position.x + (position.width - textWidth) / 2;
        textPosition.y = position.y + 0.52f * position.height;
    }


    private void updateBandPosition() {
        bandPosition.width = 0.8f * position.width;
        bandPosition.x = position.x + (position.width - bandPosition.width) / 2;
        bandPosition.height = bandHeight;
        bandPosition.y = position.y + 0.45f * position.height;
    }


    private void launchLoading() {
        readyToLaunchLoading = false;

        menuControllerYio.yioGdxGame.loadingManager.startLoading(loadingType, loadingParameters);
    }


    @Override
    public void onDestroy() {

    }


    public void updateProgress(double progress) {
        if (getFactor().get() < 1) return;

        this.progress = progress;
        updateProgressPosition();
    }


    public double getProgress() {
        return progress;
    }


    @Override
    public void onAppear() {
        progress = 0;
        readyToLaunchLoading = true;
    }


    @Override
    public boolean checkToPerformAction() {
        return checkToLaunchLoading();
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
        return MenuRenders.renderLoadingScreenView;
    }
}
