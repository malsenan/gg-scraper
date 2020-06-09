package scraper;


public class OpponentChampion implements Comparable<OpponentChampion> {
	
	private String name;
	public double gamesPlayedAgainst;
	public double winsAgainst;
	public double againstWinRate;
	
	public OpponentChampion() {
		name = null;
	}
	
	public OpponentChampion(String nameIn){
		name = nameIn;
	}
	
	public String getName() {
		return name;
	}
	
	public int getGames() {
		return (int) gamesPlayedAgainst;
	}
	
	public double getWR() {
		return Math.round(againstWinRate * 100) / 100;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OpponentChampion)) {
			return false;
		}
		OpponentChampion c = (OpponentChampion) o;
		return this.name.equals(c.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/*@Override
	//Compare games played to sort champions (compare names if games played are equal)
	public int compare(OpponentChampion c1, OpponentChampion c2) {
		int compareGames = (int) (c1.gamesPlayedAgainst - c2.gamesPlayedAgainst);
		if (compareGames == 0) {
			return c1.name.compareTo(c2.name);
		}
		else if (compareGames > 0) {
			return -1;
		}
		return 1;
	}*/

	@Override
	public int compareTo(OpponentChampion c) {
		return (int) (c.gamesPlayedAgainst - gamesPlayedAgainst);
	}
}
