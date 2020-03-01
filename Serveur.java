import java.net.*;
import java.io.*;
import java.util.*;


public class Serveur implements Runnable
{  private ServeurThread clients[] = new ServeurThread[50];
   private ServerSocket serveur = null;
   private Thread  thread = null;
   private int nbrClient = 0;


  public static final String ANSI_RESET = "\u001B[0m";
  public static final String BLACK_BOLD = "\033[1;30m";
  public static final String RED_BOLD = "\033[1;31m";
  public static final String GREEN_BOLD = "\033[1;32m";
  public static final String YELLOW_BOLD = "\033[1;33m";
  public static final String BLUE_BOLD = "\033[1;34m";
  public static final String PURPLE_BOLD = "\033[1;35m";
  public static final String CYAN_BOLD = "\033[1;36m";
  public static final String WHITE_BOLD = "\033[1;37m";

   public Serveur(int port){
     try{
        System.out.println("Ouverture du serveur au port " + port + ",Veuillez patienter ...");
         serveur = new ServerSocket(port);
         System.out.println("Serveur ouvert: " + serveur);
         start();
       }

    catch(IOException ioe){
        System.out.println("Echec  bind  port " + port + ": " + ioe.getMessage());
       }
   }
   public void run(){
     while (thread != null){
        try
         {  System.out.println("En attente d'un client ...");
            ajouteThread(serveur.accept());
         }
         catch(IOException ioe){
            System.out.println("Serveur erreur accept: " + ioe); stop();
          }
      }
   }
   public void start(){

     if (thread == null){
       thread = new Thread(this);
        thread.start();
     }
   }

   public void stop(){
     if (thread != null){
        thread.stop();
         thread = null;
      }
   }

   private int trouveClient(int ID){
     for (int i = 0; i < nbrClient; i++){
         if (clients[i].getID() == ID)
            return i;
      }
      return -1;
   }

   private String renvoie_id(){
     String tous_les_id =  RED_BOLD + "[Système] Les clients connectés sont" + ANSI_RESET;
     for(int i = 0;i< nbrClient;++i){
       tous_les_id = tous_les_id + " " + YELLOW_BOLD + clients[i].getID() + ANSI_RESET;
     }
     return tous_les_id;
   }

   private String renvoie_pseudo(){
     String tous_les_id = RED_BOLD +  "[Système] Les clients connectés sont"  + ANSI_RESET ;
     for(int i = 0;i< nbrClient;++i){
       tous_les_id = tous_les_id + " " + YELLOW_BOLD + clients[i].getPseudo() + ANSI_RESET;
     }
     return tous_les_id;
   }


   private void message_sauf_un(String pseudo,int ID){
     for(int i = 0 ;i< nbrClient;++i){
       if(clients[i].getID() != ID){
       clients[i].envoie( RED_BOLD +"[Système] "+ YELLOW_BOLD +  pseudo + RED_BOLD + " a quitté le chat" + ANSI_RESET );
     }
     }
   }

   private boolean pseudo_acceptable(String pseudo){
     for(int i = 0;i < nbrClient;++i){
       if(clients[i].getPseudo().equals(pseudo)){
         return false;
       }
     }
     return true;
   }

   public boolean pseudo_validator(String pseudo){
     int longueur = pseudo.length();
     if(longueur < 2 || longueur > 12){
       return false;
     }

  for(int i = 0;i != longueur;++i){
    char c = pseudo.charAt(i);
    if ((c != 45) && !(c > 47 && c < 58) && !(c > 64 && c < 91) && (c != 95) && !(c > 96 && c < 123)){
      return false;
    }
  }
  return true;
}

public static int nbrMots(String s){
    if (s == null)
       return 0;
    return s.trim().split("\\s+").length;
}

