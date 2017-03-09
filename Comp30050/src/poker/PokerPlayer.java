package poker;

public class PokerPlayer {
	
	/*
	 * Private internal fields for the class
	 */
	private DeckOfCards deck;
	private HandOfCards hand;
	
	/**
	 * Constructor takes a deck of cards and deals the player a hand
	 */
	public PokerPlayer(DeckOfCards deck) throws InterruptedException{
		this.deck = deck;
		hand = new HandOfCards(this.deck);
	}
	
	/**
	 * Discards up to three cards from the hand and replaces them with new cards from
	 * the deck
	 * Returns an int indicating how many cards were discarded
	 */
	public int discard(){
		
		int cardsDiscarded = 0;
		
		if (hand.isRoyalFlush()){
			return 0;
		}
		if (hand.isStraightFlush()){
		}
		if (hand.isFourOfAKind()){
		}
		if (hand.isFullHouse()){
		}
		if (hand.isFlush()){
		}
		if (hand.isStraight()){
		}
		if (hand.isThreeOfAKind()){
		}
		if (hand.isTwoPair()){
		}
		if (hand.isOnePair()){
		}
		if (hand.isHighHand()){
		}
		
		return cardsDiscarded;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
