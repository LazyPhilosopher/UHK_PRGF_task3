package control;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PNGSprite {
    private Color[] mColours;
    private int nWidth;
    private int nHeight;

    public PNGSprite(String filePath) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            nWidth = image.getWidth();
            nHeight = image.getHeight();
            mColours = new Color[nWidth * nHeight];

            for (int y = 0; y < nHeight; y++) {
                for (int x = 0; x < nWidth; x++) {
                    int colorValue = image.getRGB(x, y);
                    mColours[y * nWidth + x] = new Color(colorValue, true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPositionValid(double x, double y) {
        return x >= 0 && x <= 1 && y >= 0 && y <= 1;
    }

    public Color getColour(double x, double y) {
        if (isPositionValid(x, y)) {
            return mColours[(int)(y*(nHeight-1)*nWidth + x * (nWidth-1))];
        } else {
            return new Color(0x0);
        }
    }

    // Getter methods
    public Color[] getColours() {
        return mColours;
    }

    public int getWidth() {
        return nWidth;
    }

    public int getHeight() {
        return nHeight;
    }
}