   public synchronized void gestion(int ID,String pseudo,String input){
     if(input.equals("/quit")){
       clients[trouveClient(ID)].envoie("/quit");
       message_sauf_un(pseudo,ID);
       supprime(ID);
    }

    else if(input.equals("/list")){
        clients[trouveClient(ID)].envoie(RED_BOLD + "[Système] Nombre de personnes dans le chat: "  + YELLOW_BOLD + nbrClient + ANSI_RESET);
        clients[trouveClient(ID)].envoie(renvoie_id());
        clients[trouveClient(ID)].envoie(renvoie_pseudo());
        return;
      }

      if(nbrMots(input) >= 3 && (input.substring(0,4)).equals("msg ")){

        String[] arr = input.split(" ");
        List<String> itemlist = new ArrayList<String>(Arrays.asList(arr));
        String p_message = "";
        for (int i = 0;i < nbrClient;++i){
          if(String.valueOf(itemlist.get(1)).equals(clients[i].getPseudo())){
            for(int j = 2;j < itemlist.size();++j){
              p_message = p_message + String.valueOf(itemlist.get(j)) + " ";
            }

            clients[i].envoie( PURPLE_BOLD + "Message privé de "  + YELLOW_BOLD + pseudo +  PURPLE_BOLD + ": " + p_message + ANSI_RESET);
            return;
          }

        }
        clients[trouveClient(ID)].envoie( RED_BOLD + "[Système] Ce destinataire n'existe pas" + ANSI_RESET);
        }


      else
         for (int i = 0; i < nbrClient; i++){
            if(clients[i].getID() != ID){
             clients[i].envoie(YELLOW_BOLD + pseudo + ": " + input + ANSI_RESET);
            }
            else{
              clients[trouveClient(ID)].envoie(CYAN_BOLD + pseudo + ": " + input + ANSI_RESET);
            }
           }
   }
   public synchronized void supprime(int ID){
      int pos = trouveClient(ID);
      if (pos >= 0){
        ServeurThread term = clients[pos];
         if (pos < nbrClient-1)
            for (int i = pos+1; i < nbrClient; i++)
               clients[i-1] = clients[i];
         nbrClient--;
         try{
           term.ferme();
         }
         catch(IOException ioe){
           System.out.println("Erreur fermeture thread: " + ioe);
         }

         term.stop();
        }
   }


   private void ajouteThread(Socket socket){



     if (nbrClient < clients.length)
      {  System.out.println("Client accepté: " + socket);
         clients[nbrClient] = new ServeurThread(this, socket);

         try
         {  clients[nbrClient].ouvre();
            clients[nbrClient].start();
            clients[nbrClient].envoie(RED_BOLD + "[Système] Bienvenue dans le chat !" + ANSI_RESET);

            String tmp = "";
            clients[nbrClient].envoie(RED_BOLD + "[Système] Identifiez vous avec la commande /pseudo" + ANSI_RESET);
            tmp =  clients[nbrClient].streamLecture.readLine();
              clients[nbrClient].streamLecture.mark(100);

                while(true){
                  clients[nbrClient].envoie(RED_BOLD +"[Système] Veuillez rentrer un pseudo"  + ANSI_RESET);

                  clients[nbrClient].streamLecture.reset();
                  clients[nbrClient].pseudo = clients[nbrClient].streamLecture.readLine();
                  if((pseudo_validator(clients[nbrClient].pseudo) && pseudo_acceptable(clients[nbrClient].pseudo)) == true){
                  break;
                }
                  clients[nbrClient].envoie(RED_BOLD +"[Système] Ce pseudo est invalide ou deja pris" + ANSI_RESET);
                  clients[nbrClient].streamLecture.mark(100);
              }

            clients[nbrClient].envoie(RED_BOLD +"[Système] Votre pseudo est donc: "  + CYAN_BOLD + clients[nbrClient].pseudo + ANSI_RESET);
            for(int i = 0;i < nbrClient;++i){
              clients[i].envoie(RED_BOLD + "[Système] " + YELLOW_BOLD + clients[nbrClient].getPseudo()+ RED_BOLD + " a rejoint le chat !" + ANSI_RESET);

            }
            nbrClient++;

            }

         catch(IOException ioe)
         {  System.out.println("Erreur ouverture thread: " + ioe); } }
      else
         System.out.println("Client refusé: maximum " + clients.length + " atteint.");
   }
   public static void main(String args[]) {
     Serveur serveur = null;
     if (args.length != 1)
        System.out.println("Usage: java Serveur port");
     else
        serveur = new Serveur(Integer.parseInt(args[0]));

    }
}
