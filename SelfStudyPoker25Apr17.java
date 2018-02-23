/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selfstudypoker25apr17;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;


/**
 *
 * @author johngrace
 */
public class SelfStudyPoker25Apr17 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        int numberOfPlayers = 9, rounds,i;
        
        byte [] ranks = new byte[]{5,5,14,4,2};
        String [] suits = new String[]{"spade","heart","spade","heart","diamond"};
        byte [] ranks2 = new byte[]{5,5,12,4,2};
        String [] suits2 = new String[]{"spade","heart","spade","heart","diamond"};
        
        Scanner reader = new Scanner(System.in);
        Table table = new Table(numberOfPlayers);
        TableStatistics tableStatistics = new TableStatistics(numberOfPlayers);
        Table table2 = new Table(numberOfPlayers);
        TableStatistics tableStatistics2 = new TableStatistics(numberOfPlayers);
        
        table.deck.shuffleDeck();
        table.deck.stackDeck(ranks, suits);
        table2.deck.shuffleDeck();
        table2.deck.stackDeck(ranks2, suits2);
        
        for (rounds =0; rounds<10000; rounds++) {
            table.deck.shuffleTail(ranks.length);
            table2.deck.shuffleTail(ranks2.length);
            tableStatistics.setPlayerRanking(table.getPlayerHandClassifications(1));
            tableStatistics2.setPlayerRanking(table2.getPlayerHandClassifications(1));
        //    tableStatistics.getPlayerRanking().entrySet().stream().forEach(System.out::println);
          //  if (reader.nextInt()==1) System.exit(1);
            tableStatistics.updateTableStatistics();
            tableStatistics2.updateTableStatistics();
        }
        
        StaticFunctions.standardTableStatisticsPrint(tableStatistics, tableStatistics2, rounds, numberOfPlayers);
        
        for (i=0; i<ranks.length; i++) table.deck.getCard(i).printCard();
        System.out.println();
        
        
        for (i=0; i<ranks2.length; i++) table2.deck.getCard(i).printCard();
        System.out.println();
        tableStatistics.printTableStatsArray(tableStatistics.getHandsThatBeatP0Gross(),1);
        tableStatistics2.printTableStatsArray(tableStatistics2.getHandsThatBeatP0Gross(),1);
        
    }
    
}

class TableStatistics {
   
    LinkedHashMap<Integer,Integer> playerRanking;
    LinkedHashMap<Integer,Integer> handsThatBeatP0Histogram = new LinkedHashMap<>();
    
    int numberOfPlayers;
    int [] wins,ties,playedAndLost, handsThatBeatP0Gross;//histogram: index 3 is the # wins for player 3
    int [][] winsAndLossesByNoPlayersFolded;//histogram: for player 0, index [2][1] is the number of wins when 2 people folded.
    
    TableStatistics(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        wins = new int[numberOfPlayers];
        ties = new int[numberOfPlayers];
        playedAndLost = new int[numberOfPlayers];
        winsAndLossesByNoPlayersFolded = new int[numberOfPlayers][2];
        handsThatBeatP0Gross = new int[10];
    }
    
    void updateTableStatistics() {
        Scanner reader = new Scanner(System.in);
        
        //no tie
        if (playerRanking.entrySet().stream().skip(numberOfPlayers-2).map(Map.Entry::getValue).distinct().count()==2) {
            updateWinsHistogram();
            updateWinsByNoPlayersFolded(getNoPlayersFolded());
            updatePlayedAndLostHistogram();
        }
        
        //tie
        if (playerRanking.entrySet().stream().skip(numberOfPlayers-2).map(Map.Entry::getValue).distinct().count()==1) {
            updateTiesHistogram();   
            
        }
        
    }
    
    //the following function is specifically for player 0
    void updateWinsByNoPlayersFolded(int noPlayersFolded) {
        int winningPlayer;
        
        winningPlayer = playerRanking.entrySet().stream().skip(numberOfPlayers-1)
         .map(Map.Entry::getKey).collect(Collectors.toList()).get(0);
        if (winningPlayer==0) {
            winsAndLossesByNoPlayersFolded[noPlayersFolded][1]++;
            
        }else{//winningPlayer != 0 , ie player 0 lost
            winsAndLossesByNoPlayersFolded[noPlayersFolded][0]++;
            updateHandsThatBeatP0Gross(winningPlayer);

        }
    }
    
    void updateHandsThatBeatP0Gross(int winningPlayer) {
        handsThatBeatP0Gross[(int) Math.floor(playerRanking.get(winningPlayer)/1e8)]++;
    }
    
