package rasterize;

import control.PNGSprite;
import model.Point;
import model.Polygon2D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PolygonRasterizer extends LineRasterizer {
    LineRasterizerTrivial line_rasterizer;
    public PolygonRasterizer(Raster raster){
        super(raster);
        line_rasterizer = new LineRasterizerTrivial(this.raster);
    }

    public void drawFilledPolygon(Polygon2D polygon, int int_color) {
        List<Point> polygon_vertices = new ArrayList<>(polygon.getVertices());
        polygon_vertices.add(polygon_vertices.get(0));

        // getting polygon Y-axis oriented border pixels
        // no stacked pixels on Y axis
        List<Point> border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getYOrientedLinePoints(a.X(), a.Y(), b.X(), b.Y()));
            border_pixels.remove(b);
        }
        Map<Integer, List<Integer>> pixels_on_y_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_y_axis.containsKey(pixel.Y())){
                pixels_on_y_axis.put(pixel.Y(), new ArrayList<>());
            }
            pixels_on_y_axis.get(pixel.Y()).add(pixel.X());
        }

        for (int y : pixels_on_y_axis.keySet()){
            Collections.sort(pixels_on_y_axis.get(y));
        }

        // ---------------------------------------------------
        // getting polygon X-axis oriented border pixels
        // no stacked pixels on X axis
        border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getXOrientedLinePoints(a.X(), a.Y(), b.X(), b.Y()));
        }
        Map<Integer, List<Integer>> pixels_on_x_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_x_axis.containsKey(pixel.X())){
                pixels_on_x_axis.put(pixel.X(), new ArrayList<>());
            }

            pixels_on_x_axis.get(pixel.X()).add(pixel.Y());

        }
        for (int x : pixels_on_x_axis.keySet()){
            Collections.sort(pixels_on_x_axis.get(x));
        }
        // ---------------------------------------------------

        for (Integer y_axis : pixels_on_y_axis.keySet()){

            if (pixels_on_y_axis.get(y_axis).size() > 0){
                for (int idx = 1; idx < pixels_on_y_axis.get(y_axis).size(); idx += 1){
                    int x1 = pixels_on_y_axis.get(y_axis).get(idx-1);
                    int x2 = pixels_on_y_axis.get(y_axis).get(idx);

                    int mid_x = (x1+x2)/2;

                    if(pixels_on_x_axis.get(mid_x) == null){
                        continue;
                    }

                    Map<Boolean, List<Integer>> XpartitionedLists = pixels_on_x_axis.get(mid_x).stream()
                            .collect(Collectors.partitioningBy(value -> value < y_axis));

                    List<Integer> smallerThanKeyList = XpartitionedLists.get(true);
                    List<Integer> greaterOrEqualToKeyList = XpartitionedLists.get(false);

                    Map<Boolean, List<Integer>> YpartitionedLists = pixels_on_y_axis.get(y_axis).stream()
                            .collect(Collectors.partitioningBy(value -> value < mid_x));

                    List<Integer> smallerThanMidXList = YpartitionedLists.get(true);
                    List<Integer> greaterOrEqualToMidXList = YpartitionedLists.get(false);


                    if(smallerThanMidXList.size()%2 == 1 && greaterOrEqualToMidXList.size()%2 == 1){
                        for (Point point : getLinePoints(x1, y_axis, x2, y_axis)) {
                            raster.setPixel(point.X(), point.Y(), int_color);
                        }
                    }
                    if(smallerThanKeyList.size()%2 == 1 && greaterOrEqualToKeyList.size()%2 == 1){
                        for (Point point : getLinePoints(x1, y_axis, x2, y_axis)) {
                            raster.setPixel(point.X(), point.Y(), int_color);
                        }
                    }
                }
            }
        }
        drawShallowPolygon(polygon, 0xFFFFFF);
    }

    public void drawFilledTriangle(Polygon2D polygon, Color color){
        List<Point> polygon_vertices = new ArrayList<>(polygon.getVertices());
        polygon_vertices.add(polygon_vertices.get(0));

        // getting polygon Y-axis oriented border pixels
        // no stacked pixels on Y axis
        List<Point> border_pixels = new ArrayList<>();
        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            border_pixels.addAll(getYOrientedLinePoints(a.X(), a.Y(), b.X(), b.Y()));
            border_pixels.remove(b);
        }
        Map<Integer, List<Integer>> pixels_on_y_axis = new HashMap<>();
        for (Point pixel : border_pixels){
            if (!pixels_on_y_axis.containsKey(pixel.Y())){
                pixels_on_y_axis.put(pixel.Y(), new ArrayList<>());
            }
            pixels_on_y_axis.get(pixel.Y()).add(pixel.X());
        }

