import java.util.concurrent.ConcurrentHashMap;


public class Segment {
	private ConcurrentHashMap<String,Double> accumVals; //holds all values
	private MomentEmoteSpace[] newBounderies;
	private MomentEmotion[] momentAvgs; //averages for all thangs
	private Moment[] momentList; //list of all moments contained in this segment
	private int size, curSize, xTot, x; //size - how many moments this segment is composed of / xStart-xEnd index
	private boolean full, empty; //when the segment has enough moments, it is full (true)  /   empty is true when there are no moments in this segment
	private MomentEmoteSpace anchor;
	
	public Segment(int size){
		this.size = size;
		full = false;
		setEmpty(true);
		momentList = new Moment[size];
		accumVals = new ConcurrentHashMap<String,Double>();
		curSize = 0;
		xTot = 0;
	}

	public void addMoment(Moment newMoment){
		if (empty){
			momentAvgs = newMoment.getMomentSpectrum();
			empty = false;
		}
		if (!full){    //if the segment isn't full (has reached max number of allowed Moment objects)
			momentList[curSize]=newMoment;  //the actual Moment object is added to an array
			curSize++;  //curSize is increased
			MomentEmotion[] tempME = newMoment.getMomentSpectrum();
			for (int i=0; i<tempME.length;i++){ // for each primary
				momentAvgs[i].setPrimaryVal(momentAvgs[i].getPrimaryVal() + tempME[i].getPrimaryVal()); //add the primary value
				MomentEmoteSec[] secME = momentAvgs[i].getSecEmote(); //get secondaries
				System.out.println(secME.length);
				for (int j=0;j<secME.length;j++){ //add secondary value
					System.out.println(secME[j].getSecondaryString()+" "+tempME[i].getPrimaryString());
					secME[j].setSecondaryVal(secME[j].getSecondaryVal() + momentAvgs[i].getSecEmote()[j].getSecondaryVal()); // add the secondary val
				}
			}
			xTot+=newMoment.getX(); //x is added to to later be averaged
			if (curSize==size){ //if we have used all our space, we are now full
				full = true;
				createNewBounderies();
			}
		}
		else{
			System.out.println("Segement is full -- no moment added");
		}
	}
	
	public void sortMomentAvgs(){
		if (momentAvgs.length>1){
			for (int i=1;i<momentAvgs.length;i++){
				for (int j=i;j>0;j--){
					MomentEmotion tempEmote = momentAvgs[j];
					if (tempEmote.getPrimaryVal()>momentAvgs[j-1].getPrimaryVal()){
						momentAvgs[j] = momentAvgs[j-1];
						momentAvgs[j-1] = tempEmote;
					}
				}
			}
		}
	}
	
