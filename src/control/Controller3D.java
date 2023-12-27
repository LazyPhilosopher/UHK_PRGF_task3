package control;

import model.*;
import model.Point;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import rasterize.Raster;
import renderer.WiredRenderer;
import solid.Cube;
import solid.Solid;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

public class Controller3D implements Controller {
    private final Panel panel;

    private LineRasterizer line_rasterizer;
    private PolygonRasterizer polygon_rasterizer;
    private WiredRenderer renderer;
    private float elapsed_time = 0;
    private boolean in_progress = false;

    private Vec3D camera = new Vec3D(0,0,0);
    private Vec3D look_direction = new Vec3D(0,0,1);
    private Vec3D light_direction = new Vec3D(1,1,0);
    private float azimuth = 0;
    private Mat4 proj;

    Mesh cube = new Mesh(new ArrayList<>(Arrays.asList(
            new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0), new Color(0xFF0000)),
            new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0), new Color(0xFF0000)),

            new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1), new Color(0xFF0000)),
            new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1), new Color(0xFF0000)),

            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Color(0x00FF00)),
            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1), new Color(0x00FF00)),

            new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0), new Color(0x00FF00)),
            new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0), new Color(0x00FF00)),

            new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1), new Color(0x0000FF)),
            new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0), new Color(0x0000FF)),

            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0), new Color(0x0000FF)),
            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Color(0x0000FF))
    )));

//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\VideoShip.obj");
    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\teapot.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\tie_fighter.obj");
//    Mesh tie = new Mesh("C:\\Users\\Call_me_Utka\\Desktop\\PGRF-1\\UHK_PRGF_task3\\src\\blender\\mountains.obj");
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
                    camera.setZ(camera.getZ() + 8);
                }else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    camera.setZ(camera.getZ() - 8);
                }else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    camera.setX(camera.getX() - 8);
                }else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    camera.setX(camera.getX() + 8);
                }else if (e.getKeyCode() == KeyEvent.VK_E) {
                    azimuth += 0.1;
                }else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    azimuth -= 0.1;
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.add(look_direction.mul(8));
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.sub(look_direction.mul(8));
                }
                System.out.println(azimuth*180/Math.PI);
                System.out.println(new Mat4RotY(azimuth).Multiply3DVector(new Vec3D(0,0,1)));

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
//        renderer.setView(camera.getViewMatrix());

//        Solid cube = new Cube();
//        cube.setModel(new Mat4Transl(1, 0, 0));
        look_direction = new Mat4RotY(azimuth).Multiply3DVector(new Vec3D(0,0,1));
        look_direction.normSelf();

//        look_direction = new Vec3D(0.02,0.02,1);
        Vec3D up = new Vec3D(0,1,0);
        Vec3D target = camera.add(look_direction);
        Mat4PointAt camera_matrix = new Mat4PointAt(camera, target, up);
        Mat4 view_matrix = camera_matrix.Mat4QuickInverse();

        Mat4Proj proj_matrix = new Mat4Proj();
//        Mat4RotX mat_rot_x = new Mat4RotX(Math.PI);
//        Mat4RotY mat_rot_y = new Mat4RotY(Math.PI-elapsed_time);
//        Mat4RotZ mat_rot_z = new Mat4RotZ(elapsed_time);

        Mat4 shift_matrix = new Mat4Identity();
        shift_matrix = shift_matrix.mul(new Mat4RotX(Math.PI));
        shift_matrix = shift_matrix.mul(new Mat4RotY(Math.PI-elapsed_time));
        shift_matrix = shift_matrix.mul(new Mat4RotZ(elapsed_time));
        shift_matrix = shift_matrix.mul(new Mat4Transl(new Vec3D(0,0,8)));
//        world_matrix.mul(proj_matrix);

//        Triangle tri = new Triangle(new Vec3D(10,15,15), new Vec3D(10,15,15), new Color(0xFFFFFF));

