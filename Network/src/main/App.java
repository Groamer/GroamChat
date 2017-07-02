package main;

/*
Created by 'Groamer' also known as 'Tom Remeeus'.
Published by ComputerInfor.

Feel free to edit and use my source code.
Just make sure you credit me, and I'll be fine!
*/

public class App 
{
    public static void main(String args[])
    {
        new Server();
        
        Client tom = new Client("Tom");
        Client sanne = new Client("Sanne");
        Client marchell = new Client("Marchell");
        
        tom.sendMessage("ik ben Tom", "Sanne");
        sanne.sendMessage("Ik ben geen tom", "Tom");
        
        marchell.sendMessageAll("hoi");
    }
}