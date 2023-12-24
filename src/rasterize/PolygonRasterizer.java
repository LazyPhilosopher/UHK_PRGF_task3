package rasterize;

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

    public void drawFilledTriangle(Polygon2D polygon, int int_color){
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
                    line_rasterizer.drawLine(x1, y_axis, x2, y_axis, new Color(int_color));
                }
            }
        }
        drawShallowPolygon(polygon, 0x0000FF);
    }

    public void drawShallowPolygon(Polygon2D polygon, int int_color){
        for (Point pixel : get_polygon_pixels(polygon)){raster.setPixel(pixel.X(), pixel.Y(), int_color);}
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
