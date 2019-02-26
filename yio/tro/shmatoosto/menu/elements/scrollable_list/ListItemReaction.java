package yio.tro.shmatoosto.menu.elements.scrollable_list;

public abstract class ListItemReaction {


    public abstract void reaction(ScrollableListYio scrollableListYio, int index);


    public boolean deleteReaction(ScrollableListYio scrollableListYio, int index) {
        return true;
    }


    public boolean hasIcons() {
        return false;
    }


    public String getItemIconKey(SlyItem slyItem) {
        return null;
    }

}
