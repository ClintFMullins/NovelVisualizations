import java.awt.Color;

public class PrimaryEmotion {
	private String primaryString;   //primary emotion for the class
	private SecondaryEmotion[] secondary;  //emotions that comprise/help define the primary
	private float[] hsv = {0,100,100}; //color for this Emotion
	private Color primeColor;
	private int yTop, yBottom, yCenter; //represent graphically the top and bottom of our image
	
	public PrimaryEmotion(String primaryString, String[] secondaryStrings, double[] weights, int hue, int yTop, int yBottom){
		this.primaryString = primaryString;
		this.secondary = new SecondaryEmotion[secondaryStrings.length];
		hsv[0]=hue;
		this.yTop = yTop;
		this.yBottom = yBottom;
		this.setyCenter(Math.abs(yBottom-yTop)/2+yTop);
		int imageEmotionSlot = (yBottom-yTop)/secondaryStrings.length; //space for each secondary
		System.out.println("imageEmotionSlot: "+imageEmotionSlot);
    	int imageBottom = imageEmotionSlot+yTop;
    	int imageTop = yTop;
    	int svLow = 100-(secondaryStrings.length*4); //decides how low saturation and brightness values can be for secondary color
    	int[] secHsv = {hue,svLow,100}; //actual value for sat and bright for each secondary color
    	int svChange = ((100-svLow)*2)/(secondaryStrings.length-1); //gets our differences
    	System.out.println("PRIMARY "+primaryString+":");
		for (int i=0; i<secondaryStrings.length;i++){
			secondary[i] = new SecondaryEmotion(secondaryStrings[i],weights[i],secHsv,imageTop,imageBottom);
			imageBottom=(secondaryStrings.length-1==i?yBottom:imageBottom+imageEmotionSlot);
			imageTop+=imageEmotionSlot;
			//we want to run along the saturation and then the value for each hue
			if (secHsv[1]<100){ //this places saturation of the given hue that start from svLow and go to 100
				if (secHsv[1]+svChange>=100){
					secHsv[2]=100-(svChange-(100-secHsv[1]));  //carries over the difference
					secHsv[1]=100;  //sets the saturation at 100
				}
				else{ //this starts at 100 and decreases brightness (v) until there are no more secondary emotions
					secHsv[1]+=svChange;
				}
			}
			else{
				secHsv[2]-=svChange; //descend from 100 brightness(v) to about svLow
			}
		}
		setPrimeColor(Color.getHSBColor(hsv[0]/360, hsv[1]/100, hsv[2]/100));
	}
	
	public SecondaryEmotion getSecondaryEmotion(String emote){
		for (SecondaryEmotion tempSec: secondary){
			if (tempSec.getSecondary().equals(emote)){
				return tempSec;
			}
		}
		return null;
	}

	public String getPrimary() {
		return primaryString;
	}

	public SecondaryEmotion[] getSecondary() {
		return secondary;
	}

	public float[] getHSV() {
		return hsv;
	}
	
	public int getyTop() {
		return yTop;
	}

	public int getyBottom() {
		return yBottom;
	}

	public int getyCenter() {
		return yCenter;
	}

	public void setyCenter(int yCenter) {
		this.yCenter = yCenter;
	}

	public Color getPrimeColor() {
		return primeColor;
	}

	public void setPrimeColor(Color primeColor) {
		this.primeColor = primeColor;
	}
}
