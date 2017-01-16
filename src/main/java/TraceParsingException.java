public class TraceParsingException extends Exception {

	private static final long serialVersionUID = 1L;

	public TraceParsingException() {
		super("ERROR parsing trace. ");
	}

	public TraceParsingException(String file, String msg) {
		super("ERROR parsing trace from file '"+file+"' : \n"+msg);
	}

}
