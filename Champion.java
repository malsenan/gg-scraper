package scraper;


import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Champion implements Comparable<Champion>{
	
	private String name;
	private int games;
	private double winRate;
	
	//opponents
	public ArrayList<OpponentChampion> opponentList;
	
	public Champion() {
		name = "null";
	}
	
	public Champion(String nameIn) {
		name = nameIn;
	}
	
	public Champion(String nameIn, HtmlPage gg) {
		name = nameIn;
		//initialize opponent set
		opponentList = new ArrayList<OpponentChampion>();
		double gamesPlayed = 0;
		double wins = 0;

		List<DomElement> allGames = gg.getByXPath("//div[@class='GameItemWrap']");
		//iterate through all the games and find the games played with the champ and the wins
		for (DomElement game : allGames) {
			boolean victory = false; //used later
			if (game.getFirstElementChild().getFirstElementChild().getFirstElementChild().getFirstElementChild().asText().equals("Normal") || game.getFirstElementChild().getFirstElementChild().getFirstElementChild().getFirstElementChild().asText().equals("Ranked Solo")) {
				String champ = game.getFirstElementChild().getFirstElementChild().getChildNodes().get(3).getChildNodes().get(7).getChildNodes().get(1).asText();
				if (champ.equals(nameIn)) {
					gamesPlayed++;
					
					//win condition
					if (game.getFirstElementChild().getFirstElementChild().getFirstElementChild().getChildNodes().get(7).asText().equals("Victory")) {
						victory = true;
						wins++;
					}
					DomElement teamOne = game.getFirstElementChild().getFirstElementChild().getChildNodes().get(9).getNextElementSibling().getFirstElementChild();
					DomElement teamTwo = teamOne.getNextElementSibling();
					Iterable<DomElement> blue1 = teamOne.getChildElements(); //blue team
					Iterable<DomElement> blue2 = teamOne.getChildElements(); //another blue team to reset iterator
					Iterable<DomElement> red = teamTwo.getChildElements(); //red team
					String opponent = null;
					int i = 0;
					boolean blueSide = false;
					for (DomElement element : blue1) {
						if (element.getAttribute("class").equals("Summoner Requester")) {
							blueSide = true;
							break;
						}
						i++;
					}
					if (blueSide) { 
						int j = 0;
						for (DomElement element : red) {
							if (j == i) {
								opponent = element.getFirstElementChild().getFirstElementChild().asText();
							}
							j++;
						}
					}
					else {
						int k = 0;
						for (DomElement element : red) {
							if (element.getAttribute("class").equals("Summoner Requester")) {
								break;
							}
							k++;
						}
						int l = 0;
						//use blue2 to reset iterator
						for (DomElement element : blue2) {
							if (l == k) {
								opponent = element.getFirstElementChild().getFirstElementChild().asText();
							}
							l++;
						}
					}
					
					boolean alreadyInList = false;
					if (opponent != null) {
						OpponentChampion opp = new OpponentChampion(opponent);
						for (OpponentChampion op : opponentList) {
							if (op.getName().equals(opponent)) {
								alreadyInList = true;
								op.gamesPlayedAgainst++;
								if (victory) {
									op.winsAgainst++;
								}
								op.againstWinRate = op.winsAgainst/op.gamesPlayedAgainst * 100;
							}
						}
						if (!alreadyInList){
							opponentList.add(opp);
							opp.gamesPlayedAgainst++;
							if (victory) {
								opp.winsAgainst++;
								
							}
							opp.againstWinRate = opp.winsAgainst/opp.gamesPlayedAgainst * 100;
						}
						
					}
				}
			}
		}
		
		Collections.sort(opponentList);
		games = (int) gamesPlayed;
		winRate = wins/gamesPlayed * 100;
		System.out.println(name + " initialized");
	}
	
	public String getName() {
		return name;
	}
	
	public int gamesPlayed() {
		return games;
	}
	
	public double getWR() {
		return Math.round(winRate * 100.0) / 100.0;
	}
	

	
	public void printData() {
		System.out.println(getName() + ": ");
		System.out.println(gamesPlayed() + " games played with " + getWR() + "% win rate");
		System.out.println("Top opponents: ");
		int i = 0;
		for (OpponentChampion opponent : opponentList) {
			System.out.println(opponent.getName() + ": " + (int) opponent.gamesPlayedAgainst + " games played against with " + opponent.getWR() + "% win rate");
			i++;
			if (i == 10) {
				break;
			}
		}
		System.out.println("");
	}
	
	
	//Compare games played to sort champions
	public int compareTo(Champion c) {
		return c.games - games;
	}


}
