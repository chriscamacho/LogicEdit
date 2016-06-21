package uk.co.bedroomcoders.chrisc.LogicEdit;

import javafx.beans.property.*;
import javafx.beans.value.*;

import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Rotate;

import javafx.stage.Stage;

import javafx.event.*;
import javafx.geometry.Pos;
import javafx.application.Application;
import java.util.Random;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.lang.StackTraceElement;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

// the main GUI of the application, also in doTick all stateEvents are
// handled as their time (tick) becomes current

public class Main extends Application {

	public static ArrayList<LogicBundle> logicClasses = new ArrayList<LogicBundle>();	
    public static Panel mainPane;
    public static final int GateDelay = 4; // in ticks (all gates have a delay)
	public static Panel selectedPane=null;
	public static Output lastOutputClicked=null;
	
	Stage mainStage = null;
	Pane paneHolder; // holds the main pane
	Label infoLabel;
    LabeledSlider scaleSlider;
    ChoiceBox<LogicBundle> logicChoice;
    private ArrayList<StateEvent> updateList = new ArrayList<StateEvent>();
	
	static long Tick=0;


    public static void main(String[] args) { launch(args); }
    public void start(final Stage stage) {
        stage.setTitle("Logice");
        mainStage=stage;

		scaleSlider = new LabeledSlider("Scale",.2,2,1);

        mainPane = new Panel(null, scaleSlider.slider.valueProperty(), true);
        mainPane.setStyle("-fx-background-color: red;");
        mainPane.isParent=true;
        
        paneHolder = new Pane();
        VBox controls = new VBox(10);
        controls.setStyle("-fx-background-color: lightblue; -fx-padding: 10;");
        controls.setAlignment(Pos.TOP_CENTER);
        
        makeLogicList();
        
        logicChoice = new ChoiceBox<LogicBundle>();
        logicChoice.getItems().addAll(logicClasses); 
        logicChoice.getSelectionModel().clearAndSelect(0);
        

        
        Button paneButton = new Button("New Gate");
        //paneButton.setStyle("-fx-font-size: 16; -fx-text-alignment: center; -fx-padding: 10; -fx-graphic-text-gap: 10;");
        paneButton.setOnAction((e)->onNewGateAction(e));

		Button saveButton = new Button("Save");
		saveButton.setOnAction((e)->onSaveAction(e));

		Button newButton = new Button("New");
		newButton.setOnAction(e->onNewWorkspace(e));

		Button loadButton = new Button("Load");
		loadButton.setOnAction(e->onLoad(e));
		
		Button rrotButton = new Button("R.Right");
		rrotButton.setOnAction(e->onRrot(e));
		
		infoLabel = new Label("info");
		
		Button eventDump = new Button("dump events");
		eventDump.setOnAction(e->onEventDump(e));

        controls.getChildren().addAll(logicChoice, paneButton, new Separator(), 
										scaleSlider, new Separator(), 
										saveButton, newButton, loadButton, new Separator()
										//,rrotButton
										,infoLabel
										,eventDump
										);
        controls.setPrefWidth(180);
        controls.setMinWidth(180);
        controls.setMaxWidth(Control.USE_PREF_SIZE);

        // layout the scene.
        HBox layout = new HBox();
        
        paneHolder.getChildren().add(mainPane);
        layout.getChildren().addAll(controls, paneHolder);
        HBox.setHgrow(mainPane, Priority.ALWAYS);
        mainPane.scaleXProperty().bind(scaleSlider.slider.valueProperty());
        mainPane.scaleYProperty().bind(scaleSlider.slider.valueProperty());
        controls.toFront();
        layout.setPrefSize(800,600);
        final Scene scene = new Scene(layout);

        scene.setOnKeyPressed(e->onKeyPressed(e));

        // show the stage.
        stage.setScene(scene);

        stage.show();
        mainPane.setPrefSize(4096, 4096);
        mainPane.setTranslateX(-1648);
        mainPane.setTranslateY(-1848);
        
        BackgroundImage myBI= new BackgroundImage(new Image("gfx/grid.png"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
				BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		mainPane.setBackground(new Background(myBI));
		
		doTick();
    }
    
    // process all events for the current tick
    // a stateEvent may change the input of another gate, probably causing
    // it to emit a stateEvent itself
    
    void doTick() {
		
		//if (StateEvent.events.size()!=0) System.out.println("tick "+Tick+" "+StateEvent.events.size()+" events");
		
		boolean done=false;
		while (!done) {
			
			// reduce feedback events
			for (Iterator<StateEvent> iterator = StateEvent.events.iterator(); iterator.hasNext(); ) {
				StateEvent se = iterator.next(); 
				for (Iterator<StateEvent> iterator2 = StateEvent.events.iterator(); iterator2.hasNext(); ) {
					StateEvent se2 = iterator2.next();
					if (se!=se2) if (se2.equals(se)) se2.processed = true; 			
				}
			}
			
			done = true;
			for (ListIterator<StateEvent> iter = StateEvent.events.listIterator(); iter.hasNext(); ) {
				
				StateEvent se = iter.next();
				if (se.tick<=Tick && !se.processed) {
					done=false;
					se.processed=true;
					//System.out.println("targetLogic="+se.targetLogic);	
					updateList.add(se);
				}
			}
			
			// avoids comodification
			for (ListIterator<StateEvent> iter = updateList.listIterator(); iter.hasNext(); ) {
				StateEvent se = iter.next();
				se.targetLogic.process(se);
			}
			
			updateList.clear();



			
			// remove all the processed events
			for (ListIterator<StateEvent> iter = StateEvent.events.listIterator(); iter.hasNext(); ) {
				StateEvent se = iter.next();
				if (se.processed) iter.remove();
			}
		}
		
		Tick++;
		Timeline t = new Timeline( new KeyFrame(Duration.millis(2), e->doTick()));
		t.setCycleCount(1);
		t.play();
		infoLabel.setText(StateEvent.events.size()+" events");
	}
    
	// only has its own method to keep it out of the way of start
	// TODO tidy start method!
	private void makeLogicList() {

		String packageName = getClass().getPackage().getName();		
		File folder = new File("./bin/"+packageName.replace(".","/"));
		File[] listOfFiles = folder.listFiles();

		for (File f: listOfFiles) {
			String name = f.getName().replace(".class","");
			Class<?> clazz=null;
			Constructor<?> ctor=null;
			Logic lg=null;
			try {
				clazz = (Class<?>)Class.forName(packageName+"."+name);
				if (clazz==null) System.out.println("class null");
				ctor=clazz.getConstructor(Panel.class);
				if (ctor!=null) {
					lg=(Logic)ctor.newInstance((Object)null);
					if (lg!=null) {
						// if we got here without an exception we must
						// have a Child of Logic (Logic constructor deliberatly protected)
						logicClasses.add(new LogicBundle(name, ctor));
						
					}
				}
			} catch (Exception e) {

				if (!(e instanceof java.lang.NoSuchMethodException)) 
					System.out.println(f.getName()+" "+e.getClass().getName()); 
					
					//StackTraceElement[] ste=e.getStackTrace();
					//System.out.println(ste[0].toString()); // so it dumps to stdout not stderr
					//System.out.println(ste[1].toString());
					//System.out.println(ste[2].toString());
					//System.out.println(ste[3].toString());
				
				
			}

		}
		java.util.Collections.sort(logicClasses);
		Logic.nextId=0; // reset the id's after making logics for discovery
		Input.nextId=0;
		Output.nextId=0;
		//for (LogicBundle l: logicClasses) System.out.println(">>>"+l.name);		

	}

	public void onKeyPressed(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.DELETE)) {
			if (selectedPane != null) {
				if (selectedPane!=mainPane) {
					Panel.remove(selectedPane);		
				}
			}
		}
	}
	
