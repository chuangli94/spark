package core.wordutils;

import java.util.ArrayList;
import java.util.List;

public class MultiplierCalculator {
	// This is so inefficient and ugly... gotta fix later by using comparator interface
	public static float calculateMultipler(List<String> userCat, List<String> otherCat){
		
		float multiplier = 1;
		List<String> userCategories = new ArrayList<String>(userCat);
		List<String> otherCategories = new ArrayList<String>(otherCat);
		Integer userMaxScore = null;
		String userMaxCategory = null;
		Integer userSecondMaxScore = null;
		String userSecondMaxCategory = null;
		Integer otherMaxScore = null;
		String otherMaxCategory = null;
		Integer otherSecondMaxScore = null;
		String otherSecondMaxCategory = null;
		for (String uString: userCategories){
			if (userMaxScore == null || userMaxCategory == null){
				userMaxScore = Integer.valueOf(uString.substring(uString.indexOf(":") + 1));
				userMaxCategory = uString.substring(0, uString.indexOf(":"));
			} else {
				int currentScore = Integer.valueOf(uString.substring(uString.indexOf(":") + 1));
				if (currentScore > userMaxScore){
					userMaxScore = currentScore;
					userMaxCategory = uString.substring(0, uString.indexOf(":"));
				}
			}
		}
		userCategories.remove(userMaxCategory + ":" + userMaxScore);
		for (String uString: userCategories){
			if (userSecondMaxScore == null || userSecondMaxCategory == null){
				userSecondMaxScore = Integer.valueOf(uString.substring(uString.indexOf(":") + 1));
				userSecondMaxCategory = uString.substring(0, uString.indexOf(":"));
			} else {
				int currentScore = Integer.valueOf(uString.substring(uString.indexOf(":") + 1));
				if (currentScore > userSecondMaxScore){
					userSecondMaxScore = currentScore;
					userSecondMaxCategory = uString.substring(0, uString.indexOf(":"));
				}
			}
		}
		
		for (String oString: otherCategories){
			if (otherMaxScore == null || otherMaxCategory == null){
				otherMaxScore = Integer.valueOf(oString.substring(oString.indexOf(":") + 1));
				otherMaxCategory = oString.substring(0, oString.indexOf(":"));
			} else {
				int currentScore = Integer.valueOf(oString.substring(oString.indexOf(":") + 1));
				if (currentScore > otherMaxScore){
					otherMaxScore = currentScore;
					otherMaxCategory = oString.substring(0, oString.indexOf(":"));
				}
			}
		}
		otherCategories.remove(otherMaxCategory + ":" + otherMaxScore);
		for (String oString: otherCategories){
			if (otherSecondMaxScore == null || otherSecondMaxCategory == null){
				otherSecondMaxScore = Integer.valueOf(oString.substring(oString.indexOf(":") + 1));
				otherSecondMaxCategory = oString.substring(0, oString.indexOf(":"));
			} else {
				int currentScore = Integer.valueOf(oString.substring(oString.indexOf(":") + 1));
				if (currentScore > otherSecondMaxScore){
					otherSecondMaxScore = currentScore;
					otherSecondMaxCategory = oString.substring(0, oString.indexOf(":"));
				}
			}
		}
		
		if ((userMaxCategory != null) && (otherMaxCategory != null) && userMaxCategory.equals(otherMaxCategory)
				|| (userSecondMaxCategory != null) && (otherSecondMaxCategory != null) && userSecondMaxCategory.equals(otherSecondMaxCategory)
					|| (userMaxCategory != null) && (otherSecondMaxCategory != null) && userMaxCategory.equals(otherSecondMaxCategory)
						|| (userSecondMaxCategory != null) && (otherMaxCategory != null) && userSecondMaxCategory.equals(otherMaxCategory)){
				multiplier = multiplier * 1.5f;
		}
		
		return multiplier;
	}

}