//        for (int y : pixels_on_y_axis.keySet()){
//            Collections.sort(pixels_on_y_axis.get(y));
//        }

        for (Integer y_axis : pixels_on_y_axis.keySet()){
            if (pixels_on_y_axis.get(y_axis).size() > 0){
                for (int idx = 1; idx < pixels_on_y_axis.get(y_axis).size(); idx += 1){
                    int x1 = pixels_on_y_axis.get(y_axis).get(idx-1);
                    int x2 = pixels_on_y_axis.get(y_axis).get(idx);
                    line_rasterizer.drawLine(x1, y_axis, x2, y_axis, color);
                }
            }
        }
        drawShallowPolygon(polygon, 0x0000FF);
    }

    public void drawShallowPolygon(Polygon2D polygon, int int_color){
        for (Point pixel : get_polygon_pixels(polygon)){raster.setPixel(pixel.X(), pixel.Y(), int_color);}
    }

    public void drawTexturedTriangle(Polygon2D screen_polygon, Polygon2D texture_polygon, PNGSprite sprite){
        List<Point> screen = screen_polygon.getVertices();
        List<Point> texture = texture_polygon.getVertices();

        List<List<Integer>> coordinates = new ArrayList<>();
        coordinates.add(List.of(screen.get(0).X(), screen.get(0).Y(), texture.get(0).X(), texture.get(0).Y()));
        coordinates.add(List.of(screen.get(1).X(), screen.get(1).Y(), texture.get(1).X(), texture.get(1).Y()));
        coordinates.add(List.of(screen.get(2).X(), screen.get(2).Y(), texture.get(2).X(), texture.get(2).Y()));
        // Sort the list by y coordinate in ascending order
        coordinates.sort(Comparator.comparingDouble(list -> list.get(1)));

        double screen_x1 = coordinates.get(0).get(0); double screen_y1 = coordinates.get(0).get(1);
        double texture_x1 = coordinates.get(0).get(2); double texture_y1 = coordinates.get(0).get(3);
        double screen_x2 = coordinates.get(1).get(0); double screen_y2 = coordinates.get(1).get(1);
        double texture_x2 = coordinates.get(1).get(2); double texture_y2 = coordinates.get(1).get(3);
        double screen_x3 = coordinates.get(2).get(0); double screen_y3 = coordinates.get(2).get(1);
        double texture_x3 = coordinates.get(2).get(2); double texture_y3 = coordinates.get(2).get(3);


        double screen_dy1 = screen_y2 - screen_y1; double screen_dx1 = screen_x2 - screen_x1;
        double texture_dy1 = texture_y2 - texture_y1; double texture_dx1 = texture_x2 - texture_x1;
        double screen_dy2 = screen_y3 - screen_y1; double screen_dx2 = screen_x3 - screen_x1;
        double texture_dy2 = texture_y3 - texture_y1; double texture_dx2 = texture_x3 - texture_x1;

        double  dax_step,  dbx_step, texture_dx1_step, texture_dy1_step,
                texture_dx2_step, texture_dy2_step, texture_x, texture_y;
        if(screen_dy1 != 0){dax_step = screen_dx1 / screen_dy1; } else {dax_step = 0;}
        if(screen_dy2 != 0){dbx_step = screen_dx2 / screen_dy2; } else {dbx_step = 0;}
        if(screen_dy1 != 0){texture_dx1_step = texture_dx1 / screen_dy1; } else {texture_dx1_step = 0;}
        if(screen_dy1 != 0){texture_dy1_step = texture_dy1 / screen_dy1; } else {texture_dy1_step = 0;}
        if(screen_dy2 != 0){texture_dx2_step = texture_dx2 / screen_dy2; } else {texture_dx2_step = 0;}
        if(screen_dy2 != 0){texture_dy2_step = texture_dy2 / screen_dy2; } else {texture_dy2_step = 0;}


        for (double i = screen_y1; i < screen_y2; i++){
            // int ax = x1 + (float)(i - y1) * dax_step;
            // int bx = x1 + (float)(i - y1) * dbx_step;
            double ax = screen_x1 + (i-screen_y1) * dax_step;
            double bx = screen_x1 + (i-screen_y1) * dbx_step;

            double tex_sx = texture_x1 + (i-screen_y1) * texture_dx1_step;
            double tex_ex = texture_x1 + (i-screen_y1) * texture_dx2_step;

            double tex_sy = texture_y1 + (i-screen_y1) * texture_dy1_step;
            double tex_ey = texture_y1 + (i-screen_y1) * texture_dy2_step;

//            if(ax > bx){
//                double temp;
//                temp = ax; ax = bx; bx = temp;
//                temp = tex_sx; tex_sx = tex_ex; tex_ex = temp;
//                temp = tex_sy; tex_sy = tex_ey; tex_ey = temp;
//            }

            texture_x = tex_sx;
            texture_y = tex_sy;

            double tstep = 1 / (bx - ax);
            double t = 0;

            for (double j = ax; j < bx; j++){
                texture_x = (1-t) * tex_sx + t * tex_ex;
                texture_y = (1-t) * tex_sy + t * tex_ey;

//                double test_y = tex_sy + ((ax-j)/bx) * (tex_sy - tex_ex);
//                double test_x = tex_sx + ((j-ax)/bx) * (tex_sx - tex_ey);
                double test_x = tex_sx + ((ax-j)/bx) * (tex_ex - tex_sx);
                double test_y = tex_sy + ((i-screen_y1)/screen_y2) * (tex_ey - tex_sy);
//                System.out.printf("%f %f%n", texture_x, texture_y);
                raster.setPixel((int)j, (int)i, sprite.getColour(texture_x, texture_y));
                t += tstep;
            }
        }
        Polygon2D polygon = new Polygon2D(Arrays.asList(new Point(screen_x1, screen_y1), new Point(screen_x2, screen_y1), new Point(screen_x2, screen_y2)), new Color(0xFF0000));
//        drawShallowPolygon(polygon, 0xFF0000);

        screen_dy1 = screen_y3 - screen_y2;
        screen_dx1 = screen_x3 - screen_x2;
        texture_dx1 = texture_x3 - texture_x2;
        texture_dy1 = texture_y3 - texture_y2;

        if(screen_dy1 != 0){ dax_step = screen_dx1 / screen_dy1; }
        if(screen_dy2 != 0){ dbx_step = screen_dx2 / screen_dy2; }

        texture_dx1_step = 0; texture_dy1_step = 0;
        if(screen_dx1 != 0){ texture_dx1_step = texture_dx1 / screen_dy1; }
        if(screen_dy1 != 0){ texture_dy1_step = texture_dy1 / screen_dy1; }

        for (double i = screen_y2; i < screen_y3; i++){
            double ax = screen_x2 + (i-screen_y2) * dax_step;
            double bx = screen_x1 + (i-screen_y1) * dbx_step;

            double tex_sx = texture_x2 + (i-screen_y2) * texture_dx1_step;
            double tex_sy = texture_y2 + (i-screen_y2) * texture_dy1_step;

            double tex_ex = texture_x1 + (i-screen_y1) * texture_dx2_step;
            double tex_ey = texture_y1 + (i-screen_y1) * texture_dy2_step;

            if(ax > bx){
                double temp;
                temp = ax; ax = bx; bx = temp;
                temp = tex_sx; tex_sx = tex_ex; tex_ex = temp;
                temp = tex_sy; tex_sy = tex_ey; tex_ey = temp;
            }

            texture_x = tex_sx;
            texture_y = tex_sy;

            double tstep = 1 / (bx - ax);
            double t = 0;

            for (double j = ax; j < bx; j++){
                texture_x = (1-t) * tex_sx + t * tex_ex;
                texture_y = (1-t) * tex_sy + t * tex_ey;


                raster.setPixel((int)j, (int)i, sprite.getColour(texture_x, texture_y));
                t += tstep;
            }
        }
        polygon = new Polygon2D(Arrays.asList(new Point(screen_x3, screen_y3), new Point(screen_x2, screen_y3), new Point(screen_x2, screen_y2)), new Color(0xFF0000));
        drawShallowPolygon(polygon, 0x00FF00);

    }


    public List<Point> get_polygon_pixels(Polygon2D polygon){
        List<Point> out = new ArrayList<>();

        List<Point> polygon_vertices = new ArrayList<>(polygon.getVertices());
        polygon_vertices.add(polygon_vertices.get(0));

        for (int i = 0; i < polygon_vertices.size()-1; i++){
            Point a = polygon_vertices.get(i);
            Point b = polygon_vertices.get(i+1);
            List<Point> edge_points = getLinePoints(a, b);
            for(Point point : edge_points){
               // point.addRelatedStruct(a);
//                point.addRelatedStruct(b);
//                point.addRelatedStruct(polygon);
            }
            out.addAll(edge_points);
        }
        return out;
    }

}
