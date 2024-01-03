package control;

import java.awt.*;
import java.io.*;

public class Sprite {
    private int nWidth = 0;
    private int nHeight = 0;
    private char[] mGlyphs = null;
    private Color[] mColours = null;

    // Equivalent to FG_BLACK in C++ class (assuming it's a constant)
    private static final short FG_BLACK = 0;

    public Sprite() {
        // Default constructor
    }

    public Sprite(int w, int h) {
        create(w, h);
    }

    public Sprite(String sFile) {
        if (!load(sFile)) {
            create(8, 8);
        }
    }

    private void create(int w, int h) {
        nWidth = w;
        nHeight = h;
        mGlyphs = new char[w * h];
        mColours = new Color[w * h];
        for (int i = 0; i < w * h; i++) {
            mGlyphs[i] = ' ';
            mColours[i] = new Color(0x0);
        }
    }

    public boolean load(String sFile) {
        try (FileInputStream fis = new FileInputStream(sFile);
             DataInputStream dis = new DataInputStream(fis)) {

            nWidth = Integer.reverseBytes(dis.readInt());
            nHeight = Integer.reverseBytes(dis.readInt());
//
//            nWidth = 32;
//            nHeight = 32;

            create(nWidth, nHeight);

            for (int i = 0; i < nWidth * nHeight; i++) {
                int value = dis.readUnsignedByte();
                int red = (value >> 5) & 0x07; // Extracts the first 3 bits
                int green = (value >> 2) & 0x07; // Extracts the next 3 bits
                int blue = value & 0x03; // Extracts the last 2 bits

                // Scale up to 8-bit per channel (0 to 255 range)
                red = (red * 255) / 7;
                green = (green * 255) / 7;
                blue = (blue * 255) / 3;

                mColours[i] = new Color(red,green,blue);
            }
            for (int i = 0; i < nWidth * nHeight; i++) {
//                mGlyphs[i] = dis.readChar();
                int value = dis.readUnsignedByte();
                int red = (value >> 5) & 0x07; // Extracts the first 3 bits
                int green = (value >> 2) & 0x07; // Extracts the next 3 bits
                int blue = value & 0x03; // Extracts the last 2 bits

                // Scale up to 8-bit per channel (0 to 255 range)
                red = (red * 255) / 7;
                green = (green * 255) / 7;
                blue = (blue * 255) / 3;

                mColours[i] = new Color(red,green,blue);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setGlyph(int x, int y, char c) {
        if (isPositionValid(x, y)) {
            mGlyphs[y * nWidth + x] = c;
        }
    }

    public void setColour(int x, int y, Color c) {
        if (isPositionValid(x, y)) {
            mColours[y * nWidth + x] = c;
        }
    }

    public char getGlyph(int x, int y) {
        if (isPositionValid(x, y)) {
            return mGlyphs[y * nWidth + x];
        } else {
            return ' ';
        }
    }

    public Color getColour(double x, double y) {
        if (isPositionValid(x, y)) {
            return mColours[(int)(y*(nHeight-1)*nWidth + x * nWidth)];
        } else {
            return new Color(0x0);
        }
    }

    public char sampleGlyph(float x, float y) {
        int sx = (int)(x * (float)nWidth);
        int sy = (int)(y * (float)nHeight - 1.0f);
        return getGlyph(sx, sy);
    }

    public Color sampleColour(float x, float y) {
        int sx = (int)(x * (float)nWidth);
        int sy = (int)(y * (float)nHeight - 1.0f);
        return getColour(sx, sy);
    }

    private boolean isPositionValid(double x, double y) {
        return x >= 0 && x <= 1 && y >= 0 && y <= 1;
    }
}
