package yio.tro.shmatoosto.game.loading.level_generators;

import yio.tro.shmatoosto.game.GameController;

public class EmptyLevelGenerator extends LevelGenerator{

    public EmptyLevelGenerator(GameController gameController) {
        super(gameController);
    }



    @Override
    protected void createInternals() {
        // nothing
    }

}
