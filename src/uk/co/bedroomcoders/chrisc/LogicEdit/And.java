package uk.co.bedroomcoders.chrisc.LogicEdit;

// a single output is the logical AND of its 2 inputs

class And extends Logic {
	
	// must have a public constructor providing parent to the
	// super class
	public And(Panel Parent) { 
		super(Parent);
		if (Parent==null) return; // happens when making a dummy logic during discovery
	
		numInputs=2;
		numOutputs=1;
		
		initialiseInterface();	
		
		if (parent.logicPane!=null) parent.logicPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");		

	} 
	
	void process(StateEvent se) {

		se.target.state = se.newState;
		boolean state = inputs[0].state & inputs[1].state;
		/*
		if (state) {
			parent.logicPane.setStyle("-fx-background-color: red;");
		} else {
			parent.logicPane.setStyle("-fx-background-color: saddlebrown;");
		}
		*/
		if (outputs[0].connectedTo!=null)
			new StateEvent(Main.Tick+Main.GateDelay, outputs[0].connectedTo.to, state );
	}

}