    void updateHandsThatBeatP0Histogram( int winningPlayer) {
        
        int handThatBeatP0 = playerRanking.get(winningPlayer);
        
        if (handsThatBeatP0Histogram.containsKey(handThatBeatP0)) {
            handsThatBeatP0Histogram.put(handThatBeatP0, 1 + handsThatBeatP0Histogram.get(handThatBeatP0));
        } else {
            handsThatBeatP0Histogram.put(handThatBeatP0,1);
        }
    }
    
    void updateTiesHistogram() {
        Scanner reader = new Scanner(System.in);
        
        
        playerRanking.entrySet().stream().skip(numberOfPlayers-2)
         .map(Map.Entry::getKey).forEach(player -> ties[player]++);
        
    }
    
    void updateWinsHistogram() {
        
             playerRanking.entrySet().stream().skip(numberOfPlayers-1)
              .map(Map.Entry::getKey).forEach( player -> wins[player]++);
    }
    
    void updatePlayedAndLostHistogram() {
        playerRanking.entrySet().stream().limit(numberOfPlayers-1).filter(me -> me.getValue()>100)
         .map(Map.Entry::getKey).forEach(player -> playedAndLost[player]++);
    }
    
    void setPlayerRanking(HashMap<Integer,Integer> inputPlayerRanking) {
        playerRanking = inputPlayerRanking.entrySet().stream().sorted(Map.Entry.comparingByValue())
         .collect(
          Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2) -> e1, LinkedHashMap::new)
         );
    }
    
    int [] getHandsThatBeatP0Gross () {
        return handsThatBeatP0Gross;
    }
    
    LinkedHashMap<Integer,Integer> getPlayerRanking() {
        return playerRanking;
    }
    int [] getWinsHistogram() {
        return wins;
    }
    int [] getTiesHistogram() {
        return ties;
    }
    int [] getPlayedAndLostHistogram() {
        return playedAndLost;
    }
    int [][] getWinsAndLossesByNoPlayersFolded() {
        return winsAndLossesByNoPlayersFolded;
    }
    
    int getNoPlayersFolded() {
        return (int) playerRanking.entrySet().stream().map(Map.Entry::getValue).filter(v -> v<1e8).count();
    }
    
    void printTableStatsArray( int [] arrayToPrint, int [] complementaryArray) {
        int i;
        for (i=0; i<arrayToPrint.length; i++) System.out.format("%d: %.2f%n" ,i , (float)arrayToPrint[i]/(arrayToPrint[i]+complementaryArray[i]));
            
    }
    
    void printTableStatsArray( int [] arrayToPrint, int denominator) {
        int i;
        for (i=0; i<arrayToPrint.length; i++) System.out.format("%d: %.2f%n", i, (float)arrayToPrint[i]/denominator);
            
    }
    
    void printTableStatsArray( int [][] matrixToDraw, int column, int [][] complementMatrixFromWhichToDraw, int columnComplement) {
        int i;
        for (i=0; i<matrixToDraw.length; i++) {
            if (matrixToDraw[i][column]+complementMatrixFromWhichToDraw[i][columnComplement] !=0){
             System.out.format("%5.2f" , (float) matrixToDraw[i][column]/(matrixToDraw[i][column]+complementMatrixFromWhichToDraw[i][columnComplement]));
            }else{
                System.out.print("  NaN");
            }
        }
        System.out.println();
    }
    
    void printTableStatsArray( int [][] matrixToDraw, int column, int denominator) {
        int i;
        for (i=0; i<matrixToDraw.length; i++) {
            System.out.format("%d: %.2f%n", i , (float)matrixToDraw[i][column]/denominator);
        }
    }
    
}

class StaticFunctions {
    
    static void standardTableStatisticsPrint( TableStatistics tableStatistics, TableStatistics tableStatistics2, int rounds, int numberOfPlayers) {
        int i;
        System.out.format("overall win, loss%n");
        
        System.out.format("table   %.2f,  %.2f%n",
         (float) tableStatistics.getWinsHistogram()[0]/rounds, (float) tableStatistics.getPlayedAndLostHistogram()[0]/rounds);
     
        System.out.format("table2  %.2f, %.2f%n",
         (float) tableStatistics2.getWinsHistogram()[0]/rounds, (float) tableStatistics2.getPlayedAndLostHistogram()[0]/rounds);
        
        System.out.format("%ncurrent bettors%n      ");
        for (i =0; i<numberOfPlayers; i++) System.out.format("%2d   ", numberOfPlayers-i);
        System.out.println();
        
        System.out.format("table ");
        tableStatistics.printTableStatsArray(tableStatistics.getWinsAndLossesByNoPlayersFolded(), 1,
         tableStatistics.getWinsAndLossesByNoPlayersFolded(), 0);
        
        System.out.format("table2");
        tableStatistics2.printTableStatsArray(tableStatistics2.getWinsAndLossesByNoPlayersFolded(), 1,
         tableStatistics2.getWinsAndLossesByNoPlayersFolded(), 0);
        
        System.out.format("%n");
    }
    
