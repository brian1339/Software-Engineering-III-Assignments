package poker;

import java.util.concurrent.Semaphore;
import java.util.Random;

public class DeckOfCards {

	private int cardsDealt;
	private PlayingCard[] deck;
	private Semaphore dealerAvailable;
	
	//TODO
	public DeckOfCards(){
		dealerAvailable = new Semaphore(1);
		deck = PlayingCard.newFullPack();
		reset();
		shuffle();
	}
	
	/*
	 * Returns next non-dealt card and increments cardsDealt index
	 * Uses the semaphore to lock down a critical section in case parallel access occurs
	 */
	public PlayingCard dealNext() throws InterruptedException{
		dealerAvailable.acquire();
		PlayingCard outputCard = null;
		if (cardsDealt < 52){
			outputCard = deck[cardsDealt];
			cardsDealt++;
		}
		dealerAvailable.release();
		return outputCard;
	}
	
	/*
	 * Shuffles the deck by randomly selecting two card indexes in the deck array and swapping the cards
	 * This is repeated by the size of the deck squared times to ensure the deck is thoroughly shuffled
	 */
	public void shuffle(){
		Random rand = new Random(System.currentTimeMillis());
		for(int i=0; i<(deck.length*deck.length); i++){
			int index1, index2;
			do {
				index1 = rand.nextInt(deck.length);
				index2 = rand.nextInt(deck.length);
			} while (index1 == index2);
			
			PlayingCard temp = deck[index1];
			deck[index1] = deck[index2];
			deck[index2] = temp;
		}
	}
	
	/*
	 * Returns a card to the bottom of the deck.
	 * Uses semaphore to ensure parallel access is not an issue
	 */
	public void returnCard(PlayingCard discarded) throws InterruptedException{
		int previousIndex = 0;
		dealerAvailable.acquire();
		// Find previous index of card in the deck array
		for (int i=0; i<deck.length && !deck[i].equals(discarded); i++){
			previousIndex = i;
		}
		// Move all other cards up one index in array
		for (int i=previousIndex; i<deck.length-1; i++){
			deck[i] = deck[i+1];
		}
		// Put discarded on bottom of deck
		deck[deck.length] = discarded;
		cardsDealt--;
		dealerAvailable.release();;
	}
	
	/*
	 * Sets cards dealt to zero
	 */
	public void reset(){
		cardsDealt = 0;
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		DeckOfCards testDeck = new DeckOfCards();
		PlayingCard[] testHand = new PlayingCard[5];
		PlayingCard[] cardsAlreadyDealt = new PlayingCard[52];
		
		
		for (int i=0; i<10 ; i++){
			for(int j=0; j<5; j++){
				testHand[j] = testDeck.dealNext();
				System.out.println(testHand[j].toString());
			}
		}
		
	}

}
