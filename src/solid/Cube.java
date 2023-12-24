package solid;

import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {
        // vb
        vb.add(new Point3D(0, 0, 0)); // v0
        vb.add(new Point3D(1, 0, 0)); // v1
        vb.add(new Point3D(1, 1, 0)); // v2
        vb.add(new Point3D(0, 1, 0)); // v3

        // ib
        addIndices(
                0, 1,
                1, 2,
                2, 3,
                3, 0
        );
    }
}
