package yio.tro.shmatoosto.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import yio.tro.shmatoosto.YioGdxGame;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.debug.DebugFlags;
import yio.tro.shmatoosto.game.loading.LoadingListener;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.elements.BackgroundYio;
import yio.tro.shmatoosto.stuff.*;
import yio.tro.shmatoosto.stuff.factor_yio.FactorYio;

public class GameView implements LoadingListener{

    YioGdxGame yioGdxGame;
    public GameController gameController;
    public static final double TOP_BEZEL_HEIGHT = 0.052;

    public int w, h;

    public FactorYio appearFactor;
    FrameBuffer frameBuffer;
    public SpriteBatch batchMovable, batchSolid;
    public OrthographicCamera orthoCam;

    TextureRegion animationTextureRegion;
    public TextureRegion blackPixel, whitePixel;
    public AtlasLoader atlasLoader, boardAtlas;
    TextureRegion greenBackground;
    private TextureRegion chapaevoBackground;
    private TextureRegion soccerBackground;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        appearFactor = new FactorYio();
        frameBuffer = FrameBufferYio.getInstance(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
        batchMovable = new SpriteBatch();
        batchSolid = yioGdxGame.batch;
        createOrthoCam();
        loadTextures();
        createAtlas();
        GameRendersList.getInstance().updateGameRenders(this);
    }


    private void createAtlas() {
        atlasLoader = new AtlasLoader("game/atlas/atlas_texture.png", "game/atlas/atlas_structure.txt", true);
        boardAtlas = new AtlasLoader("game/board/atlas_texture.png", "game/board/atlas_structure.txt", false);
    }


    public void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        updateCam();
    }


    void loadTextures() {
        blackPixel = GraphicsYio.loadTextureRegion("pixels/black.png", false);
        whitePixel = GraphicsYio.loadTextureRegion("pixels/white.png", false);
        greenBackground = GraphicsYio.loadTextureRegion("game/background/green.png", false);
        chapaevoBackground = GraphicsYio.loadTextureRegion("game/background/chapaevo.png", false);
        soccerBackground = GraphicsYio.loadTextureRegion("game/background/soccer.png", false);
    }


    public void updateCam() {
        orthoCam.update();
        batchMovable.setProjectionMatrix(orthoCam.combined);
    }


    public void defaultValues() {

    }


    public void appear() {
        appearFactor.setValues(0.02, 0);
        appearFactor.appear(6, 1.3); // 3, 1
        updateAnimationTexture();
    }


    public void forceAppear() {
        appearFactor.setValues(1, 0);
        appearFactor.stop();
    }


    public void destroy() {
        if (appearFactor.get() == 0) return;

        appearFactor.destroy(1, 5);

        updateAnimationTexture();
    }


    public void updateAnimationTexture() {
        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
    }


    public void onPause() {
        atlasLoader.atlasRegion.getTexture().dispose();
        boardAtlas.atlasRegion.getTexture().dispose();
        GameRender.disposeAllTextures();
    }


    public void onResume() {
        createAtlas();
        GameRendersList.getInstance().updateGameRenders(this);
        GameRender.updateAllTextures();
    }


    public void renderInternals() {
        batchMovable.begin();

        GameRendersList instance = GameRendersList.getInstance();

        BackgroundYio background = gameController.gameplayManager.getBackground();
        batchMovable.draw(getBackgroundTexture(background), 0, 0, GraphicsYio.width, GraphicsYio.height);

        gameController.gameplayManager.getBoardRender().renderBottomLayer();
        instance.renderObstacles.renderShadows();
        instance.renderBalls.renderFallBallAnimations();
        instance.renderBalls.render();
        gameController.gameplayManager.getBoardRender().render();
        instance.renderObstacles.render();

        renderDebug();
        batchMovable.end();
    }


    private TextureRegion getBackgroundTexture(BackgroundYio backgroundYio) {
        switch (backgroundYio) {
            default:
            case green:
                return greenBackground;
            case chapaevo:
                return chapaevoBackground;
            case soccer:
                return soccerBackground;
        }
    }


    private void renderDebug() {
        if (!DebugFlags.renderDebug) return;

        GameRendersList instance = GameRendersList.getInstance();

        instance.renderPosMap.render();
//        instance.renderBilliardBoard.renderDebug();
//        instance.renderCollisionLines.render();
//        instance.renderBalls.renderDebug();
//        instance.renderAiPreparation.render();
//        instance.renderSpecificDebugData.render();
    }


    public void render() {
        if (appearFactor.get() < 0.01) {
            return;
        } else if (appearFactor.get() < 1) {
            renderTransitionFrame();
        } else {
            renderInternals();
        }
    }


    void renderTransitionFrame() {
        batchSolid.begin();
        Color c = batchSolid.getColor();
        float cx = w / 2;
        float cy = h / 2;
        float fw = appearFactor.get() * cx;
        float fh = appearFactor.get() * cy;
        batchSolid.setColor(c.r, c.g, c.b, appearFactor.get());
        batchSolid.draw(animationTextureRegion, cx - fw, cy - fh, 2 * fw, 2 * fh);
        batchSolid.setColor(c.r, c.g, c.b, c.a);
        batchSolid.end();
    }


    public void moveFactors() {
        appearFactor.move();
    }


    public boolean coversAllScreen() {
        return appearFactor.get() == 1;
    }


    public boolean isInMotion() {
        return appearFactor.get() > 0 && appearFactor.get() < 1;
    }


    @Override
    public void onLevelCreationEnd() {
        defaultValues();
    }


    @Override
    public void makeExpensiveLoadingStep(int step) {
        if (step == 0) {
            appear();
        }
    }
}
