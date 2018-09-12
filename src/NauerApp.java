import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
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
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 1280, 720);
        primaryStage.setScene(scene);
        this.initContent(pane, primaryStage);
        primaryStage.show();
    }

    TextArea textArea;
    File currentFile;
    final FileChooser fileChooserOpen = new FileChooser();
    final FileChooser fileChooserSave = new FileChooser();

    /**
     * Initializes the content on the stage
     * @param pane the BorderPane from start()
     * @param primaryStage the Stage
     */
    protected void initContent(BorderPane pane, Stage primaryStage) {
        pane.setVisible(true);

        //Creates the top panel for file, edit...
        MenuBar topPanel = new MenuBar();
        Menu fileMenu = new Menu("File");
        topPanel.getMenus().add(fileMenu);

        MenuItem newOption = new MenuItem("New | CTRL + n");
        MenuItem openOption = new MenuItem("Open | CTRL + o");
        MenuItem saveOption = new MenuItem("Save | CTRL + s");
        MenuItem closeOption = new MenuItem("Quit | CTRL + q");
        fileMenu.getItems().add(newOption);
        fileMenu.getItems().add(openOption);
        fileMenu.getItems().add(saveOption);
        fileMenu.getItems().add(closeOption);

        Menu textMenu = new Menu("Font");
        for (String font:
             javafx.scene.text.Font.getFontNames()) {
            MenuItem fontItem = new MenuItem(font);
            textMenu.getItems().add(fontItem);
        }
        topPanel.getMenus().add(textMenu);

        Menu textSizeMenu = new Menu("Font Size");
        for (int i = 10; i <= 36; i = i +2) {
            MenuItem fontSizeItem = new MenuItem("" + i);
            textSizeMenu.getItems().add(fontSizeItem);
        }
        topPanel.getMenus().add(textSizeMenu);
        pane.setTop(topPanel);

        //Creates the resizable TextArea
        textArea = new TextArea();
        textArea.setMaxWidth(1920);
        textArea.setMaxHeight(1080);
        HBox container = new HBox(textArea);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(1));
        HBox.setHgrow(textArea, Priority.ALWAYS);
        pane.setCenter(container);

        //Creates the bottom panel for settings...
        MenuBar bottomPanel = new MenuBar();
        Menu settings = new Menu("Settings");
        MenuItem languageSettings = new MenuItem("Language");
        settings.getItems().add(languageSettings);
        bottomPanel.getMenus().add(settings);

        pane.setBottom(bottomPanel);



        //Attaches functionality to file buttons
        newOption.setOnAction(event -> this.newFileOption(primaryStage));
        openOption.setOnAction(event -> this.openFileEvent(fileChooserOpen, primaryStage));
        saveOption.setOnAction(event -> this.saveFileEvent(fileChooserSave, primaryStage));
        closeOption.setOnAction(event -> this.closeApp());

        for (MenuItem fontMenu:
             textMenu.getItems()) {
            fontMenu.setOnAction(event -> this.setTextAreaFont(fontMenu.getText()));
        }

        for (MenuItem fontSizeMenu:
             textSizeMenu.getItems()) {
            fontSizeMenu.setOnAction(event -> this.setTextAreaFontSize(Integer.parseInt(fontSizeMenu.getText())));
        }

        //Key bindings for menu items
        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.N && event.isControlDown()) {
                    newFileOption(primaryStage);
                }
            }
        });

        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.O && event.isControlDown()) {
                    openFileEvent(fileChooserOpen, primaryStage);
                }
            }
        });

        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.S && event.isControlDown()) {
                    saveFileEvent(fileChooserSave, primaryStage);
                }
            }
        });

        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.Q && event.isControlDown()) {
                    closeApp();
                }
            }
        });

    }

    //Configurations --------

    private static void configureFileChooserOpen(final FileChooser fileChooser) {
        fileChooser.setTitle("Open Resource File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt")
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html")
        );
    }

    private static void configureFileChooserSave(final FileChooser fileChooser) {
        fileChooser.setTitle("Save File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

    }

    //Menu Buttons --------

    private void newFileOption(Stage primaryStage) {
        textArea.setText(" ");
        primaryStage.setTitle("NauerPad [Unsaved File ...]");
    }

    private void openFileEvent(FileChooser fileChooserOpen, Stage primaryStage) {
        configureFileChooserOpen(fileChooserOpen);
        currentFile = fileChooserOpen.showOpenDialog(null);
        if (currentFile != null) {
            openTheFile(currentFile, primaryStage);
        }
    }

    private void openTheFile(File file, Stage stage) {
        try {
            Scanner in = new Scanner(file);
            textArea.setText("");
            while (in.hasNext()) {
                textArea.appendText(in.nextLine() + "\n");
            }
            stage.setTitle("NauerPad [" + file.getName() + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFileEvent(FileChooser fileChooserSave, Stage primaryStage) {
        configureFileChooserSave(fileChooserSave);
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

    private void setTextAreaFont(String fontName) {
        textArea.setFont(Font.font(fontName));
    }

    private void setTextAreaFontSize(int size) {
        textArea.setFont(Font.font(size));
    }

    private void closeApp() {
        System.exit(0);
    }

}
