package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.scene.input.*;

// a source input to the "circuit" while it has no normal input, its
// state can be changed interactivly by the user

class In extends Logic {

	boolean state;

	public In(Panel Parent) { 
		super(Parent);
		if (Parent==null) return; // happens when making a dummy logic during discovery
	
		numInputs=0;
		numOutputs=1;
		
		initialiseInterface();
	
		if (parent.logicPane!=null) parent.logicPane.setOnMouseClicked((e)->mouseClicked(e));
	} 
	
	void process(StateEvent se) {
		if (outputs[0].connectedTo!=null)
			new StateEvent(Main.Tick, outputs[0].connectedTo.to, state );
	}
	
	public void mouseClicked(MouseEvent me) {
		state = !state;
		if (state) {
			parent.logicPane.setStyle("-fx-background-color: red;");
		} else {
			parent.logicPane.setStyle("-fx-background-color: saddlebrown;");
		}
		me.consume();
		if (outputs[0].connectedTo!=null)
			new StateEvent(Main.Tick, outputs[0].connectedTo.to, state );
	}
 
}
