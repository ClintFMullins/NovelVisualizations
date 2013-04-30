
public class MomentEmoteSpace {
	private String emotion;
	private boolean primary;
	private int top, bottom, points;
	private double value;
	private MomentEmoteSpace[] secondary;
	
	/**
	 * Primary Emote space initializer
	 * @param prime
	 */
	public MomentEmoteSpace(MomentEmotion momEmote){
		emotion = momEmote.getPrimaryString();
		primary = true;
		top = EmotionSpectrum.getPrimaryEmotion(emotion).getyTop();
		bottom = EmotionSpectrum.getPrimaryEmotion(emotion).getyBottom();
		secondary = new MomentEmoteSpace[momEmote.getSecEmote().length];  //secondary emotion list created
		for (int i=0; i<momEmote.getSecEmote().length;i++){
			String seconString = EmotionSpectrum.getPrimaryEmotion(emotion).getSecondary()[i].getSecondary();
			secondary[i] = new MomentEmoteSpace(seconString, momEmote.getSecondaryVal(seconString));
		}
	}
	
	/**
	 * If this is a secondary, we initialize here. No bounds are set yet. That comes later.
	 * @param emotion
	 * @param value
	 */
	public MomentEmoteSpace(String emotion, double value){
		this.emotion = emotion;
		primary = false;
		this.value = value;
	}
	
	/**
	 * secondary Emote space initializer
	 * @param sec
	 */
	public MomentEmoteSpace(SecondaryEmotion sec){
		emotion = sec.getSecondary();
		primary = false;
		top = sec.getyTop();
		bottom = sec.getyBottom();
	}
	
	public void assignSecondaries(){
		if (primary){ //can't assign secondaries if you are a secondary!
			int yspace = bottom - top;
			double total = 0;
			for (MomentEmoteSpace sec: secondary){ //get the total value
				total+=sec.getValue();
			}
			System.out.println("SECONDARYSPACING!\n"+top + " " + bottom);
			for (int i=0;i<secondary.length;i++){
				double percent = secondary[i].getValue()/total; //get our percentage of the total
				int curYSpace = (int)(yspace*percent); //get our actual y-space value
				if (i==0){ //if this is the first category, then the y-top is the y-top of the primary
					secondary[i].setTop(top);
				}
				else{
					secondary[i].setTop(secondary[i-1].getBottom());
				}
				if (i==secondary.length-1){
					secondary[i].setBottom(bottom);
				}
				else{
					secondary[i].setBottom(secondary[i].getTop()+curYSpace);
				}
				System.out.println(secondary[i].getEmotion()+ " "+ secondary[i].getTop()+ " " + secondary[i].getBottom());
			}
			
		}
	}
	
	public MomentEmoteSpace[] getSecondary(){
		if (primary){
			return secondary;
		}
		return null;
	}
	
	public double getValue(){
		return value;
	}


	public boolean isPrimary() {
		return primary;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
