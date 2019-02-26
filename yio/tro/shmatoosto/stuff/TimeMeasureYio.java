package yio.tro.shmatoosto.stuff;

import yio.tro.shmatoosto.Yio;

public class TimeMeasureYio {


    private static long time1;


    public static void beginMeasure() {
        time1 = System.currentTimeMillis();
    }


    public static void endMeasure(String message) {
        Yio.safeSay(message + ": " + (System.currentTimeMillis() - time1));
        beginMeasure();
    }


    public static void endMeasure() {
        endMeasure("Time taken");
    }

}