    static void comparisonOutput(int[][] matrixToDraw, int desiredColumnM1, int columnComplementM1,
     int[][] matrixToDraw2,int desiredColumnM2, int columnComplementM2) {
        int i;
        for (i=0; i<matrixToDraw.length; i++) {
            if (matrixToDraw[i][desiredColumnM1]+matrixToDraw[i][columnComplementM1] !=0)
             System.out.format("%d: %.2f   %.2f%n" , i ,
                     (float) matrixToDraw[i][desiredColumnM1]/(matrixToDraw[i][desiredColumnM1]+matrixToDraw[i][columnComplementM1]),
                     (float) matrixToDraw2[i][desiredColumnM2]/(matrixToDraw2[i][desiredColumnM2]+matrixToDraw2[i][columnComplementM2]));
        }
    }
    
    static boolean foldingTests(Table table, int foldingCode, int playerIterator) {
        if (foldingCode ==0 || playerIterator==0) return false; //means the player does not fold
        if (foldingCode==1) {
            if (pair(table,playerIterator))  return bothLessThanX(table, playerIterator,5);
            if (!pair(table,playerIterator)) {
                if(!suited(table,playerIterator)) {
                    return (bothLessThanX(table, playerIterator,13) && eitherLessThanX(table, playerIterator,11))
                    || (eitherLessThanX(table, playerIterator,10));
                }
                if (suited(table,playerIterator)) {
                    return (bothLessThanX(table, playerIterator,14) && eitherLessThanX(table, playerIterator,8))
                    || (bothLessThanX(table, playerIterator,12) && eitherLessThanX(table, playerIterator,10));
                }
            }
        }
        
        if (foldingCode==2) {
            if (pair(table,playerIterator))  return bothLessThanX(table, playerIterator,8);
            if (!pair(table,playerIterator)) {
                if(!suited(table,playerIterator)) {
                    return (bothLessThanX(table, playerIterator,14) && eitherLessThanX(table, playerIterator,13))
                    || (eitherLessThanX(table, playerIterator,12));
                }
                if (suited(table,playerIterator)) {
                    return (eitherLessThanX(table, playerIterator,10))
                    || (bothLessThanX(table, playerIterator,14) && eitherLessThanX(table, playerIterator,12));
                }
            }
        }
        
        return false;
    }
    
   
    
    static boolean suited(Table table,int playerIterator) {
        return table.getPlayersHoleCards(playerIterator).get(0).getSuit().equals(table.getPlayersHoleCards(playerIterator).get(1).getSuit());
    }
    
    static boolean pair(Table table, int playerIterator) {
        return table.getPlayersHoleCards(playerIterator).get(0).getRank()==table.getPlayersHoleCards(playerIterator).get(1).getRank();
    }
    
    static boolean bothLessThanX(Table table, int playerIterator, int X) {
        return table.getPlayersHoleCards(playerIterator).get(0).getRank() < X &&
                        table.getPlayersHoleCards(playerIterator).get(1).getRank() < X;
    }
    
    static boolean eitherLessThanX(Table table, int playerIterator, int X) {
        return (table.getPlayersHoleCards(playerIterator).get(0).getRank()<X || table.getPlayersHoleCards(playerIterator).get(1).getRank()<X);
    }
    
    static int distance(Table table, int playerIterator) {
        return Math.abs(table.getPlayersHoleCards(playerIterator).get(1).getRank() -table.getPlayersHoleCards(playerIterator).get(0).getRank())  ;
    }
    
}


class Table {
    int numberOfPlayers;
    int tableStatistics;
    
    Scanner reader = new Scanner(System.in);
    
    Deck deck = new Deck();
    
    Table (int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        
    }
    
