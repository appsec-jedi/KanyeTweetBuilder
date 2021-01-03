import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Java project to build sample tweets that are built based on real Kanye tweets
 *
 * @author     Jake Jacobs-Smith (jacobjv@auburn.edu)
 * @version    2021-1-1
 *
 */

public class TweetBuilder {
	
	public static class Word {
		int number;
		String word;
		ArrayList<NextWord> nextWords = new ArrayList<NextWord>();
		
		public Word(String wordIn) {
			word = wordIn;
			number = 1;
		}
	}
	
	public static class NextWord {
		String word;
		int weight;
		
		public NextWord(String wordIn) {
			word = wordIn;
			weight = 0;
		}

	}
	
	private static ArrayList<Word> wordList = new ArrayList<Word>();
	private static File fileName = new File("KanyeTweets.txt");
	private static int totalWords = 0;
	
	public static void main(String[] args) {
		modelBuilder();
		//printArray();
		System.out.println(tweetBuilder(5));
	}
	
	public static void modelBuilder() {
		try {
			//read in the file
			Scanner textInput = new Scanner(fileName);
			//iterate over the file line by line and separate out each word
			while (textInput.hasNextLine()) {
				String line = textInput.nextLine();
				Pattern removeSpaces = Pattern.compile("\\w+");
				Matcher match = removeSpaces.matcher(line);
				String wordToAdd = "";
				ArrayList<String> wordsFound = new ArrayList<String>();
				while (match.find()) {
					wordToAdd = match.group();
					wordsFound.add(wordToAdd);
					//if the list is empty, add the first word
					if (wordList.isEmpty()) {
						Word newWord = new Word(wordToAdd);
						wordList.add(newWord);
						totalWords++;
					} else {
						//check if the word has been added already
						boolean wordFound = false;
						for (Word w : wordList) {
							//if the word is already in the list, increment the number
							if (w.word.toLowerCase().equals(wordToAdd.toLowerCase())) {
								w.number++;
								wordFound = true;
								break;
							} 
						}
						if (!wordFound) {
							//if it is not found, create a Word object and add it to the list
							Word newWord = new Word(wordToAdd);
							wordList.add(newWord);
							totalWords++;
						}
					}
				}
				//use the word array for the line to find the next words
				findNextWords(wordsFound);
			}
			textInput.close();
			System.out.println("Model Built!");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void findNextWords(ArrayList<String> wordsFound) {
		//System.out.println("Finding the next words");
		//iterate over the words found and find their object in the word list
		for (int i = 0; i < wordsFound.size() - 1; i++) {
			//System.out.println("Finding " + wordsFound.get(i));
			String currentWord = wordsFound.get(i);
			String nextWord = wordsFound.get(i + 1);
			for (Word w : wordList) {				
				//once the word object is found add the next word
				if (w.word.equals(currentWord)) {
					//System.out.println(wordsFound.get(i) + " found! Adding: " + wordsFound.get(i + 1));
					//check if the list is empty. If it is, add the first word
					if (w.nextWords.isEmpty()) {
						NextWord next = new NextWord(nextWord);
						w.nextWords.add(next);
						//System.out.println(next.word + " added");
					} else {
						boolean found = false;
						for (NextWord nw : w.nextWords) {
							//System.out.println("Checking for: " + nw.word + " against: " + wordsFound.get(i + 1));
							//if the word has already been added, increase the weight
							if (nw.word.toLowerCase().equals(nextWord.toLowerCase())) {
								nw.weight++;
								//System.out.println(nw.word + " increased to " + nw.weight);
								found = true;
								break;
							}
						}
						if (!found) {
							//the the word isn't already in the next words list, add it with a weight of zero
							NextWord next = new NextWord(nextWord);
							w.nextWords.add(next);
							//System.out.println(next.word + " added");
						}
					}
					break;
				}
			}
		}
	}
	
	public static String tweetBuilder(int length) {
		String tweet = "";
		//randomly select the first word to use
		Random rng = new Random();
		Word firstWord = wordList.get(rng.nextInt(totalWords));
		tweet = firstWord.word;
		length--;
		//check to see if there are any possible next words
		//if it is not empty continue
		Word currentWord = firstWord;
		while (length > 0) {
			if (!currentWord.nextWords.isEmpty()) {
				//grab a next word based off the weight and add it to the tweet
				int highestWeight = 0;
				//set the nextWord to the first word in the list
				String nextWord = currentWord.nextWords.get(0).word;
				//iterate over the next words to see if a higher weight exists
				for (NextWord nw : currentWord.nextWords) {
					if (nw.weight >= highestWeight) {
						//if the weight is higher, use that word instead
						nextWord = nw.word;
					}
				}
				//add the next word to the tweet
				tweet += " " + nextWord;
				//find the word object of the word that was just added
				for (Word w : wordList) {
					//when the word object is found, update the current word
					if (w.word.equals(nextWord)) {
						currentWord = w;
					}
				}
			}
			length--;
		}
		return tweet;
	}
	
	public static void printArray() {
		for (TweetBuilder.Word word : wordList) {
			System.out.println(word.word + " appears " + word.number + " times");
			for (TweetBuilder.NextWord nextWord : word.nextWords) {
				System.out.println("Common next words: " + nextWord.word);
			}
		}
	}
}