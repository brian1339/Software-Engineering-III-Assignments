package poker;

public class HandOfCards {
	
	private static final int CARDS_HELD = 5;
	private PlayingCard[] cardArray;
	private DeckOfCards deck;
	
	
	public HandOfCards(DeckOfCards deck) throws InterruptedException {
		this.deck = deck;
		cardArray = new PlayingCard[CARDS_HELD];
		for (int i=0; i<CARDS_HELD; i++){
			cardArray[i] = deck.dealNext();
		}
		sort();
	}
	
	/*
	 * Uses a bubble sort to sort the cards in the hand from high game value to low
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
	
	/*
	 * Returns a string of all the cards in the hand separated by spaces
	 */
	public String toString(){
		String output = "";
		for (int i=0; i<cardArray.length; i++){
			output += cardArray[i] +"(" + cardArray[i].getGameValue() + ")" + " ";
		}
		return output;
	}
	
	/**
	 * Checks whether cards are straight ie. they all decrement in gameValue by 1 as we go along the array
	 * This saves code repeat in our public boolean hand checks below
	 */
	private boolean straightCards(){
		boolean straightCards = true;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i].getGameValue() != cardArray[i-1].getGameValue()-1){
				straightCards = false;
			}
		}
		return straightCards;
	}
	
	/**
	 * Checks whether all cards in the array are the same suit
	 * Again saves code repeat in public boolean hand checks
	 */
	private boolean sameSuitCards(){
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
	 * with length equal the number given as input
	 * Saves code repeat in all gameValue matching boolean methods for Hand checking except for isTwoPair
	 * @return False if no segment match of that length or segment of matching cards is longer than length 
	 * specified. Returns true if segment where all card values match of exact input length exists
	 */
	private boolean segmentMatch(int segmentLength){
		boolean segmentMatch = false;
		for (int i=segmentLength-1; i<cardArray.length; i++){
			boolean thisSegmentMatches = true;
			for (int j=i-1; j>i-segmentLength; j--){
				if (cardArray[j].getGameValue() != cardArray[i].getGameValue()){
					thisSegmentMatches = false;
					break;
				}
			}
			if (thisSegmentMatches){
				segmentMatch = true;
			}
			if(segmentMatch && i<cardArray.length-1){
				if (cardArray[i].getGameValue() == cardArray[i+1].getGameValue()){
					segmentMatch = false;
				}
			}
			if(segmentMatch && (i-segmentLength)>cardArray.length+1){
				if (cardArray[i-segmentLength].getGameValue() == cardArray[i-segmentLength-1].getGameValue()){
					segmentMatch = false;
				}
			}
			if(segmentMatch){
				break;
			}
		}
		return segmentMatch;
	}
	
	public boolean isRoyalFlush() {
		return cardArray[0].getGameValue() == 14 && straightCards() && sameSuitCards();
	}
	
	public boolean isStraightFlush() {
		//Check for RoyalFlush first
		if (isRoyalFlush()){
			return false;
		}
		return straightCards() && sameSuitCards();
	}
	
	public boolean isFourOfAKind() {
		return segmentMatch(4);
	}
	
	public boolean isFullHouse() {
		return segmentMatch(3) && segmentMatch(2);
	}
	
	public boolean isFlush(){
		//First, check all hands that supersede this one 
		if (isStraightFlush() || isRoyalFlush()){
			return false;
		}
		
		return sameSuitCards();
	}
	
	public boolean isStraight(){
		//Check all hands that supersede this one 
		if (isStraightFlush() || isRoyalFlush()){
			return false;
		}
		
		return straightCards();
	}
	
	public boolean isThreeOfAKind() {
		//Check all hands that could supersede this one 
		if (isFullHouse()){
			return false;
		}
		
		return segmentMatch(3);
	}
	
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
	
	public boolean isOnePair(){
		//First, check all hands that could supersede this one 
		if (isTwoPair() || isFullHouse()){
			return false;
		}
		return segmentMatch(2);
	}
	
	public boolean isHighHand(){
		if (isRoyalFlush() || isStraightFlush() || isFourOfAKind() || isFullHouse() || isFlush() 
				|| isStraight() || isThreeOfAKind() || isTwoPair() || isOnePair()){
			return false;
		}
		else{
			return true;
		}
	}
	
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
	
	private void setHand(PlayingCard[] newHand){
		cardArray = newHand;
		sort();
	}
	
	public static void main(String[] args) throws InterruptedException {
		DeckOfCards testDeck = new DeckOfCards();
		HandOfCards testHand;
		for (int i=0; i<10000; i++){
			testHand = new HandOfCards(testDeck);
			System.out.println(testHand.toString() + testHand.handType());
			testDeck.reset();
			testDeck.shuffle();
		}
		PlayingCard[] royalFlush = new PlayingCard[5];
		for (int i=0; i<5; i++){
			royalFlush[i] = new PlayingCard(PlayingCard.CARD_TYPES[(9+i)%13], PlayingCard.SUITS[0], PlayingCard.FACE_VALUES[(9+i)%13], PlayingCard.GAME_VALUES[(9+i)%13] );
		}
		testHand = new HandOfCards(testDeck);
		testHand.setHand(royalFlush);
		
		testHand.setHand(PlayingCard.newFullPack());
		System.out.println(testHand.toString());
	
		//System.out.println(testHand.toString() + testHand.handType());
	}

}
