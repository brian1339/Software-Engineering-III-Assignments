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
	 * 
	 * Fixed to include Ace low sequences 
	 */
	private boolean hasSequentialOrder(){
		boolean sequentialCards = true;
		
		// Check for decrementing gameValues along the array
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i].getGameValue() != cardArray[i-1].getGameValue()-1){
				sequentialCards = false;
			}
		}
		
		// If we don't have sequential cards by game value, we check for an ace low sequence
		if (!sequentialCards){
			sequentialCards = hasAceLowSequence();
		}
		
		return sequentialCards;
	}
	
	/**
	 * Checks if there is an ace low sequence of cards based on face value
	 * ie. cardArray contains A,5,4,3,2 of any suit
	 */
	private boolean hasAceLowSequence() {
		boolean aceLowSequence = true;
		for(int i=1; i<cardArray.length+1; i++){
			if (cardArray[i-1].getFaceValue() != cardArray[i%cardArray.length].getFaceValue()-1){
				aceLowSequence = false;
			}
		}
		return aceLowSequence;
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
		return cardArray[0].getGameValue() == 14 && hasSequentialOrder() && hasAllSameSuit() && !hasAceLowSequence();
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
				for (int j = i-segmentLength+1; j<=i; j++){
					segmentSortedCards[filledArrayIndex] = cardArray[j];
					filledArrayIndex++;
				}
				
				// Copy the cards of higher value into the array in order
				for (int j=0; j<= i-segmentLength; j++){
					segmentSortedCards[filledArrayIndex] = cardArray[j];
					filledArrayIndex++;
				}
				
				// Copy the cards of higher value into the array in order
				for (int j = i+1; j < cardArray.length; j++){
					PlayingCard temp = cardArray[j];
					segmentSortedCards[filledArrayIndex] = temp;
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
			if (hasAceLowSequence()){
				// Ace is low in straight so we add the high card's game Value
				gameValue+= cardArray[1].getGameValue(); 
			}
			else{
				// Add highest gamevalue in the array
				gameValue += cardArray[0].getGameValue();
			}
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
		 * There can never be two full houses with the same 3 matching cards so 
		 * we ignore the 2 matched cards
		 */
		if(isFullHouse()){
			gameValue = FULL_HOUSE_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(3);
			gameValue += segmentSorted[0].getGameValue();
		}
		
		/*
		 * For flush add to the default the values of the cards by their base 15 
		 * exponentials according to their order in the sorted array
		 */
		if (isFlush()) {
			gameValue = FLUSH_DEFAULT;
			for (int i=0; i<cardArray.length; i++){
				gameValue += cardArray[i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
		}
		
		/*
		 * For simple straight, add the value of the largest card in the sorted 
		 * array to the default value
		 */
		if (isStraight()){
			gameValue = STRAIGHT_DEFAULT;
			
			if (hasAceLowSequence()){
				// Ace is low in straight so we add the high card's game Value
				gameValue+= cardArray[1].getGameValue(); 
			}
			else{
				// Add highest gamevalue in the array
				gameValue += cardArray[0].getGameValue();
			}
		}
		/*
		 * For three of a kind add to the default the value of the matching cards
		 * It is impossible to have two hands with the same three matching cards
		 * so we ignore the remaining two cards
		 */
		if (isThreeOfAKind()) {
			gameValue = THREE_OF_A_KIND_DEFAULT;
			PlayingCard[] segmentSorted = segmentSort(3);
			gameValue += segmentSorted[0].getGameValue();
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
			 * 3 cases, stray unmatched card is at front, middle or end of sorted array
			 */
			if (cardArray[0].getGameValue() != cardArray[1].getGameValue()){
				factorBase2 = cardArray[1].getGameValue();
				factorBase1 = cardArray[3].getGameValue();
				factorBase0 = cardArray[0].getGameValue();
			}
			else if (cardArray[2].getGameValue() != cardArray[3].getGameValue()){
				factorBase2 = cardArray[0].getGameValue();
				factorBase1 = cardArray[3].getGameValue();
				factorBase0 = cardArray[2].getGameValue();
			}
			else if (cardArray[3].getGameValue() != cardArray[4].getGameValue()){
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
			for (int i=1; i<segmentSorted.length; i++){
				gameValue += segmentSorted[i].getGameValue() 
						* Math.pow(exponentialBase, segmentSorted.length-i-1);
			}
			
		}
		/*
		 * For high hand, add to the default value the game values of the cards
		 * multiplied in order by 15^4, 15^3, 15^2, 15^1 and 15^0 respectively
		 */
		if (isHighHand()){
			gameValue = HIGH_HAND_DEFAULT;
			for (int i=0; i<cardArray.length; i++){
				gameValue += cardArray[i].getGameValue() 
						* Math.pow(exponentialBase, cardArray.length-i-1);
			}
		}
		
		return gameValue;
	}
	
	/**
	 * Returns a boolean of whether the hand is considered a busted flush
	 * ie. all cards are of the same suit but one
	 */
	public boolean isBustedFlush(){
		boolean bustedFlush = false;
		
		//We check each card in the array and compare its suit to the next
		for (int i=0; i<cardArray.length-1; i++){
			

			//If there is a single card that does not match the previous set true
			if (cardArray[i+1].getSuit() != cardArray[i].getSuit()){
				bustedFlush = true;
				
				//Then check the rest of the array matches card [i]
				for (int j=i+2; j<cardArray.length; j++){
					
					//If another card doesn't match i, we don't have a busted flush
					if (cardArray[i].getSuit() != cardArray[j].getSuit()){
						bustedFlush = false;
					}
					
				}
				//We only want to check for a single card difference so break after one check
				break;
			}
		}
		
		return bustedFlush;
	}
	
	/**
	 * Returns whether the hand is a broken straight
	 * ie. The cards are all in sequential order like a straight but one 
	 */
	public boolean isBrokenStraight(){
		boolean brokenStraight = false;
		
		//Check each card of the array has decrementing value from the one previous
		for (int i=0; i<cardArray.length-1; i++){
			
			if (cardArray[i+1].getGameValue() != cardArray[i].getGameValue()-1){
				brokenStraight = true;
				
			}
		}
		//TODO
		return brokenStraight;
	}
	
	/**
	 * Tests all boundary cases between game values hands and all 
	 * other workings of the class work correctly
	 */
	private static boolean executeBoundaryTests() throws InterruptedException{
        boolean boundaryTestSuccess = true;
		
		DeckOfCards testDeck = new DeckOfCards();
		HandOfCards highHand = new HandOfCards(testDeck);
		HandOfCards lowHand = new HandOfCards(testDeck);
		
		// Generating cards of different suits in arrays to use for testing
		PlayingCard [] aces, kings, queens, jacks, tens, nines, 
				eights, sevens, sixes, fives, fours, threes, twos;

		aces = new PlayingCard[4];
		for (int i=0; i<4; i++){
			aces[i] = new PlayingCard(PlayingCard.CARD_TYPES[0], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[0], PlayingCard.GAME_VALUES[0]);
		}

		kings = new PlayingCard[4];
		for (int i=0; i<4; i++){
			kings[i] = new PlayingCard(PlayingCard.CARD_TYPES[12], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[12], PlayingCard.GAME_VALUES[12]);
		}

		queens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			queens[i] = new PlayingCard(PlayingCard.CARD_TYPES[11], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[11], PlayingCard.GAME_VALUES[11]);
		}

		jacks = new PlayingCard[4];
		for (int i=0; i<4; i++){
			jacks[i] = new PlayingCard(PlayingCard.CARD_TYPES[10], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[10], PlayingCard.GAME_VALUES[10]);
		}

		tens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			tens[i] = new PlayingCard(PlayingCard.CARD_TYPES[9], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[9], PlayingCard.GAME_VALUES[9]);
		}

		nines = new PlayingCard[4];
		for (int i=0; i<4; i++){
			nines[i] = new PlayingCard(PlayingCard.CARD_TYPES[8], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[8], PlayingCard.GAME_VALUES[8]);
		}

		eights = new PlayingCard[4];
		for (int i=0; i<4; i++){
			eights[i] = new PlayingCard(PlayingCard.CARD_TYPES[7], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[7], PlayingCard.GAME_VALUES[7]);
		}

		sevens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			sevens[i] = new PlayingCard(PlayingCard.CARD_TYPES[6], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[6], PlayingCard.GAME_VALUES[6]);
		}

		sixes = new PlayingCard[4];
		for (int i=0; i<4; i++){
			sixes[i] = new PlayingCard(PlayingCard.CARD_TYPES[5], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[5], PlayingCard.GAME_VALUES[5]);
		}

		fives = new PlayingCard[4];
		for (int i=0; i<4; i++){
			fives[i] = new PlayingCard(PlayingCard.CARD_TYPES[4], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[4], PlayingCard.GAME_VALUES[4]);
		}

		fours = new PlayingCard[4];
		for (int i=0; i<4; i++){
			fours[i] = new PlayingCard(PlayingCard.CARD_TYPES[3], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[3], PlayingCard.GAME_VALUES[3]);
		}

		threes = new PlayingCard[4];
		for (int i=0; i<4; i++){
			threes[i] = new PlayingCard(PlayingCard.CARD_TYPES[2], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[2], PlayingCard.GAME_VALUES[2]);
		}

		twos = new PlayingCard[4];
		for (int i=0; i<4; i++){
			twos[i] = new PlayingCard(PlayingCard.CARD_TYPES[1], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[1], PlayingCard.GAME_VALUES[1]);
		}

		/*
		 * Testing royal flushes against high straight flushes, all suits tested
		 */
		System.out.println("\n\n~~~~~~~~~----------Royal Flush vs. Straight Flush Boundary Test----------~~~~~~~~~");
		for (int i=0; i<4; i++){
			highHand.setHand(new PlayingCard[] {aces[i], kings[i], queens[i], jacks[i], tens[i]});
			for (int j=(i+1)%4; j!=i; j = (j+1)%4){
				PlayingCard[] straightFlush = {kings[j], queens[j], jacks[j], tens[j], nines[j]};
				lowHand.setHand(straightFlush);
				if (highHand.getGameValue() <= lowHand.getGameValue()){
					System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
							+ " vs. " + lowHand.toString() + lowHand.handType());
					boundaryTestSuccess = false;
				}
				else {
					System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
							+ " vs. " + lowHand.toString() + lowHand.handType());
				}
			}
		}
		
		/*
		 * Boundary test between low straight flush and high four of a kind
		 */
		System.out.println("\n\n~~~~~~~~~----------Straight Flush vs. Four of a Kind Boundary Test----------~~~~~~~~~");
		for (int i =0; i<4; i++){
			highHand.setHand(new PlayingCard[] {sixes[i], fives[i], fours[i], threes[i], twos[i]});
			lowHand.setHand(new PlayingCard[] {aces[0], aces[1], aces[2], aces[3], kings[0]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				boundaryTestSuccess = false;
			}
			else {
				System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Boundary test between low four of a kind and high full house
		 */
		System.out.println("\n\n~~~~~~~~~----------Four of a Kind vs. Full House Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] {threes[0], twos[0], twos[1], twos[2], twos[3]});
		lowHand.setHand(new PlayingCard[] {aces[0], aces[1], aces[2], kings[0], kings[1]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			boundaryTestSuccess = false;
		}
		else {
			System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		/*
		 * Boundary test between low full house and high flush
		 */
		System.out.println("\n\n~~~~~~~~~----------Full House vs. Flush Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] {threes[0], threes[1], twos[0], twos[1], twos[2]});
		for (int i=0; i<4; i++){
			lowHand.setHand(new PlayingCard[] {aces[i], kings[i], queens[i], jacks[i], nines[i]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				boundaryTestSuccess = false;
			}
			else {
				System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Boundary test between low flush and high straight
		 */
		System.out.println("\n\n~~~~~~~~~----------Flush vs. Straight Boundary Test----------~~~~~~~~~");
		lowHand.setHand(new PlayingCard[] {aces[0], kings[0], queens[0], jacks[0], tens[1]});
		for (int i=0; i<4; i++){
			highHand.setHand(new PlayingCard[] {aces[i], kings[i], queens[i], jacks[i], nines[i]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				boundaryTestSuccess = false;
			}
			else {
				System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Boundary test between low straight and high three of a kind
		 */
		System.out.println("\n\n~~~~~~~~~----------Straight vs. Three of a Kind Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] {threes[0], threes[1], twos[0], twos[1], twos[2]});
		lowHand.setHand(new PlayingCard[] {aces[0], aces[1], aces[2], kings[0], queens[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			boundaryTestSuccess = false;
		}
		else {
			System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		/*
		 * Boundary test between low three of a kind and high two pair
		 */
		System.out.println("\n\n~~~~~~~~~----------Three of a Kind vs. Two Pair Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] { fours[0], threes[0], twos[2], twos[1], twos[0]});
		lowHand.setHand(new PlayingCard[] {aces[0], aces[1], kings[0], kings[1], queens[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			boundaryTestSuccess = false;
		}
		else {
			System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		/*
		 * Boundary test between low two pair and high one pair
		 */
		System.out.println("\n\n~~~~~~~~~----------Two Pair vs. One Pair Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] {fours[0], threes[0], threes[1], twos[0], twos[1]});
		lowHand.setHand(new PlayingCard[] {aces[0], aces[1], kings[0], queens[0], jacks[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			boundaryTestSuccess = false;
		}
		else {
			System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		/*
		 * Boundary test between low one pair and high high card
		 */
		System.out.println("\n\n~~~~~~~~~----------One Pair vs. High Card Boundary Test----------~~~~~~~~~");
		highHand.setHand(new PlayingCard[] {fives[0], fours[0], threes[0], twos[0], twos[0]});
		lowHand.setHand(new PlayingCard[] {aces[0], kings[0], queens[0], jacks[0], nines[1]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Boundary error: " + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			boundaryTestSuccess = false;
		}
		else {
			System.out.println("Boundary test success: "+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		return boundaryTestSuccess;
	}
	
	/**
	 * Tests within hand types whether HandOfCards.getGameValue is reflecting the value
	 * of the hands correctly and whether all other workings of the class work correctly
	 */
	private static boolean executeInnerTests() throws InterruptedException{
		boolean innerTestsSuccess = true;
		
		DeckOfCards testDeck = new DeckOfCards();
		HandOfCards highHand = new HandOfCards(testDeck);
		HandOfCards lowHand = new HandOfCards(testDeck);
		
		// Generating cards of different suits in arrays to use for testing
		PlayingCard [] aces, kings, queens, jacks, tens, nines, eights, sevens, sixes, fives, fours, threes, twos;

		aces = new PlayingCard[4];
		for (int i=0; i<4; i++){
			aces[i] = new PlayingCard(PlayingCard.CARD_TYPES[0], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[0], PlayingCard.GAME_VALUES[0]);
		}

		kings = new PlayingCard[4];
		for (int i=0; i<4; i++){
			kings[i] = new PlayingCard(PlayingCard.CARD_TYPES[12], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[12], PlayingCard.GAME_VALUES[12]);
		}

		queens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			queens[i] = new PlayingCard(PlayingCard.CARD_TYPES[11], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[11], PlayingCard.GAME_VALUES[11]);
		}

		jacks = new PlayingCard[4];
		for (int i=0; i<4; i++){
			jacks[i] = new PlayingCard(PlayingCard.CARD_TYPES[10], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[10], PlayingCard.GAME_VALUES[10]);
		}

		tens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			tens[i] = new PlayingCard(PlayingCard.CARD_TYPES[9], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[9], PlayingCard.GAME_VALUES[9]);
		}

		nines = new PlayingCard[4];
		for (int i=0; i<4; i++){
			nines[i] = new PlayingCard(PlayingCard.CARD_TYPES[8], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[8], PlayingCard.GAME_VALUES[8]);
		}

		eights = new PlayingCard[4];
		for (int i=0; i<4; i++){
			eights[i] = new PlayingCard(PlayingCard.CARD_TYPES[7], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[7], PlayingCard.GAME_VALUES[7]);
		}

		sevens = new PlayingCard[4];
		for (int i=0; i<4; i++){
			sevens[i] = new PlayingCard(PlayingCard.CARD_TYPES[6], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[6], PlayingCard.GAME_VALUES[6]);
		}

		sixes = new PlayingCard[4];
		for (int i=0; i<4; i++){
			sixes[i] = new PlayingCard(PlayingCard.CARD_TYPES[5], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[5], PlayingCard.GAME_VALUES[5]);
		}

		fives = new PlayingCard[4];
		for (int i=0; i<4; i++){
			fives[i] = new PlayingCard(PlayingCard.CARD_TYPES[4], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[4], PlayingCard.GAME_VALUES[4]);
		}

		fours = new PlayingCard[4];
		for (int i=0; i<4; i++){
			fours[i] = new PlayingCard(PlayingCard.CARD_TYPES[3], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[3], PlayingCard.GAME_VALUES[3]);
		}

		threes = new PlayingCard[4];
		for (int i=0; i<4; i++){
			threes[i] = new PlayingCard(PlayingCard.CARD_TYPES[2], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[2], PlayingCard.GAME_VALUES[2]);
		}

		twos = new PlayingCard[4];
		for (int i=0; i<4; i++){
			twos[i] = new PlayingCard(PlayingCard.CARD_TYPES[1], PlayingCard.SUITS[i], 
					PlayingCard.FACE_VALUES[1], PlayingCard.GAME_VALUES[1]);
		}
		
		//Put all the cards in the array so sequential cards are easy to access from a loop
		PlayingCard[][] allCards = {twos, threes, fours, fives, sixes, sevens, eights,
				nines, tens, jacks, queens, kings, aces};

		
		/*
		 * Royal flush inner tests. Check all royal flushes are equal
		 */
		System.out.println("\n\n~~~~~~~~~----------Royal Flush Inner Test----------~~~~~~~~~");
		for (int i=0; i<4; i++){
			highHand.setHand(new PlayingCard[] {aces[i], kings[i], queens[i], jacks[i], tens[i]});
			lowHand.setHand(new PlayingCard[] {aces[(i+1)%4], kings[(i+1)%4], queens[(i+1)%4], jacks[(i+1)%4], tens[(i+1)%4]});
			if (highHand.getGameValue() != lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Not Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (equal) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Straight flush inner tests. Check all straight flushes of same cards are equal across suits
		 * Check that incrementing rank of sequence of cards produces higher hand value
		 */
		System.out.println("\n\n~~~~~~~~~----------Straight Flush Inner Test----------~~~~~~~~~");
		//Test all straight flushes of equal rank are equal across suits
		for (int i=0; i<allCards.length - 5; i++){
			for (int j=0; j<4; j++){
				highHand.setHand(new PlayingCard[] {allCards[i][j], allCards[i+1][j], allCards[i+2][j], 
					allCards[i+3][j], allCards[i+4][j]});
				lowHand.setHand(new PlayingCard[] {allCards[i][(j+1)%4], allCards[i+1][(j+1)%4], allCards[i+2][(j+1)%4], 
					allCards[i+3][(j+1)%4], allCards[i+4][(j+1)%4]});
				if (highHand.getGameValue() != lowHand.getGameValue()){
					System.out.println("####### Inner Test Error (Not Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
					innerTestsSuccess = false;
				}
				else {
					System.out.println("Inner test success (equal) :"+ highHand.toString() + highHand.handType() 
							+ " vs. " + lowHand.toString() + lowHand.handType());
				}
			}
			System.out.println("");
		}
		
		//Test all straight flushes are better than straight flushes of lower ranks
		for (int i=1; i<allCards.length - 5; i++){
			highHand.setHand(new PlayingCard[] {allCards[i][0], allCards[i+1][0], allCards[i+2][0], 
				allCards[i+3][0], allCards[i+4][0]});
			lowHand.setHand(new PlayingCard[] {allCards[i-1][0], allCards[i][0], allCards[i+1][0], 
				allCards[i+2][0], allCards[i+3][0]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Four of a Kind Inner Tests. Checks that incrementing the matched cards produces higher score
		 */
		System.out.println("\n\n~~~~~~~~~----------Four of a Kind Inner Test----------~~~~~~~~~");
		for (int i=1; i<allCards.length; i++){
			highHand.setHand(new PlayingCard[] {allCards[i][0], allCards[i][1], allCards[i][2], allCards[i][3], allCards[(i+1)%13][0]});
			lowHand.setHand(new PlayingCard[] {allCards[i-1][0], allCards[i-1][1], allCards[i-1][2], allCards[i-1][3], allCards[(i+1)%13][0]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Full House Inner Tests. Checks that incrementing the 3 matched cards produces higher score
		 */
		System.out.println("\n\n~~~~~~~~~----------Full House Inner Test----------~~~~~~~~~");
		//Check that incrementing the 3 matched cards increases score
		for (int i=1; i<allCards.length; i++){
			highHand.setHand(new PlayingCard[] {allCards[i][0], allCards[i][1], allCards[i][2], allCards[(i+1)%13][0], allCards[(i+1)%13][1]});
			lowHand.setHand(new PlayingCard[] {allCards[i-1][0], allCards[i-1][1], allCards[i-1][2], allCards[(i+1)%13][2], allCards[(i+1)%13][3]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}

		/*
		 * Flush inner tests. Checks that each card in the flush has weighted value depending
		 * on it's order
		 */
		System.out.println("\n\n~~~~~~~~~----------Flush Inner Test----------~~~~~~~~~");
		for (int i=1; i < CARDS_HELD+1; i++){
			PlayingCard[] highCards = new PlayingCard[CARDS_HELD];
			PlayingCard[] lowCards = new PlayingCard[CARDS_HELD];
			
			for (int j=0; j < CARDS_HELD; j++){
				if (j <= i-1){
					highCards[j] = allCards[CARDS_HELD + 1 - j][0];
				}
				else {
					highCards[j] = allCards[CARDS_HELD - j][0];
				}
				
				if (j < i-1){
					lowCards[j] = allCards[CARDS_HELD + 1 - j][1];
				}
				else {
					lowCards[j] = allCards[CARDS_HELD - j][1];
				}
			}
			
			//Account for possibility of a straight occurring on first and last iteration
			if (i==1){
				lowCards[4] = twos[1];
			}
			if (i==CARDS_HELD){
				highCards[4] = threes[0];
				lowCards[4] = twos[1];
			}
			
			highHand.setHand(highCards);
			lowHand.setHand(lowCards);
			
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Straight inner tests. Checks that straight hands of equal rank are the same and that increasing
		 * rank of straight increases score
		 */
		System.out.println("\n\n~~~~~~~~~----------Straight Inner Test----------~~~~~~~~~");
		//Test that straight hands of equal rank have same score
		for (int i=0; i<allCards.length - 4; i++){
			for (int j=0; j<4; j++){
				highHand.setHand(new PlayingCard[] {allCards[i][j], allCards[i+1][j], allCards[i+2][j], 
					allCards[i+3][j], allCards[i+4][(j+3)%4]});
				lowHand.setHand(new PlayingCard[] {allCards[i][(j+1)%4], allCards[i+1][(j+1)%4], allCards[i+2][(j+1)%4], 
					allCards[i+3][(j+1)%4], allCards[i+4][(j+2)%4]});
				if (highHand.getGameValue() != lowHand.getGameValue()){
					System.out.println("####### Inner Test Error (Not Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
					innerTestsSuccess = false;
				}
				else {
					System.out.println("Inner test success (equal) :"+ highHand.toString() + highHand.handType() 
							+ " vs. " + lowHand.toString() + lowHand.handType());
				}
			}
			System.out.println("");
		}
		
		//Test all straight hands are better than straight hands of lower ranks
		for (int i=1; i<allCards.length - 5; i++){
			highHand.setHand(new PlayingCard[] {allCards[i][0], allCards[i+1][0], allCards[i+2][0], 
				allCards[i+3][0], allCards[i+4][1]});
			lowHand.setHand(new PlayingCard[] {allCards[i-1][0], allCards[i][0], allCards[i+1][0], 
				allCards[i+2][0], allCards[i+3][1]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Three of a Kind inner test. Checks that incrementing the three matched cards produces a
		 * better score
		 */
		System.out.println("\n\n~~~~~~~~~----------Three of a Kind Inner Test----------~~~~~~~~~");
		//Check that incrementing the 3 matched cards increases score
		for (int i=1; i<allCards.length; i++){
			highHand.setHand(new PlayingCard[] {allCards[i][0], allCards[i][1], allCards[i][2], allCards[(i+1)%13][0], allCards[(i+2)%13][1]});
			lowHand.setHand(new PlayingCard[] {allCards[i-1][0], allCards[i-1][1], allCards[i-1][2], allCards[(i+1)%13][2], allCards[(i+2)%13][3]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * Two Pair inner test. Checks that there is a weighted difference between the higher pair
		 * the lower pair and the stray unmatched card
		 */
		System.out.println("\n\n~~~~~~~~~----------Two Pairs Inner Test----------~~~~~~~~~");
		// Check pairs of different suits with equal ranking unmatched cards produce equal score 
		highHand.setHand(new PlayingCard[] {fours[0], fours[1], threes[0], threes[1], twos[0]});
		lowHand.setHand(new PlayingCard[] {fours[2], fours[3], threes[2], threes[3], twos[1]});
		if (highHand.getGameValue() != lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Not Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (equal) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		// Check that the upper pair increases score more than the rest of the cards
		highHand.setHand(new PlayingCard[] {fours[0], fours[1], threes[2], threes[3], fives[1]});
		lowHand.setHand(new PlayingCard[] {threes[0], threes[1], twos[2], twos[3], aces[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		//Check that if the upper pair are the same, the middle increases score more than unmatched card
		highHand.setHand(new PlayingCard[] {fours[0], fours[1], threes[2], threes[3], twos[1]});
		lowHand.setHand(new PlayingCard[] {fours[2], fours[3], twos[2], twos[3], aces[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		//Check that the last unmatched card increases score when incremented
		for (int i=3; i < allCards.length-1; i++){
			highHand.setHand(new PlayingCard[] {fours[0], fours[1], threes[2], threes[3], allCards[(i+1)%13][1]});
			lowHand.setHand(new PlayingCard[] {fours[2], fours[3], threes[0], threes[1], allCards[i][1]});
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}
		
		/*
		 * One pair inner test. Checks there is a weighted difference between the matched pair 
		 * and the remaining cards in order
		 */
		System.out.println("\n\n~~~~~~~~~----------One Pair Inner Test----------~~~~~~~~~");
		
		// Check pairs of different suits with equal ranking unmatched cards produce equal score 
		highHand.setHand(new PlayingCard[] {fives[0], fours[0], threes[0], threes[1], twos[0]});
		lowHand.setHand(new PlayingCard[] {fives[1], fours[1], threes[2], threes[3], twos[1]});
		if (highHand.getGameValue() != lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Not Equal):" + highHand.toString() + highHand.handType() 
				+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (equal) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		// Check weighted difference between matched pairs of cards
		highHand.setHand(new PlayingCard[] {fives[0], fours[0], threes[0], threes[1], twos[0]});
		lowHand.setHand(new PlayingCard[]  {aces[0], kings[0], queens[0], twos[0], twos[0]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		// Check weighted difference of highest ranking unmatched card
		highHand.setHand(new PlayingCard[] {sixes[0], fours[0], threes[0], twos[0], twos[1]});
		lowHand.setHand(new PlayingCard[] {fives[0], fours[1], threes[1], twos[2], twos[3]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		// Check weighted difference of second highest ranking unmatched card
		highHand.setHand(new PlayingCard[] {sixes[0], fives[0], threes[0], twos[0], twos[1]});
		lowHand.setHand(new PlayingCard[] {sixes[1], fours[0], threes[1], twos[2], twos[3]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		// Check weighted difference of lowest ranking unmatched card
		highHand.setHand(new PlayingCard[] {sixes[0], fives[0], fours[0], twos[0], twos[1]});
		lowHand.setHand(new PlayingCard[] {sixes[1], fives[1], threes[0], twos[2], twos[3]});
		if (highHand.getGameValue() <= lowHand.getGameValue()){
			System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
			innerTestsSuccess = false;
		}
		else {
			System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
					+ " vs. " + lowHand.toString() + lowHand.handType());
		}
		
		/*
		 * High Card inner test. Checks the weighted difference between the sequential 
		 * ranks of the cards in order 
		 */
		System.out.println("\n\n~~~~~~~~~----------High Card Inner Test----------~~~~~~~~~");
		for (int i=1; i < CARDS_HELD+1; i++){
			PlayingCard[] highCards = new PlayingCard[CARDS_HELD];
			PlayingCard[] lowCards = new PlayingCard[CARDS_HELD];
			
			for (int j=0; j < CARDS_HELD; j++){
				if (j <= i-1){
					highCards[j] = allCards[CARDS_HELD + 1 - j][j%4];
				}
				else {
					highCards[j] = allCards[CARDS_HELD - j][j%4];
				}
				
				if (j < i-1){
					lowCards[j] = allCards[CARDS_HELD + 1 - j][(j+1)%4];
				}
				else {
					lowCards[j] = allCards[CARDS_HELD - j][(j+1)%4];
				}
			}
			
			//Account for possibility of a straight occurring on first and last iteration
			if (i==1){
				lowCards[4] = twos[1];
			}
			if (i==CARDS_HELD){
				highCards[4] = threes[0];
				lowCards[4] = twos[1];
			}
			
			highHand.setHand(highCards);
			lowHand.setHand(lowCards);
			
			if (highHand.getGameValue() <= lowHand.getGameValue()){
				System.out.println("####### Inner Test Error (Less than or Equal):" + highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
				innerTestsSuccess = false;
			}
			else {
				System.out.println("Inner test success (Greater Than) :"+ highHand.toString() + highHand.handType() 
						+ " vs. " + lowHand.toString() + lowHand.handType());
			}
		}

		lowHand.setHand(new PlayingCard[] {aces[0], kings[1], queens[0], jacks[0], nines[0]});
		System.out.println("isBustedFlush test: " + lowHand.isBustedFlush());
		
		return innerTestsSuccess;
	}
	
	/*
	 * Executes boundary tests and inner hand tests and prints the test status 
	 * in the terminal after
	 */
	public static void main(String[] args) throws InterruptedException {
		
		boolean boundaryTestSuccess = executeBoundaryTests();
		boolean innerTestsSuccess = executeInnerTests();
		
		
		if (boundaryTestSuccess){
			System.out.println("### All Boundary tests between hands successful.");
		}
		else {
			System.out.println("XXX Boundary test(s) failed, please check terminal above for failures");
		}
		if (innerTestsSuccess){
			System.out.println("### All Inner tests of hands successful.");
		}
		else {
			System.out.println("XXX Inner test(s) failed, please check terminal above for failures");
		}
		
	}

}
