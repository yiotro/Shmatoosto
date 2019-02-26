package yio.tro.shmatoosto.menu;

import yio.tro.shmatoosto.menu.elements.*;
import yio.tro.shmatoosto.menu.elements.gameplay.*;
import yio.tro.shmatoosto.menu.elements.scrollable_list.ScrollableListYio;
import yio.tro.shmatoosto.menu.elements.slider.SliderYio;
import yio.tro.shmatoosto.menu.scenes.SceneYio;

public class UiFactory {

    MenuControllerYio menuControllerYio;
    SceneYio sceneYio;
    private static int currentID = 0;


    public UiFactory(SceneYio sceneYio) {
        this.sceneYio = sceneYio;
        menuControllerYio = sceneYio.menuControllerYio;
    }


    public ButtonYio getButton() {
        ButtonYio buttonYio = new ButtonYio(menuControllerYio, getNewID());
        addElementToScene(buttonYio);

        return buttonYio;
    }


    public ScrollableListYio getScrollableList() {
        ScrollableListYio scrollableListYio;
        scrollableListYio = new ScrollableListYio(menuControllerYio, getNewID());
        addElementToScene(scrollableListYio);
        return scrollableListYio;
    }


    public SliderYio getSlider() {
        SliderYio sliderYio = new SliderYio(menuControllerYio, getNewID());
        addElementToScene(sliderYio);
        return sliderYio;
    }


    public CheckButtonYio getCheckButton() {
        CheckButtonYio checkButtonYio = new CheckButtonYio(menuControllerYio, getNewID());
        addElementToScene(checkButtonYio);
        return checkButtonYio;
    }


    public ResizableBorder getResizableBorder() {
        ResizableBorder resizableBorder = new ResizableBorder(menuControllerYio, getNewID());
        addElementToScene(resizableBorder);
        return resizableBorder;
    }


    public CircleButtonYio getCircleButton() {
        CircleButtonYio circleButtonYio = new CircleButtonYio(menuControllerYio, getNewID());
        addElementToScene(circleButtonYio);
        return circleButtonYio;
    }


    public DebugGraphView getDebugGraphView() {
        DebugGraphView debugGraphView = new DebugGraphView(menuControllerYio, getNewID());
        addElementToScene(debugGraphView);
        return debugGraphView;
    }


    public ScrollableAreaYio getScrollableAreaYio() {
        ScrollableAreaYio scrollableAreaYio;
        scrollableAreaYio = new ScrollableAreaYio(menuControllerYio, getNewID());
        addElementToScene(scrollableAreaYio);
        return scrollableAreaYio;
    }


    public UiGroupYio getUiGroupYio() {
        UiGroupYio uiGroupYio;
        uiGroupYio = new UiGroupYio(menuControllerYio, getNewID());
        addElementToScene(uiGroupYio);
        return uiGroupYio;
    }


    public LoadingScreenView getLoadingScreen() {
        LoadingScreenView loadingScreenView = new LoadingScreenView(menuControllerYio, getNewID());
        addElementToScene(loadingScreenView);
        return loadingScreenView;
    }


    public NotificationElement getNotificationElement() {
        NotificationElement notificationElement = new NotificationElement(menuControllerYio, getNewID());
        addElementToScene(notificationElement);
        return notificationElement;
    }


    public TestRoundRectElement getTestRoundRectElement() {
        TestRoundRectElement testRoundRectElement = new TestRoundRectElement(menuControllerYio, getNewID());
        addElementToScene(testRoundRectElement);
        return testRoundRectElement;
    }


    public BilliardAimUiElement getBilliardAimUI() {
        BilliardAimUiElement billiardAimUiElement = new BilliardAimUiElement(menuControllerYio, getNewID());
        addElementToScene(billiardAimUiElement);
        return billiardAimUiElement;
    }


    public ScoreViewElement getScoreViewElement() {
        ScoreViewElement scoreViewElement = new ScoreViewElement(menuControllerYio, getNewID());
        addElementToScene(scoreViewElement);
        return scoreViewElement;
    }


    public ChapaevoAimUiElement getChapaevoAimUiElement() {
        ChapaevoAimUiElement chapaevoAimUiElement = new ChapaevoAimUiElement(menuControllerYio, getNewID());
        addElementToScene(chapaevoAimUiElement);
        return chapaevoAimUiElement;
    }


    public SoccerAimUiElement getSoccerAimUiElement() {
        SoccerAimUiElement soccerAimUiElement = new SoccerAimUiElement(menuControllerYio, getNewID());
        addElementToScene(soccerAimUiElement);
        return soccerAimUiElement;
    }


    public CurrentEntityViewElement getCurrentEntityViewElement() {
        CurrentEntityViewElement currentEntityViewElement = new CurrentEntityViewElement(menuControllerYio, getNewID());
        addElementToScene(currentEntityViewElement);
        return currentEntityViewElement;
    }


    private int getNewID() {
        currentID++;
        return currentID;
    }


    private void addElementToScene(InterfaceElement interfaceElement) {
        sceneYio.addLocalElement(interfaceElement);
        menuControllerYio.addElement(interfaceElement);
    }
}
