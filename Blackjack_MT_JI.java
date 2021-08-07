

/* Author: Michael Tinnin
 * Author: Jason Ioerger
 * Due Date: 5/13/2019
 * Class: CSC 160 Combo
 * Assignment: Final Project - Blackjack
 * 
 * Plays through the game of Blackjack. No insurance, surrender, or "even money" options.
 */

import java.io.*;
import java.util.*;

public class Blackjack_MT_JI
{
	public static Scanner input = new Scanner ( System.in ); // Initializing input Scanner

	// Main method. Author: Jason & Michael
	public static void main( String[ ] args )
	{
		LinkedList<Cards> deck = new LinkedList<> ( ); // Deck of 52 cards
		File inDeck = new File ( "deck.txt" ); // File to read
		Scanner inputDeck = null;

		// Begin gameplay coding
		char again = 'y'; // Allows the players to continue playing
		int players; // Number of players
		char correct = 'Y'; // Allows players to verify starting information
		LinkedList<Cards> houseHand = new LinkedList<> ( ); // Hand for house
		LinkedList<Cards> player1Hand = new LinkedList<> ( ); // Hand for player 1
		LinkedList<Cards> player2Hand = new LinkedList<> ( ); // Hand for player 2
		LinkedList<Cards> player3Hand = new LinkedList<> ( ); // Hand for player 3
		LinkedList<Player> playerList = new LinkedList<> ( ); // Players object
		Player house = new Player ( "House", "Continue", 0, 500000 ); // House Player object

		// Reads Deck.txt file to build deck
		try
		{
			inputDeck = new Scanner ( inDeck );
			String[ ] deckArray;
			while ( inputDeck.hasNextLine ( ) )
			{
				Cards nextItem = new Cards ( );
				String oneItem = inputDeck.nextLine ( );
				deckArray = oneItem.split ( "," );
				nextItem.setSuit ( deckArray[0] );
				nextItem.setRank ( deckArray[1] );
				nextItem.setValue ( Integer.parseInt ( deckArray[2] ) );
				deck.add ( nextItem );
			}
		} catch ( FileNotFoundException e )
		{
			throw new RuntimeException ( "file not found" + e );
		} finally
		{
			if ( inputDeck != null )
			{
				inputDeck.close ( );
			}
		}

		// Do/while loop until player's starting information is correct
		do
		{
			System.out.printf ( "How many players will play today?\n" );
			players = input.nextInt ( );

			// Verifies the number of players
			while ( players < 1 || players > 3 )
			{
				System.out.printf ( "There must be at least one player, but no more than 3.\nPlease try again.\n" );
				players = input.nextInt ( );
			}

			// Creates blank Player objects for the playerList
			for ( int i = 0; i < 3; i++ )
			{
				Player blankPlayer = new Player ( );
				playerList.add ( blankPlayer );
			}

			// Sets Continue status for the number of active players
			for ( int i = 0; i < players; i++ )
			{
				playerList.get ( i ).setStatus ( "Continue" );
			}

			System.out.printf ( "\nHow much would you like to buy-in for?\n" );
			System.out.printf ( "1. $25\n2. $50\n3. $75\n4. $100\n" );
			int buyInChoice = input.nextInt ( ); // Determines initial buy-in amount

			// Verifies user input
			while ( buyInChoice < 1 || buyInChoice > 4 )
			{
				System.out.printf ( "You must select a buy-in option between 1 and 4.\nPlease try again:\n" );
				buyInChoice = input.nextInt ( );
			}
			setBuyIn ( playerList, buyInChoice );

			input.nextLine ( ); // Clears input for receiving nextLine input below

			// Loop for each player to enter their name
			for ( int i = 0; i < players; i++ )
			{
				System.out.printf ( "\nEnter the name for player %s:\n", i + 1 );
				playerList.get ( i ).setName ( input.nextLine ( ) );
			}

			System.out.printf ( "\nPlayer Information:\n" );
			// Displays initial players information
			for ( int i = 0; i < players; i++ )
			{
				System.out.printf ( "Name: %s\nCash: $%.2f\n", playerList.get ( i ).getName ( ),
						playerList.get ( i ).getCash ( ) );
				System.out.println ( );
			}
			System.out.printf ( "Is this correct (Y/N)?\n" );
			correct = input.next ( ).toUpperCase ( ).charAt ( 0 );
			while ( correct != 'Y' && correct != 'N' )
			{
				System.out.printf ( "You must enter Y or N. Please try again:" );
				correct = input.next ( ).toUpperCase ( ).charAt ( 0 );
			}
		} while ( correct == 'N' );
		System.out.printf ( "\n**************************************\n\n" );

		// Do/while loop to continue playing until done
		do
		{
			shuffle ( deck );

			// Deals cards to house and players
			deal ( houseHand, deck );
			for ( int i = 0; i < players; i++ )
			{
				switch ( i )
				{
					case 0:
						deal ( player1Hand, deck );
						break;
					case 1:
						deal ( player2Hand, deck );
						break;
					case 2:
						deal ( player3Hand, deck );
						break;
				}
			}

			// Initial bets
			System.out.printf ( "Minimum bet is $5.\n" );
			for ( int i = 0; i < players; i++ )
			{
				int bet; // amount being bet
				System.out.printf ( "%s, how much would you like to bet (in whole dollars)?\n",
						playerList.get ( i ).getName ( ) );
				bet = input.nextInt ( );
				while ( bet < 5 || bet > playerList.get ( i ).getCash ( ) )
				{
					if ( bet < 5 )
					{
						System.out.printf ( "You must bet at least $5. Please try again:\n" );
						bet = input.nextInt ( );
					}
					else
					{
						System.out.printf (
								"You only have $%.2f on hand. You must enter a bet below this amount. Please try again:\n",
								playerList.get ( i ).getCash ( ) );
						bet = input.nextInt ( );
					}
				}
				playerList.get ( i ).setBet ( bet );
				playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) - bet );
				System.out.printf ( "\n%s has bet $%.2f.\n\n", playerList.get ( i ).getName ( ),
						playerList.get ( i ).getBet ( ) );
			}

