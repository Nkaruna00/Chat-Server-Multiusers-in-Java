import java.net.*;
import java.io.*;
import java.util.*;

public class ServeurThread extends Thread
{  private Serveur serveur = null;
   private Socket socket = null;
   private int ID = -1;
   public String pseudo = "";
   public BufferedReader streamLecture = null;
   private PrintWriter streamEcriture = null;

   public ServeurThread(Serveur _serveur, Socket _socket){
     super();
      serveur = _serveur;
      socket = _socket;
      ID     = socket.getPort();

   }

   public void envoie(String msg){
     streamEcriture.println(msg);
     streamEcriture.flush();

   }


   public int getID(){
     return ID;
   }

   public String getPseudo(){
     return pseudo;
   }


   public void run(){
      System.out.println("Server Thread " + ID + " actif.");
      while (true){
        try{
           serveur.gestion(ID,pseudo,streamLecture.readLine());
         }
         catch(IOException ioe){
            System.out.println(ID + " Erreur lecture: " + ioe.getMessage());
            serveur.supprime(ID);
            stop();
         }
      }
   }
   public void ouvre() throws IOException{
      streamLecture = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      streamEcriture = new PrintWriter(socket.getOutputStream(),true);
   }

   public void ferme() throws IOException{
      if (socket != null)  socket.close();
      if (streamLecture != null)  streamLecture.close();
      if (streamEcriture != null) streamEcriture.close();
   }
}
