package synchronization;
import java.util.LinkedList;
import java.util.List;

public class Lock_function {
//initilize lock which is open
    private Boolean lock;
    private List<String> kolejka=new LinkedList<String>();
    //initialize queue FIFO;

    String name;
    Object fileName;
    //it can be usfull we will see(never enough variable :D )
    Lock_function(Object fileName)
    {
        this.fileName=fileName;
        this.lock=false;
    }

    void lock(String name)
    {
        if(lock==true)
        {
            System.out.println("Ktos juz uzywa ten plik. "+name+"musi czekac");

        }
        else{
        System.out.println("LOCK zostal zablokowany "+name);
        lock=true;
        name=name;
        //this function take the lock
        //procces is been stopped
    }
    }

    void unlock()
    {
        System.out.println(name+" Zakonczyl swoje zadanie");
        this.lock=false;
        if(kolejka.size()!=0)
        {
            signal();
            //Procces which is in quer will be start
        }
        //reclaim the lock
        //procces is been activate from the stopped
    }


 void waiting()
{


}

private void signal()
{
    if(!kolejka.isEmpty())
    {
      String proces=kolejka.get(0);
      //adding more

    }
    else {
        this.lock = false;
    }
}

}
