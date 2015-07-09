package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.layout.*;
import javafx.scene.control.*;

// simple convienence to lump a slider and a label together

class LabeledSlider extends HBox {
    Label label;
    Slider slider;
    LabeledSlider(String name, double min, double max, double value) {
        slider = new Slider(min, max, value);
        label = new Label(name);
        label.setPrefWidth(60);
        label.setLabelFor(slider);
        this.getChildren().addAll(label, slider);
    }
}
