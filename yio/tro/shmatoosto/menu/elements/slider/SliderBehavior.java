package yio.tro.shmatoosto.menu.elements.slider;

import yio.tro.shmatoosto.menu.LanguagesManager;

public abstract class SliderBehavior {


    public abstract String getValueString(LanguagesManager languagesManager, SliderYio sliderYio);


    public void onAnotherSliderValueChanged(SliderYio sliderYio, SliderYio anotherSlider) {

    }


    public void onValueChanged(SliderYio sliderYio) {

    }
}
