package scraper;


import java.io.IOException;

import java.util.Scanner;


//Project by Muhannad Alsenan
//Started 5/30/2020
//Finished 6/8/2020
public class Driver{
	
	public static void main(String [ ] args) throws IOException, InterruptedException {
		run();
	}
	
	
	private static void run() throws InterruptedException, IOException {
		try {
			Scanner input = new Scanner(System.in);
			System.out.println("Enter your summoner name: ");
			String ign = input.nextLine();

			final long startTime = System.currentTimeMillis();
			ChampionList myList = new ChampionList(ign);
			myList.printData();
			final long endTime = System.currentTimeMillis();
			System.out.println("Total execution time: " + (endTime - startTime)/1000 + " seconds");
		}
		catch (NullPointerException e) {
			System.out.println("That summoner does not exist. Please try again.");
			run();
		}

	}
	
}