    HashMap<Integer,Integer> getPlayerHandClassifications(int foldingCode) {
        int playerIterator;
        HashMap<Integer,Integer> tempHandClassificationStore = new HashMap<>(numberOfPlayers);
        
        SevenCardSet currentPlayersHand = new SevenCardSet((ArrayList<Card>) getPlayersHand(0));
        
        for (playerIterator=0; playerIterator<numberOfPlayers; playerIterator++){
        //for (playerIterator=numberOfPlayers-1; playerIterator>=0; playerIterator--){
            currentPlayersHand.changeCardSet(getPlayersHand(playerIterator));
            
            
            //players fold if cards satisfy the given condition. Folding is represented as giving a very
            //low hand classification, which is equal to the player's ID.
            
            if (StaticFunctions.foldingTests(this, foldingCode,playerIterator)) {
                tempHandClassificationStore.put(playerIterator,playerIterator); //fold the hand
            } else {
                tempHandClassificationStore.put(playerIterator,currentPlayersHand.findTheBest5CardSubHandClassification()); //play the hand
            }

        }
        return tempHandClassificationStore;
    }
    
    
    
    //how do I want to return a set of cards? three cards? 7 cards?
    ArrayList<Card> getFlop() {
        ArrayList<Card> flop = new ArrayList<>(3);
        flop.add(deck.getCard(2));
        flop.add(deck.getCard(3));
        flop.add(deck.getCard(4));
        return flop;
    }
    
    Card getTurn() {
        return deck.getCard(5);
    }
    
    Card getRiver() {
        return deck.getCard(6);
    }
    //playersPosition ranges from 0 to numberOfPlayers-1
    //the cards are in the following order: p0,board,p1,p2,...., burn,burn,burn, not used...
    ArrayList<Card> getPlayersHoleCards(int playersPosition) {
        ArrayList<Card> holeCards = new ArrayList<>(2);
        if (playersPosition==0){
            holeCards.add(deck.getCard(0));
            holeCards.add(deck.getCard(1));
        }
        if (playersPosition>0){
            holeCards.add(deck.getCard(7+(playersPosition-1)*2));
            holeCards.add(deck.getCard(1+7+(playersPosition-1)*2));
        }
        
        return holeCards;
    }
    
    ArrayList<Card> getPlayersHand(int playersPosition) {
        ArrayList<Card> playersHand =getPlayersHoleCards(playersPosition);
        playersHand.addAll(getFlop());
        playersHand.add(getTurn());
        playersHand.add(getRiver());
        return playersHand;
    }
    
    void printSetOfCards(ArrayList<Card> setOfCards) {
        for (Card card : setOfCards)
            System.out.print(card.getRank() + card.getSuit() + " ");
        System.out.println();
    }
    
    void printDeck() {
        deck.printDeck();
    }
    
}







class SevenCardSet extends CardSet {
    
    SevenCardSet(ArrayList<Card> inputCardSetStore) {
        super(inputCardSetStore);
    }
    
    int findTheBest5CardSubHandClassification() {
        int i,j,highestEvaluation=0, tempEvaluation=0;
        Scanner reader = new Scanner(System.in);
        
        ArrayList<Card> tempCardArray = cardSetStore.stream().limit(5).collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
        FiveCardSet tempFiveCardSet = new FiveCardSet(tempCardArray);
        
        for (i=0; i<6; i++) {
            for (j=i+1; j<7; j++) {
                tempCardArray = cardSetStore.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                tempCardArray.remove(j);
                tempCardArray.remove(i);
                tempFiveCardSet.changeCardSet(tempCardArray);
                tempEvaluation = tempFiveCardSet.getHandClassification();
             
                if ( tempEvaluation > highestEvaluation) highestEvaluation = tempEvaluation;
                
            }
        }
      
        return highestEvaluation;
    }
    
}



class FiveCardSet extends CardSet {
    
    
    FiveCardSet(ArrayList<Card> inputCardSetStore) {
        
        super(inputCardSetStore);
    }
    
    FiveCardSet() {
        
    }
    