			// Displays player cards
			showHand ( player1Hand, playerList, 0 );
			System.out.println ( );
			if ( players > 1 )
			{
				showHand ( player2Hand, playerList, 1 );
				System.out.println ( );
			}
			if ( players > 2 )
			{
				showHand ( player3Hand, playerList, 2 );
				System.out.println ( );
			}

			System.out.println ( "Face up dealer card:" );
			System.out.println ( houseHand.get ( 1 ) );
			System.out.println ( );

			// Checks initial hands for Blackjack
			for ( int i = 0; i < players; i++ )
			{
				switch ( i )
				{
					case 0:
						chkBlkjck ( player1Hand, playerList, 0 );
						break;
					case 1:
						chkBlkjck ( player2Hand, playerList, 1 );
						break;
					case 2:
						chkBlkjck ( player3Hand, playerList, 2 );
						break;
				}
			}
			System.out.printf ( "\n**************************************\n\n" );

			// Runs through player turns
			if ( playerList.get ( 0 ).getStatus ( ).equals ( "Continue" ) )
			{
				System.out.printf ( "%s, it is your turn.\n", playerList.get ( 0 ).getName ( ) );
				showHand ( player1Hand, playerList, 0 );
				playHand ( player1Hand, deck, playerList, 0 );
			}
			if ( players > 1 && playerList.get ( 1 ).getStatus ( ).equals ( "Continue" ) )
			{
				System.out.printf ( "\n\n**************************************\n\n" );
				System.out.printf ( "%s, it is your turn.\n", playerList.get ( 1 ).getName ( ) );
				showHand ( player2Hand, playerList, 1 );
				playHand ( player2Hand, deck, playerList, 1 );
			}
			if ( players > 2 && playerList.get ( 2 ).getStatus ( ).equals ( "Continue" ) )
			{
				System.out.printf ( "\n\n**************************************\n\n" );
				System.out.printf ( "%s, it is your turn.\n", playerList.get ( 2 ).getName ( ) );
				showHand ( player3Hand, playerList, 2 );
				playHand ( player3Hand, deck, playerList, 2 );
			}
			System.out.printf ( "\n**************************************\n" );
			playDlr ( houseHand, deck, house, playerList, players );
			System.out.printf ( "\n**************************************\n" );
			gameCheck ( house, houseHand, playerList, player1Hand, player2Hand, player3Hand, players, deck );
			System.out.printf ( "\n**************************************\n\n" );
			// Outputs players current cash on hand after results
			for ( int i = 0; i < players; i++ )
			{
				System.out.printf ( "%s has $%.2f.\n", playerList.get ( i ).getName ( ), playerList.get ( i ).getCash ( ) );
			}

