package fr.dawan.CenterManager.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import fr.dawan.CenterManager.dao.Database;

public class CenterManagerApplication extends Application{

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Database.initDatabase();
        scene = new Scene(loadFXML("/view/main"), 1200, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CenterManagerApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        Application.launch(CenterManagerApplication.class, args);
    }

}
