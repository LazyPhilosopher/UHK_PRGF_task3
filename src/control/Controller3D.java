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

    private Camera camera;
    private Mat4 proj;

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

        camera = new Camera(
          new Vec3D(0, -1, 0.3),
          Math.toRadians(90),
          Math.toRadians(-15),
          1,
          true
        );

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
        renderer.setView(camera.getViewMatrix());

        Solid cube = new Cube();
        cube.setModel(new Mat4Transl(1, 0, 0));
        Mat4Proj proj_matrix = new Mat4Proj();
        Mat4RotX mat_rot_x = new Mat4RotX(elapsed_time*1);
        Mat4RotZ mat_rot_z = new Mat4RotZ(elapsed_time*0.5);

//        Triangle tri = new Triangle(new Vec3D(10,15,15), new Vec3D(10,15,15), new Color(0xFFFFFF));

        Mesh mesh = new Mesh(new ArrayList<>(Arrays.asList(
                new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(0, 1, 0), new Vec3D(1, 1, 0), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(0, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 0, 0), new Color(0xFFFFFF)),

                new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 0), new Vec3D(1, 1, 1), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(1, 0, 0), new Vec3D(1, 1, 1), new Vec3D(1, 0, 1), new Color(0xFFFFFF)),

                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(1, 1, 1), new Vec3D(0, 1, 1), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 0, 1), new Color(0xFFFFFF)),

                new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 1), new Vec3D(0, 1, 0), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(0, 0, 1), new Vec3D(0, 1, 0), new Vec3D(0, 0, 0), new Color(0xFFFFFF)),

                new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(0, 1, 1), new Vec3D(1, 1, 1), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(0, 1, 0), new Vec3D(1, 1, 1), new Vec3D(1, 1, 0), new Color(0xFFFFFF)),

                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 1), new Vec3D(0, 0, 0), new Color(0xFFFFFF)),
                new Triangle3D(new Vec3D(1, 0, 1), new Vec3D(0, 0, 0), new Vec3D(1, 0, 0), new Color(0xFFFFFF))
        )));

        for (Triangle3D tri : mesh.polygons){

            Triangle3D rot_X_triangle = mat_rot_x.Multiply3DTriangle(tri);
            Triangle3D rot_XZ_triangle = mat_rot_z.Multiply3DTriangle(rot_X_triangle);
            rot_XZ_triangle.shift_Z(3.0);
            Triangle3D projected_triangle = proj_matrix.Multiply3DTriangle(rot_XZ_triangle);

            Triangle2D triangle_2D_projected = new Triangle2D(projected_triangle);
            triangle_2D_projected.shift_XY(1.0, 1.0);
            triangle_2D_projected.mul_XY(0.5 * panel.getHeight(), 0.5 * panel.getWidth());

            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y, (int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y, new Color(0xFFFFFF));
            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y, (int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y, new Color(0xFFFFFF));
            renderer.lineRasterizer.rasterize((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y, (int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y, new Color(0xFFFFFF));

            Polygon2D polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                    new Point((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y),
                    new Point((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y),
                    new Point((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y)
                    )));
            renderer.polygonRasterizer.drawFilledTriangle(polygon, 0xAAAAAA);
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
        }, 0, 30);
    }
}
