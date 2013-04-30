import java.util.LinkedList;

public class Word {
	private String word;
	private LinkedList occurences;
	private int recentIndex;
	
	public Word(String word, int wordInd, String pOS){
		System.out.println(word);
		occurences = new LinkedList();
		addOccurence(wordInd);
		this.word = word;
	}
	
	public void addOccurence(int i){
		recentIndex = i;
		occurences.add(i);
	}
	
	public LinkedList getIndex(){
		return occurences;
	}
	
	public int getRecentIndex(){
		return recentIndex;
	}
	
	public String getWord(){
		return word;
	}
	
	public int getCount(){
		return occurences.size();
	}
}
