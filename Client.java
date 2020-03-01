import java.net.*;
import java.io.*;
import java.util.*;

public class Client implements Runnable{
  private Socket socket = null;
  private Thread thread = null;
  private BufferedReader console = null;
  private PrintWriter streamEcriture = null;
  private ClientThread client = null;

   public Client(String serveur_nom, int serveur_port){
     System.out.println("Connexion en cours. Veuillez patienter ...");
      try{
         socket = new Socket(serveur_nom, serveur_port);
         System.out.println("Connecté: " + socket);
         start();
      }
      catch(UnknownHostException uhe){
        System.out.println("Hote incoonnu " + uhe.getMessage());
       }
      catch(IOException ioe){
        System.out.println(" exception: " + ioe.getMessage());
      }
   }
   public void run(){
     while (thread != null){
        try{
           streamEcriture.println(console.readLine());
           streamEcriture.flush();
         }

         catch(IOException ioe){
           System.out.println("erreur envoi" + ioe.getMessage());
           stop();
         }
      }
   }

   public void gestion(String msg){
     if (msg.equals("/quit")){
        System.out.println("Au revoir , appuyer ENTREE pour quitter");
         stop();
      }

      else
         System.out.println(msg);
   }

   public void start() throws IOException{
     console   = new BufferedReader(new InputStreamReader(System.in));
      streamEcriture = new PrintWriter(socket.getOutputStream(),true);
      if (thread == null){
        client = new ClientThread(this, socket);
         thread = new Thread(this);
         thread.start();
      }
   }


   public void stop(){
     if (thread != null){
        thread.stop();
         thread = null;
      }

      try{
         if (console != null)  console.close();
         if (streamEcriture != null)  streamEcriture.close();
         if (socket != null)  socket.close();
      }

      catch(IOException ioe){
      System.out.println("Erreur fermeture ..."); }
      client.ferme();
      client.stop();
   }


   public static void main(String args[]){
     Client client = null;
      if (args.length != 2)
         System.out.println("Usage: java Client host port");
      else
         client = new Client(args[0], Integer.parseInt(args[1]));
   }
}
