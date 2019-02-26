package yio.tro.shmatoosto.stuff;


import yio.tro.shmatoosto.YioGdxGame;

public abstract class RepeatYio<ProviderType> {

    protected ProviderType parent;
    int frequency, countDown;


    public RepeatYio(ProviderType parent, int frequency) {
        this(parent, frequency, YioGdxGame.random.nextInt(Math.max(1, frequency)));
    }


    public RepeatYio(ProviderType parent, int frequency, int defCount) {
        this.parent = parent;
        this.frequency = frequency;
        countDown = defCount;
    }


    public void move() {
        if (countDown == 0) {
            resetCountDown();
            performAction();
        } else countDown--;
    }


    public void resetCountDown() {
        countDown = frequency;
    }


    public abstract void performAction();


    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

}
