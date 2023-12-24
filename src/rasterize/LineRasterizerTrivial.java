package rasterize;

import model.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        // y = k * x + q
        float k = (y2 - y1) / (float)(x2 - x1);
        float q = y1 - k * x1;

        for (int x = x1; x <= x2; x++) {
            float y = k * x + q;
            raster.setPixel(x, Math.round(y), this.color.getRGB());
        }

    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        for (Point point : getLinePoints(x1, y1, x2, y2)) {
            this.raster.setPixel((int) point.X(), (int) point.Y(), color.getRGB());
        }
    }

    public void drawDottedLine(int dot_size, double x1, double y1, double x2, double y2, Color color) {
        List<Point> points = getLinePoints(x1, y1, x2, y2);
        int step = 0;
        for (Point point : points) {
            step += 1;
            if (step % (2*dot_size) < dot_size){
                continue;
            }
            raster.setPixel((int) point.X(), (int) point.Y(), this.color.getRGB());
        }
    }

    public List<Point> getLinePoints(double x1, double y1, double x2, double y2){
        return getLinePoints(null, x1, y1, x2, y2);
    }

    public List<Point> getLinePoints(String mode, double x1, double y1, double x2, double y2){

        java.util.List<Point> out = new ArrayList<>();
        double k = (y2 - y1) /(x2-x1);
        double q = y1 - k*x1;

        if ( Objects.equals(mode, "Y") || (Objects.equals(mode, null) && Math.abs(k) > 1)) {
            // Y-axis oriented
            int max_y = (int) Math.max(y1, y2);
            int min_y = (int) Math.min(y1, y2);
            for (int i = min_y; i <= max_y; i++) {
                double temp_x = (i - q) / k;
                if(temp_x != temp_x){
                    // temp_x i NaN
                    temp_x = x1;
                }
                out.add(new Point((int) temp_x, i));
            }
        } else if (Objects.equals(mode, "X") || (Objects.equals(mode, null) && Math.abs(k) < 1)){
            // X-axis oriented
            int max_x = (int) Math.max(x1, x2);
            int min_x = (int) Math.min(x1, x2);
            for (int i = min_x; i <= max_x; i++) {
                double temp_y = k * i + q;
                if(temp_y != temp_y){
                    // temp_x i NaN
                    temp_y = y1;
                }
                out.add(new Point(i, (int) temp_y));
            }
        }
        return out;
    }

    /**Draw solid line without forced y-axis preference. */
    public java.util.List<Point> getLinePoints(int x1, int y1, int x2, int y2){
        return getLinePoints(null, x1, y1, x2, y2);
    }

    public java.util.List<Point> getYOrientedLinePoints(double x1, double y1, double x2, double y2){
        return getLinePoints("Y", x1, y1, x2, y2);
    }
    public List<Point> getXOrientedLinePoints(double x1, double y1, double x2, double y2){
        return getLinePoints("X", x1, y1, x2, y2);
    }
}
