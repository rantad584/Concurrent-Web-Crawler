package crawler;

import java.util.ArrayDeque;
import java.util.Queue;

public class WorkResult
{
    public String URL;
    public int count;
    public Queue<String> queue = new ArrayDeque<>();
}
