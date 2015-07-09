package uk.co.bedroomcoders.chrisc.LogicEdit;

import java.io.File;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

// creates a logic network and gui from an XML document

class loader {
	public static void load(String filename) {
		
		// we have to wire everything up after all gates have been
		// created, so use these hashmaps to record where the connections
		// go from (connectTos) and to (inputs) 
		HashMap<String,Input> inputs = new HashMap<String,Input>();
		HashMap<Output,String> connectTos = new HashMap<Output,String>();

		try {

			File fXmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			doc.getDocumentElement().normalize();
			
			Element docE=doc.getDocumentElement();
			NodeList nList = docE.getChildNodes();
		 
		    int maxPanelID=0;
		    int maxLogicID=0;
		    int maxInputID=0;
		    int maxOutputID=0;

		    
			for (int n = 0; n < nList.getLength(); n++) {
		 
				Node nNode = nList.item(n);

				if (nNode.getNodeName().equals("Panel")) {
					//System.out.println("id="+(((Element)nNode).getAttribute("id")));
					//System.out.println("x="+(((Element)nNode).getAttribute("x")));
					//System.out.println("y="+(((Element)nNode).getAttribute("y")));
					
					Panel newPanel = new Panel(Main.mainPane);
					String idString = (((Element)nNode).getAttribute("id"));
					newPanel.setId(idString);
					int id = Integer.parseInt(idString.substring(5));
					if (maxPanelID<id) maxPanelID=id;
					
					newPanel.setTranslateX(Double.parseDouble((((Element)nNode).getAttribute("x"))));
					newPanel.setTranslateY(Double.parseDouble((((Element)nNode).getAttribute("y"))));
					
					Main.mainPane.getChildren().add(newPanel);
					
					NodeList ll = ((Element)nNode).getElementsByTagName("Logic");
					if (ll.getLength()==1) {
						Node ln = ll.item(0);
						//System.out.println("    id="+(((Element)ln).getAttribute("id")));
						//System.out.println("    type="+(((Element)ln).getAttribute("type")));
						
						String LidString = (((Element)ln).getAttribute("id"));
						String typeString = (((Element)ln).getAttribute("type"));
						Logic logic = LogicBundle.get(typeString).createInstance(newPanel);
						newPanel.logic = logic;
						//newPanel.setLabelText(typeString);
						newPanel.setLabelText(typeString+id);
						int lid=Integer.parseInt(LidString.substring(5));
						logic.id=lid;
						if (maxLogicID<lid) maxLogicID=lid;
						
						
						NodeList il = ((Element)nNode).getElementsByTagName("Input");
						for (int i=0; i<il.getLength(); i++) {
							Node in = il.item(i);
							//System.out.println("        id="+(((Element)in).getAttribute("id")));
							//System.out.println("        state="+(((Element)in).getAttribute("state")));
							
							String inputid = (((Element)in).getAttribute("id"));
							String inputstate = (((Element)in).getAttribute("state"));
							logic.inputs[i].setId(inputid);
							if (inputstate.equals("true")) { logic.inputs[i].state=true; } else { logic.inputs[i].state=false; }
							inputs.put(inputid,logic.inputs[i]);
							int iid=Integer.parseInt(inputid.substring(5));
							if (maxInputID<iid) maxInputID=iid;
						}
						//System.out.println();
						NodeList ol = ((Element)nNode).getElementsByTagName("Output");
						for (int i=0; i<ol.getLength(); i++) {
							Node out = ol.item(i);
							//System.out.println("        id="+(((Element)out).getAttribute("id")));
							//System.out.println("        connectedTo="+(((Element)out).getAttribute("connectedTo")));
							
							String outputid = (((Element)out).getAttribute("id"));
							String connectedTo = (((Element)out).getAttribute("connectedTo"));
							logic.outputs[i].setId(outputid);
							connectTos.put(logic.outputs[i],connectedTo);
							int oid=Integer.parseInt(outputid.substring(6));
							if (maxOutputID<oid) maxOutputID=oid;
						}
												
					}
					
				}
				
			}
			
			Panel.nextId=maxPanelID+1;		// so adding new items to this
			Logic.nextId=maxLogicID+1;		// project will have unique id's
			Input.nextId=maxInputID+1;
			Output.nextId=maxOutputID+1;
				
		} catch (Exception e) {
			e.printStackTrace();
		}	


		// connect up the wires...
		Set<Output> froms = connectTos.keySet();
		for (Iterator<Output> iter = froms.iterator(); iter.hasNext(); ) {
			Output from = iter.next();
			Input to = inputs.get(connectTos.get(from));
			if (from!=null && to!=null) new Wire(from,to);
		}


		// now all wires are connected send  stateEvents to all the logics
		// with their own state - this will ensure the circuit is in an
		// a working state...
		StateEvent.events.clear();
		Main.Tick=0;

		for (Iterator<Output> iter = froms.iterator(); iter.hasNext(); ) {
			Output from = iter.next();
			Input to = inputs.get(connectTos.get(from));
			new StateEvent(Main.Tick+Main.GateDelay, to, to.state);
		}
	}
}
