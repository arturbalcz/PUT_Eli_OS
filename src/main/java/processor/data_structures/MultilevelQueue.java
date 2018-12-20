package processor.data_structures;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class MultilevelQueue<T>
{
    private int levelNumber;
    private Vector<Queue<T>> queueVector = new Vector<>();

    public MultilevelQueue(int levelNumber)
    {
        this.levelNumber = levelNumber;

        for(int i = 0; i< levelNumber; i++)
        {
            Queue<T> Que = new PriorityQueue<>();
            queueVector.add(Que);
        }
    }

    /** Inserts specified element into queue on specified level */
    public void add(T element, int level)
    {
        queueVector.elementAt(level).offer(element);
    }

    /** Retrieves, but does not remove, the head of the highest level queue, or returns null if this queue is empty */
    public T peek()
    {
        for(int i = levelNumber -1; i>=0; i--)
        {
            if(!queueVector.elementAt(i).isEmpty())
            {
                return queueVector.elementAt(i).peek();
            }
        }

        return null;
    }

    /** Retrieves, but does not remove, the head of the queue on specified level, or returns null if this queue is empty */
    public T peek(int level)
    {
        if(!queueVector.elementAt(level).isEmpty())
        {
            return queueVector.elementAt(level).peek();
        }

        return null;
    }

    /** Retrieves and removes, the head of the highest level queue, or returns null if this queue is empty */
    public T poll()
    {
        for(int i = levelNumber -1; i>=0; i--)
        {
            if(!queueVector.elementAt(i).isEmpty())
            {
                return queueVector.elementAt(i).poll();
            }
        }

        return null;
    }

    /** Retrieves and removes, the head of the queue on specified level, or returns null if this queue is empty */
    public T poll(int level)
    {
        if(!queueVector.elementAt(level).isEmpty())
        {
            return queueVector.elementAt(level).poll();
        }

        return null;
    }

    /** Removes specified element on specified level or returns false if it does not exist */
    public boolean remove(T element, int level)
    {
        return queueVector.elementAt(level).remove(element);

    }

    /** Removes specified element or returns false if it does not exist */
    public boolean remove(T element)
    {
        for(int i = levelNumber -1; i>=0; i--)
        {
            if(queueVector.elementAt(i).remove(element))
            {
                return true;
            }
        }
        return false;

    }

    /** Returns true if specified level collection contains no elements */
    public boolean isEmpty(int level)
    {
        return queueVector.elementAt(level).isEmpty();
    }

    public int size(int level)
    {
        return queueVector.elementAt(level).size();
    }


}
