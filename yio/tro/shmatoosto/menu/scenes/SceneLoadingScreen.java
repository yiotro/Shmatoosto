package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.menu.elements.LoadingScreenView;

public class SceneLoadingScreen extends SceneYio {


    public LoadingScreenView loadingScreenView;


    @Override
    protected void initialize() {
        setBackground(4); // black

        loadingScreenView = uiFactory.getLoadingScreen()
                .setSize(1, 1)
                .setAppearParameters(6, 1.5)
                .setDestroyParameters(6, 2)
                .setOnTopOfGameView(false);
    }


    @Override
    protected void onEndCreation() {
        super.onEndCreation();

        forceElementsToTop();
    }
}
