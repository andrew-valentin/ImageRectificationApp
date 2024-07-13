package com.example.camscanner;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

public class CamScanner extends Application {
    private Stage stage;

    private double screenWidth;
    private double screenHeight;

    private Group logo;

    private Label label;

    private Button uploadButton;
    private Button downloadButton;
    private Button yesButton;
    private Button noButton;
    private Button chooseCornersButton;
    private Button autoChooseCornersButton;
    private Button warpButton;

    private ImageView imageView;

    private FileChooser fileChooser;
    private File file;
    private String path;
    private BufferedImage newImage;

    // Constructor
    public CamScanner() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.getWidth();
        screenHeight = screenSize.getHeight();

        // Create rectangle
        Rectangle csRect = new Rectangle();
        csRect.setX((screenWidth/2)-200);
        csRect.setY(50.0);
        csRect.setWidth(400.0);
        csRect.setHeight(200.0);
        csRect.setArcWidth(50.0);
        csRect.setArcHeight(50.0);
        csRect.setFill(Color.DARKSEAGREEN);
        csRect.setStrokeWidth(5.0);
        csRect.setStroke(Color.BLACK);

        // Create text
        Text csText = new Text();
        csText.setFont(Font.font("verdana",
                FontWeight.LIGHT,
                FontPosture.ITALIC,
                50));
        csText.setText("CamScanner");
        csText.setFill(Color.GHOSTWHITE);
        csText.setStrokeWidth(1.5);
        csText.setStroke(Color.BLACK);
        csText.setX((screenWidth/2)-160);
        csText.setY(160.0);

        // Create logo
        this.logo = new Group();
        ObservableList<Node> nodes = logo.getChildren();
        nodes.addAll(csRect, csText);

        uploadButton = new Button("Upload image");
        downloadButton = new Button("Download image");
        yesButton = new Button("Yes");
        noButton = new Button("No");
        chooseCornersButton = new Button("Manually corners");
        autoChooseCornersButton = new Button("Automatically choose");
        warpButton = new Button("Get new image");
        warpButton.setDisable(true);

        label = new Label();
        label.setText("No image selected.");
        imageView = new ImageView();
        imageView.setImage(null);
        newImage = null;

        fileChooser = new FileChooser();
        file = null;
        path = null;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stage = primaryStage;

        // Create scene
        VBox mainPage = new VBox(10, logo, label, uploadButton);
        mainPage.setPrefSize(screenWidth, screenHeight-70);
        mainPage.setAlignment(Pos.CENTER);

        Scene mainScene = new Scene(mainPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        mainScene.setFill(Color.LIGHTGRAY);

        stage.setTitle("CamScanner");
        stage.setScene(mainScene);
        stage.show();

        uploadButton.setOnAction(e -> {
            try {
                uploadEvent(e);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void uploadEvent(ActionEvent e) throws FileNotFoundException {
        file = fileChooser.showOpenDialog(stage);
        System.out.println(file);

        if (file != null) {
            path = file.getAbsolutePath();
            label.setText(path + " selected");
        }
        else {
            return;
        }

        InputStream imagePath = new FileInputStream(path);
        Image image = new Image(imagePath);
        imageView.setImage(image);
        imageView.setFitHeight(screenHeight/2);
        imageView.setFitWidth(screenWidth/2);
        imageView.setPreserveRatio(true);

        // Create scene
        Label verifyLabel = new Label("Is this the correct image?");
        HBox verifyButtons = new HBox(10, yesButton, noButton);
        verifyButtons.setAlignment(Pos.CENTER);

        VBox uploadPage = new VBox(10, logo, imageView, label, verifyLabel, verifyButtons);
        uploadPage.setPrefSize(screenWidth, screenHeight-70);
        uploadPage.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(uploadPage);
        scrollPane.setFitToHeight(true);

        Scene uploadScene = new Scene(uploadPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        uploadScene.setFill(Color.LIGHTGRAY);

        stage.setScene(uploadScene);
        stage.show();

        yesButton.setOnAction(this::cornersEvent);

        noButton.setOnAction(ex -> {
            try {
                label.setText("No image selected.");
                start(stage);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        });
    }

    public void cornersEvent(ActionEvent e) {
        label.setText("Would you like to manually or automatically choose the corners?");
        HBox buttons = new HBox(10, chooseCornersButton, autoChooseCornersButton);
        buttons.setAlignment(Pos.CENTER);

        VBox cornerPage = new VBox(10, logo, label, buttons);
        cornerPage.setAlignment(Pos.CENTER);

        Scene cornerScene = new Scene(cornerPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        cornerScene.setFill(Color.LIGHTGRAY);

        stage.setScene(cornerScene);
        stage.show();

        //chooseCornersButton.setOnAction(this::manualEvent);

        autoChooseCornersButton.setOnAction(ex -> {
            try {
                loadEvent(ex, null);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        });
    }

    public void manualEvent(ActionEvent e) {
        label.setText("Choose the corners");

        // corner picker
        int [][] corners = new int[4][2];
        boolean cornersChosen = false;

        //corners written as [tl, bl, br, tr]

        // REMEMBER TO CHANGE CORNER THAT IS CLOSEST TO ANOTHER CORNER
        // IF LENGTH IS 4


        //VBox cornerPage = new VBox(10, csRect, csText, label, ???, warpButton);
        //Scene cornerScene = new Scene(cornerPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        //cornerScene.setFill(Color.LIGHTGRAY);

        //stage.setScene(cornerScene);
        stage.show();

        if (cornersChosen) warpButton.setDisable(false);
/*
        warpButton.setOnAction(ex -> {
            try {
                loadEvent(ex, corners);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        });

 */
    }

    public void loadEvent(ActionEvent e, int[][] corners) throws IOException {
        label.setText("Creating new image...");
        VBox loadPage = new VBox(10, logo, label);
        Scene loadScene = new Scene(loadPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        loadScene.setFill(Color.LIGHTGRAY);

        stage.setScene(loadScene);
        stage.show();

        CamScannerLogic logic = new CamScannerLogic();
        this.newImage = logic.getNewImage(this.path, corners);

        warpEvent();
    }
    public void warpEvent() {
        label.setText("New image.");
        imageView.setImage(null);


        VBox downloadPage = new VBox(10, logo, imageView, label, downloadButton);
        downloadPage.setPrefSize(screenWidth, screenHeight-70);
        downloadPage.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(downloadPage);
        scrollPane.setFitToHeight(true);

        Scene downloadScene = new Scene(downloadPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        downloadScene.setFill(Color.LIGHTGRAY);

        stage.setScene(downloadScene);
        stage.show();

        downloadButton.setOnAction(this::downloadEvent);
    }

    public void downloadEvent(ActionEvent e) {
        CamScannerLogic logic = new CamScannerLogic();

        // Save the BufferedImage to a file
        try {
            File outputFile = new File(logic.getNewImagePath(this.path));
            assert this.newImage != null;
            ImageIO.write((RenderedImage) newImage, "png", outputFile);
            System.out.println("New image saved.");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        label.setText("New image saved.");

        VBox finishedPage = new VBox(10, logo, label);
        finishedPage.setPrefSize(screenWidth, screenHeight-70);
        finishedPage.setAlignment(Pos.CENTER);

        Scene finishedScene = new Scene(finishedPage, screenWidth, screenHeight-70);

        // Set the color and title, add the scene to the stage, and show the stage
        finishedScene.setFill(Color.LIGHTGRAY);

        stage.setScene(finishedScene);
        stage.show();

        // set a timeout of 3 seconds then return to start scene
    }
}