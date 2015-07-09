package uk.co.bedroomcoders.chrisc.LogicEdit;

// provides a visual indication of a logic level also terminating
// a branch as it has no output.

class Out extends Logic {

	boolean state;

	public Out(Panel Parent) { 
		super(Parent);
		if (Parent==null) return; // happens when making a dummy logic during discovery
	
		numInputs=1;
		numOutputs=0;
		
		initialiseInterface();

	} 
	
	void process(StateEvent se) {
		state = se.newState;
		
		if (state) {
			parent.logicPane.setStyle("-fx-background-color: red;");
		} else {
			parent.logicPane.setStyle("-fx-background-color: saddlebrown;");
		}
	}
}
