



/* Author: Jason Ioerger
 * Date: 4/30/2019
 * Class: CSC 160 Combo
 * Assignment: Final Project
 * 
 * Builds the deck of 52 cards as objects.
 */

public class Cards
{
	// Initialization of all fields for Deck object
	private String suit; // Suit of card
	private String rank; // Rank of card
	private int value; // Point value of card
	
	/**
	 * Creates a Deck object
	 * 
	 * @param suit
	 * @param rank
	 * @param value
	 */
	public Cards(String suit, String rank, int value)
	{
		super ( );
		this.suit = suit;
		this.rank = rank;
		this.value = value;
	}
	
	/**
	 * Creates a blank Deck object
	 * 
	 * @param suit
	 * @param rank
	 * @param value
	 */
	public Cards( )
	{
		super ( );
		this.suit = "null";
		this.rank = "null";
		this.value = 0;
	}
	
	/**
	 * @return the suit
	 */
	public String getSuit( )
	{
		return suit;
	}
	
	/**
	 * @param suit the suit to set
	 */
	public void setSuit( String suit )
	{
		this.suit = suit;
	}
	
	/**
	 * @return the rank
	 */
	public String getRank( )
	{
		return rank;
	}
	
	/**
	 * @param rank the rank to set
	 */
	public void setRank( String rank )
	{
		this.rank = rank;
	}
	
	/**
	 * @return the value
	 */
	public int getValue( )
	{
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue( int value )
	{
		this.value = value;
	}
	
	@Override
	//custom override for to String method
	public String toString ()
	{
		return ( rank != null ? rank + " of  " : "" ) + ( suit != null ?  suit  : "" );
	}
}