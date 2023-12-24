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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Controller3D implements Controller {
    private final Panel panel;

    private LineRasterizer line_rasterizer;
    private PolygonRasterizer polygon_rasterizer;
    private WiredRenderer renderer;
    private float elapsed_time = 0;
    private boolean in_progress = false;

    private Vec3D camera = new Vec3D();
    private Vec3D light_direction = new Vec3D(0,0,-1);
    private Mat4 proj;

    Mesh mesh = new Mesh(new ArrayList<>(Arrays.asList(
            new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0), new Color(0xFF0000)),
            new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0), new Color(0xFF0000)),

            new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1), new Color(0x00FF00)),
            new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1), new Color(0x00FF00)),

            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Color(0x0000FF)),
            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1), new Color(0x0000FF)),

            new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0), new Color(0xFF0000)),
            new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0), new Color(0xFF0000)),

            new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1), new Color(0x00FF00)),
            new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0), new Color(0x00FF00)),

            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0), new Color(0x0000FF)),
            new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Color(0x0000FF))
    )));

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
        Mat4RotX mat_rot_x = new Mat4RotX(elapsed_time);
        Mat4RotY mat_rot_y = new Mat4RotY(elapsed_time);
        Mat4RotZ mat_rot_z = new Mat4RotZ(elapsed_time);

//        Triangle tri = new Triangle(new Vec3D(10,15,15), new Vec3D(10,15,15), new Color(0xFFFFFF));



        for (Triangle3D tri : mesh.polygons){

            Triangle3D rot_X_triangle = mat_rot_x.Multiply3DTriangle(tri);
            Triangle3D rot_XY_triangle = mat_rot_y.Multiply3DTriangle(rot_X_triangle);
            Triangle3D rot_XYZ_triangle = mat_rot_z.Multiply3DTriangle(rot_XY_triangle);
            rot_XYZ_triangle.shift_Z(3.0);
            Triangle3D projected_triangle = proj_matrix.Multiply3DTriangle(rot_XYZ_triangle);

            Vec3D line1 = new Vec3D();
            Vec3D line2 = new Vec3D();
            Vec3D norm = new Vec3D();

            line1.setX(projected_triangle.b.getX() -  projected_triangle.a.getX());
            line1.setY(projected_triangle.b.getY() -  projected_triangle.a.getY());
            line1.setZ(projected_triangle.b.getZ() -  projected_triangle.a.getZ());

            line2.setX(projected_triangle.c.getX() -  projected_triangle.a.getX());
            line2.setY(projected_triangle.c.getY() -  projected_triangle.a.getY());
            line2.setZ(projected_triangle.c.getZ() -  projected_triangle.a.getZ());

            norm = crossProduct(line1, line2);
            norm.normSelf();

            Vec3D sight = new Vec3D();
            sight.setX(projected_triangle.a.getX() - camera.getX());
            sight.setY(projected_triangle.a.getY() - camera.getY());
            sight.setZ(projected_triangle.a.getZ() - camera.getZ());


            if(dotProduct(norm, sight) > 0){
                continue;
            }

            Triangle2D triangle_2D_projected = new Triangle2D(projected_triangle);
            triangle_2D_projected.shift_XY(1.0, 1.0);
            triangle_2D_projected.mul_XY(0.5 * panel.getHeight(), 0.5 * panel.getWidth());

//            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y, (int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y, new Color(0xFFFFFF));
//            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y, (int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y, new Color(0xFFFFFF));
//            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y, (int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y, new Color(0xFFFFFF));



            Polygon2D polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                    new Point((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y),
                    new Point((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y),
                    new Point((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y)
                    )));
            double light_amount = (dotProduct(light_direction, norm)/2 + 0.5);
            Color color = new Color((int)(tri.color.getRed()*light_amount),
                    (int)(tri.color.getGreen()*light_amount),
                    (int)(tri.color.getBlue()*light_amount));
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
                elapsed_time += 0.005;
                update();

//                panel.repaint();
            }
        }, 0, 7);
    }

    public Vec3D crossProduct(Vec3D a, Vec3D b){
        Vec3D out = new Vec3D();
        out.setX(a.getY() * b.getZ() - a.getZ() * b.getY());
        out.setY(a.getZ() * b.getX() - a.getX() * b.getZ());
        out.setZ(a.getX() * b.getY() - a.getY() * b.getX());
        return out;
    }

    public double dotProduct(Vec3D a, Vec3D b){
        return  a.getX() * b.getX() +
                a.getY() * b.getY() +
                a.getZ() * b.getZ();
    }

}
