package com.system.radius.ai.action;

import com.system.radius.ai.Ai;
import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.utils.BombUtils;
import com.system.radius.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

public class BombBlocksAction extends MultiTargetAction {

  private int targetsCount;

  public BombBlocksAction(Ai ai, Action... chained) {
    super(ai, chained);
  }

  @Override
  protected void chooseTarget(Node source) {

    // Choose the target which has the most number of blocks that can be destroyed,
    // and if the defense action can be doable when the action for the target is complete.
    int range = player.getFirePower();
    Node currentTarget = null;

    for (Node target : possibleTargets) {
      int x = target.getX();
      int y = target.getY();

      int tempTargetCount = boardState.checkBlocks(x, y, range);
      if (tempTargetCount <= targetsCount ||
          (currentTarget != null && currentTarget.getCost() < target.getCost())) {
        // Do not process a target that has less destruction.
        // Or if the new target is farther than the current target selected.
        continue;
      }

      // Back-up the hypothetical board before conducting anything on it.
      int[][] tempBoard = boardState.copyBoard(hypotheticalBoard);

      // Continue processing the target here. Apply a bomb at the specified position,
      // then check for the if the chained action is still doable.
      BombUtils.updateBoardCost(tempBoard, target, player);
      tempBoard[y][x] = -2;

      LOGGER.info("Checking target: (" + x + ", " + y + ")");
      // There should be at least one chained action for this.
      if (activeChainedAction != null && activeChainedAction.isDoable(tempBoard, target)) {
        // If the action is doable (along with the chained action), mark the processed target
        // as the current target.
        currentTarget = target;
        targetsCount = tempTargetCount;
        LOGGER.info("Target shifted!");
//        LOGGER.info("Board: ");
//        AStarUtils.printMaze(tempBoard);
      }

    }

    if (currentTarget == null) {

      if (targetsCount == 0) {
        actionPath = null;
        return;
      }

      if (mainTarget != null) {
        actionPath = pathFinder.findShortestPath(hypotheticalBoard, source, mainTarget);
      }

      return;
    }

    mainTarget = currentTarget;
    LOGGER.info("Current targets: " + targetsCount);
    actionPath = pathFinder.findShortestPath(hypotheticalBoard, source, mainTarget);

  }

  /**
   * Finds targets based on the empty spaces.
   *
   * @param emptySpaces - The list of empty spaces.
   * @return The possible targets that has blocks around.
   */
  private List<Node> findTargets(List<Node> emptySpaces) {

    List<Node> targets = new ArrayList<>();

    int range = player.getFirePower();
    // Then check for the number of blocks that could be destroyed for each of the spaces.
    for (Node space : emptySpaces) {
      int x = space.getX();
      int y = space.getY();

      if (boardState.checkBlocks(x, y, range) > 0) {
        // If the area has more than one destroyable block, then it is a possible target.
        targets.add(space);
      }
    }

    return targets;
  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node sourceNode) {

    if (player.getRemainingBombs() <= 0) {
      return false;
    }

    hypotheticalBoard = boardState.copyBoard(ai.getBoard());
    if (parentBoard == null) {
      LOGGER.info("Checking action doability from AI.");
    } else {
      LOGGER.info("Checking action doability from parent.");
//      AStarUtils.printMaze(parentBoard, hypotheticalBoard);
    }

    boardState.adaptBoard(hypotheticalBoard, parentBoard);

    // Apply the adaptation of parent board.
    if (!isTargetAcquired()) {
      LOGGER.info("Target not found!");
      return false;
    }

    chooseTarget(sourceNode);

    LOGGER.info("Has an action path: " + (actionPath != null));
    return actionPath != null;
  }

  @Override
  public boolean isTargetAcquired() {

    possibleTargets.clear();
    // Find empty spaces with the default depth value.
    List<Node> emptySpaces = pathFinder.searchSpaces(hypotheticalBoard,
        NodeUtils.createNode(player),
        (int) (player.getSpeedLevel() * WorldConstants.DETECTION_RANGE));

    possibleTargets.addAll(findTargets(emptySpaces));

    if (possibleTargets.size() == 0) {

      List<Node> largerEmptySpaces = pathFinder.searchSpaces(hypotheticalBoard,
          NodeUtils.createNode(player),
          (int) (WorldConstants.WORLD_HEIGHT * WorldConstants.WORLD_WIDTH));

      possibleTargets.addAll(findTargets(NodeUtils.removeDuplicates(largerEmptySpaces,
          emptySpaces)));

    }

    return possibleTargets.size() > 0;
  }

  @Override
  public void act() {

    // The act of placing bomb completes this action.
    LOGGER.info("Planting bomb!");
    player.plantBomb();
    complete = true;

    onComplete();
    targetsCount = 0;

  }
}
