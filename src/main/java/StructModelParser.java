/**
 *
 * @author ivazquezsandoval
 */

public class StructModelParser {
	
	/**
	 * Formats model's elements name so that they are well formed
	 * names for actions and conditions in K.
	 * Actions can not start with capital letters
	 * @param attribName The string to be formatted
	 * @return
	 */
	protected static String formatVarName(String attribName){
		String wellFormed = attribName.replaceAll("\\s+","_");
		return Character.toLowerCase(wellFormed.charAt(0)) + wellFormed.substring(1);
	}



}