	public void createNewBounderies(){
		newBounderies = new MomentEmoteSpace[EmotionSpectrum.getPrimaryStrings().length]; //list of new old sized boundaries created
		for (int i=0;i<EmotionSpectrum.getPrimaryStrings().length;i++){
			newBounderies[i] = new MomentEmoteSpace(getMomEmotion(EmotionSpectrum.getPrimaryStrings()[i]));  //list of old sized boundaries (to be changed) is populated;
		}
		//average all values with # of moments
		for (MomentEmotion curME: momentAvgs){
			double curMEVal = curME.getPrimaryVal()/curSize;
			curME.setPrimaryVal(curMEVal);
			for (MomentEmoteSec curSec: curME.getSecEmote()){
				curSec.setSecondaryVal(curSec.getSecondaryVal()/curSize);
			}
		}
		sortMomentAvgs(); //sort the momentemotespaces! based on most relevant
		x = xTot/curSize; //gets the average x
		int winPoints = 5;  //how many winners/points they get = how many players there are
		int totalPoints = 0; //this will hold total points given out
		int curPoints = winPoints;
		for (MomentEmotion curME: momentAvgs){ //add all the points to their respective spaces
			getMomSpace(curME.getPrimaryString()).setPoints(curPoints);
			totalPoints+=curPoints;
			curPoints--;
			if (curPoints==0){
				break;
			}
		}
		int onePointHeight = Visualize.getImageHeight()/totalPoints;
		int curBottomNewYTop = 0;
		int maxPoints = 0; //holds the max points 
		anchor = newBounderies[0];
		//here we assign the Y changes
		for (int i=0; i<newBounderies.length;i++){ //for each boundary 
			int tempPoints = newBounderies[i].getPoints(); //we get the amount of won points
			System.out.println(newBounderies[i].getEmotion()+" TEMPPOINTS: "+tempPoints);
			if (tempPoints>maxPoints){
				maxPoints = tempPoints;
				anchor = newBounderies[i];
			}
			int threshold = 10;
			double pointHeightChange = getMomEmotion(newBounderies[i].getEmotion()).getPrimaryVal()/.05; //value that makes onePointHeight worth less;
			System.out.println("HEEEEY! "+newBounderies[i].getEmotion() +" actual value "+ accumVals.get(newBounderies[i].getEmotion())+" ych "+pointHeightChange + " "+onePointHeight);
			if (pointHeightChange<1){ //if it is less than one, it is equal to one
				pointHeightChange=1;
			}
			else if (pointHeightChange>threshold){
				pointHeightChange=threshold*.8;
			}
			System.out.println("point height change "+pointHeightChange+" "+(onePointHeight*pointHeightChange)*tempPoints);
			curBottomNewYTop = (int)((newBounderies[i].getTop()+(((onePointHeight/(threshold-pointHeightChange))*tempPoints)))); //we get a new yBottom based on points
			System.out.println("tot "+curBottomNewYTop);
			newBounderies[i].setBottom(curBottomNewYTop);  //we set a new bottom
			if (i!=newBounderies.length-1){ //if we are not on the last entry
				newBounderies[i+1].setTop(curBottomNewYTop);
			}
		}
		shiftBounderies();
		for (MomentEmoteSpace momEmo: newBounderies){
			momEmo.assignSecondaries();  //this assigns our secondary y-bounds based on their relative value to each other
		}
	}
	
	public MomentEmotion getMomEmotion(String primary){
		for (MomentEmotion curMomEmo: momentAvgs){
			if (curMomEmo.getPrimaryString().equals(primary)){
				return curMomEmo;
			}
		}
		System.out.println("THIS IS MESSED UPPPPPPPP\n\n\n\n\n");
		return null;
	}
	
	public MomentEmoteSpace getMomSpace(String primary){
		for (MomentEmoteSpace curMom: newBounderies){
			if (curMom.getEmotion().equals(primary)){
				return curMom;
			}
		}
		return null;
	}
	
	/**
	 * Shifts the y points based on the anchor
	 */
	public void shiftBounderies(){
		int origTop = EmotionSpectrum.getPrimary(anchor.getEmotion()).getyTop();
		int curTop = anchor.getTop();
		int curBottom = anchor.getBottom();
		int origBottom = EmotionSpectrum.getPrimary(anchor.getEmotion()).getyBottom();
		int origDist = origBottom-origTop; 
		int curDist = curBottom-curTop;
		int fringeDist = (curDist - origDist)/2; //get the fringe distance
		int newTop = origTop - fringeDist;
		int travelAll=newTop - origTop;
		System.out.println(travelAll);
		for (MomentEmoteSpace tempSpace: newBounderies){
			System.out.println("SETTOP: "+(tempSpace.getTop()+travelAll));
			tempSpace.setTop(tempSpace.getTop()+travelAll);
			tempSpace.setBottom(tempSpace.getBottom()+travelAll);
		}
	}
	
	/**
	 * Adds to count of wins this emotion has
	 */
	public void addMomentEmoteWinCount(String emote, int points){
		for (MomentEmoteSpace tempSpace: newBounderies){
			if (tempSpace.getEmotion().equals(emote)){
				tempSpace.setPoints(tempSpace.getPoints()+points);
				return;
			}
		}
	}
	
	public MomentEmoteSpace[] getBounderies(){
		if (!full){
			createNewBounderies();
		}
		return newBounderies;
	}

	public boolean isFull(){
		return full;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
}
