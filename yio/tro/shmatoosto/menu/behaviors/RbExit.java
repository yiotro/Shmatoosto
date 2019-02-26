package yio.tro.shmatoosto.menu.behaviors;

public class RbExit extends Reaction {

    @Override
    public void reaction() {
        yioGdxGame.exitApp();
    }
}
