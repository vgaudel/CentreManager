module fr.dawan.CenterManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires java.logging;

    opens fr.dawan.CenterManager.app to javafx.fxml;
    opens fr.dawan.CenterManager.controller to javafx.fxml;

    exports fr.dawan.CenterManager.app;
}