    int getHandClassification() {
        //a lot of the following tests assume the card ranks are low to high
        if (isStraight() && isFlush()) return (int) (9e8+1e6*cardSetStore.get(0).getRank()); //the first card is the highest of the straight
        if (isQuads()) return (int) (8e8+ 1e6*cardSetStore.get(1).getRank()); //the second card will always be a part of the quads
        if (isFullHouse()) {
            if (cardSetStore.get(1).getRank() == cardSetStore.get(2).getRank())
                return (int) (7e8 + 1e6*cardSetStore.get(2).getRank() + 1e4*cardSetStore.get(3).getRank());
            return (int) (7e8 + 1e6*cardSetStore.get(2).getRank() + 1e4*cardSetStore.get(0).getRank());
        } //the third card will always be a part of the trips
        if (isFlush()) return (int) (6e8 + 1e6*cardSetStore.get(0).getRank()+
                1e4*cardSetStore.get(1).getRank()+1e2*cardSetStore.get(2).getRank() + cardSetStore.get(3).getRank()); //the first card will be the highest
        if (isStraight()) return (int) (5e8+ 1e6*cardSetStore.get(0).getRank());
        if (isTrips()) {
            if (cardSetStore.get(0).getRank() == cardSetStore.get(1).getRank())
                return (int) (4e8 + 1e6*cardSetStore.get(2).getRank()+1e4*cardSetStore.get(3).getRank()+1e2*cardSetStore.get(4).getRank());
            if (cardSetStore.get(1).getRank() == cardSetStore.get(2).getRank())
                return (int) (4e8 + 1e6*cardSetStore.get(2).getRank()+1e4*cardSetStore.get(0).getRank()+1e2*cardSetStore.get(4).getRank());
            return (int) (4e8 + 1e6*cardSetStore.get(2).getRank()+1e4*cardSetStore.get(0).getRank()+1e2*cardSetStore.get(1).getRank());
        }
        if (isTwoPair()) return (int) (3e8 + 1e6*cardSetStore.get(1).getRank() +1e4*cardSetStore.get(3).getRank()); 
            //the second card is guaranteed to be in the highest ranked pairs
            //the fourth card is guaranteed to be in the lowest ranked pairs
        if (isPair()) {
            if (cardSetStore.get(0).getRank()==cardSetStore.get(1).getRank()) 
                return (int) (2e8 + 1e6*cardSetStore.get(1).getRank()+1e4*cardSetStore.get(2).getRank()+1e2*cardSetStore.get(3).getRank());
            if (cardSetStore.get(1).getRank()==cardSetStore.get(2).getRank()) 
                return (int) (2e8 + 1e6*cardSetStore.get(2).getRank()+1e4*cardSetStore.get(0).getRank()+1e2*cardSetStore.get(3).getRank());
            if (cardSetStore.get(2).getRank()==cardSetStore.get(3).getRank()) 
                return (int) (2e8 + 1e6*cardSetStore.get(3).getRank()+1e4*cardSetStore.get(0).getRank()+1e2*cardSetStore.get(1).getRank());
            if (cardSetStore.get(3).getRank()==cardSetStore.get(4).getRank()) 
                return (int) (2e8 + 1e6*cardSetStore.get(4).getRank()+1e4*cardSetStore.get(0).getRank()+1e2*cardSetStore.get(1).getRank());
        }
        return (int) (1e8 + 1e6*cardSetStore.get(0).getRank()+1e4*cardSetStore.get(1).getRank()+1e2*cardSetStore.get(2).getRank());
    }
    
    boolean isQuads() {
        if (Arrays.binarySearch(rankHistogram,(byte) 4)   >0) return true;
        return false;
    }
    
    boolean isFullHouse() {
        if (Arrays.binarySearch(rankHistogram, (byte) 3) >0   && 
            Arrays.binarySearch(rankHistogram, (byte) 2) >0      ) return true;
        return false;
    }
    
    boolean isFlush() {
        if (Arrays.binarySearch(suitHistogram, (byte) 5) >0) return true;
        return false;
    }
    
    boolean isStraight() {
        
        if (cardSetStore.get(0).getRank()-cardSetStore.get(4).getRank() == 4 &&
                rankHistogram[12] ==1) return true;
        if (cardSetStore.get(0).getRank()==14 && cardSetStore.get(1).getRank()-cardSetStore.get(4).getRank() == 3 &&
                rankHistogram[12] ==1 && cardSetStore.get(1).getRank()==5) return true;
        
        return false;
    }
    
    boolean isTrips() {
        if (Arrays.binarySearch(rankHistogram, (byte) 3) >0   && 
            Arrays.binarySearch(rankHistogram, (byte) 2) <0      ) return true;
        return false;
    }
    
    boolean isTwoPair() {
        int locationOfKey=Arrays.binarySearch(rankHistogram,(byte)2);
       
        if (locationOfKey>0) {
            
            if ( Arrays.binarySearch(rankHistogram,0,locationOfKey,(byte)2)                      >0 ||
                 Arrays.binarySearch(rankHistogram,locationOfKey+1,rankHistogram.length,(byte)2) >0    ){
                
                
                return true;
            }
        }
        return false;
    }
    
