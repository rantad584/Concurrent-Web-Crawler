package crawler;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebAnalyser
{
	private String seedURL;
	private String keyword;
	private int size;
	private int maxPages;
	private int maxQueueSize = 10000;
	private int TIME_OUT = 10000;
	private List<String> visited = new ArrayList<>();
	private Map<String, Integer> results = new HashMap<>();
	private Map<String, Integer> depth = new HashMap<>();

	public WebAnalyser(String seedURL, String keyword, int size, int maxPages)
	{
		this.seedURL = seedURL;
		this.keyword = keyword;
		this.size = size;
		this.maxPages = maxPages;
	}

	public void find(String keyword) throws InterruptedException
	{
		long startTime = System.currentTimeMillis();

		Queue<String> Q = new ArrayDeque<>(); // Queue to store urls to be visited
		ResourceQueue<WorkResult> workResultQueue = new ResourceQueue<>(maxQueueSize);

		ExecutorService executor = Executors.newFixedThreadPool(20); // Initialise a thread pool of 20 threads

		Q.add(seedURL); // Adds the seed page to the queue

		while(!Q.isEmpty())
		{
			String currentURL = Q.peek();
			Worker worker = new Worker(Q.poll(), keyword, workResultQueue);
			executor.execute(worker); // Executes the threads

			WorkResult workResult = workResultQueue.take();
			visited.add(workResult.URL);

			depth.putIfAbsent(seedURL, 0);

			if(depth.get(currentURL) > size
					|| visited.size() > maxPages
					|| (System.currentTimeMillis() - startTime) >= TIME_OUT)
			{
				break;
			}

			results.put(workResult.URL, workResult.count); // stores the current URL and number of occurrences

			for(String link : workResult.queue)
			{
				if(depth.containsKey(link))
				{
					continue;
				}
				if(!visited.contains(link))
				{
					Q.add(link);
					depth.put(link, depth.get(currentURL) + 1);
				}
			}
		}

		executor.shutdown(); // Initiates shutdown of threads

		try
		{
			long waitTime = Math.max(0, TIME_OUT - (System.currentTimeMillis() - startTime));
			if(executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS))
			{
				System.out.println("Threads did not finish in " + waitTime + " milliseconds");
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void printStatistics()
	{
		int count = 0;
		int total = 0;

		System.out.println("\nWord: '" + keyword + "' appears");

		for(Map.Entry<String, Integer> link : results.entrySet()) // prints out the number of keywords for each link
		{
			System.out.println(link.getValue() + " time(s) on " + link.getKey());
			count++;
			total += link.getValue();
		}

		System.out.println("Total: " + count + " page(s)");
		System.out.println("'" + keyword + "' appears " + total + " time(s)");
	}
}
