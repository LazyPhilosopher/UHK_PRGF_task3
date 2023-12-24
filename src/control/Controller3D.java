package control;

import model.Mesh;
import model.Triangle2D;
import model.Triangle3D;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
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

    private LineRasterizer rasterizer;
    private WiredRenderer renderer;
    private float elapsed_time = 0;

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
        rasterizer = new LineRasterizerGraphics(raster);
        renderer = new WiredRenderer(rasterizer);

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

                rasterizer.rasterize(
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
        panel.clear();

        renderer.setProj(proj);
        renderer.setView(camera.getViewMatrix());

        Solid cube = new Cube();
        cube.setModel(new Mat4Transl(1, 0, 0));
        Mat4Proj proj_matrix = new Mat4Proj();
        double theta = elapsed_time;
        Mat4RotX mat_rot_x = new Mat4RotX(theta*1);
        Mat4RotZ mat_rot_z = new Mat4RotZ(theta*0.5);

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


            Vec3D a = mat_rot_x.MultiplyVector(tri.a);
            Vec3D b = mat_rot_x.MultiplyVector(tri.b);
            Vec3D c = mat_rot_x.MultiplyVector(tri.c);

            a = mat_rot_z.MultiplyVector(a);
            b = mat_rot_z.MultiplyVector(b);
            c = mat_rot_z.MultiplyVector(c);

            a.setZ(a.getZ()+3.0);
            b.setZ(b.getZ()+3.0);
            c.setZ(c.getZ()+3.0);

            a = proj_matrix.MultiplyVector(a);
            b = proj_matrix.MultiplyVector(b);
            c = proj_matrix.MultiplyVector(c);

            Triangle2D tri_projected = new Triangle2D(a.getX(), a.getY(),
                    b.getX(), b.getY(),
                    c.getX(), c.getY());


            tri_projected.a_x += 1.0; tri_projected.a_y += 1.0;
            tri_projected.b_x += 1.0; tri_projected.b_y += 1.0;
            tri_projected.c_x += 1.0; tri_projected.c_y += 1.0;

            tri_projected.a_x *= 0.5 * panel.getHeight(); tri_projected.a_y *= 0.5 * panel.getWidth();
            tri_projected.b_x *= 0.5 * panel.getHeight(); tri_projected.b_y *= 0.5 * panel.getWidth();
            tri_projected.c_x *= 0.5 * panel.getHeight(); tri_projected.c_y *= 0.5 * panel.getWidth();

            renderer.lineRasterizer.rasterize((int)tri_projected.a_x, (int)tri_projected.a_y, (int)tri_projected.b_x, (int)tri_projected.b_y, new Color(0xFFFFFF));
            renderer.lineRasterizer.rasterize((int)tri_projected.b_x, (int)tri_projected.b_y, (int)tri_projected.c_x, (int)tri_projected.c_y, new Color(0xFFFFFF));
            renderer.lineRasterizer.rasterize((int)tri_projected.c_x, (int)tri_projected.c_y, (int)tri_projected.a_x, (int)tri_projected.a_y, new Color(0xFFFFFF));
        }

//        renderer.render(cube);
//        renderer.lineRasterizer.rasterize(25+(int)elapsed_time,25+(int)elapsed_time,-25+(int)elapsed_time,-25+(int)elapsed_time, new Color(0xFFFFFF));



        panel.repaint();
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
        }, 0, 10);
    }
}
