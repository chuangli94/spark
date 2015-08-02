package core.wordutils;
//package core.Lemmatizer;
//
//import it.uniroma1.lcl.adw.ADW;
//import it.uniroma1.lcl.adw.DisambiguationMethod;
//import it.uniroma1.lcl.adw.ItemType;
//import it.uniroma1.lcl.adw.comparison.Cosine;
//import it.uniroma1.lcl.adw.comparison.Jaccard;
//import it.uniroma1.lcl.adw.comparison.SignatureComparison;
//import it.uniroma1.lcl.adw.comparison.WeightedOverlap;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class AdwCalculateWordSimilarity {
//
//	private static ADW pipeLine = new ADW();
//	public static double getADWScore(String word1, String word2){
//		
//		
//		
//		DisambiguationMethod disMethod = DisambiguationMethod.ALIGNMENT_BASED;
//		
//		SignatureComparison wOverlap = new WeightedOverlap();
////		SignatureComparison cosine = new Cosine();
////		SignatureComparison jaccard = new Jaccard();
////	    
//		
//		double maxScore = -1D;
//		
//		double score;
//			
//		List<String> word1AllForms = new ArrayList<String>();
//		List<String> word2AllForms = new ArrayList<String>();
//		
//		word1AllForms.add(word1+"#n");
//		word1AllForms.add(word1+"#v");
//		word2AllForms.add(word2+"#n");
//		word2AllForms.add(word2+"#v");
//		
////		if (word1.indexOf(" ") == -1){
////			word1AllForms.add(word1+"#n");
////			word1AllForms.add(word1+"#v");
////		}
//		
////		word1AllForms.add(word1+".n.2");
////		word1AllForms.add(word1+".v.2");
////		word1AllForms.add(word1+".a.2");
////		word1AllForms.add(word1+".r.2");
//		
////		if (word2.indexOf(" ") == -1){
////			word2AllForms.add(word2+"#n");
////			word2AllForms.add(word2+"#v");
////		}
//		
////		word2AllForms.add(word2+".n.2");
////		word2AllForms.add(word2+".v.2");
////		word2AllForms.add(word2+".a.2");
////		word2AllForms.add(word2+".r.2");
//		
//		
//		for (String word1String: word1AllForms){
//			for (String word2String: word2AllForms){
//				score = getPairSimilarityAllForms(word1String, word2String,
//					disMethod, wOverlap);
//				
//				if (score > maxScore){
//					maxScore = score;
//				}
//				
////				score = getPairSimilarityAllForms(word1String, word2String,
////						disMethod, cosine);
////					
////				if (score > maxScore){
////					maxScore = score;
////				}
//				
////				score = getPairSimilarityAllForms(word1String, word2String,
////						disMethod, jaccard);
//					
//				if (score > maxScore){
//					maxScore = score;
//				}		
//				
//			}
//		}
//		
//		if (maxScore == -1D){
//			maxScore = 0.0;
//		}
//		
//		return maxScore;
//	}
//	
//	public static double getPairSimilarityAllForms(String word1, String word2, DisambiguationMethod disMethod, SignatureComparison measure){
//		if (word1.indexOf("#") != -1){
//			if (word2.indexOf("#") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE_TAGGED, ItemType.SURFACE_TAGGED);
//			} else if (word2.indexOf(".") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE_TAGGED, ItemType.WORD_SENSE);
//			} else {
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE_TAGGED, ItemType.SURFACE);
//			}
//		} else if (word1.indexOf(".") != -1){
//			if (word2.indexOf("#") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.WORD_SENSE, ItemType.SURFACE_TAGGED);
//			} else if (word2.indexOf(".") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.WORD_SENSE, ItemType.WORD_SENSE);
//			} else {
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.WORD_SENSE, ItemType.SURFACE);
//			}
//		} else {
//			if (word2.indexOf("#") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE, ItemType.SURFACE_TAGGED);
//			} else if (word2.indexOf(".") != -1){
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE, ItemType.WORD_SENSE);
//			} else {
//				return pipeLine.getPairSimilarity(word1, word2, disMethod, measure, ItemType.SURFACE, ItemType.SURFACE);
//			}
//		}
//	}
//
//	
//	public static void main(String[] args){
//		System.out.println(getADWScore("cold", "ice"));
//	}
//	
//}
