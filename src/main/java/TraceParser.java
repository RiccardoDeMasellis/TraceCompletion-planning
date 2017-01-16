import org.apache.log4j.Logger;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class TraceParser {

	private final XFactory factory; 
	private static final Logger classLog = Logger.getLogger(TraceParser.class.getName());


	public TraceParser() {
		factory = XFactoryRegistry.instance().currentDefault();
	}


	public static Set<List<ObservedAction>> parseTracesFile(String logFile) throws TraceParsingException {
		TraceParser read = new TraceParser();
		XLog log = read.readTracesLog(logFile);
		Set<List<ObservedAction>> trcs = new HashSet<List<ObservedAction>>();
		List<ObservedAction> obs;
		int order;

		for(XTrace trace: log){
			obs = new LinkedList<ObservedAction>();
			order=0;

			for(XEvent e : trace)
				obs.add(new ObservedAction(
						StructModelParser.formatVarName(e.getAttributes().get("concept:name").toString()),
						order++));
			trcs.add(obs);
		}
		classLog.info("Traces successfully parsed from "+logFile);
		return trcs;
	}


	public static List<ObservedAction> parseFirstTrace(String logFile) throws TraceParsingException {
		TraceParser read = new TraceParser();
		List<ObservedAction> obs = new LinkedList<ObservedAction>();
		XTrace trace = read.readTracesLog(logFile).get(0);
		ObservedAction act;
		List<Variable> obsVar;
		int order=1;

		for(XEvent e : trace){
			classLog.debug(e.getAttributes());
			//the observed action in ascendent order
			act = new ObservedAction(
					StructModelParser.formatVarName(e.getAttributes().get("concept:name").toString())
					, order++);
			obsVar = new LinkedList<Variable>();

			for(XAttribute elem : e.getAttributes().values())
				//get the container tag for an event in the file
				if(elem instanceof XAttributeContainer){
					for(Entry<String, XAttribute> v: elem.getAttributes().entrySet())
						//obsVar.add(new Variable(v.getKey(),
						obsVar.add(new Variable(StructModelParser.formatVarName(v.getKey()),
								v.getValue().toString().equalsIgnoreCase("true")?true:false));
					act.setObservedVar(obsVar);
					break;
				}
			obs.add(act);

		}
		classLog.info("Trace successfully parsed from "+logFile);
		return obs;
	}


	protected XLog readTracesLog(String xesFileName) throws TraceParsingException {
		File logFile = new File(xesFileName);
		return readLog(logFile);
	}


	/**
	 * Parses the given XES file
	 * @param logFile The name and path of the file to read and parse
	 * @return The parsed object containing the read set of traces
	 * @throws TraceParsingException For any exception occurring during the parsing process
	 */
	private XLog readLog(File logFile) throws TraceParsingException {
		String filename = logFile.getName();
		XParser parser;
		XLog log = null;

		if (filename.toLowerCase().endsWith(".xes") || filename.toLowerCase().endsWith(".xez")
				|| filename.toLowerCase().endsWith(".xes.gz")) {
			parser = new XesXmlParser(factory);
		} else 
			parser = new XMxmlParser(factory);

		try {
			log = parser.parse(logFile).get(0);
			if (log.isEmpty()) {
				classLog.warn("No process instances contained in log!");
			}
			// Create a default log name if not set
			if (XConceptExtension.instance().extractName(log) == null) 
				XConceptExtension.instance().assignName(log, "Anonymous log imported from " + filename);
		}catch(NullPointerException npe){
			throw new TraceParsingException(filename,"No process instances contained in log.");
		} catch (Exception e) {
			throw new TraceParsingException(filename,e.getMessage());
		}
		return log;
	}


	/**
	 * Creates a file in XES format describing the plans in the
	 * given set.
	 * @param plans A set of sequences of actions, each in the format "a_1; .... ;a_n"
	 * @param file The name of the file to create
	 * @throws IOException 
	 */
	public void createTraceXESfile(Set<String> plans, String file) throws IOException{
		XLog body = factory.createLog();
		XTrace trc;
		XAttributeMap namesMap;
		XEvent action;

		for(String plan: plans){
			trc = factory.createTrace();
			String [] acts = plan.split(";");

			//add actions as events to the trace
			for(int k=0; k<acts.length; k++){
				namesMap = factory.createAttributeMap();
				action = factory.createEvent();
				namesMap.put("concept:name", new XAttributeLiteralImpl("concept:name",acts[k].trim()));
				action.setAttributes(namesMap);
				trc.add(action);
			}
			//add plans to the log
			body.add(trc);		
		}
		writeXLog(body, file);
	}


	/**
	 * Writes an XLog in a file in XES format
	 * @param newLog The log to write
	 * @param xesName The name of the file to create
	 * @throws IOException If any exception occurs while writing 
	 */
	private void writeXLog(XLog newLog,String xesName) throws IOException {
		XesXmlSerializer xstream;
		OutputStream oStream = null;
		File sFile = null;
		try {
			sFile = new File(xesName);

			if (sFile.exists()) 
				sFile.delete();
			sFile.createNewFile();

			xstream = new XesXmlSerializer();
			oStream = new BufferedOutputStream(new FileOutputStream(sFile));
			if(newLog.isEmpty())
				classLog.warn("No traces found!");
			xstream.serialize(newLog, oStream);
		}catch(IOException e){
			throw new IOException("ERROR writing reconstructed traces file. "+e.getMessage());
		}finally{
			if(oStream != null)
				try{
					oStream.close();
				}catch(IOException e){
					e.printStackTrace();
				}
		}
	}

}
