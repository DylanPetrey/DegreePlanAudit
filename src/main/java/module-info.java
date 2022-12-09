/*
    make sure the following command is added to the VM classpath:
    --add-exports javafx.base/com.sun.javafx.event=org.controlsfx.controls
*/

module utd.dallas {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.controlsfx.controls;
    requires org.apache.pdfbox;
    requires json.simple;
    requires json.path;
    requires org.apache.poi.ooxml;
    requires java.desktop;

    opens utd.dallas.frontend to javafx.fxml;
    exports utd.dallas.frontend;
    opens utd.dallas.backend to javafx.fxml;
    exports utd.dallas.backend;
}