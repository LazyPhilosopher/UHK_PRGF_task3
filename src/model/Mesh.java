package model;

import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Mesh {
    public ArrayList<Triangle3D> polygons;

    public Mesh(ArrayList<Triangle3D> _polygons){
        this.polygons = _polygons;
    }

    public Mesh(String file_path){

        try {
            // Creating FileReader and BufferedReader objects
            FileReader fileReader = new FileReader(file_path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            List<Vec3D> vertices = new ArrayList<>();
            this.polygons = new ArrayList<>();

            String line;
            // Reading the file line by line
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("\\s+");
                if(words[0].equals("v")){
                    double x = Double.parseDouble(words[1]);
                    double y = Double.parseDouble(words[2]);
                    double z = Double.parseDouble(words[3]);
                    vertices.add(new Vec3D(x,y,z));
                }
                else if(words[0].equals("f")){
                    Vec3D a = vertices.get(Integer.parseInt(words[1])-1);
                    Vec3D b = vertices.get(Integer.parseInt(words[2])-1);
                    Vec3D c = vertices.get(Integer.parseInt(words[3])-1);
                    this.polygons.add(new Triangle3D(a,b,c, new Color(0xFFFFFF)));
                }
            }

            // Closing the BufferedReader and FileReader
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
