package yio.tro.shmatoosto.menu.elements.slider;

import yio.tro.shmatoosto.Yio;
import yio.tro.shmatoosto.menu.LanguagesManager;

public class SbTime extends SliderBehavior {

    @Override
    public String getValueString(LanguagesManager languagesManager, SliderYio sliderYio) {
        return Yio.convertTime(sliderYio.getValueIndex());
    }


    @Override
    public void onAnotherSliderValueChanged(SliderYio sliderYio, SliderYio anotherSlider) {

    }


    @Override
    public void onValueChanged(SliderYio sliderYio) {

    }
}
