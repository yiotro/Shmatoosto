package yio.tro.shmatoosto.menu.menu_renders;

import yio.tro.shmatoosto.menu.MenuViewYio;

import java.util.ArrayList;

public class MenuRenders {

    static ArrayList<RenderInterfaceElement> list = new ArrayList<>();

    public static RenderButton renderButton = new RenderButton();
    public static RenderSlider renderSlider = new RenderSlider();
    public static RenderScrollableListYio renderScrollableListYio = new RenderScrollableListYio();
    public static RenderResizableBorder renderResizableBorder = new RenderResizableBorder();
    public static RenderCheckButton renderCheckButton = new RenderCheckButton();
    public static RenderScrollableArea renderScrollableArea = new RenderScrollableArea();
    public static RenderCircleButton renderCircleButton = new RenderCircleButton();
    public static RenderDebugGraphView renderDebugGraphView = new RenderDebugGraphView();
    public static RenderUiGroup renderUiGroup = new RenderUiGroup();
    public static RenderLoadingScreenView renderLoadingScreenView = new RenderLoadingScreenView();
    public static RenderNotificationElement renderNotificationElement = new RenderNotificationElement();
    public static RenderTestRoundRectElement renderTestRoundRectElement = new RenderTestRoundRectElement();
    public static RenderRoundBackground renderRoundShape = new RenderRoundBackground();
    public static RenderShadow renderShadow = new RenderShadow();
    public static RenderBilliardAimUI renderBilliardAimUI = new RenderBilliardAimUI();
    public static RenderScoreView renderScoreView = new RenderScoreView();
    public static RenderChapaevoAimUI renderChapaevoAimUI = new RenderChapaevoAimUI();
    public static RenderSoccerAimUI renderSoccerAimUI = new RenderSoccerAimUI();
    public static RenderCurrentEntityView renderCurrentEntityView = new RenderCurrentEntityView();


    public static void updateRenderSystems(MenuViewYio menuViewYio) {
        for (RenderInterfaceElement renderInterfaceElement : list) {
            renderInterfaceElement.update(menuViewYio);
        }
    }
}
