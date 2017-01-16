import java.util.List;

public class ObservedAction {

	private String id;
	private int order;
	//variables observed true in this step
	private List<Variable> observedVar;
	
	public ObservedAction(String id, int order) {
		this.id = id.replaceAll("\\s+", "_");
		this.order = order;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public List<Variable> getObservedVar() {
		return observedVar;
	}

	public void setObservedVar(List<Variable> observedVar) {
		this.observedVar = observedVar;
	}

	public String toString(){
		return "observed(" + id + "," + order + ")";
	}
}
