package scraper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ChampionList {
	
	private TreeSet<Champion> list;
	private final String[] champArray = {"Aatrox", "Ahri", "Akali", "Alistar", "Amumu", "Anivia", "Annie", "Aphelios", "Ashe", "Aurelion Sol", "Azir", "Bard", "Blitzcrank", "Brand", "Braum", "Caitlyn", "Camille", "Cassiopeia", "Cho'Gath", "Corki", "Darius", "Diana", "Dr. Mundo", "Draven", "Ekko", "Elise", "Evelynn", "Ezreal", "Fiddlesticks", "Fiora", "Fizz", "Galio", "Gangplank", "Garen", "Gnar", "Gragas", "Graves", "Hecarim", "Heimerdinger", "Illaoi", "Irelia", "Ivern", "Janna", "Jarvan IV", "Jax", "Jayce", "Jhin", "Jinx", "Kai'Sa", "Kalista", "Karma", "Karthus", "Kassadin", "Katarina", "Kayle", "Kayn", "Kennen", "Kha'Zix", "Kindred", "Kled", "Kog'Maw", "LeBlanc", "Lee Sin", "Leona", "Lissandra", "Lucian", "Lulu", "Lux", "Malphite", "Malzahar", "Maokai", "Master Yi", "Miss Fortune", "Mordekaiser", "Morgana", "Nami", "Nasus", "Nautilus", "Neeko", "Nidalee", "Nunu & Willump", "Olaf", "Orianna", "Ornn", "Pantheon", "Poppy", "Pyke", "Qiyana", "Quinn", "Rakan", "Rammus", "Rek'Sai", "Renekton", "Rengar", "Riven", "Rumble", "Ryze", "Sejuani", "Senna", "Sett", "Shaco", "Shen", "Shyvana", "Singed", "Sion", "Sivir", "Skarner", "Sona", "Soraka", "Swain", "Sylas", "Syndra", "Tahm Kench", "Taliyah", "Talon", "Taric", "Teemo", "Thresh", "Tristana", "Trundle", "Tryndamere", "Twisted Fate", "Twitch", "Udyr", "Urgot", "Varus", "Vayne", "Veigar", "Vel'Koz", "Vi", "Viktor", "Vladimir", "Volibear", "Warwick", "Wukong", "Xayah", "Xerath", "Xin Zhao", "Yasuo", "Yorick", "Yuumi", "Zac", "Zed", "Ziggs", "Zilean", "Zoe", "Zyra"};
	private final List<String> champList = Arrays.asList(champArray);
	
	public ChampionList() {
		list = new TreeSet<Champion>();
	}
	
	public ChampionList(String summoner) throws InterruptedException, IOException {
		list = new TreeSet<Champion>();
		HtmlPage gg = updateAndLoadPage(summoner);
		//fill up champ list with initialized champions
		for (String champ : champList) {
			list.add(new Champion(champ, gg));
		}
		System.out.println("Champs:");
	}
	
	public void printData() {
		int i = 0;
		for (Champion champ : list) {
			if (champ.gamesPlayed() >= 1) {
				champ.printData();
			}			
			i++;
			if (i == 10) {
				break;
			}
		}
	}
	
	//method to load the whole gg page
	private HtmlPage updateAndLoadPage(String summoner) throws InterruptedException, IOException {
		//first few lines are to ignore some dumb warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		
		//get browser (FireFox)
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		
		//get page for specified summoner
		String gg = "https://na.op.gg/summoner/userName=" + summoner;
		HtmlPage summonerPage = webClient.getPage(gg);
		
		//update op.gg page before scraping
		HtmlButton update = summonerPage.getFirstByXPath("//button[@class='Button SemiRound Blue']");
		String beforeUpdate = summonerPage.asXml();
		webClient.waitForBackgroundJavaScript(2000);
		summonerPage = update.click();
		String afterUpdate = summonerPage.asXml();
		for (int i = 0; i < 50; i++) {
			if (!(beforeUpdate.equals(afterUpdate))) {
				System.out.println("Page Updated");
				break;
			}
			synchronized(summonerPage) {
				summonerPage.wait();
			}
		}
		
		//clicks "Load More" until it can't anymore
		boolean loadMore = true;
		int j = 2;
		try {
			while(loadMore) {
				try {
					//webClient.waitForBackgroundJavaScript(1500);
					HtmlAnchor more = summonerPage.getAnchorByText("Show More");
					String beforeLoad = summonerPage.asXml();
					webClient.waitForBackgroundJavaScript(2750);
					summonerPage = more.click();
					String afterLoad = summonerPage.asXml();
					for (int i = 0; i < 50; i++) {
						if (!(beforeLoad.equals(afterLoad))) {
							System.out.println("Page " + j + " loaded");
							j++;
							break;
						}
						synchronized(summonerPage) {
							summonerPage.wait();
						}
					}
				}
				catch(ElementNotFoundException e) {
					loadMore = false;
				}
			}
		}
		catch (NoSuchElementException e) {
			System.out.println("Oops, something went wrong... Let's try that again");
			updateAndLoadPage(summoner);
		}
		return summonerPage;
	}
	
}
