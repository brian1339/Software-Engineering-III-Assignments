package poker;

import poker.PlayingCard;

import java.util.concurrent.Semaphore;
import java.util.Random;

public class DeckOfCards {

	private int cardsDealt;
	private PlayingCard[] deck;
	private Semaphore dealAvailable;
	
	//TODO
	public DeckOfCards(){
		reset();
		shuffle();
		dealAvailable = new Semaphore(1);
	}
	
	//TODO
	public PlayingCard dealNext() throws InterruptedException{
		dealAvailable.acquire();
		PlayingCard outputCard = deck[cardsDealt];
		cardsDealt++;
		dealAvailable.release();
		return outputCard;
	}
	
	//TODO
	public void returnCard(PlayingCard discarded){
		
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
		// TODO Auto-generated method stub

	}

}
