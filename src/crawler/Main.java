package crawler;

public class Main
{
	public static void main(String[] args)
	{
		String seedURL = "http://www.columbia.edu/~fdc/sample.html";
		String keyword = "the";
		int depth = 2;
		int maxPages = 50;

		WebAnalyser webAnalyser = new WebAnalyser(seedURL, keyword, depth, maxPages);

		try
		{
			webAnalyser.find(keyword);
			webAnalyser.printStatistics();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
