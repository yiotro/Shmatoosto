package yio.tro.shmatoosto.menu.scenes.options;

import yio.tro.shmatoosto.SettingsManager;
import yio.tro.shmatoosto.game.view.game_renders.GameRender;
import yio.tro.shmatoosto.game.view.game_renders.GameRendersList;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.CheckButtonYio;
import yio.tro.shmatoosto.menu.elements.ScrollableAreaYio;
import yio.tro.shmatoosto.menu.scenes.SceneYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;
import yio.tro.shmatoosto.stuff.GraphicsYio;

public class SceneSettingsMenu extends SceneYio {

    ButtonYio mainLabel;
    public CheckButtonYio chkSound;
    private ButtonYio langButton;
    private ButtonYio infoButton;
    private ScrollableAreaYio scrollableAreaYio;
    public CheckButtonYio chkFullScreen;
    public CheckButtonYio chkAimAssist;
    public CheckButtonYio chkLowGraphics;
    boolean lowGraphicsValueOnAppear;


    @Override
    public void initialize() {
        setBackground(2);

        createInternals();

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                applyValues();
                SettingsManager.getInstance().saveValues();
                Scenes.mainMenu.create();

                if (SettingsManager.getInstance().requestRestartApp) {
                    Scenes.notification.show("restart_app");
                }
            }
        });
        createInfoButton();
    }


    private void createInternals() {
        scrollableAreaYio = uiFactory.getScrollableAreaYio()
                .setSize(1, 0.85);

        createMainSection();
    }


    @Override
    protected void onAppear() {
        super.onAppear();

        loadValues();
    }


    private void createMainSection() {
        createMainLabel();
        createLangButton();

        initMainChecks();
    }


    private void createInfoButton() {
        double infoWidth = GraphicsYio.convertToWidth(0.07);
        infoButton = uiFactory.getButton()
                .setPosition(0.95 - infoWidth, 0.9, infoWidth)
                .loadTexture("menu/info_icon.png")
                .setBorder(true)
                .setShadow(true)
                .setForcedShadow(true)
                .setAnimation(AnimationYio.none)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        Scenes.aboutMenu.create();
                    }
                });
    }


    private void createLangButton() {
        langButton = uiFactory.getButton()
                .setParent(mainLabel)
                .setSize(0.4, 0.05)
                .centerHorizontal()
                .alignBottom(0.01)
                .applyText("lang")
                .setReaction(Reaction.rbLanguageMenu);
    }


    private void createMainLabel() {
        mainLabel = uiFactory.getButton()
                .setParent(scrollableAreaYio)
                .setSize(0.9, 0.6)
                .alignTop()
                .centerHorizontal()
                .applyText("settings", 10)
                .setTouchable(false)
                .setAnimation(AnimationYio.center);
    }


    private void initMainChecks() {
        chkSound = uiFactory.getCheckButton()
                .setParent(mainLabel)
                .setName("sound")
                .alignTop(0.08)
                .setReaction(new Reaction() {
                    @Override
                    protected void reaction() {
                        SettingsManager.getInstance().soundEnabled = chkSound.isChecked();
                    }
                });

        chkFullScreen = uiFactory.getCheckButton()
                .setParent(mainLabel)
                .setName("full_screen")
                .alignBottom(previousElement);

        chkAimAssist = uiFactory.getCheckButton()
                .setParent(mainLabel)
                .setName("aim_assist")
                .alignBottom(previousElement);

        chkLowGraphics = uiFactory.getCheckButton()
                .setParent(mainLabel)
                .setName("low_graphics")
                .alignBottom(previousElement);
    }


    private void applyValues() {
        SettingsManager instance = SettingsManager.getInstance();
        instance.requestRestartApp = false;

        instance.soundEnabled = chkSound.isChecked();
        instance.fullScreenMode = chkFullScreen.isChecked();
        instance.aimAssistEnabled = chkAimAssist.isChecked();
        instance.lowGraphics = chkLowGraphics.isChecked();
    }


    private void loadValues() {
        SettingsManager instance = SettingsManager.getInstance();

        chkSound.setChecked(instance.soundEnabled);
        chkFullScreen.setChecked(instance.fullScreenMode);
        chkAimAssist.setChecked(instance.aimAssistEnabled);
        chkLowGraphics.setChecked(instance.lowGraphics);

        lowGraphicsValueOnAppear = chkLowGraphics.isChecked();
    }


}
