package renderer;

import rasterize.LineRasterizer;
import rasterize.PolygonRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;

import java.awt.*;

public class WiredRenderer {
    public LineRasterizer lineRasterizer;
    public PolygonRasterizer polygonRasterizer;

    private Mat4 view, proj;

    public WiredRenderer(LineRasterizer lineRasterizer, PolygonRasterizer polygonRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.polygonRasterizer = polygonRasterizer;
    }

    public void render(Solid solid) {
        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);

            // modelovací tranformace
            a = a.mul(solid.getModel());
            b = b.mul(solid.getModel());

            // pohledová tranformace
            a = a.mul(view);
            b = b.mul(view);

            // projekční tranformace
            a = a.mul(proj);
            b = b.mul(proj);

            // TODO: ořezání

            // TODO: dehomogenizace

            // TODO: tranformace do okna obrazovky

            // Rasterizace
            lineRasterizer.rasterize(
                    (int)Math.round(a.getX()), (int)Math.round(a.getY()),
                    (int)Math.round(b.getX()), (int)Math.round(b.getY()),
                    Color.RED
            );
        }
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}