	public void onSaveAction(ActionEvent actionEvent) {

		FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./circuits"));
		fileChooser.setTitle("Save project File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
		File selectedFile = fileChooser.showSaveDialog(mainStage);
		if (selectedFile == null) return;

		File fout = new File(selectedFile.getAbsolutePath());
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(fout);
		} catch(Exception e) {
			System.out.println(e);
			return;
		}
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));


		String project="<Project name=\"project1\">\n";

		for (ListIterator<Panel> iter = Panel.panes.listIterator(); iter.hasNext(); ) {
			Panel dp = iter.next();
			project=project+dp.toXml();		
		}
		
		project=project+"</Project>\n";

		try {			
			bw.write(project);
			bw.close();
		} catch(Exception e) {
			System.out.println(e);
		}

	}

	public void onNewWorkspace(ActionEvent actionEvent) {
		ArrayList<?> tmp = (ArrayList<?>)Panel.panes.clone();
		for (ListIterator<?> iter = tmp.listIterator(); iter.hasNext(); ) {
			Panel p = (Panel)iter.next();
			Panel.remove(p);
		}
		Panel.nextId=0;
		Logic.nextId=0;
		Input.nextId=0;
		Output.nextId=0;
		Wire.nextId=0;		
	}
	
	public void onLoad(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./circuits"));
		fileChooser.setTitle("Open project File");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
		File selectedFile = fileChooser.showOpenDialog(mainStage);
		if (selectedFile == null) return;
		
		onNewWorkspace(null);
		loader.load(selectedFile.getPath());
	}
	
	public void onNewGateAction(ActionEvent actionEvent) {

		final Panel newPane = new Panel(mainPane);
		LogicBundle lb = logicChoice.getSelectionModel().getSelectedItem();
		Logic l = lb.createInstance(newPane);
		newPane.logic = l;
		//newPane.setLabelText(lb.toString());
		newPane.setLabelText(lb.toString()+l.id);
		
		mainPane.getChildren().add(newPane);

		//System.out.println(newPane.getWidth()); // zip!

		double s = Panel.Scale.getValue();
		Transform t = mainPane.getLocalToParentTransform();
		newPane.setTranslateX((-t.getTx()+(paneHolder.getWidth()/2))/s);
		newPane.setTranslateY((-t.getTy()+(paneHolder.getHeight()/2))/s);
		
		selectedPane=newPane;
	}
	
	public void onRrot(ActionEvent actionEvent) {
		if (selectedPane==null) return;
		double a = selectedPane.rotation.getAngle();
		//double a = selectedPane.getRotate();
		a=a+90;
		selectedPane.rotation.setAngle(a);
		//selectedPane.setRotate(a);
	}

	public void onEventDump(ActionEvent ae) {
		StateEvent.dumpEvents();
	}
}
