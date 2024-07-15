package com.example.rectification;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RectificationLogic {
    private BufferedImage getWarpedImage(String path, String corners) {
        try{
            // Specify the command to run your Python script
            String scriptPath = System.getProperty("user.dir") + "\\src\\main\\python\\image_warper.py";

            // Start the process
            ProcessBuilder processBuilder = new ProcessBuilder("python", scriptPath, path, corners);
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

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code " + exitCode);

            return image;
        } catch(Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public String getNewImagePath(String path) {
        int extensionIndex;
        for (extensionIndex = path.length()-1; extensionIndex >= 0; extensionIndex--) {
            if (path.charAt(extensionIndex) == '.') break;
        }

        String imgPath = path.substring(0,extensionIndex) + "_new.png";
        System.out.println(path);
        System.out.println(imgPath);

        return imgPath;
    }

    public BufferedImage getNewImage(String path, int[][]corners) throws IOException {
        String cornersString = "";

        if (corners != null)
            for (int[] corner : corners)
                for (int coord: corner)
                    cornersString += coord + " ";

        return getWarpedImage(path, cornersString);
    }
}