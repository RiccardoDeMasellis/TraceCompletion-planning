public class Variable {

	public static final short INTERNAL = 1;
	public static final short EXOGENOUS = 2;

	private String name;
	private short type;
	private boolean value;

	public Variable(){
	}
	public Variable(String name, boolean value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public boolean isTrue(){
		return this.value;
	}
	public void setValue(boolean value){
		this.value = value;
	}
	/**
	 * @return A string representing the value of the variable
	 * 	 	as a constraint in k, thus, (name) if is true,
	 * 		otherwise (not name) 
	 */
	public String valuatedInK(){
		return value?name:"not "+name;
	}
	public String toString(){
		return "V::"+name+"-"+(type==INTERNAL?"internal":(type==EXOGENOUS?"exogenous":type));
	}
}
