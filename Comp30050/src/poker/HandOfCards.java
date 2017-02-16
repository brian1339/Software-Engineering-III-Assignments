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
	
	public boolean isRoyalFlush() {
		
	}
	
	public boolean isStraightFlush() {
		
	}
	
	public boolean isFourOfAKind() {
		
	}
	
	public boolean isFullHouse() {
		
	}
	
	public boolean isFlush(){
		boolean flush = true;
		for (int i=1; i<cardArray.length; i++){
			if (cardArray[i-1].getSuit() != cardArray[i].getSuit()){
				flush = false;
			}
		}
		return flush;
	}
	
	public boolean isStraight(){
		boolean straight = true;
		for (int i=0; i<cardArray.length; i++){
			
		}
	}
	
	public boolean isThreeOfAKind() {
		
	}
	
	public boolean isTwoPair(){
		
	}
	
	public boolean isOnePair(){
		
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
