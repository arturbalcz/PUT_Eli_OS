package processor.data_structures;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

public class Multilevel_queue<T>
{
    private int level_number;
    private Vector<Queue<T>> Queue_Vector = new Vector<>();

    public Multilevel_queue(int level_number) //constructor
    {
        this.level_number = level_number;

        for(int i=0; i<level_number; i++)
        {
            Queue<T> Que = new PriorityQueue<>();
            Queue_Vector.add(Que);
        }
    }

    public void add(T element, int level) //Inserts specified element into queue on specified level
    {
        Queue_Vector.elementAt(level).offer(element);
    }

    public T peek() //Retrieves, but does not remove, the head of the highest level queue, or returns null if this queue is empty
    {
        for(int i=level_number-1; i>=0; i--)
        {
            if(Queue_Vector.elementAt(i).isEmpty()==false)
            {
                return Queue_Vector.elementAt(i).peek();
            }
        }

        return null;
    }

    public T peek(int level) //Retrieves, but does not remove, the head of the queue on specified level, or returns null if this queue is empty
    {
        if(Queue_Vector.elementAt(level).isEmpty()==false)
        {
            return Queue_Vector.elementAt(level).peek();
        }

        return null;
    }

    public T poll() //Retrieves and removes, the head of the highest level queue, or returns null if this queue is empty
    {
        for(int i=level_number-1; i>=0; i--)
        {
            if(Queue_Vector.elementAt(i).isEmpty()==false)
            {
                return Queue_Vector.elementAt(i).poll();
            }
        }

        return null;
    }

    public T poll(int level) //Retrieves and removes, the head of the queue on specified level, or returns null if this queue is empty
    {
        if(Queue_Vector.elementAt(level).isEmpty()==false)
        {
            return Queue_Vector.elementAt(level).poll();
        }

        return null;
    }

    public boolean remove(T element, int level) //Removes specified element on specified level or returns false if it does not exist
    {
        if(Queue_Vector.elementAt(level).remove(element))
        {
            return true;
        }
        else {return false;}

    }

    public boolean remove(T element) //Removes specified element or returns false if it does not exist
    {
        for(int i=level_number-1; i>=0; i--)
        {
            if(Queue_Vector.elementAt(i).remove(element))
            {
                return true;
            }
        }
        return false;

    }

    public boolean isEmpty(int level) //Returns true if specified level collection contains no elements
    {
        if(Queue_Vector.elementAt(level).isEmpty()) return true;
        else return false;
    }

    public int size(int level)
    {
        return Queue_Vector.elementAt(level).size();
    }


}
