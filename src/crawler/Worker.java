package crawler;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable
{
    String URL;
    String keyword;
    LinkedBlockingQueue<WorkResult> workResultQueue;

    public Worker(String URL, String keyword, LinkedBlockingQueue<WorkResult> workResultQueue)
    {
        this.URL = URL;
        this.keyword = keyword;
        this.workResultQueue = workResultQueue;
    }

    static class WorkResult
    {
        public String URL;
        public int count;
        public Queue<String> queue = new ArrayDeque<>();
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
