package poker;

import poker.PlayingCard;

import java.util.concurrent.Semaphore;
import java.util.Random;

public class DeckOfCards {

	private int cardsDealt;
	private PlayingCard[] deck;
	private Semaphore dealerAvailable;
	
	//TODO
	public DeckOfCards(){
		dealerAvailable = new Semaphore(1);
		reset();
		shuffle();
	}
	
	/*
	 * Returns next non-dealt card and increments cardsDealt index
	 * Uses the semaphore to lock down a critical section in case parallel access occurs
	 */
	public PlayingCard dealNext() throws InterruptedException{
		dealerAvailable.acquire();
		PlayingCard outputCard = deck[cardsDealt];
		cardsDealt++;
		dealerAvailable.release();
		return outputCard;
	}
	
	/*
	 * Places a previously dealt card back on the bottom of the deck
	 * Uses the semaphore to ensure other threads don't alter card indexes while running
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
			} while (index1 != index2);
			
			PlayingCard temp = deck[index1];
			deck[index1] = deck[index2];
			deck[index2] = temp;
		}
	}
	
	/*
	 * Creates a new deck of cards array and sets cards dealt to zero
	 */
	public void reset(){
		deck = PlayingCard.newFullPack();
		cardsDealt = 0;
	}
	
	//TODO
	public static void main(String[] args) {

	}

}
