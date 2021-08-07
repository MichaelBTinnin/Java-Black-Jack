


public class Player
{
	// Initialization of all fields for Player object
	private String name;// player's identity
	private String status;// players status
	private double bet;// amount of player's bet
	private double cash;// amount of players buy in

	/**
	 * @param name
	 * @param status
	 * @param bet
	 * @param cash
	 */
	public Player(String name, String status, double bet, double cash)
	{
		this.name = name;
		this.status = status;
		this.bet = bet;
		this.cash = cash;
	}

	/**
	 * @param name
	 * @param status
	 * @param bet
	 * @param cash
	 */
	public Player()
	{
		this.name = null;
		this.status = "Done";
		this.bet = 0;
		this.cash = 0;
	}

	/**
	 * @return the name
	 */
	public String getName( )
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName( String name )
	{
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public String getStatus( )
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus( String status )
	{
		this.status = status;
	}

	/**
	 * @return the bet
	 */
	public double getBet( )
	{
		return bet;
	}

	/**
	 * @param bet
	 *           the bet to set
	 */
	public void setBet( double bet )
	{
		this.bet = bet;
	}

	/**
	 * @return the cash
	 */
	public double getCash( )
	{
		return cash;
	}

	/**
	 * @param cash
	 *           the cash to set
	 */
	public void setCash( double cash )
	{
		this.cash = cash;
	}

	// Custom override for toString method
	@Override
	public String toString( )
	{
		return "Player:\n" + ( name != null ? "Name: " + name + "\n" : "" )
				+ ( status != null ? "Status: " + status + "\n" : "" ) + "Bet: " + bet + "\nCash: " + cash + "\n";
	}

}
