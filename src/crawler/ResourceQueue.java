package crawler;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceQueue<T> // implementation of a LinkedBlockingQueue
{
    private ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private int maxSize;

    public ResourceQueue(int maxSize)
    {
        this.maxSize = maxSize;
    }

    public synchronized void put(T resource) throws InterruptedException
    {
        while(queue.size() >= maxSize)
        {
            wait();
        }

        queue.add(resource);
        if(queue.size() == 1)
        {
            notifyAll(); // wake up any blocked threads in take
        }
    }

    public synchronized T take()
    {
        while(queue.isEmpty())
        {
            try
            {
                wait();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        T resource = queue.poll();
        if(queue.size() == maxSize - 1)
        {
            notifyAll(); // wake up any blocked threads in put
        }

        return resource;
    }
}
