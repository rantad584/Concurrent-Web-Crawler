package crawler;

public class Worker implements Runnable
{
    private String URL;
    private String keyword;
    private ResourceQueue<WorkResult> workResultQueue;

    public Worker(String URL, String keyword, ResourceQueue<WorkResult> workResultQueue)
    {
        this.URL = URL;
        this.keyword = keyword;
        this.workResultQueue = workResultQueue;
    }

    public void run()
    {
        String content = Helper.getContentFromURL(URL);

        WorkResult workResult = new WorkResult();
        workResult.URL = URL;

        workResult.count = Helper.countNumberOfOccurrences(keyword, content);

        // Put links into result queue
        workResult.queue.addAll(Helper.getHyperlinksFromContent(URL, content));

        // Put work result into queue
        try
        {
            workResultQueue.put(workResult);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
