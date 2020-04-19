package com.system.radius.ai.action;

import com.system.radius.ai.Ai;
import com.system.radius.ai.Node;
import com.system.radius.objects.board.BoardState;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;
import com.system.radius.utils.BombermanLogger;
import com.system.radius.utils.NodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Action {

  protected final BombermanLogger LOGGER;

  /**
   * The path that will be traversed.
   */
  protected List<Node> actionPath;

  /**
   * A board state instance.
   */
  protected BoardState boardState = BoardState.getInstance();

  /**
   * The artificial intelligence using this action.
   */
  protected Ai ai;

  /**
   * The player instance which will be the point of reference for acting.
   */
  protected Player player;

  /**
   * The target of the action, the action path will be constructed with regards to this target.
   */
  protected Node target;

  /**
   * Another action that is to be performed after this action is completed.
   */
  protected Action activeChainedAction;

  /**
   * A collection of the other actions that can be performed after this action is completed.
   */
  protected List<Action> chainedActions = new ArrayList<>();

  /**
   * A path finder tool.
   */
  protected AStarUtils pathFinder;

  /**
   * Indicates that this action is completed, and can move on to another action.
   */
  protected boolean complete;

  /**
   * Holds the most recent representation of the board from the parent action.
   */
  protected int[][] hypotheticalBoard;

  public Action(Ai ai, Action... chained) {
    this.ai = ai;
    this.player = ai.getPlayer();

    LOGGER = new BombermanLogger(this.getClass().getSimpleName() + ai.getIndex());

    pathFinder = new AStarUtils();
    addChainedAction(chained);
  }

  public void addChainedAction(Action... actions) {
    if (actions == null || actions.length == 0) {
      return;
    }

    chainedActions.addAll(Arrays.asList(actions));
    if (activeChainedAction == null) {
      // Get the first action as the active chained action.
      activeChainedAction = chainedActions.get(0);
    }
  }

  /**
   * Checks if the current action is doable. This will also find the actionPath.
   *
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public boolean isDoable() {

    return isDoable(null, NodeUtils.createNode(player));
  }

  /**
   * Checks if the current action is doable with consideration to the parent action.
   *
   * @param parentBoard - The state of the board after the parent action is completed.
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public boolean isDoable(int[][] parentBoard) {

    return isDoable(parentBoard, NodeUtils.createNode(player));
  }

  /**
   * Checks if the current action is doable with consideration to the parent action and the new
   * position.
   *
   * @param parentBoard - The state of the board after the parent action is completed.
   * @param sourceNode  - The node as the source of the current action.
   * @return {@code true} if the action can be done; {@code false} otherwise.
   */
  public abstract boolean isDoable(int[][] parentBoard, Node sourceNode);

  /**
   * Attempts to retrieve the target for this action.
   *
   * @return {@code true} if the target is acquired; {@code false} otherwise.
   */
  public abstract boolean isTargetAcquired();

  /**
   * Do the action after reaching the target.
   */
  public abstract void act();

  /**
   * What will the action do upon completion?
   */
  public abstract void onComplete();

  public List<Node> getActionPath() {
    return actionPath;
  }

  public List<Action> getChainedActions() {
    return chainedActions;
  }

  public Action getChainedAction() {
    return activeChainedAction;
  }

  public int[][] getHypotheticalBoard() {
    return hypotheticalBoard;
  }

  public boolean isComplete() {
    return complete;
  }

  public void resetComplete() {
    complete = false;
  }

}
