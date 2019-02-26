package yio.tro.shmatoosto.game.view.game_renders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.view.GameView;
import yio.tro.shmatoosto.stuff.AtlasLoader;
import yio.tro.shmatoosto.stuff.Storage3xTexture;

public abstract class GameRender {

    protected GameView gameView;
    protected GameController gameController;
    protected SpriteBatch batchMovable, batchSolid;
    float w, h;
    protected AtlasLoader atlasLoader;
    protected AtlasLoader boardAtlas;


    public GameRender() {
        GameRendersList.getInstance().gameRenders.listIterator().add(this);
    }


    void update(GameView gameView) {
        this.gameView = gameView;
        gameController = gameView.gameController;
        batchMovable = gameView.batchMovable;
        batchSolid = gameView.batchSolid;
        w = gameView.w;
        h = gameView.h;
        atlasLoader = gameView.atlasLoader;
        boardAtlas = gameView.boardAtlas;

        loadTextures();
    }


    protected Storage3xTexture load3xTexture(String name) {
        return new Storage3xTexture(atlasLoader, name + ".png");
    }


    protected Storage3xTexture loadBoardTexture(String name) {
        return new Storage3xTexture(boardAtlas, name + ".png");
    }


    public static void updateAllTextures() {
        for (GameRender gameRender : GameRendersList.getInstance().gameRenders) {
            gameRender.loadTextures();
        }
    }


    public static void disposeAllTextures() {
        for (GameRender gameRender : GameRendersList.getInstance().gameRenders) {
            gameRender.disposeTextures();
        }
    }


    protected abstract void loadTextures();


    public abstract void render();


    protected abstract void disposeTextures();


    public SpriteBatch getBatchMovable() {
        return batchMovable;
    }
}
