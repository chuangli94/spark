package core.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

@Document(collection="User")
public class UserDocument {

	@Id
	private String id;
	
	private String name;
	private String profilePictureKey;
	private List<String> queue;
	private List<String> alreadySeen;
	private List<String> categoriesScore;
	private List<Match> matches;
	
	public UserDocument(){}
	
	public UserDocument(String name, List<String> queue, List<String> alreadySeen, List<String> categoriesScore){
		this.name = name;
		this.queue = queue;
		this.alreadySeen = alreadySeen;
		this.categoriesScore = categoriesScore;
		this.profilePictureKey = null;
		//this.matches = new ArrayList<Match>();
	}
	
	public void setProfilePictureKey(String profilePictureKey){
		this.profilePictureKey = profilePictureKey;
	}
	
	public String getProfilePictureKey(){
		return this.profilePictureKey;
	}
	
	public void setCategoriesScore(List<String> categoriesScore){
		this.categoriesScore = categoriesScore;
	}
	
	public List<String> getCategoriesScore(){
		return categoriesScore;
	}
	
	public void addCategoryScore(String category, int score){
		if (categoriesScore == null){
			categoriesScore = new ArrayList<String>();
		}
		if (category == null || category.isEmpty()){
			return;
		}
		for (int itr = 0; itr < categoriesScore.size(); itr++){
			String categoryString = categoriesScore.get(itr).substring(0, categoriesScore.get(itr).indexOf(":"));
			if (categoryString.equals(category)){
				String scoreString = categoriesScore.get(itr).substring(categoriesScore.get(itr).indexOf(":") + 1);
				int newscore = Integer.valueOf(scoreString) + score;
				String newCategoryScore = categoryString + ":" + String.valueOf(newscore);
				categoriesScore.set(itr, newCategoryScore);
				return;
			}
		}
		String newCategoryScore = category + ":" + String.valueOf(score);
		categoriesScore.add(newCategoryScore);
	}
	
	public void enQueue(String item){
		if (queue == null){
			queue = new ArrayList<String>();
		}
		queue.add(item);
	}
	
	public void enQueueAll(List<String> items){
		if (queue == null){
			queue = new ArrayList<String>();
		}
		queue.addAll(items);
	}
	
	public String deQueue(){
		if ((queue == null) || (queue.isEmpty())){
			return null;
		}
		
		return queue.remove(0);
	}
	
	public void deQueueAll() {
		queue.clear();
	}
	
	public String getName(){
		return name;
	}
	
	public List<String> getQueue(){
		return queue;
	}
	
	public List<String> getAlreadySeen(){
		return alreadySeen;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setQueue(List<String> queue){
		this.queue = queue;
	}
	
	public void setAlreadySeen(List<String> alreadySeen){
		this.alreadySeen = alreadySeen;
	}
	
	public void addAlreadySeen(List<String> alreadySeen) {
		if (this.alreadySeen == null) {
			this.alreadySeen = new ArrayList<String>();
		}
		this.alreadySeen.addAll(alreadySeen);
	}
	
	
	public void addMatch(Match match) {
		if (this.matches == null) {
			matches = new ArrayList<Match>();
		}
		this.matches.add(match);
	}
	
	public List<Match> getMatches() {
		return this.matches;
	}
	
	public Match getUnshownMatch() {
		if (this.matches == null) return null;
		for(Match match : this.matches) {
			if (!match.getShown()) return match;
		}
		return null;
	}
	
	private List<Match> getAllShownMatches() {
		return this.matches.stream().filter(m -> m.getShown()).collect(Collectors.toList());
	}
	
	public Boolean containsMatch(String match) {
		for(Match m : getAllShownMatches()) {
			if (match.equals(m.getName())) return true;
		}
		return false;
	}
	
	public void removeMatch(String match) {
		for(int i=0; i<this.matches.size(); i++) {
			if (match.equals(this.matches.get(i).getName())) {
				this.matches.remove(i);
			}
		}
	}
	
}
