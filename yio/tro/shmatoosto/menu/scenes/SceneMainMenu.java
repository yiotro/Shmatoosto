package yio.tro.shmatoosto.menu.scenes;

import yio.tro.shmatoosto.SoundManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CircleButtonYio;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class SceneMainMenu extends SceneYio {


    public static final int MM_BACKGROUND = 1;
    public CircleButtonYio exitButton;
    public CircleButtonYio settingsButton;
    public CircleButtonYio playButton;
    private double verticalPosition;
    private double iconSize;
    private double playButtonSize;
    private ButtonYio logoButton;
    private double logoWidth;
    private double iconOffset;
    private double touchOffset;
    int secretCount;


    @Override
    public void initialize() {
        setBackground(MM_BACKGROUND);

        verticalPosition = 0.4;
        iconSize = 0.16;
        playButtonSize = 0.32;
        logoWidth = 0.6;
        iconOffset = 0.07;
        touchOffset = 0.05;
        secretCount = 0;

        exitButton = uiFactory.getCircleButton()
                .setSize(iconSize)
                .alignBottom(verticalPosition - GraphicsYio.convertToHeight(iconSize) / 2)
                .alignRight(iconOffset)
                .setTouchOffset(touchOffset)
                .loadTexture("menu/main_menu/quit_icon.png")
                .setReaction(Reaction.rbExit)
                .setAnimation(AnimationYio.horizontal_center)
                .tagAsBackButton();

        settingsButton = uiFactory.getCircleButton()
                .loadTexture("menu/main_menu/settings_icon.png")
                .clone(exitButton)
                .alignBottom(verticalPosition - GraphicsYio.convertToHeight(iconSize) / 2)
                .alignLeft(iconOffset)
                .setReaction(Reaction.rbSettingsMenu);

        playButton = uiFactory.getCircleButton()
                .loadTexture("menu/main_menu/play_button.png")
                .setPosition((1 - playButtonSize) / 2, verticalPosition - GraphicsYio.convertToHeight(playButtonSize) / 2, playButtonSize)
                .setTouchOffset(touchOffset)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        onPlayButtonPressed();
                    }
                });

        logoButton = uiFactory.getButton()
                .setSize(logoWidth, GraphicsYio.convertToHeight(logoWidth) / 2)
                .centerHorizontal()
                .alignBottom(0.5)
                .loadTexture("menu/main_menu/mm_logo.png")
                .setSilentReactionMode(true)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        secretCount += 120;

                        if (secretCount > 300) {
                            Scenes.secretScreen.create();
                            SoundManager.playSound(SoundManager.button);
                        }
                    }
                })
                .setAnimation(AnimationYio.center);

        createSecretButton();
    }


    @Override
    protected void onAppear() {
        super.onAppear();
        secretCount = 0;
    }


    @Override
    public void move() {
        super.move();

        if (secretCount > 0) {
            secretCount--;
        }
    }


    private void onPlayButtonPressed() {
        Scenes.chooseGameMode.create();
    }


    private void createSecretButton() {
        ButtonYio secretButton = uiFactory.getButton()
                .setPosition(0.95, 1 - GraphicsYio.convertToHeight(0.05), 0.05)
                .loadTexture("pixels/empty.png")
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {

                    }
                });
    }
}
