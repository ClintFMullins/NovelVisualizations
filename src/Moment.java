import java.util.ArrayList;
import java.util.Collections;


public class Moment {
	private String word;  //word the moment surrounds
	private int x; //visual placement
	private MomentEmotion[] momentSpectrum;
	
	/**
	 * A moment is defined by one word related to a Character and how that relates to our emotional spectrum
	 * 
	 * 1. String word is saved
	 * 2. momentSpectrum is filled with MomentEmotion objects for relatedness
	 * 3. Saved x position as the Word's index
	 * 
	 * @param wordObj
	 */
	public Moment(Word wordObj){
		word = wordObj.getWord();
		momentSpectrum = EmotionSpectrum.momentValue(this.word);
		x = wordObj.getRecentIndex();
		sortMomentSpectrum();  //sorts the momentEmotions by relevance
		//giveAllSizeAndY(); //assigns a size and y placement for a point
	}
	
//	//After moment spectrum is sorted, here we give the size and Y position of the point
//	public void giveAllSizeAndY(){
//		int originalSize=100;  //size of the first one
//		for (int i=0;i<momentSpectrum.length;i++){ //all emotions
//			int size=originalSize;  //size of the first one
//			int increment = size/momentSpectrum[i].getSecEmote().length;
//			for (int j=0;j<momentSpectrum[i].getSecEmote().length;j++){
//				momentSpectrum[i].getSecEmote()[j].setSize(size);
//				size-=increment;
//				//confusing below, I know. Here we want to set the Y of the MomentEmoteSec
//				//set that emote in the moment spectrum by getting our EmotionSpectrum's Primary's Secondary's yCenter.
//				momentSpectrum[i].getSecEmote()[j].setY(EmotionSpectrum.getPrimary(momentSpectrum[i].getPrimaryString()).getSecondaryEmotion(momentSpectrum[i].getSecEmote()[j].getSecondaryString()).getyCenter());
//			}
//			originalSize/=1.6;
//		}
//	}
//	
	//Insertion sort used due to small list size (it should generally be less than 10).
	public void sortMomentSpectrum(){
		if (momentSpectrum.length>1){
			for (int i=1;i<momentSpectrum.length;i++){
				for (int j=i;j>0;j--){
					MomentEmotion tempEmote = momentSpectrum[j];
					if (tempEmote.getPrimaryVal()>momentSpectrum[j-1].getPrimaryVal()){
						momentSpectrum[j] = momentSpectrum[j-1];
						momentSpectrum[j-1] = tempEmote;
					}
				}
			}
		}
	}
	
	public void printSpectrumOrderVals(){
		for (MomentEmotion tempMom: momentSpectrum){
			System.out.println(tempMom.getPrimaryString()+": "+tempMom.getPrimaryVal());
		}
	}
	
	public MomentEmotion[] getMomentSpectrum(){
		return momentSpectrum;
	}
	public String getWord(){
		return word;
	}
	public int getX(){
		return x;
	}
}
