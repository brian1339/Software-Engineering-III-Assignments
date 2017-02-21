package poker;

public class HandOfCards {
	
	/*
	 * Static constant default values for the calculation of game values for hands
	 */
	public static final int ROYAL_FLUSH_DEFAULT = 1000000000;
	public static final int STRAIGHT_FLUSH_DEFAULT = 900000000;
	public static final int FOUR_OF_A_KIND_DEFAULT = 800000000;
	public static final int FULL_HOUSE_DEFAULT = 700000000;
	public static final int FLUSH_DEFAULT = 600000000;
	public static final int STRAIGHT_DEFAULT = 500000000;
	public static final int THREE_OF_A_KIND_DEFAULT = 400000000;
	public static final int TWO_PAIR_DEFAULT = 300000000;
	public static final int ONE_PAIR_DEFAULT = 200000000;
	public static final int HIGH_HAND_DEFAULT = 100000000;
	
	/*
	 * Internal fields of hand
	 */
	private static final int CARDS_HELD = 5;
	private PlayingCard[] cardArray;
	private DeckOfCards deck;
	
	/*
	 * Constructor takes in deck, initializes card array and then fills in with 5
	 * cards dealt from deck
	 */
	public HandOfCards(DeckOfCards deck) throws InterruptedException {
		this.deck = deck;
		cardArray = new PlayingCard[CARDS_HELD];
		for (int i=0; i<CARDS_HELD; i++){
			cardArray[i] = this.deck.dealNext();
		}
		sort();
	}
	
	/**
	 * Uses a bubble sort to sort the cards by game value in the hand from high game value to low
	 */
	private void sort(){
		boolean swapped = true;
		while (swapped) {
			swapped = false;
			for (int i=1; i < cardArray.length; i++) {
				PlayingCard temp;
				if (cardArray[i-1].getGameValue() < cardArray[i].getGameValue()) {
					temp = cardArray[i-1];
					cardArray[i-1] = cardArray[i];
					cardArray[i] = temp;
					swapped = true;
				}
			}
		}
	}
	
	/**
	 * Returns a string of all the cards in the hand, along with their game value, separated by spaces
	 */
	public String toString(){
		String output = "";
		for (int i=0; i<cardArray.length; i++){
			output += cardArray[i] +"(" + cardArray[i].getGameValue() + ")" + " ";
		}
		return output;
	}
	
