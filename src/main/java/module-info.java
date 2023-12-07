module VisualiseNodeGraph {

    requires javafx.controls;
    requires javafx.fxml;


    opens graphvisualisation to javafx.fxml;
    exports graphvisualisation;



}