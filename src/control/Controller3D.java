package control;

import model.*;
import model.Point;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import rasterize.Raster;
import renderer.WiredRenderer;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Controller3D implements Controller {
    private final Panel panel;

    private LineRasterizer line_rasterizer;
    private PolygonRasterizer polygon_rasterizer;
    private WiredRenderer renderer;
    private float elapsed_time = 0;
    private boolean in_progress = false;

    private Vec3D camera_position_vector = new Vec3D( 4.3,-1.2,11.1);
    private Vec3D look_direction = new Vec3D(0,0,1);
    private Vec3D light_direction = new Vec3D(1,1,0);
    private Vec3D scene_up_vector = new Vec3D(0,1,0);

    private float azimuth = (float) -3.5;
    private Mat4 proj;

    //    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\VideoShip.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\teapot.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\axis.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\tie_fighter.obj");
    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\mountains.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\mountains.obj");

    Mesh cube = new Mesh(new ArrayList<>(Arrays.asList(
                new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1)),

                new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1)),

                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1)),

                new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1)),

                new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1)),

                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0), new Vec2D(0,1), new Vec2D(0,0), new Vec2D(1,0)),
                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Vec2D(0,1), new Vec2D(1,0), new Vec2D(1,1))
                ))
    );

    public Controller3D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);

        update();
        setLoop();
    }

    public void initObjects(Raster raster) {
        line_rasterizer = new LineRasterizerGraphics(raster);
        polygon_rasterizer = new PolygonRasterizer(raster);
        renderer = new WiredRenderer(line_rasterizer, polygon_rasterizer);

//        camera = new Camera(
//          new Vec3D(0, -1, 0.3),
//          Math.toRadians(90),
//          Math.toRadians(-15),
//          1,
//          true
//        );

        proj = new Mat4PerspRH(
                Math.PI / 4,
                raster.getHeight() / (double)raster.getWidth(),
                0.1,
                20
        );
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    // rasterizer.rasterize(x, y, e.getX(),e.getY(), Color.RED);
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                }

                update();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        //TODO
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        //TODO
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                panel.clear();

                line_rasterizer.rasterize(
                        panel.getRaster().getWidth() / 2,
                        panel.getRaster().getHeight() / 2,
                        e.getX(),
                        e.getY(),
                        Color.YELLOW
                );

                panel.repaint();

                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                }
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    //TODO
                }else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    camera_position_vector = camera_position_vector.add(look_direction.mul(1));
                }else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    camera_position_vector = camera_position_vector.sub(look_direction.mul(1));
                }else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    azimuth -= 0.01;
                }else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    azimuth += 0.01;
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera_position_vector.addY(.1);
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera_position_vector.addY(-.1);
                }
                System.out.println(azimuth);
                System.out.println(camera_position_vector);

            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private void update() {
        in_progress = true;
        panel.clear();

        renderer.setProj(proj);
        look_direction = new Mat4RotY(azimuth).Multiply3DVector(new Vec3D(0,0,1));
        look_direction.normSelf();


        Vec3D camera_look_direction_vector = camera_position_vector.add(look_direction);
        Mat4PointAt camera_matrix = new Mat4PointAt(camera_position_vector, camera_look_direction_vector, scene_up_vector);
        Mat4 view_matrix = camera_matrix.Mat4QuickInverse();

        Mat4Proj proj_matrix = new Mat4Proj();

        Mat4 shift_matrix = new Mat4Identity();
//        shift_matrix = shift_matrix.mul(new Mat4RotX(Math.PI));
//        shift_matrix = shift_matrix.mul(new Mat4RotY(Math.PI-elapsed_time));
//        shift_matrix = shift_matrix.mul(new Mat4RotZ(elapsed_time));
//        shift_matrix = shift_matrix.mul(new Mat4Transl(new Vec3D(0,0,0)));


        ArrayList<Triangle3D> polygons = new ArrayList<>();
        for (Triangle3D tri : cube.polygons){

            // translate and rotate origin triangle
            Triangle3D shifted_triangle = shift_matrix.Multiply3DTriangle(tri);
            shifted_triangle.t1 = tri.t1; shifted_triangle.t2 = tri.t2; shifted_triangle.t3 = tri.t3;
            Vec3D norm = shifted_triangle.calculateNorm();
//            norm.normSelf();

            Vec3D sight_vector = new Vec3D();
            sight_vector.setX(shifted_triangle.a.getX() - camera_position_vector.getX());
            sight_vector.setY(shifted_triangle.a.getY() - camera_position_vector.getY());
            sight_vector.setZ(shifted_triangle.a.getZ() - camera_position_vector.getZ());

            // transform triangle to form viewed from camera perspective
            Triangle3D triangle_view = view_matrix.Multiply3DTriangle(shifted_triangle);
            triangle_view.color = shifted_triangle.color;
            triangle_view.t1 = shifted_triangle.t1; triangle_view.t2 = shifted_triangle.t2; triangle_view.t3 = shifted_triangle.t3;

            // clip polygons too close in front of camera
            List<Triangle3D> far_enough_clipped_triangles = triangleClipAgainstPlane(new Vec3D(0,0,10), new Vec3D(0,0,1), triangle_view);

            // clip polygons too far in front of camera
            List<Triangle3D> clipped_triangles = new ArrayList<>();
            for(Triangle3D triangle : far_enough_clipped_triangles){
                clipped_triangles.addAll(triangleClipAgainstPlane(new Vec3D(0,0,500), new Vec3D(0,0,-1), triangle));
            }

            for(Triangle3D clipped_triangle : clipped_triangles){
                // project triangle to 2D space (Z-axis used for distance measurement)
                Triangle3D projected_triangle =  proj_matrix.Multiply3DTriangle(clipped_triangle);
                projected_triangle.setNorm(norm);
                projected_triangle.color = clipped_triangle.color;
                projected_triangle.t1 = clipped_triangle.t1; projected_triangle.t2 = clipped_triangle.t2; projected_triangle.t3 = clipped_triangle.t3;

                // visible triangle norm shall aim outside the mesh
                if(projected_triangle.norm.dotProduct(sight_vector) >= 0){
                    continue;
                }
                else {
                    polygons.add(projected_triangle);
                }
            }

        }
        // Sort the list based on the Z attribute
        polygons.sort((tri1, tri2) -> Double.compare(tri2.a.getZ() + tri2.b.getZ() + tri2.c.getZ(), tri1.a.getZ() + tri1.b.getZ() + tri1.c.getZ()));

        // clip polygons by four field-of-view planes
        for(Triangle3D rasterized_triangle : polygons){
            List<Triangle3D> clipped = new ArrayList<>();
            List<Triangle3D> listTriangles = new ArrayList<>();

            // Add initial triangle
            listTriangles.add(rasterized_triangle);
            int nNewTriangles = 1;

            for (int p = 0; p < 4; p++)
            {
                while (nNewTriangles > 0)
                {
                    // Take triangle from front of queue
                    Triangle3D test = listTriangles.get(0);
                    listTriangles.remove(0);
                    nNewTriangles--;

                    switch (p) {
                        case 0 -> clipped = triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, 1, -0.09), test);
                        case 1 -> clipped = triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(0, -1, -0.04), test);
                        case 2 -> clipped = triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(1, 0, -0.08), test);
                        case 3 -> clipped = triangleClipAgainstPlane(new Vec3D(0, 0, 0), new Vec3D(-1, 0, -0.15), test);
                    }
                    // Clipping may yield a variable number of triangles, so
                    // add these new ones to the back of the queue for subsequent
                    // clipping against next planes
                    listTriangles.addAll(clipped);
                }
                nNewTriangles = listTriangles.size();
            }

            // draw clipped triangles
            for (Triangle3D triangle : listTriangles)
            {
                Triangle2D triangle_2D_projected = new Triangle2D(triangle);
                triangle_2D_projected.shift_XY(1.0, 1.0);
                triangle_2D_projected.mul_XY(0.5 * panel.getHeight(), 0.5 * panel.getWidth());

                light_direction.normSelf();
                double light_amount = (light_direction.dotProduct(rasterized_triangle.norm)/2.0001+0.5);
                Color color = new Color((int)(rasterized_triangle.color.getRed()*light_amount),
                        (int)(rasterized_triangle.color.getGreen()*light_amount),
                        (int)(rasterized_triangle.color.getBlue()*light_amount));

                Polygon2D polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                        new Vec2D((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y),
                        new Vec2D((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y),
                        new Vec2D((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y)
                )),
                        color);
                Polygon2D texture_polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                        new Vec2D(triangle.t1.getX(), triangle.t1.getY()),
                        new Vec2D(triangle.t2.getX(), triangle.t2.getY()),
                        new Vec2D(triangle.t3.getX(), triangle.t3.getY())
                )),
                        new Color(0x0000FF));
                PNGSprite sprite = new PNGSprite("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\creeper.png");
                renderer.polygonRasterizer.drawTexturedTriangle(polygon, texture_polygon, sprite);
            }
        }

        panel.repaint();


        in_progress = false;
    }

    HashMap<String, Object> vectorIntersectPlane(Vec3D plane_point, Vec3D plane_normal, Vec3D line_start, Vec3D line_end){
        HashMap<String, Object> output = new HashMap<>();
        plane_normal.normSelf();
        double plane_d = -plane_point.dotProduct(plane_normal);
        double ad = plane_normal.dotProduct(line_start);
        double bd = plane_normal.dotProduct(line_end);
        double t = (-plane_d - ad) / (bd - ad);
        Vec3D line_start_to_end = line_end.sub(line_start);
        Vec3D line_to_intersect = line_start_to_end.mul(t);
        output.put("vector", line_start.add(line_to_intersect));
        output.put("t", t);
        return output;
    }

    double pointPlaneDistance(Vec3D plane_normal, Vec3D plane_point, Vec3D point){
        plane_normal.normSelf();
        return (plane_normal.dotProduct(point) - plane_point.dotProduct(plane_normal));
    }

    List<Triangle3D> triangleClipAgainstPlane(Vec3D plane_point, Vec3D plane_normal, Triangle3D input_triangle){
        plane_normal.normSelf();
        double d0 = pointPlaneDistance(plane_normal, plane_point, input_triangle.a);
        double d1 = pointPlaneDistance(plane_normal, plane_point, input_triangle.b);
        double d2 = pointPlaneDistance(plane_normal, plane_point, input_triangle.c);
        List<Vec3D> inside_points = new ArrayList<>();
        List<Vec3D> outside_points = new ArrayList<>();
        List<Vec2D> inside_textures = new ArrayList<>();
        List<Vec2D> outside_textures = new ArrayList<>();

        // Put input triangle coordinates either to inside or outside lists.
        if(d0 > 0){
            inside_points.add(input_triangle.a); inside_textures.add(input_triangle.t1);
        } else {
            outside_points.add(input_triangle.a); outside_textures.add(input_triangle.t1);}
        if(d1 > 0){
            inside_points.add(input_triangle.b); inside_textures.add(input_triangle.t2);
        } else {
            outside_points.add(input_triangle.b); outside_textures.add(input_triangle.t2);}
        if(d2 > 0){
            inside_points.add(input_triangle.c); inside_textures.add(input_triangle.t3);
        } else {
            outside_points.add(input_triangle.c); outside_textures.add(input_triangle.t3);
        }

        List<Triangle3D> output = new ArrayList<>();
        if(inside_points.size() == 3){
            output.add(input_triangle);
            // return input triangle
        }
        else if (outside_points.size() == 3){
            // do nothing
        }
        else if(inside_points.size() == 1 && outside_points.size() == 2){
            Triangle3D out1 = new Triangle3D();
//            out1.color = new Color(0x0000FF);
            out1.color = input_triangle.color;

            out1.a = inside_points.get(0);
            out1.t1 = inside_textures.get(0);

            double t, new_texture_x, new_texture_y;

            HashMap<String, Object> out1_b_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(0));
            out1.b = (Vec3D) out1_b_clipped.get("vector");
            t = (double) out1_b_clipped.get("t");
            new_texture_x = t*(outside_textures.get(0).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
            new_texture_y = t*(outside_textures.get(0).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
            out1.t2 = new Vec2D(new_texture_x, new_texture_y);

            HashMap<String, Object> out1_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(1));
            out1.c = (Vec3D) out1_c_clipped.get("vector");
            t = (double) out1_c_clipped.get("t");
            new_texture_x = t*(outside_textures.get(1).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
            new_texture_y = t*(outside_textures.get(1).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
            out1.t3 = new Vec2D(new_texture_x, new_texture_y);

            output.add(out1);
        }
        else if(inside_points.size() == 2 && outside_points.size() == 1){
            Triangle3D out1 = new Triangle3D();
            Triangle3D out2 = new Triangle3D();
            out1.color = input_triangle.color;
            out2.color = input_triangle.color;
//            out1.color = new Color(0xFF0000);
//            out2.color = new Color(0x00FF00);

            out1.a = inside_points.get(0);
            out1.b = inside_points.get(1);
            out1.t1 = inside_textures.get(0);
            out1.t2 = inside_textures.get(1);

            double t, new_texture_x, new_texture_y;

            HashMap<String, Object> out2_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(0));
            out1.c = (Vec3D) out2_c_clipped.get("vector");
            t = (double) out2_c_clipped.get("t");
            new_texture_x = t*(outside_textures.get(0).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
            new_texture_y = t*(outside_textures.get(0).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
            out1.t3 = new Vec2D(new_texture_x, new_texture_y);

            out2.a = inside_points.get(1);
            out2.t1 = inside_textures.get(1);
            out2.b = out1.c;
            out2.t2 = out1.t3;

            HashMap<String, Object> out1_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.b, outside_points.get(0));
            out2.c = (Vec3D) out1_c_clipped.get("vector");
            t = (double) out1_c_clipped.get("t");
            new_texture_x = t*(outside_textures.get(0).getX() - inside_textures.get(1).getX()) + inside_textures.get(1).getX();
            new_texture_y = t*(outside_textures.get(0).getY() - inside_textures.get(1).getY()) + inside_textures.get(1).getY();
            out2.t3 = new Vec2D(new_texture_x, new_texture_y);

            output.add(out1);
            output.add(out2);
        }
        return output;
    }

    private void hardClear() {
        panel.clear();
    }

    private void setLoop() {
        // časovač, který N krát za vteřinu obnoví obsah plátna aktuálním img
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                elapsed_time += 0.01;
                if (in_progress){
//                    System.out.println("in_progress");
                    return;
                }
//                System.out.println("not in_progress");
                update();
            }
        }, 0, 50);
    }
}
