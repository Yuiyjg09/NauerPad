import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;


public class NauerApp extends Application {
    Boolean enableMenuItems;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("NauerPad [Unsaved File ...]");
        GridPane pane = new GridPane();
        this.initContent(pane, primaryStage);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    TextArea textArea;
    File currentFile;
    final FileChooser fileChooserOpen = new FileChooser();
    final FileChooser fileChooserSave = new FileChooser();
    protected void initContent(GridPane pane, Stage primaryStage) {
        pane.setVisible(true);
        pane.setGridLinesVisible(false);
        MenuBar topPanel = new MenuBar();
        Menu fileMenu = new Menu("File");
        topPanel.getMenus().add(fileMenu);
        MenuItem newOption = new MenuItem("New");
        MenuItem openOption = new MenuItem("Open");
        MenuItem saveOption = new MenuItem("Save");
        MenuItem closeOption = new MenuItem("Close");
        fileMenu.getItems().add(newOption);
        fileMenu.getItems().add(openOption);
        fileMenu.getItems().add(saveOption);
        fileMenu.getItems().add(closeOption);
        pane.add(topPanel, 0, 0);
        textArea = new TextArea();
        pane.add(textArea, 0, 1);

        newOption.setOnAction(event -> this.newFileOption(primaryStage));
        openOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                configureFileChooserOpen(fileChooserOpen);
                currentFile = fileChooserOpen.showOpenDialog(null);
                if (currentFile != null) {
                    openTheFile(currentFile, primaryStage);
                }
            }
        });
        saveOption.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                configureFileChooserSave(fileChooserSave, currentFile);
                currentFile = fileChooserSave.showSaveDialog(null);
                if (currentFile != null) {
                    try {
                        FileWriter fileWriter = new FileWriter(currentFile);
                        fileWriter.write(textArea.getText());
                        fileWriter.close();
                        primaryStage.setTitle("NauerPad [" + currentFile.getName() + "]");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        closeOption.setOnAction(event -> this.closeApp());
    }

    private static void configureFileChooserOpen(final FileChooser fileChooser) {
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    private static void configureFileChooserSave(final FileChooser fileChooser, File currentFile) {
        fileChooser.setTitle("Save File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

    }

    private void newFileOption(Stage primaryStage) {
        textArea.setText(" ");
        primaryStage.setTitle("NauerPad [Unsaved File ...]");
    }

    private void openTheFile(File file, Stage stage) {
        try {
            Scanner in = new Scanner(file);
            textArea.setText(" ");
            while (in.hasNext()) {
                textArea.appendText(in.nextLine() + "\n");
            }
            stage.setTitle("NauerPad [" + file.getName() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeApp() {
        System.exit(0);
    }

}
