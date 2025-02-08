package model;

/**
 * Structure of a poker game.
 */
public interface Game {

  /**
   * Deals the flop (three cards to board).
   */
  void dealFlop();

  /**
   * Deals the turn (fourth card).
   */
  void dealTurn();

  /**
   * Deals the river (fifth card).
   */
  void dealRiver();
}
