import java.util.Comparator;


/**
 * @author Clint Mullins
 *
 */
public class MomentEmotion{
	private String primaryString; //name of primary emotion
	private double primaryVal;   //primary emotion relatedness
	private MomentEmoteSec[] secEmote; //holds secondary emotions with name, value, and size

	/**
	 * One primary emotion with its secondary emotions defined in one Moment. This saves the relatedness.
	 * 
	 * @param primaryString
	 * @param secondaryString
	 * @param primary
	 * @param secondary
	 */
	public MomentEmotion(String primaryString, String[] secondaryString, double primary, double[] secondaryVal){
		this.primaryString = primaryString;
		this.primaryVal = primary;
		secEmote = new MomentEmoteSec[secondaryString.length];
		for (int i=0;i<secondaryString.length;i++){
			secEmote[i]=new MomentEmoteSec(secondaryString[i],secondaryVal[i]);
		}
		sortSecondary();
	}
	
	/**
	 * Sorts the secondary emotions
	 */
	public void sortSecondary(){
		for (int i=1;i<secEmote.length;i++){
			for (int j=i;j>0;j--){
				MomentEmoteSec tempEmoteSec = secEmote[j];
				if (tempEmoteSec.getSecondaryVal()>secEmote[j-1].getSecondaryVal()){
					secEmote[j] = secEmote[j-1];
					secEmote[j-1] = tempEmoteSec;
				}
			}
		}
	}
	
	public double getSecondaryVal(String sec){
		for (MomentEmoteSec momSec: secEmote){
			if (momSec.getSecondaryString().equals(sec)){
				return momSec.getSecondaryVal();
			}
		}
		return 0.0;
	}

	public String getPrimaryString() {
		return primaryString;
	}

	public void setPrimaryString(String primaryString) {
		this.primaryString = primaryString;
	}

	public double getPrimaryVal() {
		return primaryVal;
	}

	public void setPrimaryVal(double primaryVal) {
		this.primaryVal = primaryVal;
	}
	
	public MomentEmoteSec[] getSecEmote() {
		return secEmote;
	}

	public void setSecEmote(MomentEmoteSec[] secEmote) {
		this.secEmote = secEmote;
	}
}
