import java.net.*;
import java.io.*;

public class ClientThread extends Thread{
  private Socket           socket   = null;
   private Client       client   = null;
   private BufferedReader  streamLecture = null;

   public ClientThread(Client _client, Socket _socket){
     client   = _client;
     socket   = _socket;
     ouvre();
     start();
   }


   public void ouvre(){
     try{
        streamLecture  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      }

      catch(IOException ioe){
        System.out.println("Erreur ouverture input stream: " + ioe);
        client.stop();
      }
   }

   public void ferme(){
     try{
        if (streamLecture != null) streamLecture.close();
      }

      catch(IOException ioe){
        System.out.println("Erreur fermeture input stream: " + ioe);
      }
   }

   public void run(){
     while (true){
        try{
           client.gestion(streamLecture.readLine());
         }

         catch(IOException ioe){
            System.out.println("erreur ecoute " + ioe.getMessage());
            client.stop();
         }
      }
   }
}
