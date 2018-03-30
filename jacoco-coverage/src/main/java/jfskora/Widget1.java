package jfskora;

/**
 * Hello world!
 *
 */
public class Widget1
{
    Long twelve;
    Long sixtyTwo;
    Double factor;

    public Widget1() {
        twelve = 12L;
        sixtyTwo = 62L;
        factor = sixtyTwo.doubleValue() / twelve;
    }

    public Long getTwelve() {
        return twelve;
    }

    public Long getSixtyTwo() {
        return sixtyTwo;
    }

    public Double getFactor() {
        return factor;
    }
}