	/**
	 * Checks whether cards are in sequential order ie. they all decrement in gameValue by 1 as we 
	 * go along the sorted array.
	 * This saves a lot of code repeat in our public boolean hand checks below
	 */
	private boolean hasSequentialOrder(){
		boolean sequentialCards = true;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i].getGameValue() != cardArray[i-1].getGameValue()-1){
				sequentialCards = false;
			}
		}
		return sequentialCards;
	}
	
	/**
	 * Checks whether all cards in the array are the same suit
	 * Again, saves code repeat in public boolean hand checks
	 */
	private boolean hasAllSameSuit(){
		boolean sameSuit = true;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getSuit() != cardArray[i].getSuit()){
				sameSuit = false;
			}
		}
		return sameSuit;
	}
	
	/**
	 * Checks whether there is a segment of the card array, where the card game values all match,
	 * with length equal EXACTLY to the number given as input
	 * Saves code repeat in all gameValue matching boolean methods for Hand checking except for isTwoPair
	 * @return False if no segment match of that length or segment of matching cards is longer than length 
	 * specified. Returns true if segment where all card values match of exact input length exists
	 */
	private boolean segmentMatch(int segmentLength){
		// Assume false first
		boolean segmentMatch = false;
		
		// Check all possible end points of segments for the cardArray
		for (int i=segmentLength-1; i<cardArray.length; i++){
			boolean thisSegmentMatches = true;
			
			// Check if previous cards in segment ending at i are equal in game value to cardArray[i]
			for (int j=i-1; j>i-segmentLength; j--){
				if (cardArray[j].getGameValue() != cardArray[i].getGameValue()){
					thisSegmentMatches = false;
					break;
				}
			}
			
			// If all cards in current segment match assume true for segmentMatch first
			if (thisSegmentMatches){
				segmentMatch = true;
			}
			
			/*
			 * If a potential match is found, then check if the card matches beyond the lower boundary
			 * of the segment if they exist
			 */
			if (thisSegmentMatches && i-segmentLength>=0){
				// If the card beyond the lower boundary also match, then the segment length is longer so we set false
				if (cardArray[i].getGameValue() == cardArray[i-segmentLength].getGameValue()){
					segmentMatch = false;
				}
			}
			
			/*
			 * If a potential match is found check if cards match beyond the upper bound of the segment if they exist
			 */
			if (thisSegmentMatches && i<cardArray.length-1){
				// If the card beyond the lower boundary also match, then the segment length is longer so we set false
				if (cardArray[i].getGameValue() == cardArray[i+1].getGameValue()){
					segmentMatch = false;
				}
			}
			
			// If the segment is a match, end the loop and return the boolean
			if (segmentMatch){
				break;
			}
		}
		return segmentMatch;
	}
	
	/**
	 * Checks if the hand matches the criteria for a royal flush
	 * ie. A,K,Q,J,10 of same suit 
	 */
	public boolean isRoyalFlush() {
		// Check first card in array is an ace & sequential order & same suit 
		return cardArray[0].getGameValue() == 14 && hasSequentialOrder() && hasAllSameSuit();
	}
	
	/**
	 * Checks if hand matches criteria for straight flush
	 * ie. Not royal flush, cards of same suit in sequential order
	 */
	public boolean isStraightFlush() {
		
		if (isRoyalFlush()){
			return false;
		}
		return hasSequentialOrder() && hasAllSameSuit();
	}
	
	/**
	 * Checks if hand matches criteria for four of a kind
	 * ie. there are 4 cards of equal game value in the hand
	 */
	public boolean isFourOfAKind() {
		
		return segmentMatch(4);
	}
	
	/**
	 * Checks if hand matches criteria for full house
	 * ie. Cards contain 3 of a kind and an additional pair
	 */
	public boolean isFullHouse() {
		
		return segmentMatch(3) && segmentMatch(2);
		
	}
	
	/**
	 * Checks if hand matches criteria for a simple flush
	 * ie. Not straight flush or royal flush, cards are all of same suit
	 */
	public boolean isFlush() {
		
		if (isStraightFlush() || isRoyalFlush()) {
			return false;
		}
		return hasAllSameSuit();
	}
	
	/**
	 * Checks if hand matches criteria for a simple straight
	 * ie. Not straight flush or royal flush, cards all in sequential order
	 */
	public boolean isStraight(){
		
		if (isStraightFlush() || isRoyalFlush()){
			return false;
		}
		return hasSequentialOrder();
	}
	
	/**
	 * Checks if hand matches criteria for three of a kind
	 * ie. Not a full house, contains exactly 3 matching cards
	 */
	public boolean isThreeOfAKind() {
		
		if (isFullHouse()){
			return false;
		}
		return segmentMatch(3);
	}
	
	/**
	 * Checks if hand matches criteria for Two Pair
	 * ie. hand contains two separate pairs of matching cards
	 */
	public boolean isTwoPair(){
		//Check all hands that could supersede this one 
		if (isFullHouse() || isFourOfAKind()){
			return false;
		}
		
		//Check if there are two pairs of adjacent cards in sorted array with the same game value
		boolean twoPair = false;
		//First check if there is a pair in the first three entries
		for (int i=1; i<cardArray.length-2; i++){
			if (cardArray[i-1].getGameValue() == cardArray[i].getGameValue()){
				//If one pair exists, we check the rest of the array for another pair of adjacent cards with the same game value
				for (int j=i+2; j<cardArray.length; j++){
					if (cardArray[j-1].getGameValue() == cardArray[j].getGameValue()){
						twoPair = true;
					}
				}
			}
		}
		return twoPair;
	}
	
	/**
	 * Checks if hand matches criteria for a pair hand
	 * ie. Contains exactly one pair of matching cards
	 */
	public boolean isOnePair(){

		if (isTwoPair() || isFullHouse()){
			return false;
		}
		return segmentMatch(2);
	}
	
	/**
	 * Checks if hand is simply a high card hand
	 * ie. matches none of the other hand types
	 */
	public boolean isHighHand(){
		if (isRoyalFlush() || isStraightFlush() || isFourOfAKind() || isFullHouse() || isFlush() 
				|| isStraight() || isThreeOfAKind() || isTwoPair() || isOnePair()){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * Private method returns a string containing the hand type info
	 * Useful for testing
	 */
	private String handType(){
		String handType ="";
		if (isRoyalFlush()){
			handType = "Royal Flush";
		}
		if(isStraightFlush()){
			handType = "Straight Flush";
		}
		if(isFourOfAKind()){
			handType = "Four Of A Kind";
		}
		if(isFullHouse()){
			handType = "Full House";
		}
		if (isFlush()) {
			handType = "Flush";
		}
		if (isStraight()){
			handType = "Straight";
		}
		if (isThreeOfAKind()) {
			handType = "Three Of A Kind";
		}
		if (isTwoPair()) {
			handType = "Two Pair";
		}
		if (isOnePair()) {
			handType = "One Pair";
		}
		if (isHighHand()){
			handType = "High Hand";
		}
		return handType;
	}
	
	/**
	 * Sets the hand to a specific array of cards for testing
	 */
	private void setHand(PlayingCard[] newHand){
		cardArray = newHand;
		sort();
	}
	
	/**
	 * Returns array of cards with segment of matching cards with length equal
	 * exactly to the input parameter brought to the front.
	 * Used below in calculating game values of hands 
	 */
	private PlayingCard[] segmentSort(int segmentLength){
		
		PlayingCard[] segmentSortedCards = new PlayingCard[CARDS_HELD];
		// Assume segment match is false first
		boolean segmentMatch = false;
		
		// Check all possible end points of segments for the cardArray
		for (int i=segmentLength-1; i<cardArray.length; i++){
			boolean thisSegmentMatches = true;
			
			// Check if previous cards in segment ending at i are equal in game value to cardArray[i]
			for (int j=i-1; j>i-segmentLength; j--){
				if (cardArray[j].getGameValue() != cardArray[i].getGameValue()){
					thisSegmentMatches = false;
					break;
				}
			}
			
			// If all cards in current segment match assume true for segmentMatch first
			if (thisSegmentMatches){
				segmentMatch = true;
			}
			
			/*
			 * If a potential match is found, then check if the card matches beyond the lower boundary
			 * of the segment if they exist
			 */
			if (thisSegmentMatches && i-segmentLength>=0){
				// If the card beyond the lower boundary also match, then the segment length is longer so we set false
				if (cardArray[i].getGameValue() == cardArray[i-segmentLength].getGameValue()){
					segmentMatch = false;
				}
			}
			
			/*
			 * If a potential match is found check if cards match beyond the upper bound of the segment if they exist
			 */
			if (thisSegmentMatches && i<cardArray.length-1){
				// If the card beyond the lower boundary also match, then the segment length is longer so we set false
				if (cardArray[i].getGameValue() == cardArray[i+1].getGameValue()){
					segmentMatch = false;
				}
			}
			
			/*
			 * If the card is a match, fill the segmentSortedCards first with the matching 
			 * segment, then with the remaining cards in order
			 */
			if (segmentMatch){
				int filledArrayIndex = 0; 
				// First copy the cards which match into the array
				for (int j = i-segmentLength; j<i; j++){
					segmentSortedCards[filledArrayIndex] = cardArray[j];
					filledArrayIndex++;
				}
				// Copy the cards of higher value into the array in order
				for (int j=0; j< i-segmentLength; j++){
					segmentSortedCards[filledArrayIndex] = cardArray[j];
					filledArrayIndex++;
				}
				// Copy the cards of higher value into the array in order
				for (int j = i; j < cardArray.length; j++){
					segmentSortedCards[filledArrayIndex] = cardArray[j];
					filledArrayIndex++;
				}
				break;
			}
		}
		return segmentSortedCards;
	}
	
	/**
	 * Calculates the game value of the hand of cards and returns as int.
	 * 
	 * Uses base 15 exponentials to differentiate card values within hands
	 * as this will ensure no overlap of values between hands and hands with
	 * higher card game values will return a higher hand game value.
	 * 
	 * Uses the official rules of poker that different suits are neither better 
	 * or worse than others. 
	 */
	public int getGameValue(){
		int gameValue =0;
		int exponentialBase = 15;
		/*
		 * If hand is royal flush, set to royal flush default.
		 * All royal flushes are same value across suits according to
		 * official rules of poker.
		 */
		if (isRoyalFlush()){
			gameValue = ROYAL_FLUSH_DEFAULT;
		}
		
		/*
		 * If straight Flush, add the game value of the highest card from the 
		 * sorted array to the default value.
		 * All suits same value according to poker rules
		 */
		if(isStraightFlush()){
			gameValue = STRAIGHT_FLUSH_DEFAULT;
			gameValue += cardArray[0].getGameValue();
		}
		
		/*
		 * If four of a kind, add the game value of the 4 matching cards by 15^1
		 * and add the value of the remaining card by 15^0 to the default value
		 */
		if(isFourOfAKind()){
			gameValue = FOUR_OF_A_KIND_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(4);
			gameValue += segmentSorted[0].getGameValue() * exponentialBase;
			gameValue += segmentSorted[4].getGameValue();
		}
		
		/*
		 * If full house add to the default the game value of the 3 matching cards 
		 * by 15^1 and add the game value of the remaining two matching cards by 15^0
		 */
		if(isFullHouse()){
			gameValue = FULL_HOUSE_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(3);
			gameValue += segmentSorted[0].getGameValue() * exponentialBase;
			gameValue += segmentSorted[4].getGameValue();
		}
		
		/*
		 * For flush add to the default the values of the cards by their base 15 
		 * exponentials according to their order in the sorted array
		 */
		if (isFlush()) {
			gameValue = FLUSH_DEFAULT;
			for (int i=0; i<cardArray.length; i++){
				gameValue += cardArray[cardArray.length-i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
		}
		
		/*
		 * For simple straight, add the value of the largest card in the sorted 
		 * array to the default value
		 */
		if (isStraight()){
			gameValue = STRAIGHT_DEFAULT;
			gameValue += cardArray[0].getGameValue();
		}
		/*
		 * For three of a kind add to the default the value of the matching cards by 15^2 and
		 * the two remaining cards in order by 15^1 and 15^0 respectively
		 */
		if (isThreeOfAKind()) {
			gameValue = THREE_OF_A_KIND_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(3);
			for (int i=2; i<cardArray.length; i++){
				gameValue += cardArray[cardArray.length-i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
		}
		
		/*
		 * For two pair, add to the default the value of the higher pair by 15^2, the lower pair
		 * by 15^1 and the remaining card by 15^0
		 */
		if (isTwoPair()) {
			gameValue = TWO_PAIR_DEFAULT;
			
			// Ints to store game value of upper pair, lower pair and stray card respectively
			int factorBase2 =0, factorBase1 =0, factorBase0 =0;
			/*
			 * 3 cases, stray unmatched card is at front middle or end of sorted array
			 */
			if (cardArray[0].getGameValue() != cardArray[1].getGameValue()){
				factorBase2 = cardArray[1].getGameValue();
				factorBase1 = cardArray[3].getGameValue();
				factorBase0 = cardArray[0].getGameValue();
			}
			if (cardArray[2].getGameValue() != cardArray[3].getGameValue()){
				factorBase2 = cardArray[0].getGameValue();
				factorBase1 = cardArray[3].getGameValue();
				factorBase0 = cardArray[2].getGameValue();
			}
			if (cardArray[3].getGameValue() != cardArray[4].getGameValue()){
				factorBase2 = cardArray[0].getGameValue();
				factorBase1 = cardArray[2].getGameValue();
				factorBase0 = cardArray[4].getGameValue();
			}
			
			gameValue += factorBase2 * Math.pow(exponentialBase, 2);
			gameValue += factorBase1 * Math.pow(exponentialBase, 1);
			gameValue += factorBase0 * Math.pow(exponentialBase, 0);
			
		}
		/*
		 * For 1 pair, add to the default value the value of the pair by 15^3 plus
		 * the remaining cards in order by 15^2, 15^1 and 15^ 0 respectively
		 */
		if (isOnePair()) {
			gameValue = ONE_PAIR_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(2);
			for (int i=1; i<cardArray.length; i++){
				gameValue += cardArray[cardArray.length-i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
			
		}
		/*
		 * For high hand, add to the default value the game values of the cards
		 * multiplied in order by 15^4, 15^3, 15^2, 15^1 and 15^0 respectively
		 */
		if (isHighHand()){
			gameValue = HIGH_HAND_DEFAULT;
			for (int i=0; i<cardArray.length; i++){
				gameValue += cardArray[cardArray.length-i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
		}
	}
	
	/*
	 * Simple test, prints 10000 random hands of cards with their identified hand type for visual testing
	 * Hands should be sorted in descending order of game value with their correct hand type adjacent
	 * Then tests and prints a royal flush, simple flush and straight flush
	 */
	public static void main(String[] args) throws InterruptedException {
		DeckOfCards testDeck = new DeckOfCards();
		HandOfCards testHand;
		//Print 10000 random hands
		for (int i=0; i<10000; i++){
			testHand = new HandOfCards(testDeck);
			System.out.println(testHand.toString() + testHand.handType());
			testDeck.shuffle();
			testDeck.reset();
		}
		PlayingCard[] royalFlush = new PlayingCard[5];
		for (int i=0; i<5; i++){
			royalFlush[i] = new PlayingCard(PlayingCard.CARD_TYPES[(9+i)%13], PlayingCard.SUITS[0], 
					PlayingCard.FACE_VALUES[(9+i)%13], PlayingCard.GAME_VALUES[(9+i)%13] );
		}
		
		//Set and print a royal flush
		testHand = new HandOfCards(testDeck);
		testHand.setHand(royalFlush);
		System.out.println(testHand.toString()+ testHand.handType()); //Should print royal flush
		
		//Make it a simple flush and print
		testHand.cardArray[0] = new PlayingCard(PlayingCard.CARD_TYPES[5], PlayingCard.SUITS[0], 
				PlayingCard.FACE_VALUES[5], PlayingCard.GAME_VALUES[5]);
		testHand.sort();
		System.out.println(testHand.toString()+ testHand.handType()); //Should print flush
		
		//Make it a straight flush and print
		testHand.cardArray[4] = new PlayingCard(PlayingCard.CARD_TYPES[8], PlayingCard.SUITS[0], 
				PlayingCard.FACE_VALUES[8], PlayingCard.GAME_VALUES[8]);
		testHand.sort();
		System.out.println(testHand.toString()+ testHand.handType()); //Should print straight flush


	}

}
