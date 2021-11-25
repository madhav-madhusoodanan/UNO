/* 
  1. Threading
  2. play function
  3. main function 

*/
/* imports */
import java.util.*;
import java.io.*;
import java.lang.*;

class InvalidCardException extends Exception{}  

enum Color {
    RED,
    BLUE,
    BLACK,
    YELLOW,
    GREEN,
}

enum Power {
    DRAW4CARDS,
    DRAW2CARDS,
    SKIP,
    REVERSE,
    WILDCARD,
    DUMMY
}

/* Utlilty class */
/* make a Utility object and use it */
class Utility {
    /* constants */
 final int PLAYER_HAND_SIZE = 7;

    /* members */
    public static Draw draw;
    public static Discard discard;
    public static ArrayList<Player> players;
    public static boolean direction;
    public static Scanner sc;

    /* functions */
    Utility(){
        /* true for clockwise or going front */
        /* false for anticlockwise or going back */
        direction = true;
    }
    public static void makePlayers(){
        /* input part */
        int numberOfPlayers = Utility.sc.nextInt();

        /* making the player part */
        Utility.players = new ArrayList<Player>();
        for(int i = 0; i < numberOfPlayers; i++){
            Player player = new Player("" + i);
            Utility.players.add(player);
        }
    }
    public static ArrayList<Cards> makeDeck(){
        ArrayList<Cards> deck = new ArrayList<Cards>();
        Cards deckCard;

        for(int i = 0; i < 108; i++){
            /* randomly choose a card */
            if(Utility.flipACoin()) deckCard = new SpecialCards();
            else deckCard = new NormalCards();
            deck.add(deckCard);
        }
        return deck;
    }
    public static void start(ArrayList<Cards> deck, Draw draw, Discard discard, ArrayList<Player> players){
        /* INITIALLY */
        /* deck will be full
           draw, discard is empty */
        /* FINALLY */
        /* deck is popped out, and the remaining is assigned to draw */
        for(int i = 0; i < players.size(); i++){
            /* assign 7 cards */
            for(int j = 0; j < 7; j++){
                Cards card = deck.remove(deck.size() - 1);
                players.get(i).playerHand.add(card);
            }
        }
        discard.cards.add(deck.remove(deck.size() - 1));
        draw.cards = deck;
    }
    public static boolean flipACoin(){
        return (Math.random() > 0.5);
    }
    public static boolean checkPlayers(ArrayList<Player> players){

        /* returns true if no players have won
            else returns false */

        for(int i = 0; i < players.size(); i++){
            if(players.get(i).playerHand.size() == 0) {
                System.out.println(players.get(i).getPlayerId() + " HAS WON THE GAME!" );
                return false;
            }
        }
        return true;
    }
    public static Player findWinner(ArrayList<Player> players){
        Player player = new Player();
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).playerHand.size() == 0) return players.get(i);
        }
        return player;
    }
    public static void toggleDirection(){
        direction = !direction;
    }
}

class Discard {
    /* members */
    public ArrayList<Cards> cards;
    Discard(){
        cards = new ArrayList<Cards>();
    }
}



class Draw {
    public ArrayList<Cards> cards; 
    Draw(){
        cards = new ArrayList<Cards>();
    }
}

/* Abstract classes */

/* 
 Thread class ->
 1. start()
 2. run()

  */
class Player extends Thread {
    String playerId;
    ArrayList<Cards> playerHand;
    boolean hasStarted; /* to know whether it has started */

    /* this is needed because run function doesnt return amd doesnt accept arguments */
    ArrayList<Cards> cardToPlay;

    /* functions */
    Player(){
        playerId = "random";
        playerHand = new ArrayList<Cards>();
    }

    Player(String id){
        playerId = id;
        playerHand = new ArrayList<Cards>();
    }
    public static boolean isDefaultPlayer(Player p){
        return p.getPlayerId() != "random";
    }
    public String getPlayerId(){
        return this.playerId;
    }
    Cards recogniseCard(String input) throws InvalidCardException {
        for(int i = 0; i < playerHand.size(); i++){
            if (playerHand.get(i).toString().trim().equals(input)){
                return playerHand.get(i);
            }
        }
        System.out.println("No such card present");
        throw new InvalidCardException();
    }

    public synchronized ArrayList<Cards> play(ArrayList<Cards> draw, Cards discard){
        boolean repeat = false;
        ArrayList<Cards> played = new ArrayList<Cards>();
        do{
            try {
                    System.out.println("PlayerId:" + playerId);
                    for(Cards card:playerHand){
                        System.out.print(card);
                    }

                    System.out.println("\n\nSelect your card: ");
                    String input = Utility.sc.next().trim();

                    Cards card = this.recogniseCard(input);
                    /* remove that card from playerHand */
                    if(card.getPower() == Power.WILDCARD){
                        /* return 2 cards */
                        played.add(card);
                        played.add(this.play(draw, card).get(0));

                    } else {
                        played.add(card);
                    }
                    repeat = false;
            } catch (InvalidCardException e) { 
                repeat = true;
            } finally {
            }
        } while(repeat);
        return played;
    }

