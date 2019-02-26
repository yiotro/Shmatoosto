package yio.tro.shmatoosto.menu.elements.gameplay;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.game_objects.BColorYio;
import yio.tro.shmatoosto.game.gameplay.soccer.SoccerManager;
import yio.tro.shmatoosto.game.player_entities.AbstractPlayerEntity;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.elements.InterfaceElement;
import yio.tro.shmatoosto.menu.menu_renders.MenuRenders;
import yio.tro.shmatoosto.menu.menu_renders.RenderInterfaceElement;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class CurrentEntityViewElement extends InterfaceElement<CurrentEntityViewElement> {


    public FactorYio textAlphaFactor;
    public String lastString, currentString, viewString;


    public CurrentEntityViewElement(MenuControllerYio menuControllerYio, int id) {
        super(menuControllerYio, id);

        textAlphaFactor = new FactorYio();
    }


    @Override
    protected CurrentEntityViewElement getThis() {
        return this;
    }


    @Override
    public void move() {
        updateViewPosition();
        textAlphaFactor.move();
        checkToChangeTextAlpha();
    }


    private void checkToChangeTextAlpha() {
        currentString = getCurrentViewString();
        if (currentString.equals(lastString)) return;

        if (lastString.equals("")) {
            textAlphaFactor.appear(3, 1);
            viewString = currentString;
        }

        if (currentString.equals("")) {
            textAlphaFactor.destroy(1, 1.5);
        }

        if (!currentString.equals("") && !lastString.equals("")) {
            viewString = currentString;
        }

        lastString = currentString;
    }


    @Override
    public void onDestroy() {

    }


    @Override
    public void onAppear() {
        lastString = "";
        currentString = "";
        viewString = "";
    }


    public String getCurrentViewString() {
        switch (getGameController().gameMode) {
            default:
                return getViewStringByIndex(getGameController().currentEntityIndex);
            case soccer:
                return getViewStringInSoccerMode();
        }
    }


    private String getViewStringInSoccerMode() {
        BColorYio selectedBallsColor = getGameController().objectsLayer.getSelectedBallsColor();
        if (selectedBallsColor == null) return "";

        SoccerManager soccerManager = (SoccerManager) getGameController().gameplayManager;
        AbstractPlayerEntity entity = soccerManager.getEntity(selectedBallsColor);
        if (entity == null) return "";

        int index = entity.getIndex();

        return getViewStringByIndex(index);
    }


    private String getViewStringByIndex(int index) {
        if (index == 0) {
            return LanguagesManager.getInstance().getString("player_one");
        }

        if (index == 1) {
            return LanguagesManager.getInstance().getString("player_two");
        }

        return "";
    }


    @Override
    public boolean isVisible() {
        return super.isVisible() && GameRules.twoPlayerMode;
    }


    @Override
    public boolean checkToPerformAction() {
        return false;
    }


    @Override
    public boolean isTouchable() {
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
        return MenuRenders.renderCurrentEntityView;
    }
}
