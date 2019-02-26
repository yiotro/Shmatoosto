package yio.tro.shmatoosto.game.tests;

import yio.tro.shmatoosto.game.GameController;
import yio.tro.shmatoosto.game.debug.DebugFlags;

public abstract class AbstractTest {


    GameController gameController;


    public AbstractTest(GameController gameController) {
        this.gameController = gameController;
    }


    public void perform() {
        prepare();
        execute();
        end();
    }


    protected void end() {
        DebugFlags.testingModeEnabled = false;
    }


    protected void prepare() {
        DebugFlags.testingModeEnabled = true;
    }


    protected abstract void execute();
}
