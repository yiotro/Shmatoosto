package yio.tro.shmatoosto.game;

import yio.tro.shmatoosto.stuff.PointYio;

public interface TouchableYio {


    boolean touchDown(PointYio touchPoint);


    boolean touchDrag(PointYio touchPoint);


    boolean touchUp(PointYio touchPoint);
}