    boolean isPair() {
        if (Arrays.binarySearch(rankHistogram,(byte)2) >0) return true;
        return false;
    }


}

class CardSet {
    ArrayList<Card> cardSetStore = new ArrayList<>();
    byte rankHistogram[];
    byte suitHistogram[];
    
    CardSet( ArrayList<Card> inputCardSetStore) {
        changeCardSet(inputCardSetStore);

    }
    
    CardSet() {
        
    }
    
    final void changeCardSet(ArrayList<Card> newCardSet) {
        cardSetStore = newCardSet.stream().collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
        sortByRank();
        makeHistogram();
        Arrays.sort(rankHistogram);
        Arrays.sort(suitHistogram);
    
    }
    
    
    
    final void makeHistogram() {
        int i,j;
        
        rankHistogram = new byte[13];
        suitHistogram = new byte[4];
        for (i=0; i<13; i++) rankHistogram[i]=0;
        for (i=0; i<4; i++) suitHistogram[i]=0;
        for (Card card : cardSetStore) rankHistogram[card.getRank()-2]+=1;
        for (Card card : cardSetStore) {
            switch (card.getSuit()) {
                    case "spade":
                        suitHistogram[0]+=1;
                        break;
                    case "heart":
                        suitHistogram[1]+=1;
                        break;
                    case "diamond":
                        suitHistogram[2]+=1;
                        break;
                    case "club":
                        suitHistogram[3]+=1;
                        break;
            }
                        
        }
        
        
    }
    
    final void sortByRank() {
        sortByRank(cardSetStore);
    }
    
    static final void sortByRank(ArrayList<Card> setOfCards) {
        int i,j;
        Card tempCard;
        
        for (i=0; i<setOfCards.size()-1; i++){
            for (j=i+1; j<setOfCards.size(); j++) {
                if ( setOfCards.get(j).getRank()>setOfCards.get(i).getRank()) {
                    tempCard=setOfCards.get(i);
                    setOfCards.set(i,setOfCards.get(j));
                    setOfCards.set(j, tempCard);
                }
            }
        }
    }
    
    int getSize() {
        return cardSetStore.size();
    }
    
    void printHistogram() {
        int i;
        for (i=0; i<13; i++) System.out.print(rankHistogram[i]);
        System.out.println();
        for (i=0; i<4; i++) System.out.print(suitHistogram[i]);
        System.out.println();
    }
    
    ArrayList<Card> getCardSetStore() {
        return cardSetStore;
    }
    
    void printCardSetStore() {
        for (Card card : cardSetStore)
            System.out.print(card.getRank() + card.getSuit() + " ");
        System.out.println();
    }
    
    
}


class Deck {
    
    ArrayList<Card> deckStore = new ArrayList<>(52);
    
    Deck() {
        byte i;
        
        for (i=2; i<15; i++) {
            
            deckStore.add(new Card(i,"spade"));
            deckStore.add(new Card(i,"heart"));
            deckStore.add(new Card(i,"diamond"));
            deckStore.add(new Card(i,"club"));
        }
                
    }
    
    Card getCard(int i) {
        return deckStore.get(i);
    }
    
    final void shuffleDeck() {
        Collections.shuffle(deckStore);
    }
    void shuffleTail(int indexShuffleStart) {
        int i;
        ArrayList<Card> tempArrayForStream;
        tempArrayForStream=deckStore.stream().skip(indexShuffleStart).collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
        Collections.shuffle(tempArrayForStream);
        for (i=indexShuffleStart; i<deckStore.size(); i++) deckStore.set(i,tempArrayForStream.get(i-indexShuffleStart));
    
    }
    
    
    void stackDeck(byte [] ranks, String [] suits) {
        int i,j;
        for (i=0; i<ranks.length; i++) {
            for (j=i+1; j<52; j++) {
                if (deckStore.get(j).getRank() == ranks[i] && deckStore.get(j).getSuit().equals(suits[i])) {
                    Collections.swap(deckStore,i,j);
                    break;
                }
            }
        }
    }
    
    
    void printDeck() {
        for (Card card : deckStore) {
            System.out.println(card.getRank() + card.getSuit());
        }
        
    }
    
}



class Card {
    byte rank;
    String suit;
    
    Card(byte rank, String suit) {
        this.rank=rank;
        this.suit=suit;
    }
    
    byte getRank() {
        return rank;
    }
    
    String getSuit() {
        return suit;
    }
   
    void printCard() {
        System.out.print(rank + suit + " ");
    }
    
    

}


