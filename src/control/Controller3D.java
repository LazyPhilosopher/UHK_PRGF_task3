package control;

import model.Mesh;
import model.Polygon2D;
import model.Triangle2D;
import model.Triangle3D;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.PolygonRasterizer;
import rasterize.Raster;
import renderer.WiredRenderer;
import transforms.*;
import view.Panel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class Controller3D implements Controller {
    private final Panel panel;

    private LineRasterizer line_rasterizer;
    private PolygonRasterizer polygon_rasterizer;
    private WiredRenderer renderer;
    private float elapsed_time = 0;
    private boolean in_progress = false;

    private Vec3D camera_position_vector = new Vec3D( 0.0, 0.6,64.1);
    private Vec3D look_direction = new Vec3D(0,0,1);
    private Vec3D light_direction = new Vec3D(1,1,0);
    private Vec3D scene_up_vector = new Vec3D(0,1,0);

    private float azimuth = (float) -3.46;
    private Mat4 proj;
    private double deg_field_of_view = 170;
    private boolean wireframe_mode = false;

    Map<Mesh, Map<String, Object>> mesh_list = new LinkedHashMap<>();

    String projectPath = System.getProperty("user.dir");
    Mesh starfighter = new Mesh(Paths.get(projectPath, "src\\blender\\VideoShip.obj").toString());
    Mesh teapot = new Mesh(Paths.get(projectPath, "src\\blender\\teapot.obj").toString());
    //    Mesh axis = new Mesh(Paths.get(projectPath, "UHK_PRGF_task3-main\src\\blender\\axis.obj").toString());
//    Mesh tie = new Mesh(Paths.get(projectPath, "UHK_PRGF_task3-main\src\\blender\\tie_fighter.obj").toString());
    Mesh mountains = new Mesh(Paths.get(projectPath, "src\\blender\\mountains.obj").toString());

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
    )),
            new PNGSprite(Paths.get(projectPath,"src\\blender\\sprites\\creeper.png").toString())
    );

    public Controller3D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);

        Map<String, Object> matrices_dict = new LinkedHashMap<>();
        matrices_dict.put("z", 1);
        matrices_dict.put("y", 1);
        matrices_dict.put("t", new Vec3D(0,0,50));
        this.mesh_list.put(teapot, matrices_dict);

        matrices_dict = new LinkedHashMap<>();
        matrices_dict.put("x", 5);
        matrices_dict.put("y", 5);
        matrices_dict.put("z", 5);
        matrices_dict.put("t", new Vec3D(-2,2,50));
        this.mesh_list.put(cube, matrices_dict);

        matrices_dict = new LinkedHashMap<>();
        matrices_dict.put("y", 1);
        matrices_dict.put("t", new Vec3D(-5,5,0));
        this.mesh_list.put(starfighter, matrices_dict);

        matrices_dict = new LinkedHashMap<>();
        matrices_dict.put("t", new Vec3D(-5,-10,0));
        this.mesh_list.put(mountains, matrices_dict);

        update();
        setLoop();
    }

    public void initObjects(Raster raster) {
        line_rasterizer = new LineRasterizerGraphics(raster);
        polygon_rasterizer = new PolygonRasterizer(raster);
        renderer = new WiredRenderer(line_rasterizer, polygon_rasterizer);
        proj = new Mat4PerspRH(
                Math.PI / 4,
                raster.getHeight() / (double)raster.getWidth(),
                0.1,
                20
        );
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    camera_position_vector = camera_position_vector.add(look_direction.mul(1));
                }else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    camera_position_vector = camera_position_vector.sub(look_direction.mul(1));
                }else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    azimuth -= 0.1;
                }else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    azimuth += 0.1;
                }
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    Vec3D camera_look_direction_vector = camera_position_vector.add(look_direction);
                    Mat4PointAt camera_matrix = new Mat4PointAt(camera_position_vector, camera_look_direction_vector, scene_up_vector);
                    camera_position_vector = camera_position_vector.add(camera_matrix.getDownVector());
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    Vec3D camera_look_direction_vector = camera_position_vector.add(look_direction);
                    Mat4PointAt camera_matrix = new Mat4PointAt(camera_position_vector, camera_look_direction_vector, scene_up_vector);
                    camera_position_vector = camera_position_vector.add(camera_matrix.getUpVector());
                }
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    Vec3D camera_look_direction_vector = camera_position_vector.add(look_direction);
                    Mat4PointAt camera_matrix = new Mat4PointAt(camera_position_vector, camera_look_direction_vector, scene_up_vector);
                    camera_position_vector = camera_position_vector.add(camera_matrix.getLeftVector());
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    Vec3D camera_look_direction_vector = camera_position_vector.add(look_direction);
                    Mat4PointAt camera_matrix = new Mat4PointAt(camera_position_vector, camera_look_direction_vector, scene_up_vector);
                    camera_position_vector = camera_position_vector.add(camera_matrix.getRightVector());
                }
                if (e.getKeyCode() == KeyEvent.VK_ADD){
                    deg_field_of_view+=10;
                    if(deg_field_of_view >= 180){deg_field_of_view=175;}
                }
                if (e.getKeyCode() == KeyEvent.VK_SUBTRACT){
                    deg_field_of_view-=10;
                    if(deg_field_of_view < 0){deg_field_of_view=0.1;}
                }
                if (e.getKeyCode() == KeyEvent.VK_T){
                    wireframe_mode = !wireframe_mode;
                }
                System.out.printf("Field of View: %f%n", deg_field_of_view);
                System.out.printf("Azimuth: %f%n", azimuth);
                System.out.printf("Camera position: "+ camera_position_vector + "%n");

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

        List<Triangle3D> triangles = new ArrayList<>();
        // get each mesh and its transformation matrices
        for (Map.Entry<Mesh, Map<String, Object>> entry : this.mesh_list.entrySet()){
            Mesh mesh = entry.getKey();
            Map<String, Object> matrices_dict = entry.getValue();
            // adjust transformations to elapsed_time and apply to each polygon
            triangles.addAll(getRasterizedtrianglesFromMesh(mesh, matrices_dict, view_matrix, elapsed_time));
        }
        rasterizeTriangles(triangles);

        panel.repaint();
        in_progress = false;
    }

    public List<Triangle3D> getRasterizedtrianglesFromMesh(Mesh mesh, Map<String, Object> matrices_dict, Mat4 view_matrix, float time){

        Mat4Proj proj_matrix = new Mat4Proj(this.deg_field_of_view);
        Mat4 shift_matrix = new Mat4Identity();

        for (Map.Entry<String, Object> entry : matrices_dict.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            switch (key){
                case "x":
                    shift_matrix = shift_matrix.mul(new Mat4RotX((int)value * time));
                    break;
                case "y":
                    shift_matrix = shift_matrix.mul(new Mat4RotY((int)value * time));
                    break;
                case "z":
                    shift_matrix = shift_matrix.mul(new Mat4RotZ( (int)value * time));
                    break;
                case "t":
                    shift_matrix = shift_matrix.mul(new Mat4Transl((Vec3D) value));
                    break;
            }
        }

        ArrayList<Triangle3D> polygons = new ArrayList<>();
        for (Triangle3D tri : mesh.polygons){

            // translate and rotate origin triangle
            Triangle3D shifted_triangle = shift_matrix.Multiply3DTriangle(tri);
            shifted_triangle.t1 = tri.t1; shifted_triangle.t2 = tri.t2; shifted_triangle.t3 = tri.t3;
            Vec3D norm = shifted_triangle.calculateNorm();

            Vec3D sight_vector = new Vec3D();
            sight_vector.setX(shifted_triangle.a.getX() - camera_position_vector.getX());
            sight_vector.setY(shifted_triangle.a.getY() - camera_position_vector.getY());
            sight_vector.setZ(shifted_triangle.a.getZ() - camera_position_vector.getZ());

            // transform triangle to form viewed from camera perspective
            Triangle3D triangle_view = view_matrix.Multiply3DTriangle(shifted_triangle);
            triangle_view.color = shifted_triangle.color;
            triangle_view.t1 = shifted_triangle.t1; triangle_view.t2 = shifted_triangle.t2; triangle_view.t3 = shifted_triangle.t3;

            // clip polygons too close in front of camera
            List<Triangle3D> far_enough_clipped_triangles = triangleClipAgainstPlane(new Vec3D(0,0,1), new Vec3D(0,0,1), triangle_view);

            // clip polygons too far in front of camera
            List<Triangle3D> clipped_triangles = new ArrayList<>();
            for(Triangle3D triangle : far_enough_clipped_triangles){
                clipped_triangles.addAll(triangleClipAgainstPlane(new Vec3D(0,0,100), new Vec3D(0,0,-1), triangle));
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

        List<Triangle3D> output = new ArrayList<>();
        // clip polygons by four field-of-view planes
        for(Triangle3D rasterized_triangle : polygons) {
            List<Triangle3D> clipped = new ArrayList<>();
            List<Triangle3D> clipped_triangles = new ArrayList<>();

            // Add initial triangle
            clipped_triangles.add(rasterized_triangle);
            int nNewTriangles = 1;

            for (int p = 0; p < 4; p++) {
                while (nNewTriangles > 0) {
                    // Take triangle from front of queue
                    Triangle3D test = clipped_triangles.get(0);
                    clipped_triangles.remove(0);
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
                    clipped_triangles.addAll(clipped);
                }
                nNewTriangles = clipped_triangles.size();
            }

            for (Triangle3D tri : clipped_triangles) {
                tri.setNorm(rasterized_triangle.norm);
                if (mesh.sprite != null) {
                    tri.set_sprite(mesh.sprite);
                }
            }
            output.addAll(clipped_triangles);
        }
        return output;
    }

    public void rasterizeTriangles(List<Triangle3D> triangles){
        // Sort the list based on the Z attribute
        triangles.sort((tri1, tri2) -> Double.compare(tri2.a.getZ() + tri2.b.getZ() + tri2.c.getZ(), tri1.a.getZ() + tri1.b.getZ() + tri1.c.getZ()));

        for (Triangle3D triangle : triangles)
        {
            Triangle2D triangle_2D_projected = new Triangle2D(triangle);
            triangle_2D_projected.shift_XY(1.0, 1.0);
            triangle_2D_projected.mul_XY(0.5 * panel.getHeight(), 0.5 * panel.getWidth());

            Polygon2D polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                    new Vec2D((int)triangle_2D_projected.a_x, (int)triangle_2D_projected.a_y),
                    new Vec2D((int)triangle_2D_projected.b_x, (int)triangle_2D_projected.b_y),
                    new Vec2D((int)triangle_2D_projected.c_x, (int)triangle_2D_projected.c_y)
            )),
                    new Color(0x00FFFF));

            if (wireframe_mode){
                renderer.polygonRasterizer.drawShallowPolygon(polygon, new Color(0x00FFFF));
                continue;
            }

            light_direction.normSelf();
            double light_amount = (light_direction.dotProduct(triangle.norm)/2.0001+0.5);
            Color color = new Color((int)(triangle.color.getRed()*light_amount),
                    (int)(triangle.color.getGreen()*light_amount),
                    (int)(triangle.color.getBlue()*light_amount));
            polygon.setColor(color);

            // if no texture provided
            if(triangle.t1 == null || triangle.t2 == null || triangle.t3 == null ){
                renderer.polygonRasterizer.drawFilledTriangle(polygon, color);
            } else {
                // textured polygon
                Polygon2D texture_polygon = new Polygon2D(new ArrayList<>(Arrays.asList(
                        new Vec2D(triangle.t1.getX(), triangle.t1.getY()),
                        new Vec2D(triangle.t2.getX(), triangle.t2.getY()),
                        new Vec2D(triangle.t3.getX(), triangle.t3.getY()))),
                        color);
                renderer.polygonRasterizer.drawTexturedTriangle(polygon, texture_polygon, triangle.sprite, light_amount);
            }
        }
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
            out1.color = input_triangle.color;
            out1.a = inside_points.get(0);

            double t, new_texture_x, new_texture_y;
            HashMap<String, Object> out1_b_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(0));
            out1.b = (Vec3D) out1_b_clipped.get("vector");
            if (input_triangle.is_textured()) {
                out1.t1 = inside_textures.get(0);
                t = (double) out1_b_clipped.get("t");
                new_texture_x = t * (outside_textures.get(0).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
                new_texture_y = t * (outside_textures.get(0).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
                out1.t2 = new Vec2D(new_texture_x, new_texture_y);
            }

            HashMap<String, Object> out1_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(1));
            out1.c = (Vec3D) out1_c_clipped.get("vector");
            if (input_triangle.is_textured()) {
                t = (double) out1_c_clipped.get("t");
                new_texture_x = t * (outside_textures.get(1).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
                new_texture_y = t * (outside_textures.get(1).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
                out1.t3 = new Vec2D(new_texture_x, new_texture_y);
            }
            output.add(out1);
        }
        else if(inside_points.size() == 2 && outside_points.size() == 1){
            Triangle3D out1 = new Triangle3D();
            Triangle3D out2 = new Triangle3D();
            out1.color = input_triangle.color;
            out2.color = input_triangle.color;

            out1.a = inside_points.get(0);
            out1.b = inside_points.get(1);

            double t, new_texture_x, new_texture_y;
            HashMap<String, Object> out2_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.a, outside_points.get(0));
            out1.c = (Vec3D) out2_c_clipped.get("vector");
            if (input_triangle.is_textured()) {
                out1.t1 = inside_textures.get(0);
                out1.t2 = inside_textures.get(1);
                t = (double) out2_c_clipped.get("t");
                new_texture_x = t * (outside_textures.get(0).getX() - inside_textures.get(0).getX()) + inside_textures.get(0).getX();
                new_texture_y = t * (outside_textures.get(0).getY() - inside_textures.get(0).getY()) + inside_textures.get(0).getY();
                out1.t3 = new Vec2D(new_texture_x, new_texture_y);
            }

            out2.a = inside_points.get(1);
            out2.b = out1.c;
            HashMap<String, Object> out1_c_clipped = vectorIntersectPlane(plane_point, plane_normal, out1.b, outside_points.get(0));
            out2.c = (Vec3D) out1_c_clipped.get("vector");
            if (input_triangle.is_textured()) {
                out2.t1 = out1.t2;
                out2.t2 = out1.t3;
                t = (double) out1_c_clipped.get("t");
                new_texture_x = t * (outside_textures.get(0).getX() - inside_textures.get(1).getX()) + inside_textures.get(1).getX();
                new_texture_y = t * (outside_textures.get(0).getY() - inside_textures.get(1).getY()) + inside_textures.get(1).getY();
                out2.t3 = new Vec2D(new_texture_x, new_texture_y);
            }

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
