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
	
	private void sort(){
		
		//System.out.println("Unsorted " + this);
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
			//System.out.println("Sorting pass " + this);
		}
	}
	
	public String toString(){
		String output = "";
		for (int i=0; i<CARDS_HELD; i++){
			output += cardArray[i] + " ";
		}
		return output;
	}
	
	/*
	 * Checks whether cards are straight ie. they decrement in gameValue by 1 as we go along the array
	 * This saves code repeat in our public boolean hand checks below
	 */
	private boolean straightCards(){
		boolean straightCards = true;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getGameValue() != cardArray[i].getGameValue()-1){
				straightCards = false;
			}
		}
		return straightCards;
	}
	
	/*
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
	
	public boolean isRoyalFlush() {
		boolean roayalFlush = true;
		// Check the first card in hand is an Ace, if not
		if (cardArray[0].getGameValue() != 14) {
			roayalFlush = false;
		}
		//Check suits of cards match
		if (!sameSuitCards()){
			roayalFlush = false;
		}
		//Check that cards are straight
		if (!straightCards()){
			roayalFlush = false;
		}
		return roayalFlush;
	}
	
	public boolean isStraightFlush() {
		boolean straightFlush = true;
		
		//Check for RoyalFlush first
		if (isRoyalFlush()){
			return false;
		}
		//Check suits of cards match
		if (!sameSuitCards()){
			straightFlush = false;
		}
		//Check that cards are straight ie. their game value decrements by 1 each array entry
		if (!straightCards()){
			straightFlush = false;
		}
		return straightFlush;
	}
	
	public boolean isFourOfAKind() {
		
	}
	
	public boolean isFullHouse() {
		
	}
	
	public boolean isFlush(){
		//Check for RoyalFlush and StraightFlush first
		if (isStraightFlush() || isRoyalFlush()){
			return false;
		}
		boolean flush = true;
		//Check suits of cards match
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getSuit() != cardArray[i].getSuit()){
				flush = false;
			}
		}
		return flush;
	}
	
	public boolean isStraight(){
		//Check for RoyalFlush and StraightFlush first
		if (isStraightFlush() || isRoyalFlush()){
			return false;
		}
		boolean straight = true;
		//Check that cards are straight ie. their game value decrements by 1 each array entry
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getGameValue() != cardArray[i].getGameValue()-1){
				straight = false;
			}
		}
		return straight;
	}
	
	public boolean isThreeOfAKind() {
		//Check all hands that supersede this one 
		if (isFullHouse() ||isFourOfAKind()){
			return false;
		}
		
		boolean threeOfAKind = false;
		
		
	}
	
	public boolean isTwoPair(){
		//Check all hands that supersede this one 
		if (isThreeOfAKind() || isFullHouse() ||isFourOfAKind()){
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
		//First, check all hands that supersede this one 
		if (isTwoPair() || isThreeOfAKind() || isFullHouse() ||isFourOfAKind()){
			return false;
		}
		
		//Check if there are a pair of adjacent cards in the sorted array with the same game value
		boolean onePair = false;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getGameValue() == cardArray[i].getGameValue()){
				onePair = true;
			}
		}
		return onePair;
	}
	
	public boolean isHighHand(){
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		DeckOfCards testDeck = new DeckOfCards();
		HandOfCards testHand;
		for (int i=0; i<5; i++){
			testHand = new HandOfCards(testDeck);
			System.out.println(testHand.toString());
		}
	}

}
