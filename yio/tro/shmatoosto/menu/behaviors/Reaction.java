package yio.tro.shmatoosto.menu.behaviors;

import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.menu.behaviors.menu_creation.*;

public abstract class Reaction {

    protected GameController gameController;
    protected MenuControllerYio menuControllerYio;
    protected YioGdxGame yioGdxGame;


    protected abstract void reaction();


    public void performReactActions(MenuControllerYio menuControllerYio) {
        if (menuControllerYio != null) {
            this.menuControllerYio = menuControllerYio;
            yioGdxGame = menuControllerYio.yioGdxGame;
            gameController = yioGdxGame.gameController;
        }
        reaction();
    }


    public static RbExit rbExit = new RbExit();
    public static RbMainMenu rbMainMenu = new RbMainMenu();
    public static RbPauseMenu rbPauseMenu = new RbPauseMenu();
    public static RbNothing rbNothing = new RbNothing();
    public static RbSettingsMenu rbSettingsMenu = new RbSettingsMenu();
    public static RbLanguageMenu rbLanguageMenu = new RbLanguageMenu();

}
