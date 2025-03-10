package model;

import java.util.ArrayList;
import java.util.List;

import model.rules.HandEvaluation;

/**
 * Implementation of a poker game.
 */
public class PokerGame implements Game {
  private GameState state;
  private final PokerBoard board;
  private final PokerDeck deck;
  private final Player p1;
  private final Player p2;
  private final Integer pot; // implement later

  public PokerGame() {
    this(true); // automatically default to a shuffled deck
  }

  public PokerGame(boolean shuffle) {
    this.deck = new PokerDeck();
    this.board = new PokerBoard();
    this.p1 = new Player(Position.SMALL_BLIND);
    this.p2 = new Player(Position.BIG_BLIND);
    this.state = GameState.PREFLOP;
    if (shuffle) {
      deck.shuffle();
    }
    this.pot = null;
  }

  @Override
  public void dealHoleCards() {
    this.state = GameState.PREFLOP;
    p1.setHoleCards(deck.dealCards(2));
    p2.setHoleCards(deck.dealCards(2));
  }

  public void dealP1SpecificCards(Card card1, Card card2) {
    this.state = GameState.PREFLOP;
    List<Card> p1Cards = List.of(deck.dealSpecificCard(card1), deck.dealSpecificCard(card2));
    p1.setHoleCards(p1Cards);
    p2.setHoleCards(deck.dealCards(2));
  }

  @Override
  public void dealFlop() {
    state = GameState.FLOP;
    board.addCards(deck.dealCards(3));
  }

  @Override
  public void dealTurn() {
    state = GameState.TURN;
    board.addCard(deck.dealCard());
  }

  @Override
  public void dealRiver() {
    state = GameState.RIVER;
    board.addCard(deck.dealCard());
  }

  /**
   * Returns the best possible 5-card hand for current player.
   *
   * @param player the current player
   * @param board the current board
   * @return the best possible 5-card hand for current player.
   */
  public PokerHand getBestFiveCardHand(Player player, PokerBoard board) {
    if (state == GameState.PREFLOP) {
      throw new IllegalStateException("Cannot assemble five card hand preflop");
    }
    Card[] cards = new Card[5];
    HandEvaluation eval = new HandEvaluation();

    if (state == GameState.FLOP) {
      for (int i = 0; i < 3; i++) {
        cards[i] = board.getCommunityCards().get(i);
      }
      cards[3] = player.getHoleCards().getCard1();
      cards[4] = player.getHoleCards().getCard2();

      return new PokerHand(cards);
    } else if (state == GameState.TURN) {
      List<Card> allCards = new ArrayList<>(6);

      allCards.addAll(board.getCommunityCards());
      allCards.add(player.getHoleCards().getCard1());
      allCards.add(player.getHoleCards().getCard2());

      PokerHand bestHand = new PokerHand(new Card[]{allCards.get(1), allCards.get(2),
              allCards.get(3), allCards.get(4), allCards.get(5)});

      // Implement 7 choose 5 logic and find best hand of the 21 combinations.
      for (int i = 0; i <= 6; i++) {
        Card[] cand = new Card[5]; // candidate cards
        int notIorJ = 0;
        for (int k = 0; k <= 6; k++) {
          if (k != i) {
            cand[notIorJ] = allCards.get(k);
            notIorJ++;
            System.out.println(notIorJ);
          }
        }
        bestHand = eval.getBetterHand(new PokerHand(cand), bestHand);
      }

      return bestHand;

    } else if (state ==  GameState.RIVER) {
      List<Card> allCards = new ArrayList<>(7);

      allCards.addAll(board.getCommunityCards());
      allCards.add(player.getHoleCards().getCard1());
      allCards.add(player.getHoleCards().getCard2());

      PokerHand bestHand = new PokerHand(new Card[]{allCards.get(1), allCards.get(2),
              allCards.get(3), allCards.get(4), allCards.get(5)});

      // Implement 7 choose 5 logic and find best hand of the 21 combinations.
      for (int i = 0; i <= 6; i++) {
        Card[] cand = new Card[5]; // candidate cards

        for (int j = i + 1; j <= 6; j++) {

          int notIorJ = 0;
          for (int k = 0; k <= 6; k++) {
            if (k != i && k != j) {
              cand[notIorJ] = allCards.get(k);
              notIorJ++;
            }
          }
          bestHand = eval.getBetterHand(new PokerHand(cand), bestHand);

        }
      }

      return bestHand;
    }

    return new PokerHand();
  }

  public Player getP1() {
    return p1;
  }

  public Player getP2() {
    return p2;
  }

  public GameState getState() {
    return state;
  }

  public PokerBoard getBoard() {
    return board;
  }

  public PokerDeck getDeck() {
    return deck;
  }
}
