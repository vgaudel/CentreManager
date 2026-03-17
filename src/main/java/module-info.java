module fr.dawan.CenterManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires java.logging;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client; 

    opens fr.dawan.CenterManager.app to javafx.fxml;
    opens fr.dawan.CenterManager.controller to javafx.fxml;
    exports fr.dawan.CenterManager.app;
}