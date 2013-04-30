
public class MomentEmoteSec {
	private String secondaryString;
	private int size,y;
	private double secondaryVal;
	
	public MomentEmoteSec (String secondaryString, double secondaryVal){
		this.secondaryString = secondaryString;
		this.secondaryVal = secondaryVal;
	}

	public String getSecondaryString() {
		return secondaryString;
	}

	public void setSecondaryString(String secondaryString) {
		this.secondaryString = secondaryString;
	}

	public double getSecondaryVal() {
		return secondaryVal;
	}

	public void setSecondaryVal(double secondaryVal) {
		this.secondaryVal = secondaryVal;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
