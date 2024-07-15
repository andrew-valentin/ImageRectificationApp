package com.example.rectification;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import java.lang.Integer;

public class Filereader{
    public static void main(String[] args){
        try{
            // Specify the command to run your Python script
            String path = System.getProperty("user.dir") + "\\src\\main\\python";
            String scriptPath = System.getProperty("user.dir") + "\\src\\main\\python\\image_warper.py";
            String imgPath = System.getProperty("user.dir") + "\\src\\main\\python\\test.jpg";
            int num1 = 42;
            int num2 = 8;

            System.out.println(path);

            // Start the process
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, imgPath);
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int width = Integer.parseInt(reader.readLine());
            int height = Integer.parseInt(reader.readLine());

            int[] pixels = new int[width * height];

            String line = null;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                pixels[index] = Integer.parseInt(line);
                index++;
            }

            // Create a BufferedImage with the specified width, height, and pixel array
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, width, height, pixels, 0, width);

            // Save the BufferedImage to a file
            try {
                File outputfile = new File(path+"\\test_new.png");
                ImageIO.write(image, "png", outputfile);
                System.out.println("Image saved successfully.");

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code " + exitCode);
        }catch(Exception e){System.out.println(e);}
    }
}
