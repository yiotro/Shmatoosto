package yio.tro.shmatoosto.menu.scenes.options;

import yio.tro.shmatoosto.menu.CustomLanguageLoader;
import yio.tro.shmatoosto.menu.LanguageChooseItem;
import yio.tro.shmatoosto.menu.LanguagesManager;
import yio.tro.shmatoosto.menu.MenuControllerYio;
import yio.tro.shmatoosto.menu.behaviors.Reaction;
import yio.tro.shmatoosto.menu.elements.ButtonYio;
import yio.tro.shmatoosto.menu.elements.scrollable_list.ListItemReaction;
import yio.tro.shmatoosto.menu.elements.scrollable_list.SlyItem;
import yio.tro.shmatoosto.menu.elements.scrollable_list.ScrollableListYio;
import yio.tro.shmatoosto.menu.scenes.SceneYio;

import java.util.ArrayList;

public class SceneLanguages extends SceneYio {

    ScrollableListYio scrollableListYio;
    private ButtonYio topBezel;


    @Override
    public void initialize() {
        setBackground(2);

        initList();

        spawnBackButton(Reaction.rbSettingsMenu);
    }


    private void initList() {
        scrollableListYio = uiFactory.getScrollableList();
        scrollableListYio.setItemBehavior(new ListItemReaction() {
            @Override
            public void reaction(ScrollableListYio scrollableListYio, int index) {
                onListItemClicked(scrollableListYio, index);
            }
        });

        ArrayList<LanguageChooseItem> chooseListItems = LanguagesManager.getInstance().getChooseListItems();

        scrollableListYio.setPosition(0.05, 0.05, 0.9, 0.8);
        scrollableListYio.setActiveArea(0, 0, 1, ScrollableListYio.TOP_BEZEL);

        scrollableListYio.clearItems();
        for (LanguageChooseItem chooseListItem : chooseListItems) {
            scrollableListYio.addListItem(chooseListItem.title, chooseListItem.author, chooseListItem.name);
        }
    }


    private void onListItemClicked(ScrollableListYio scrollableListYio, int index) {
        SlyItem slyItem = scrollableListYio.getListItem(index);
        String key = slyItem.getKey();

        CustomLanguageLoader.setAndSaveLanguage(key);

        menuControllerYio.clear();

        MenuControllerYio.createInitialScene();
    }

}
