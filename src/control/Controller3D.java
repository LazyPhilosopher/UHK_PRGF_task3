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
    private Vec3D light_direction = new Vec3D(0,1,0);
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
                }
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

        Solid cube = new Cube();
        cube.setModel(new Mat4Transl(1, 0, 0));
        Mat4Proj proj_matrix = new Mat4Proj();
        Mat4RotX mat_rot_x = new Mat4RotX(Math.PI);
        Mat4RotY mat_rot_y = new Mat4RotY(Math.PI-elapsed_time);
        Mat4RotZ mat_rot_z = new Mat4RotZ(elapsed_time);

//        Triangle tri = new Triangle(new Vec3D(10,15,15), new Vec3D(10,15,15), new Color(0xFFFFFF));

        Vec3D x_axis = proj_matrix.MultiplyVector(new Vec3D(100,0,0));
        Vec3D y_axis = proj_matrix.MultiplyVector(new Vec3D(0,100,0));
        Vec3D z_axis = proj_matrix.MultiplyVector(new Vec3D(0,0,100));
        line_rasterizer.drawLine(0, 0, (int)x_axis.getX(), (int)x_axis.getY(), new Color(0x00FF00));
        line_rasterizer.drawLine(0, 0, (int)y_axis.getX(), (int)y_axis.getY(), new Color(0xFF0000));
        line_rasterizer.drawLine(0, 0, (int)z_axis.getX(), (int)z_axis.getY(), new Color(0xFFFFFF));

        ArrayList<Triangle3D> polygons = new ArrayList<>();
        for (Triangle3D tri : tie.polygons){

            Triangle3D rot_X_triangle = mat_rot_x.Multiply3DTriangle(tri);
            Triangle3D rot_XY_triangle = mat_rot_y.Multiply3DTriangle(rot_X_triangle);
            Triangle3D rot_XYZ_triangle = mat_rot_z.Multiply3DTriangle(rot_XY_triangle);
            rot_XYZ_triangle.shift_Z(8.0);
            Triangle3D projected_triangle = proj_matrix.Multiply3DTriangle(rot_XYZ_triangle);

            Vec3D line1 = new Vec3D();
            Vec3D line2 = new Vec3D();
            Vec3D norm = new Vec3D();

            line1.setX(rot_XYZ_triangle.b.getX() -  rot_XYZ_triangle.a.getX());
            line1.setY(rot_XYZ_triangle.b.getY() -  rot_XYZ_triangle.a.getY());
            line1.setZ(rot_XYZ_triangle.b.getZ() -  rot_XYZ_triangle.a.getZ());

            line2.setX(rot_XYZ_triangle.c.getX() -  rot_XYZ_triangle.a.getX());
            line2.setY(rot_XYZ_triangle.c.getY() -  rot_XYZ_triangle.a.getY());
            line2.setZ(rot_XYZ_triangle.c.getZ() -  rot_XYZ_triangle.a.getZ());


            Vec3D sight = new Vec3D();
            sight.setX(rot_XYZ_triangle.a.getX() - camera.getX());
            sight.setY(rot_XYZ_triangle.a.getY() - camera.getY());
            sight.setZ(rot_XYZ_triangle.a.getZ() - camera.getZ());

            line1.normSelf();
            line2.normSelf();
            norm = line1.crossProduct(line2);
            norm.normSelf();
            projected_triangle.setNorm(norm);




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
                elapsed_time += 0.03;
                update();

//                panel.repaint();
            }
        }, 0, 60);
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