			// Asks the players if they would like to play again
			if ( players > 0 )
			{
				System.out.println ( "Do you want to play again (Y/N)?" );
				again = input.next ( ).toLowerCase ( ).charAt ( 0 );

				// Loop to verify player choice
				while ( again != 'y' && again != 'n' )
				{
					System.out.printf ( "You must enter either 'Y' or 'N'. Please try again:\n" );
					again = input.next ( ).toLowerCase ( ).charAt ( 0 );
				}
				if ( again == ( 'y' ) )
				{
					players = resetPlayerList ( playerList, players );
				}
			}
			else
			{
				again = 'n';
			}
		} while ( again == 'y' );
		System.out.printf ( "\nThank you for playing Blackjack!\n" );

	}// end of main

	/*******************************************************************************************
	 * Checks the end game circumstances and adjust values as needed Author Jason and Michael
	 * 
	 * @param house
	 *           dealer's attributes
	 * @param houseHand
	 *           dealers hand
	 * @param playerList
	 *           list of player names
	 * @param player1Hand
	 *           player1's hand
	 * @param player2Hand
	 *           player2's hand
	 * @param player3Hand
	 *           player3's hand
	 * @param players
	 * @param deck
	 */
	private static void gameCheck( Player house, LinkedList<Cards> houseHand, LinkedList<Player> playerList,
			LinkedList<Cards> player1Hand, LinkedList<Cards> player2Hand, LinkedList<Cards> player3Hand, int players,
			LinkedList<Cards> deck )
	{
		int houseSum = 0;// sum of house hand
		houseSum = handSum ( houseHand, houseSum );
		int plyr1HndSum = 0;// sum of player1Hand
		plyr1HndSum = handSum ( player1Hand, plyr1HndSum );
		int plyr2HndSum = 0;// sum of player2Hand
		plyr2HndSum = handSum ( player2Hand, plyr2HndSum );
		int plyr3HndSum = 0;// sum of player3Hand
		plyr3HndSum = handSum ( player3Hand, plyr3HndSum );

		// Outputs status of House
		if ( house.getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
		{
			System.out.printf ( "\nHouse has Blackjack.\n" );
		}
		else if ( house.getStatus ( ).equalsIgnoreCase ( "Bust" ) )
		{
			System.out.printf ( "\nHouse has busted. Any remaining bets will be paid out.\n" );
		}
		else
		{
			System.out.printf ( "\nHouse has a hand value of %s.\n", houseSum );
		}

		// Loops through players to compare against House result
		for ( int i = 0; i < players; i++ )
		{
			if ( house.getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
			{
				if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
				{
					System.out.println ( playerList.get ( i ).getName ( ) + " also has Blackjack and pushes House.\n" );
					playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) + playerList.get ( i ).getBet ( ) );
				}
				else
				{
					System.out.printf ( "%s loses to House. You lose $%.2f.\n\n", playerList.get ( i ).getName ( ),
							playerList.get ( i ).getBet ( ) );
					house.setCash ( playerList.get ( i ).getBet ( ) + house.getCash ( ) );
				}
			}
			else if ( house.getStatus ( ).equalsIgnoreCase ( "Bust" ) )
			{
				if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Stand" ) )
				{
					System.out.printf ( "%s has won %.2f.\n\n", playerList.get ( i ).getName ( ),
							playerList.get ( i ).getBet ( ) );
					playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) + playerList.get ( i ).getBet ( ) * 2 );
				}
				else if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
				{
					System.out.printf ( "%s has Blackjack and has won %.2f.\n\n", playerList.get ( i ).getName ( ),
							( playerList.get ( i ).getBet ( ) * 3 / 2 ) );
					playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) + playerList.get ( i ).getBet ( ) * 2 );
				}
			}
			else
			{
				if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
				{
					if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Blackjack" ) )
					{
						System.out.printf ( "%s has Blackjack! You win $%.2f!\n\n", playerList.get ( i ).getName ( ),
								( playerList.get ( i ).getBet ( ) * 3 / 2 ) );
						playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) + playerList.get ( i ).getBet ( )
								+ ( playerList.get ( i ).getBet ( ) * 3 / 2 ) );
						house.setCash ( house.getCash ( ) - playerList.get ( i ).getBet ( ) );
					}
				}
				if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Stand" ) )
				{
					if ( playerList.get ( i ).getName ( ).equals ( playerList.get ( 0 ).getName ( ) ) )
					{
						if ( plyr1HndSum > houseSum )
						{
							System.out.printf ( "\n%s beats House. You win $%.2f!\n", playerList.get ( 0 ).getName ( ),
									playerList.get ( 0 ).getBet ( ) );
							playerList.get ( 0 )
									.setCash ( playerList.get ( 0 ).getCash ( ) + playerList.get ( 0 ).getBet ( ) * 2 );
							house.setCash ( house.getCash ( ) - playerList.get ( 0 ).getBet ( ) );
						}
						if ( plyr1HndSum == houseSum )
						{
							System.out.printf ( "\n%s pushes House. You secured your bet.\n", playerList.get ( 0 ).getName ( ),
									playerList.get ( 0 ).getBet ( ) );
							playerList.get ( 0 )
									.setCash ( playerList.get ( 0 ).getCash ( ) + playerList.get ( 0 ).getBet ( ) );
						}
						if ( plyr1HndSum < houseSum )
						{
							System.out.printf ( "\n%s loses to House. You lose $%.2f.\n", playerList.get ( 0 ).getName ( ),
									playerList.get ( 0 ).getBet ( ) );
							house.setCash ( house.getCash ( ) + playerList.get ( 0 ).getBet ( ) );
						}
					}

					if ( players > 1 )
					{
						if ( playerList.get ( i ).getName ( ).equals ( playerList.get ( 1 ).getName ( ) ) )
						{
							if ( plyr2HndSum > houseSum )
							{
								System.out.printf ( "\n%s beats House. You win $%.2f!\n", playerList.get ( 1 ).getName ( ),
										playerList.get ( 1 ).getBet ( ) );
								playerList.get ( 1 )
										.setCash ( playerList.get ( 1 ).getCash ( ) + playerList.get ( 1 ).getBet ( ) * 2 );
								house.setCash ( house.getCash ( ) - playerList.get ( 1 ).getBet ( ) );
							}
							if ( plyr2HndSum == houseSum )
							{
								System.out.printf ( "\n%s pushes House. You secured your bet.\n",
										playerList.get ( 1 ).getName ( ), playerList.get ( 1 ).getBet ( ) );
								playerList.get ( 1 )
										.setCash ( playerList.get ( 1 ).getCash ( ) + playerList.get ( 1 ).getBet ( ) );
							}
							if ( plyr2HndSum < houseSum )
							{
								System.out.printf ( "\n%s loses to House. You lose $%.2f.\n", playerList.get ( 1 ).getName ( ),
										playerList.get ( 1 ).getBet ( ) );
								house.setCash ( house.getCash ( ) + playerList.get ( 1 ).getBet ( ) );
							}
						}
					}
					if ( players > 2 )
					{
						if ( playerList.get ( i ).getName ( ).equals ( playerList.get ( 2 ).getName ( ) ) )
						{
							if ( plyr3HndSum > houseSum )
							{
								System.out.printf ( "\n%s beats House. You win $%.2f!\n", playerList.get ( 2 ).getName ( ),
										playerList.get ( 2 ).getBet ( ) );
								playerList.get ( 2 )
										.setCash ( playerList.get ( 2 ).getCash ( ) + playerList.get ( 2 ).getBet ( ) * 2 );
								house.setCash ( house.getCash ( ) - playerList.get ( 2 ).getBet ( ) );
							}
							if ( plyr3HndSum == houseSum )
							{
								System.out.printf ( "\n%s pushes House. You secured your bet.\n",
										playerList.get ( 2 ).getName ( ), playerList.get ( 2 ).getBet ( ) );
								playerList.get ( 2 )
										.setCash ( playerList.get ( 2 ).getCash ( ) + playerList.get ( 2 ).getBet ( ) );
							}
							if ( plyr3HndSum < houseSum )
							{
								System.out.printf ( "\n%s loses to House. You lose $%.2f.\n", playerList.get ( 2 ).getName ( ),
										playerList.get ( 2 ).getBet ( ) );
								house.setCash ( house.getCash ( ) + playerList.get ( 2 ).getBet ( ) );
							}
						}
					}
				}
			}
		}

		resetHand ( houseHand, deck );
		resetHand ( player1Hand, deck );
		resetHand ( player2Hand, deck );
		resetHand ( player3Hand, deck );

	}// end of gameCheck

	/**********************************************************************************
	 * Author Michael sums the hands
	 * 
	 * @param handthe
	 *           hand being summed for comparison
	 * @param sum
	 *           sum of the hand
	 * @return sum of hand
	 */
	private static int handSum( LinkedList<Cards> hand, int sum )
	{
		for ( int i = 0; i < hand.size ( ); i++ )
		{
			sum += hand.get ( i ).getValue ( );
		}
		return sum;
	}

	/***************************************************************************************
	 * Author Michael resets hand after a game is over
	 * 
	 * @param hand
	 *           the hand being cleared
	 * @param deck
	 */
	private static void resetHand( LinkedList<Cards> hand, LinkedList<Cards> deck )
	{
		int j = hand.size ( ); // Set to stop loop on hand size
		for ( int i = 0; i < j; i++ )
		{
			deck.add ( hand.removeLast ( ) );
		}
	}

	/***************************************************************************************
	 * Author Michael & Jason clears player list after games is completed
	 * 
	 * @param playerList
	 *           list of player names
	 * @param players
	 * @return
	 */
	private static int resetPlayerList( LinkedList<Player> playerList, int players )
	{
		for ( int i = 0; i < playerList.size ( ); i++ )
		{

			if ( playerList.get ( i ).getCash ( ) >= 5 )
			{
				playerList.get ( i ).setStatus ( "Continue" );
			}
			else
			{
				System.out.printf ( "%s has been knocked out of the game.\n", playerList.get ( i ).getName ( ) );
				playerList.remove ( i );
				i--;
				players--;
			}
		}
		return players;
	}

	/*********************************************************************************************
	 * Sets the initial cash amount for players. Author: Jason
	 * 
	 * @param playerList
	 *           list of players
	 * @param buyInChoice
	 *           amount of initial bet
	 */

	private static void setBuyIn( LinkedList<Player> playerList, int buyInChoice )
	{
		int buyIn = 0;// amount of money to bet with

		// Sets buy in amount
		switch ( buyInChoice )
		{
			case 1:
				buyIn = 25;
				break;
			case 2:
				buyIn = 50;
				break;
			case 3:
				buyIn = 75;
				break;
			case 4:
				buyIn = 100;
				break;
		}
		for ( int i = 0; i < playerList.size ( ); i++ )
		{
			playerList.get ( i ).setCash ( buyIn );
		}
	}// end of setBuyIn

	/******************************************************************************************************
	 * Checks for Blackjack after initial deal. Author: Michael & Jason
	 * 
	 * @param hand
	 *           hand being checked
	 * @param playerList
	 *           list of player names
	 * @param i
	 *           location in playerList
	 */

	private static void chkBlkjck( LinkedList<Cards> hand, LinkedList<Player> playerList, int i )
	{
		int sum = 0;// sum of hand
		for ( int j = 0; j < hand.size ( ); j++ )
		{
			sum += hand.get ( j ).getValue ( );
		}
		if ( sum == 21 )
		{
			System.out.printf ( "Blackjack for %s!\n", playerList.get ( i ).getName ( ) );
			playerList.get ( i ).setStatus ( "Blackjack" );
		}
	}// end of chkBlkjck

	/***********************************************************************************
	 * Plays through current players hand. Author: Jason & Michael
	 * 
	 * @param hand
	 *           hand being played
	 * @param deck
	 *           if player hits
	 * @param playerList
	 *           list of players
	 * @param i
	 *           location in player list of a player
	 */

	private static void playHand( LinkedList<Cards> hand, LinkedList<Cards> deck, LinkedList<Player> playerList, int i )
	{
		int choice = 1; // Controls player choice
		System.out.printf ( "\n%s, your cash on hand: $%.2f\n\n", playerList.get ( i ).getName ( ),
				playerList.get ( i ).getCash ( ) );

		// Double Down option for players without Blackjack
		if ( playerList.get ( i ).getStatus ( ).equalsIgnoreCase ( "Continue" ) )
		{
			System.out.printf ( "Would you like to double down (Y/N)?\n" );
			char doubleDown = input.next ( ).toUpperCase ( ).charAt ( 0 ); // If the player would like to double down

			// Verifies player choice
			while ( doubleDown != 'Y' && doubleDown != 'N' )
			{
				System.out.printf ( "Please enter either Y or N.\n" );
				doubleDown = input.next ( ).toUpperCase ( ).charAt ( 0 );
			}

			// Doubles bet
			if ( doubleDown == 'Y' )
			{
				if ( ( playerList.get ( i ).getBet ( ) ) > playerList.get ( i ).getCash ( ) )
				{
					System.out.printf ( "You do not have enough cash on hand to double your bet.\n" );
				}
				else
				{
					playerList.get ( i ).setCash ( playerList.get ( i ).getCash ( ) - playerList.get ( i ).getBet ( ) );
					playerList.get ( i ).setBet ( doubleBet ( playerList.get ( i ).getBet ( ) ) );
				}
			}
		}
		System.out.println ( );

		// Do/while loop until player chooses to stand
		do
		{
			System.out.printf ( "%s, what would you like to do?\n", playerList.get ( i ).getName ( ) );
			System.out.printf ( "1. Hit\n2. Stand\n" );
			choice = input.nextInt ( );
			while ( choice < 1 || choice > 2 )
			{
				System.out.printf ( "You must enter a choice between 1 and 2. Please try again:\n" );
				choice = input.nextInt ( );
			}
			switch ( choice )
			{
				case 1:
					hit ( hand, deck );
					choice = checkHand ( hand, playerList, i );
					showHand ( hand, playerList, i );
					break;
				case 2:
					playerList.get ( i ).setStatus ( "Stand" );
					break;
			}
			System.out.printf ( "\n" );
		} while ( choice == 1 );
	}// end of playHand

	// Shuffles the deck. Author: Jason Ioerger
	private static void shuffle( LinkedList<Cards> deck )
	{
		for ( int i = 0; i < 52; i++ )
		{
			deck.add ( deck.remove ( (int) ( Math.random ( ) * ( 52 - i ) ) ) );
		}
	}

	/*************************************************************************
	 * Author: Michael This method deals the initial cards to a player
	 * 
	 * @param hand
	 *           list containing the players hand
	 * @param deck
	 *           list containing the deck
	 */
	public static void deal( LinkedList<Cards> hand, LinkedList<Cards> deck )
	{
		hand.add ( deck.removeFirst ( ) );
		hand.add ( deck.removeFirst ( ) );

	}

	/************************************************************************
	 * by michael This method adds a card to the players hand
	 * 
	 * @param hand
	 *           list containing the player's hand
	 * @param deck
	 *           list containing the deck
	 */
	public static void hit( LinkedList<Cards> hand, LinkedList<Cards> deck )
	{
		hand.add ( deck.removeFirst ( ) );
	}

	/***************************************************************************
	 * Author: Michael & Jason This method determines the status of a hand
	 * 
	 * @param hand
	 *           list containing the hand
	 * @param j
	 * @param playerList
	 * @return status of player's hand
	 */
	public static int checkHand( LinkedList<Cards> hand, LinkedList<Player> playerList, int j )
	{
		int sum = 0;// value of hand
		int status = 1; // Returns 1 if player total is < 21 or returns 2 if they bust
		for ( int i = 0; i < hand.size ( ); i++ )
		{
			sum += hand.get ( i ).getValue ( );
		}
		if ( sum > 21 )
		{
			int count = 0; // ace that is changed to value of 1
			for ( int i = 0; i < hand.size ( ) && count != 1; i++ )
			{
				if ( hand.get ( i ).getValue ( ) == 11 )
				{
					hand.get ( i ).setValue ( 1 );
					count++;
					sum = 0;
					for ( int k = 0; k < hand.size ( ); k++ )
					{
						sum += hand.get ( k ).getValue ( );
					}
				}
			}
			if ( sum > 21 )
			{
				System.out.printf ( "%s busts.\n", playerList.get ( j ).getName ( ) );
				playerList.get ( j ).setStatus ( "Bust" );
				status = 2;
			}
		}
		return status;
	}

	/*************************************************************************
	 * Author: Michael allows player to double bet
	 * 
	 * @param d
	 *           initial bet
	 * @return bet * 2
	 */
	public static double doubleBet( double d )
	{
		d = d * 2;
		return d;
	}

	/****************************************************************************
	 * by Jason and Michael determines if dealer needs to hit
	 * 
	 * @param hand
	 *           dealers hand
	 * @param deck
	 *           current status of the deck
	 * @param house
	 *           house object
	 * 
	 * @param playerList
	 *           list of the players
	 * @param players
	 *           number of players
	 */
	public static void playDlr( LinkedList<Cards> hand, LinkedList<Cards> deck, Player house,
			LinkedList<Player> playerList, int players )
	{
		int sum = 0;// value of the hand
		showHand ( hand, house );
		System.out.println ( );
		
		// Loop for hit/stand on House hand
		while ( sum < 17 )
		{
			sum = 0;
			for ( int i = 0; i < hand.size ( ); i++ )
			{
				sum += hand.get ( i ).getValue ( );
			}
			if ( sum == 21 )
			{
				System.out.printf ( "House has Blackjack.\n" );
			}
			if ( sum < 17 )
			{
				System.out.printf ( "Dealer hits.\n" );
				hit ( hand, deck );
				showHand ( hand, house );
			}
			System.out.println ( );
		}
		if ( sum > 21 )
		{
			System.out.printf ( "Dealer busts.\n\n" );
			// houseBusts ( house, playerList, players );
			house.setStatus ( "Bust" );
		}
	}

	/******************************************************************************
	 * by Michael This method displays a players hand
	 * 
	 * @param hand
	 *           cards the player is holding
	 * @param playerList
	 * @param j
	 *           location of player in the playerList
	 */
	public static void showHand( LinkedList<Cards> hand, LinkedList<Player> playerList, int j )
	{
		System.out.printf ( "%s, your hand is:\n", playerList.get ( j ).getName ( ) );
		
		// Loop to output hand
		for ( int i = 0; i < hand.size ( ); i++ )
		{
			System.out.println ( hand.get ( i ) );
		}
	}// end ofshowHand

	// OVERLOADED. Displays full House hand. Author: Jason
	public static void showHand( LinkedList<Cards> hand, Player player )
	{
		System.out.printf ( "House's hand:\n" );
		
		// Loop to output House hand
		for ( int i = 0; i < hand.size ( ); i++ )
		{
			System.out.println ( hand.get ( i ) );
		}
	}

}
