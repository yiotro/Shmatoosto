package yio.tro.shmatoosto;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.GameRules;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.loading.LoadingManager;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.ButtonRenderer;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.MenuViewYio;
import yio.tro.shmatoosto.menu.scenes.SceneMainMenu;
import yio.tro.shmatoosto.menu.scenes.SceneYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.FrameBufferYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.Random;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {

    public static long appLaunchTime;

    public static final int PLATFORM_ANDROID = 0;
    public static final int PLATFORM_DESKTOP = 1;
    public static int platform;
    private RenderBackElements renderBackElements;
    BackElementsController backElementsController;

    boolean alreadyShownErrorMessageOnce;
    boolean ignoreDrag, loadedResources;
    public boolean gamePaused, readyToUnPause, splashVisible, startedExitProcess;
    static boolean screenVeryNarrow;

    public int w, h;
    int frameSkipCount, splashCount, currentBackgroundIndex;

    long lastTimeButtonPressed, timeToUnPause;
    public float pressX, pressY, backgroundHeight, splashOffset;

    public GameController gameController;
    public GameView gameView;
    public SpriteBatch batch;
    public MenuControllerYio menuControllerYio;
    public MenuViewYio menuViewYio;
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion splashTexture, currentBackground, lastBackground;
    FrameBuffer frameBuffer;
    FactorYio transitionFactor, splashFactor;
    public static Random random = new Random();
    public LoadingManager loadingManager;
    public OnKeyReactions onKeyReactions;
    ArrayList<TouchListenerYio> touchListeners;
    public MovableController movableController;
    public boolean sloMo;


    @Override
    public void create() {
        loadedResources = false;
        batch = new SpriteBatch();
        splashCount = 0;
        loadTextures();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        GraphicsYio.width = (float) Gdx.graphics.getWidth();
        GraphicsYio.height = (float) Gdx.graphics.getHeight();
        pressX = 0.5f * w;
        pressY = 0.5f * h;
        frameSkipCount = 50; // >= 2
        backElementsController = new BackElementsController(this);
        renderBackElements = new RenderBackElements(this);
        movableController = new MovableController();
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        touchListeners = new ArrayList<>();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        sloMo = false;
    }


    private void loadTextures() {
        splashTexture = GraphicsYio.loadTextureRegion("menu_backgrounds/splash.png", true);
    }


    void generalInitialization() {
        appLaunchTime = System.currentTimeMillis();
        loadedResources = true;
        backgroundHeight = 1.666666f * w;
        screenVeryNarrow = (h > backgroundHeight + 2);
        if (screenVeryNarrow) say("screen is very narrow");
        loadBackgroundTextures();

        transitionFactor = new FactorYio();

        initSplash();
        gamePaused = true;
        alreadyShownErrorMessageOnce = false;
        startedExitProcess = false;

        SceneYio.onGeneralInitialization(); // before menu controller
        initializeSingletons();
        SettingsManager.getInstance().loadValues();
        Fonts.initFonts();
        menuControllerYio = new MenuControllerYio(this);
        menuViewYio = new MenuViewYio(this);
        GameRules.bootInit();
        gameController = new GameController(this); // must be called after menu controller is created. (maybe not)
        gameView = new GameView(this);
        gameView.appearFactor.destroy(1, 1);
        currentBackgroundIndex = -1;
        currentBackground = gameView.blackPixel; // call this after game view is created
        beginBackgroundChange(SceneMainMenu.MM_BACKGROUND);
        loadingManager = new LoadingManager(gameController);
        Gdx.input.setInputProcessor(this);
        SoundManager.loadSounds();
        onKeyReactions = new OnKeyReactions(this);

        loadSavedInfo();
    }


    private void initializeSingletons() {
        ButtonRenderer.initialize();
        LanguagesManager.initialize();
        OneTimeInfo.initialize();
        SettingsManager.initialize();
        GameRendersList.initialize();
    }


    private void loadSavedInfo() {
        OneTimeInfo.getInstance().load();
    }


    private void initSplash() {
        splashVisible = true;
        splashFactor = new FactorYio();
        splashFactor.setValues(0, 0);
        splashFactor.appear(1, 3);
        splashOffset = 0.2f * w;
    }


    private void loadBackgroundTextures() {
        mainBackground = GraphicsYio.loadTextureRegion("menu_backgrounds/main_menu_background.png", true);
        infoBackground = GraphicsYio.loadTextureRegion("menu_backgrounds/info_background.png", true);
        settingsBackground = GraphicsYio.loadTextureRegion("menu_backgrounds/settings_background.png", true);
        pauseBackground = GraphicsYio.loadTextureRegion("menu_backgrounds/pause_background.png", true);
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused && !this.gamePaused) { // actions when paused
            this.gamePaused = true;
            onPaused();
        } else if (!gamePaused && this.gamePaused) { // actions when unpaused
            unPauseAfterSomeTime();
            onUnPaused();
        }
    }


    private void onUnPaused() {
        beginBackgroundChange(4);
        Fonts.gameFont.setColor(Color.BLACK);
    }


    private void onPaused() {
        Fonts.gameFont.setColor(Color.BLACK);
        menuControllerYio.forceDyingElementsToEnd();
    }


    public void beginBackgroundChange(int index) {
        if (currentBackgroundIndex == index && index == 4) return;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        switch (index) {
            case 0:
                currentBackground = settingsBackground;
                break;
            case 1:
                currentBackground = pauseBackground;
                break;
            case 2:
                currentBackground = mainBackground;
                break;
            case 3:
                currentBackground = infoBackground;
                break;
            case 4:
                currentBackground = gameView.blackPixel;
                break;
        }

        transitionFactor.setValues(0.02, 0.01);
        transitionFactor.appear(3, 1);
    }


    public void move() {
        if (!loadedResources) return;

        if (splashVisible) {
            moveSplash();
        }

        if (loadingManager.working) {
            loadingManager.move();
            return;
        }

        checkForSloMo();
        transitionFactor.move();
        movableController.move();
        checkToUnPause();

        gameView.moveFactors();

        if (loadedResources && !gamePaused) {
            gameController.move();
        }

        menuControllerYio.move();
        if (loadingManager.working) return; // immediately after button press

        backElementsController.move();
    }


    private void checkForSloMo() {
        if (!sloMo) return;

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkToUnPause() {
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause && gameView.coversAllScreen()) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
            frameSkipCount = 10; // >= 2
        }
    }


    private void moveSplash() {
        splashFactor.move();

        if (splashFactor.get() == 1) {
            splashVisible = false;
        }
    }


    private void drawBackground(TextureRegion textureRegion) {
        batch.begin();
        batch.draw(textureRegion, 0, 0, w, h);
//        if (isScreenVeryNarrow()) batch.draw(textureRegion, 0, 0.985f * backgroundHeight, w, backgroundHeight);
        batch.end();
    }


    private void renderMenuLayersWhenNothingIsMoving() { // when transitionFactor.get() == 1
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        drawBackground(currentBackground);

        renderBackElements.renderBackElements();
    }


    private void renderMenuLayersWhenUsualAnimation() {
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        drawBackground(lastBackground);

        float f = (0 + transitionFactor.get());
        batch.setColor(c.r, c.g, c.b, f);
        drawBackground(currentBackground);
        batch.setColor(c.r, c.g, c.b, 1);

        renderBackElements.renderBackElements();
    }


    private void renderMenuLayers() {
        if (transitionFactor.get() == 1) {
            renderMenuLayersWhenNothingIsMoving();
            return;
        }


        renderMenuLayersWhenUsualAnimation();
    }


    public void renderInternals() {
        if (!loadedResources) return;

        if (!gameView.coversAllScreen()) {
            renderMenuLayers();
        }

        menuViewYio.renderAll(false);
        gameView.render();
        menuViewYio.renderAll(true);

        renderDebug();
    }


    private void renderDebug() {
        if (!DebugFlags.showFps) return;

        batch.begin();

        Fonts.gameFont.draw(
                batch,
                getCurrentFpsString(),
                0.07f * w, Gdx.graphics.getHeight() - 10
        );

        batch.end();
    }


    private String getCurrentFpsString() {
        return "" + Math.min(Gdx.graphics.getFramesPerSecond(), 60);
    }


    private void renderSplashScreen() {
        batch.begin();
        int splashHeight = (int) (1.666666 * w);
        int delta = splashHeight - h;
        batch.draw(splashTexture, 0, -delta / 2, w, splashHeight);
        batch.end();
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            renderSplashScreen();
            if (splashCount == 2) generalInitialization();
            splashCount++;
            return;
        }

        tryToMove();

        if (gamePaused || sloMo) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }

        if (splashVisible) {
            renderSplash();
        }
    }


    private void renderSplash() {
        batch.begin();
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1 - splashFactor.get());
        float off = splashOffset * splashFactor.get();
        batch.draw(splashTexture, -off, -off, w + 2 * off, h + 2 * off);
        batch.setColor(c.r, c.g, c.b, c.a);
        batch.end();
    }


    private void tryToMove() {
        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                onCatchedException(exception);
            }
        }
    }


    public void addTouchListener(TouchListenerYio touchListenerYio) {
        Yio.addByIterator(touchListeners, touchListenerYio);
    }


    void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 100;
    }


    public static boolean isScreenNonStandard() {
        float ratio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        return ratio < 1.51 || ratio > 1.7;
    }


    public static boolean isScreenVeryNarrow() {
        return screenVeryNarrow;
    }


    public void forceBackgroundChange() {
        transitionFactor.setValues(1, 0);
    }


    public static void say(String text) {
        System.out.println(text);
    }


    @Override
    public void pause() {
        super.pause();

        if (startedExitProcess) return;

        if (gameView != null) {
            gameView.onPause();
        }

        if (gameController != null) {
            gameController.onPause();
        }

        if (menuControllerYio != null) {
            menuControllerYio.onPause();
        }
    }


    @Override
    public void resume() {
        super.resume();

        if (startedExitProcess) return;

        if (gameView != null) {
            gameView.onResume();
        }

        if (gameController != null) {
            gameController.onResume();
        }

        if (menuControllerYio != null) {
            menuControllerYio.onResume();
        }
    }


    @Override
    public boolean keyDown(int keycode) {
        if (splashFactor.get() < 1) return true;

        try {
            onKeyReactions.keyDown(keycode);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                onCatchedException(exception);
            }
        }

        return true;
    }


    @Override
    public boolean keyUp(int keycode) {

        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    public boolean isGamePaused() {
        return gamePaused;
    }


    private void onCatchedException(Exception exception) {
        gameView.destroy();
        setGamePaused(true);
        loadingManager.working = false;

        Scenes.catchedException.setException(exception);
        Scenes.catchedException.create();
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        pressX = screenX;
        pressY = h - screenY;
//        System.out.println("Screen touch: " + Yio.roundUp(pressX / w, 4) + " " + Yio.roundUp(pressY / h, 4));
        try {
            if (touchDownReaction(screenX, screenY, pointer, button)) return false;
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                onCatchedException(exception);
            }
        }
        return false;
    }


    private boolean touchDownReaction(int screenX, int screenY, int pointer, int button) {
        for (TouchListenerYio touchListener : touchListeners) {
            if (touchListener.isActive() && touchListener.touchDown(screenX, h - screenY)) return true;
        }

        if (!gameView.isInMotion() && transitionFactor.get() > 0.99 && menuControllerYio.touchDown(screenX, h - screenY, pointer, button)) {
            lastTimeButtonPressed = System.currentTimeMillis();
            return true;
        } else {
            ignoreDrag = false;
        }

        if (!gamePaused) {
            gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        }

        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            touchUpReaction(screenX, screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                onCatchedException(exception);
            }
        }
        return false;
    }


    private void touchUpReaction(int screenX, int screenY, int pointer, int button) {
        for (TouchListenerYio touchListener : touchListeners) {
            if (touchListener.isActive() && touchListener.touchUp(screenX, h - screenY)) return;
        }

        if (menuControllerYio.touchUp(screenX, h - screenY, pointer, button)) return;

        if (!gamePaused && gameView.coversAllScreen()) {
            gameController.touchUp(screenX, h - screenY, pointer, button);
        }
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        try {
            touchDragReaction(screenX, screenY, pointer);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                onCatchedException(exception);
            }
        }

        return false;
    }


    private boolean touchDragReaction(int screenX, int screenY, int pointer) {
        for (TouchListenerYio touchListener : touchListeners) {
            if (touchListener.isActive() && touchListener.touchDrag(screenX, h - screenY)) return false;
        }

        menuControllerYio.touchDragged(screenX, h - screenY, pointer);

        if (!ignoreDrag && !gamePaused && gameView.coversAllScreen()) {
            gameController.touchDragged(screenX, h - screenY, pointer);
        }

        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        if (menuControllerYio.onMouseWheelScrolled(amount)) return true;

        return true;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }


    public void exitApp() {
        startedExitProcess = true;
        ButtonRenderer instance = ButtonRenderer.getInstance();
        instance.disposeAllTextures();
        instance.killInstance();

        Gdx.app.exit();
    }


}