    @Override
    public void run(){
        /* might be error, adjust the static thingy */
        Cards lastCard = Utility.discard.cards.get(Utility.discard.cards.size() - 1);
        if(lastCard.getPower() == Power.SKIP){
            this.cardToPlay = null;
            return;
        }
        this.cardToPlay = this.play(Utility.draw.cards, lastCard);
        Utility.discard.cards.addAll(this.cardToPlay);
    }

    public ArrayList<Cards> getCardToPlay(){
        return this.cardToPlay;
    }
       
    public int playerHandSize() {
        return playerHand.size();
    }
}

interface Cards {
    public Color getColor();
    public Power getPower();
    public int getNumber();
    public String toString();
}

/* Special case classes */
class SpecialCards implements Cards{
    Power power;
    Color color;
    /* functions */
    SpecialCards(){
        /* randomly making power and number
            it is used for making that deck function
        */

        int powerRandom = (int) Math.floor(Math.random() * 5);
        int colorRandom = (int) Math.floor(Math.random() * 4);

        switch(powerRandom){
            case 0:
                this.power = Power.DRAW2CARDS;
                break;

            case 1:
                this.power = Power.DRAW4CARDS;
                break;

            case 2:
                this.power = Power.SKIP;
                break;

            case 3:
                this.power = Power.WILDCARD;
                this.color = Color.BLACK;
                break;

            case 4:
                this.power = Power.REVERSE;
                break;
        }

        switch(colorRandom){
            case 0:
                this.color = Color.RED;
                break;

            case 1:
                this.color = Color.GREEN;
                break;

            case 2:
                this.color = Color.BLUE;
                break;

            case 3:
                this.color = Color.YELLOW;
                break;

        }
    }
    @Override
    public Power getPower(){
        return power;
    }

    @Override
    public Color getColor(){
        return color;
    }
    
    @Override
    public int getNumber(){
        /* returning a number thaat doesnt exist, to signify that a special card doesnt have number */
        return 0;
    }


    @Override
    public String toString(){
        String card = "";

        switch(this.power){
            case DRAW4CARDS:
                card = "DRAW4CARDS ";
                break;

            case DRAW2CARDS:
                card = this.color + "_DRAW2CARDS ";
                break;
            
            case SKIP:
                card = this.color + "_SKIP ";
                break;

            case REVERSE:
                card = this.color + "_REVERSE ";
                break;
            
            case WILDCARD:
                card = "WILDCARD ";
                break;
                
     }
        return card;
    }
}


class NormalCards implements Cards{
    int number;
    Color color;

   /* functions */
   NormalCards(){
        int powerRandom = (int) Math.round(Math.floor(Math.random() * 5));
        int colorRandom = (int) Math.round(Math.floor(Math.random() * 4));

        this.number = (int) Math.round(Math.floor(Math.random() * 10));


        switch(colorRandom){
            case 0 :
                this.color = Color.RED;
                break;

            case 1 :
                this.color = Color.GREEN;
                break;

            case 2 :
                this.color =Color.BLUE;
                break;

            case 3 :
                this.color = Color.YELLOW;
                break;

        }
    }
    @Override
    public int getNumber(){
        return number;
    }

    @Override
    public Power getPower(){
        /* putting dummy just to fulfill this dummy function */
        return Power.DUMMY;
    }

    @Override
    public Color getColor(){
        return color;
    }

    @Override
    public String toString(){
        return this.color + "_" + this.number + " ";
    }
}

class Submission {
    public static void main(String[] args) {

        /* make draw and discard */
        Utility.discard = new Discard();
        Utility.draw = new Draw();
        Utility.players = new ArrayList<Player>();
        Utility.sc = new Scanner(System.in);
        ArrayList<Cards> deck = Utility.makeDeck();
        int i = 0;

        /* fill the players, maybe by user input */
        Utility.makePlayers();
        System.out.println("players -> " + Utility.players.size());

        /* distribute cards */
        Utility.start(deck, Utility.draw, Utility.discard, Utility.players);

        /* start the game */
        try{
            do{
                Utility.players.get(i).start();
                Utility.players.get(i).join();
                System.out.println("-------------------------------------------------------------------------------");
                if(Utility.direction) i += 1;
                else i -= 1;

                /* the story of the line below:
                
                    1. for i += 1 case, you are taking modulo anyway, so doesnt matter
                    2. for i -= 1 case, this line makes i positive
                    
                */
                i += Utility.players.size();
                i = i % Utility.players.size();
                /* the player plays the card to the discard */
            } while (Utility.checkPlayers(Utility.players));

        } catch (InterruptedException e){
            System.out.println("Player " + (i + 1) + " couldnt play and had to abort the game");
        }
        finally{
            Utility.sc.close();
        }
    }
}
