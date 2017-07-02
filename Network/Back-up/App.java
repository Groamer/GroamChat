package main;

public class App 
{
    public static void main(String args[])
    {
        Server server = new Server();
        
        Client tom = new Client("Tom");
        Client mike = new Client("Mike");
        Client ivo = new Client("Ivo");
        Client steven = new Client("Steven");
        Client mickey = new Client("Mickey");
        
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while(true)
                    {
                        Thread.sleep(2000);
                        tom.sendMessage("sendmessageall", "Mike");
                    }
                }
                catch(Exception e)
                {
                   
                }  
            }
        }.start();
    }
}
