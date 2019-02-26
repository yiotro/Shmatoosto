package yio.tro.shmatoosto.menu.scenes.info;

import yio.tro.shmatoosto.Fonts;
import yio.tro.shmatoosto.menu.LanguageChooseItem;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.AnimationYio;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.scenes.Scenes;

import java.util.ArrayList;

public class SceneSpecialThanks extends AbstractInfoScene {


    private ButtonYio infoPanel;


    @Override
    public void initialize() {
        setBackground(1);

        spawnBackButton(new Reaction() {
            @Override
            protected void reaction() {
                Scenes.settingsMenu.create();
            }
        });

        String translatorsString = getTranslatorsString();
        String topic = languagesManager.getString("special_thanks_begin") +
                languagesManager.getString("special_thanks_people_eng") +
                translatorsString + "a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a a ";

        infoPanel = uiFactory.getButton()
                .setPosition(0.05, 0.1, 0.9, 0.7)
                .setReaction(Reaction.rbNothing)
                .setAnimation(AnimationYio.center)
                .setFont(Fonts.miniFont)
                .clearText()
                .applyFixedAmountOfLines(topic, 16);
    }


    private String getTranslatorsString() {
        ArrayList<LanguageChooseItem> chooseListItems = LanguagesManager.getInstance().getChooseListItems();

        StringBuilder builder = new StringBuilder();

        for (LanguageChooseItem chooseListItem : chooseListItems) {
            if (chooseListItem.author.equals("yiotro")) continue;

            builder.append("#").append(chooseListItem.name).append(": ").append(chooseListItem.author);
        }

        return builder.toString();
    }
}