//        Vec3D zero_axis = proj_matrix.MultiplyVector(new Vec3D(0,0,0));
//        Vec3D x_axis = proj_matrix.MultiplyVector(new Vec3D(1000,0,0));
//        Vec3D y_axis = proj_matrix.MultiplyVector(new Vec3D(0,1000,0));
//        Vec3D z_axis = proj_matrix.MultiplyVector(new Vec3D(0,0,1000));
//        zero_axis = shift_matrix.Multiply3DVector(zero_axis);
//        x_axis = shift_matrix.Multiply3DVector(x_axis);
//        y_axis = shift_matrix.Multiply3DVector(y_axis);
//        z_axis = shift_matrix.Multiply3DVector(z_axis);
//        zero_axis = view_matrix.Multiply3DVector(zero_axis);
//        x_axis = view_matrix.Multiply3DVector(x_axis);
//        y_axis = view_matrix.Multiply3DVector(y_axis);
//        z_axis = view_matrix.Multiply3DVector(z_axis);
//        zero_axis = proj_matrix.Multiply3DVector(zero_axis);
//        x_axis = proj_matrix.Multiply3DVector(x_axis);
//        y_axis = proj_matrix.Multiply3DVector(y_axis);
//        z_axis = proj_matrix.Multiply3DVector(z_axis);
//        line_rasterizer.drawLine((int)zero_axis.getX(), (int)zero_axis.getY(), (int)x_axis.getX(), (int)x_axis.getY(), new Color(0x00FF00));
//        line_rasterizer.drawLine((int)zero_axis.getX(), (int)zero_axis.getY(), (int)y_axis.getX(), (int)y_axis.getY(), new Color(0xFF0000));
//        line_rasterizer.drawLine((int)zero_axis.getX(), (int)zero_axis.getY(), (int)z_axis.getX(), (int)z_axis.getY(), new Color(0xFFFFFF));

        ArrayList<Triangle3D> polygons = new ArrayList<>();
        for (Triangle3D tri : tie.polygons){

//            Triangle3D rot_X_triangle = mat_rot_x.Multiply3DTriangle(tri);
//            Triangle3D rot_XY_triangle = mat_rot_y.Multiply3DTriangle(rot_X_triangle);
//            Triangle3D rot_XYZ_triangle = mat_rot_z.Multiply3DTriangle(rot_XY_triangle);
//            rot_XYZ_triangle.shift_Z(8.0);
//            Triangle3D projected_triangle = proj_matrix.Multiply3DTriangle(rot_XYZ_triangle);

//            Triangle3D triangle_view = view_matrix.Multiply3DTriangle(tri);
            Triangle3D shifted_triangle = shift_matrix.Multiply3DTriangle(tri);


            Vec3D line1 = new Vec3D();
            Vec3D line2 = new Vec3D();
            Vec3D norm = new Vec3D();

            line1.setX(shifted_triangle.b.getX() -  shifted_triangle.a.getX());
            line1.setY(shifted_triangle.b.getY() -  shifted_triangle.a.getY());
            line1.setZ(shifted_triangle.b.getZ() -  shifted_triangle.a.getZ());

            line2.setX(shifted_triangle.c.getX() -  shifted_triangle.a.getX());
            line2.setY(shifted_triangle.c.getY() -  shifted_triangle.a.getY());
            line2.setZ(shifted_triangle.c.getZ() -  shifted_triangle.a.getZ());

            Vec3D sight = new Vec3D();
            sight.setX(shifted_triangle.a.getX() - camera.getX());
            sight.setY(shifted_triangle.a.getY() - camera.getY());
            sight.setZ(shifted_triangle.a.getZ() - camera.getZ());

            line1.normSelf();
            line2.normSelf();
            norm = line1.crossProduct(line2);
            norm.normSelf();

            Triangle3D triangle_view = view_matrix.Multiply3DTriangle(shifted_triangle);

            Triangle3D projected_triangle =  proj_matrix.Multiply3DTriangle(triangle_view);
            projected_triangle.setNorm(norm);
            projected_triangle.color = tri.color;




            if(projected_triangle.norm.dotProduct(sight) < 0){
                continue;
            }
            else {
                polygons.add(projected_triangle);
            }



        }
        // Sort the list based on the Z attribute
        polygons.sort((tri1, tri2) -> Double.compare(tri2.a.getZ() + tri2.b.getZ() + tri2.c.getZ(), tri1.a.getZ() + tri1.b.getZ() + tri1.c.getZ()));

        for(Triangle3D projected_triangle : polygons){

            Triangle2D triangle_2D_projected = new Triangle2D(projected_triangle);
            triangle_2D_projected.shift_XY(1.0, 1.0);
            triangle_2D_projected.mul_XY(0.5 * panel.getHeight(), 0.5 * panel.getWidth());



            light_direction.normSelf();
//            projected_triangle.norm = projected_triangle.
//            projected_triangle.norm.normSelf();
//            System.out.println(light_direction.dotProduct(projected_triangle.norm)/2.001+0.5);
            double light_amount = (light_direction.dotProduct(projected_triangle.norm)/2.001+0.5);
            Color color = new Color((int)(projected_triangle.color.getRed()*light_amount),
                    (int)(projected_triangle.color.getGreen()*light_amount),
                    (int)(projected_triangle.color.getBlue()*light_amount));

            Polygon2D polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                    new Point((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y),
                    new Point((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y),
                    new Point((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y)
            )),
                    color);

            renderer.polygonRasterizer.drawFilledTriangle(polygon, color);
        }

        panel.repaint();
        in_progress = false;
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
                update();

//                panel.repaint();
            }
        }, 0, 50);
    }

//    public Vec3D crossProduct(Vec3D a, Vec3D b){
//        Vec3D out = new Vec3D();
//        out.setX(a.getY() * b.getZ() - a.getZ() * b.getY());
//        out.setY(a.getZ() * b.getX() - a.getX() * b.getZ());
//        out.setZ(a.getX() * b.getY() - a.getY() * b.getX());
//        return out;
//    }
//
//    public double dotProduct(Vec3D a, Vec3D b){
//        return  a.getX() * b.getX() +
//                a.getY() * b.getY() +
//                a.getZ() * b.getZ();
//    }

}
