package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.*;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.shape.*;
import java.util.ArrayList;
import javafx.scene.transform.Rotate;

// a draggable panel that holds together all the graphical elements
// of a gate/node (the pins (inputs and outputs) and the logic panel) 

class Panel extends Pane {

	static int nextId=-1; // so mainpane gets zero and gates start at 1
    static DoubleProperty Scale;
    static final double defaultWidth=60;
    static final double defaultHeight=30;
    double deltax=0;
    double deltay=0;
    Label label;
    boolean isParent=false;
    Rotate rotation = new Rotate(0);

	Logic logic;

    public static ArrayList<Panel> panes = new ArrayList<Panel>();
		
	final DropShadow dropShadow = new DropShadow();
	final Glow glow = new Glow();
	Pane logicPane;
		
	Panel(Parent p) {
		this(p,Scale,false);
	}

    Panel(Parent p, DoubleProperty scale, boolean isRoot) {
		getTransforms().add(rotation);
		rotation.setPivotX(defaultWidth/2);
		rotation.setPivotY(defaultHeight/2);
		
		isParent = isRoot;
		nextId++;
        setId("Panel"+nextId);
        Scale=scale;

        if (!isParent) {
			panes.add(this);
			VBox vbox = new VBox();
			
			// would be nice to let extra padding make the size
			// but then how to position logic while vbox still
			// doesn't have a size...
			// would prefer not to have bindings do this.
			vbox.setPrefSize(defaultWidth,defaultHeight);
			vbox.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
			vbox.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
			vbox.setAlignment(Pos.TOP_CENTER);
			
			String bgColor = "#22ff44";
			vbox.setStyle("-fx-background-radius: 5; -fx-background-color: linear-gradient(to bottom, " + bgColor + ", derive(" + bgColor + ", 20%)); -fx-text-fill: ladder(" + bgColor +", lavender 49%, midnightblue 50%); -fx-font: 12px 'monospace'; -fx-padding:2;");
			getChildren().add(vbox);
			
			label=new Label();
			
			label.setStyle("-fx-font: normal bold 12px 'monospace' ; -fx-padding:2;");		
			label.setWrapText(true);	
			label.setTextAlignment(TextAlignment.CENTER);
			vbox.setEffect(dropShadow);
			vbox.getChildren().add(label);

			// logicPane is the user interface for the logic
			logicPane = new Pane();
			logicPane.setStyle("-fx-background-color: saddlebrown;");
			logicPane.setPrefSize(defaultWidth/4,defaultHeight/4);
			logicPane.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
			logicPane.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
			vbox.getChildren().add(logicPane);
		}
        
        setOnMouseClicked((e)->mouseClicked(e));
        setOnMousePressed((e)->mousePressed(e));
        setOnMouseReleased((e)->mouseReleased(e));
		setOnMouseDragged((e)->mouseDragged(e));
        setOnMouseEntered((e)->mouseEntered(e));
        setOnMouseExited((e)->mouseExited(e));

		//System.out.println("created "+this);

    }

	public void setLabelText(String message) {
		label.setText(message);
	}
    
    public static void remove(Panel dp) {
		//System.out.println("removing");
		for (int i=0;i<dp.logic.inputs.length;i++) 
			if (dp.logic.inputs[i]!=null && dp.logic.inputs[i].connectedTo!=null) 
						Wire.remove(dp.logic.inputs[i].connectedTo);
		for (int i=0;i<dp.logic.outputs.length;i++) 
			if (dp.logic.outputs[i]!=null && dp.logic.outputs[i].connectedTo!=null) 
						Wire.remove(dp.logic.outputs[i].connectedTo);
		Panel.panes.remove(dp);
		Main.mainPane.getChildren().remove(dp);
	}
	
	public String toXml() {
		String x="<Panel id=\""+getId();
		x=x+"\" x=\""+getTranslateX()+"\" ";
		x=x+"y=\""+getTranslateY()+"\" ";
		x=x+">\n";
		x=x+"    "+logic.toXml()+"\n";
		for (int i=0;i<logic.numInputs;i++) {
			x=x+"        "+logic.inputs[i].toXml()+"\n";
		}
		for (int i=0;i<logic.numOutputs;i++) {
			x=x+"        "+logic.outputs[i].toXml()+"\n";
		}
		x=x+"    </Logic>\n";
		x=x+"</Panel>\n";

		return x;
	}
	
	public void mouseClicked(MouseEvent mouseEvent) {
		if (this!=Main.mainPane) Main.selectedPane=this;
		//System.out.println("clicked "+this);
		toFront();
	}
	
	
	public void mousePressed(MouseEvent mouseEvent) {
		double v = Scale.getValue();
		if (isParent) v=1;
		deltax = getTranslateX() - mouseEvent.getSceneX() / v;
		deltay = getTranslateY() - mouseEvent.getSceneY() / v;

		setCursor(Cursor.MOVE);
		mouseEvent.consume();
	}
	
	public void mouseReleased(MouseEvent mouseEvent) {
		setCursor(Cursor.HAND);
		mouseEvent.consume();
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		double v = Scale.getValue();
		if (isParent) v=1;
		setTranslateX(deltax + mouseEvent.getSceneX() / v );
		setTranslateY(deltay + mouseEvent.getSceneY() / v );
		mouseEvent.consume();
	}

	public void mouseEntered(MouseEvent mouseEvent) {
		setCursor(Cursor.HAND);
		dropShadow.setInput(glow);
		mouseEvent.consume();
	}
	
	public void mouseExited(MouseEvent mouseEvent) {
		dropShadow.setInput(null);
		mouseEvent.consume();
	}
				
}
