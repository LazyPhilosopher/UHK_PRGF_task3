package control;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PNGSprite {
    private int[][] pixelData;
    private int nWidth;
    private int nHeight;

    public PNGSprite(String filePath) {
        try {
            // Load the PNG image
            BufferedImage image = ImageIO.read(new File(filePath));

            // Access the image as a 2D array
            this.nWidth = image.getWidth();
            this.nHeight = image.getHeight();

            pixelData = new int[nWidth][nHeight];

            for (int x = 0; x < nWidth; x++) {
                for (int y = 0; y < nHeight; y++) {
                    this.pixelData[x][y] = image.getRGB(x, y);
                }
            }

//            this.pixelData = createRainbowArray(nWidth, nHeight);

            // Now pixelData contains the RGB values of each pixel in the image
            // You can access individual pixels like pixelData[x][y]

            // Example: Print RGB values of the top-left pixel
            int rgb = pixelData[0][0];
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

//            System.out.println("Top-left pixel RGB values: " + red + ", " + green + ", " + blue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPositionValid(double x, double y) {
        return x >= 0 && x <= 1 && y >= 0 && y <= 1;
    }

    public int getColor(double x, double y) {
        if (isPositionValid(x, y)) {
            x %= 0.999; y %= 0.999;
            return this.pixelData[(int) (x*(nWidth))][(int) (y*(nHeight))];
        } else {
            return 0x0;
        }
    }

    // Getter methods
    public int[][] getColours() {
        return pixelData;
    }

    public int getWidth() {
        return nWidth;
    }

    public int getHeight() {
        return nHeight;
    }

    private static int[][] createRainbowArray(int rows, int cols) {
        int[][] rainbowArray = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Calculate HSV values for a rainbow gradient
                float hue = (float) j / cols;  // Hue ranges from 0 to 1
                float saturation = 1.0f;       // Full saturation
                float brightness = 1.0f;       // Full brightness

                // Convert HSV to RGB
                Color color = Color.getHSBColor(hue, saturation, brightness);

                // Extract the RGB components
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Combine RGB components into a single integer value
                int rgbValue = (red << 16) | (green << 8) | blue;

                // Assign the RGB value to the array element
                rainbowArray[i][j] = rgbValue;
            }
        }

        return rainbowArray;
    }
}
