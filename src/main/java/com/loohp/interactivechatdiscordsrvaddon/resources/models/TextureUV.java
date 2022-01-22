package com.loohp.interactivechatdiscordsrvaddon.resources.models;

public class TextureUV {

    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public TextureUV(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public TextureUV getScaled(double scale) {
        return new TextureUV(x1 * scale, y1 * scale, x2 * scale, y2 * scale);
    }

    public TextureUV getScaled(double scaleX, double scaleY) {
        return new TextureUV(x1 * scaleX, y1 * scaleY, x2 * scaleX, y2 * scaleY);
    }

    public double getXDiff() {
        return x2 - x1;
    }

    public double getYDiff() {
        return y2 - y1;
    }

    public boolean isVerticallyFlipped() {
        return getYDiff() < 0;
    }

    public boolean isHorizontallyFlipped() {
        return getXDiff() < 0;
    }

}
