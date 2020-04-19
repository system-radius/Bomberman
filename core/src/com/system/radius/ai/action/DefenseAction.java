package com.system.radius.ai.action;

import com.system.radius.ai.Ai;
import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

public class DefenseAction extends MultiTargetAction {

  private List<Node> possibleTargets = new ArrayList<>();

  private int lastCost = 0;

  public DefenseAction(Ai ai, Action... chained) {
    super(ai, chained);
  }

  @Override
  protected void chooseTarget(Node source) {

    int spacesNear = 0;
    int range = player.getFirePower();

    if (lastCost != getBoardCost(target)) {
      // Reset the target if there is a change in the cost being tracked.
      target = null;
    }

    for (Node node : possibleTargets) {
//      if (source.toString().equals(node.toString())) {
//        // There will be no need to update the action path as the current target node is reached.
//        // Once the AI is safe, the current values for the fire path in the hypothetical board
//        // will be turned into blockers. Further added bombs after this should not be affected.
//        safe = true;
//        actionPath = new ArrayList<>();
//        break;
//      }

      int spacesCounter = pathFinder.searchSpaces(hypotheticalBoard, node, range).size();
      List<Node> path = pathFinder.findShortestPath(hypotheticalBoard, source, node);

      if (path == null || spacesCounter < spacesNear) {
        continue;
      }

      // Get the last node from the path.
      node = path.get(path.size() - 1);
      if (target == null || node.getCost() < target.getCost()) {
        target = node;
        actionPath = path;
        spacesNear = spacesCounter;

        lastCost = getBoardCost(target);
      }
    }

  }

  private int getBoardCost(Node node) {
    if (node == null) {
      return -1;
    }

    int targetX = node.getX();
    int targetY = node.getY();

    return hypotheticalBoard[targetY][targetX];
  }

  private void blockFirePaths() {

    int ms = (int) player.getSpeedLevel();
    int h = (int) WorldConstants.WORLD_HEIGHT;
    int w = (int) WorldConstants.WORLD_WIDTH;
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w; j++) {

        int value = hypotheticalBoard[i][j];
        if (value <= ms) {
          continue;
        }

        hypotheticalBoard[i][j] = -1;
      }
    }

  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node source) {

    boolean chained = parentBoard != null;
    hypotheticalBoard = boardState.copyBoard(ai.getBoard());
//    if (!chained) {
//      LOGGER.info("Checking action doability from AI.");
//    } else {
//      LOGGER.info("Checking action doability from parent.");
//      AStarUtils.printMaze(parentBoard, hypotheticalBoard);
//    }

    boardState.adaptBoard(hypotheticalBoard, parentBoard);

    isTargetAcquired();

    chooseTarget(source);

    // This action can sometimes be not doable. lol
    // Especially if checking from the side of bombing blocks.
    boolean doable = actionPath != null && possibleTargets.size() != 0;

    if (doable) {
      if (chained) {
        Node lastNode = actionPath.size() > 0 ? actionPath.get(actionPath.size() - 1) :
            NodeUtils.createNode(player);
        int x = lastNode.getX();
        int y = lastNode.getY();
        parentBoard[y][x] = 0;
      }

      LOGGER.info("Action is doable! Current path size: " + actionPath.size());
    } else {
      LOGGER.info("Defense action cannot be done!");
    }

    return doable;
  }

  @Override
  public boolean isTargetAcquired() {

    possibleTargets.clear();
//    LOGGER.info("Cleared safety spaces: " + possibleTargets.size());
    int detectionRange = (int) Math.max(WorldConstants.WORLD_WIDTH, WorldConstants.WORLD_HEIGHT);

    List<Node> emptySpaces = pathFinder.searchSpaces(hypotheticalBoard,
        NodeUtils.createNode(player), detectionRange);

    int movementCost = (int) Player.SPEED_COUNTER - (int) player.getSpeedLevel();
//    LOGGER.info("Empty spaces: " + emptySpaces.size() + ", getting targets with: " +
//    movementCost);

    for (Node space : emptySpaces) {
      int x = space.getX();
      int y = space.getY();

//      LOGGER.info("Comparing: " + hypotheticalBoard[y][x] + " vs. " + movementCost);
      if (hypotheticalBoard[y][x] == movementCost || hypotheticalBoard[y][x] == 0) {
//        LOGGER.info("Adding safe space!");
        hypotheticalBoard[y][x] = 0;
        possibleTargets.add(space);
      }

    }

    return possibleTargets.size() > 0;
  }

  @Override
  public void act() {
    // Empty action because there is no need to act, the AI will just wait.
    // Well, checking for another action possibility could be done here.
    // Especially since only in this action are the boards tagged with blocked fire paths.

//    LOGGER.info("Attempting to act. Is AI safe: " + safe);

    // Once this act method is called, it is automatically assumed that the AI is already safe.
    blockFirePaths();
    Node sourceNode = NodeUtils.createNode(player);
    for (Action action : chainedActions) {

//      LOGGER.info("Checking for doability of action: " + action.getClass().getSimpleName());
      if (action.isDoable(hypotheticalBoard, sourceNode)) {
        // Another action can be done, this defense action is complete.
//        LOGGER.info("Action is doable with the following maze: ");
//        AStarUtils.printMaze(hypotheticalBoard);
        activeChainedAction = action;
        complete = true;
        onComplete();
        break;
      }
    }
  }

  @Override
  public void onComplete() {
    target = null;
  }
}
