package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.loading.LoadingListener;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.*;
import yio.tro.shmatoosto.menu.elements.*;
import yio.tro.shmatoosto.stuff.GraphicsYio;

import java.util.ArrayList;
import java.util.StringTokenizer;

public abstract class SceneYio implements LoadingListener{

    public static ArrayList<SceneYio> sceneList;

    boolean initialized;
    protected YioGdxGame yioGdxGame;
    int backgroundIndex;
    public MenuControllerYio menuControllerYio;
    protected UiFactory uiFactory;
    protected LanguagesManager languagesManager;
    private ArrayList<InterfaceElement> localElementsList;
    protected InterfaceElement previousElement, currentAddedElement;


    public SceneYio() {
        initialized = false;
        if (sceneList == null) {
            sceneList = new ArrayList<>();
        }

        Yio.addByIterator(sceneList, this);

        localElementsList = new ArrayList<>();
        backgroundIndex = -1;
        previousElement = null;
        currentAddedElement = null;
    }


    public static void onGeneralInitialization() {
        sceneList = null;
    }


    public void addLocalElement(InterfaceElement interfaceElement) {
        Yio.addToEndByIterator(localElementsList, interfaceElement);
        interfaceElement.setSceneOwner(this);

        previousElement = currentAddedElement;
        currentAddedElement = interfaceElement;
    }


    private void checkToInitialize() {
        if (initialized) return;
        initialized = true;

        initialize();

        endInitialization();
    }


    protected void endInitialization() {

    }


    public void create() {
        beginCreation();

        checkToInitialize();
        changeBackground();
        appear();

        endCreation();
    }


    @Override
    public void onLevelCreationEnd() {

    }


    @Override
    public void makeExpensiveLoadingStep(int step) {

    }


    protected void beginCreation() {
        menuControllerYio.setCurrentScene(this);
        destroyAllVisibleElements();
        menuControllerYio.checkToRemoveInvisibleElements();
    }


    private void endCreation() {
        for (int i = 0; i < localElementsList.size(); i++) {
            InterfaceElement element = localElementsList.get(i);

            if (element instanceof ScrollableAreaYio) {
                ((ScrollableAreaYio) element).forceToTop();
            }

            element.onSceneEndCreation();
        }

        onEndCreation();
    }


    protected void onEndCreation() {
        //
    }


    public void move() {

    }


    private void changeBackground() {
        if (backgroundIndex == -1) return;

        yioGdxGame.beginBackgroundChange(backgroundIndex);
    }


    protected void onAppear() {

    }


    protected final void appear() {
        onAppear();
        for (InterfaceElement interfaceElement : localElementsList) {
            appearElement(interfaceElement);
        }
    }


    protected void appearElement(InterfaceElement interfaceElement) {
        interfaceElement.appear();
        menuControllerYio.addVisibleElement(interfaceElement);
    }


    public void destroy() {
        onDestroy();
        for (InterfaceElement interfaceElement : localElementsList) {
            interfaceElement.destroy();
        }
    }


    protected void onDestroy() {
        // nothing by default
    }


    public static void updateAllScenes(MenuControllerYio menuControllerYio) {
        for (SceneYio sceneYio : sceneList) {
            sceneYio.updateLinks(menuControllerYio);
        }
    }


    public void updateLinks(MenuControllerYio menuControllerYio) {
        this.menuControllerYio = menuControllerYio;
        yioGdxGame = menuControllerYio.yioGdxGame;
        uiFactory = new UiFactory(this);
        languagesManager = menuControllerYio.languagesManager;
    }


    protected CircleButtonYio spawnBackButton(Reaction reaction) {
        CircleButtonYio backButton;

        backButton = uiFactory.getCircleButton()
                .setSize(GraphicsYio.convertToWidth(0.09))
                .alignLeft(0.04)
                .alignTop(0.02)
                .setTouchOffset(0.05)
                .loadTexture("menu/back_icon.png")
                .setAnimation(AnimationYio.none)
                .setReaction(reaction)
                .tagAsBackButton();

        return backButton;
    }


    private ButtonYio spawnOldBackButton(Reaction reaction) {
        ButtonYio backButton;

        backButton = uiFactory.getButton()
                .setSize(0.4, 0.07)
                .alignLeft(0.05)
                .alignTop(0.03)
                .cropTexture("menu/back_icon.png")
                .setAnimation(AnimationYio.up)
                .setBorder(true)
                .setShadow(true)
                .setReaction(reaction)
                .setForcedShadow(true)
                .setTouchOffset(0.07)
                .tagAsBackButton();

        return backButton;
    }


    protected void destroyAllVisibleElements() {
        if (!isDisruptive()) return;

        for (InterfaceElement interfaceElement : menuControllerYio.getInterfaceElements()) {
            if (!interfaceElement.isVisible()) continue;
            interfaceElement.destroy();
        }
    }


    protected boolean isDisruptive() {
        return true;
    }


    protected abstract void initialize();


    public void forceElementsToTop() {
        for (InterfaceElement interfaceElement : localElementsList) {
            forceElementToTop(interfaceElement);
        }
    }


    public void forceElementToTop(InterfaceElement interfaceElement) {
        menuControllerYio.removeVisibleElement(interfaceElement);
        menuControllerYio.addVisibleElement(interfaceElement);
    }


    protected void renderTextAndSomeEmptyLines(ButtonYio buttonYio, String text, int emptyLines) {
        buttonYio.addTextLine(text);
        for (int i = 0; i < emptyLines; i++) {
            buttonYio.addTextLine(" ");
        }
        buttonYio.render();
    }


    public static ArrayList<String> convertStringToArray(String src) {
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(src, "#");
        while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }


    public void setBackground(int backgroundIndex) {
        this.backgroundIndex = backgroundIndex;
    }


    public ArrayList<InterfaceElement> getLocalElementsList() {
        return localElementsList;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
