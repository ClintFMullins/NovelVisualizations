import java.awt.Color;


public class SecondaryEmotion {
	private String secondary;  //emotions that comprise/help define the primary
	private double weight; //weights to offset a word's unfair advantage
	private int[] hsv; //color for this Emotion
	private int yTop, yBottom, yCenter; //represent graphically the top and bottom of our image
	private Color secColor;
	
	public SecondaryEmotion(String secondary, double weight, int[] hsv, int yTop, int yBottom){
		this.secondary = secondary;
		this.weight = weight;
		this.hsv = hsv;
		this.yTop = yTop;
		this.yBottom = yBottom;
		setyCenter((Math.abs(yBottom-yTop)/2)+this.yTop);
		System.out.println("H:"+hsv[0]+" S:"+hsv[1]+" V:"+hsv[2]+" ||Top/Bottom: "+getyTop()+"/"+getyBottom()+" ||Sec Y Center: "+getyCenter());
		setSecColor(Color.getHSBColor((float)(hsv[0]/360.0), (float)(hsv[1]/100.0), (float)(hsv[2]/100.0)));
	}
	
	public String getSecondary() {
		return secondary;
	}

	public double getWeight() {
		return weight;
	}
	
	public int[] getHsv() {
		return hsv;
	}

	public int getyTop() {
		return yTop;
	}

	public int getyBottom() {
		return yBottom;
	}
	
	public String toString(){
		return secondary;
	}

	public int getyCenter() {
		return yCenter;
	}

	public void setyCenter(int yCenter) {
		this.yCenter = yCenter;
	}

	public Color getSecColor() {
		return secColor;
	}

	public void setSecColor(Color secColor) {
		this.secColor = secColor;
	}
}
