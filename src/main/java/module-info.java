module utd.dallas {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires json.simple;
    requires json.path;

    opens utd.dallas.frontend to javafx.fxml;
    exports utd.dallas.frontend;
    opens utd.dallas.backend to javafx.fxml;
    exports utd.dallas.backend;
}