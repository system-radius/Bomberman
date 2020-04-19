package com.system.radius.ai.action;

import com.system.radius.ai.Ai;
import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.utils.NodeUtils;

import java.util.ArrayList;
import java.util.List;

public class AcquireBonusAction extends MultiTargetAction {

  public AcquireBonusAction(Ai ai, Action... chained) {
    super(ai, chained);
  }

  @Override
  protected void chooseTarget(Node source) {

    Node currentTarget = null;
    for (Node target : possibleTargets) {

      if (currentTarget == null || target.getCost() < currentTarget.getCost()) {
        currentTarget = target;
      }

    }

    if (currentTarget == null) {

      if (mainTarget != null) {
        actionPath = pathFinder.findShortestPath(hypotheticalBoard, source, mainTarget);
      } else {
        actionPath = null;
      }

      return;
    }

    mainTarget = currentTarget;
    LOGGER.info("Got a target!");
    actionPath = pathFinder.findShortestPath(hypotheticalBoard, source, mainTarget);

  }

  private List<Node> findTargets(List<Node> spaces) {

    List<Node> bonuses = new ArrayList<>();
    for (Node space : spaces) {
      int x = space.getX();
      int y = space.getY();

      if (boardState.getChar(x, y) == WorldConstants.BOARD_BONUS) {
        // Find the bonus-marked area from the given spaces.
        bonuses.add(space);
      }
    }

    return bonuses;
  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node sourceNode) {

    hypotheticalBoard = boardState.copyBoard(ai.getBoard());
    boardState.adaptBoard(hypotheticalBoard, parentBoard);

    if (!isTargetAcquired()) {
      return false;
    }

    chooseTarget(sourceNode);

    return actionPath != null;
  }

  @Override
  public boolean isTargetAcquired() {

    Node playerNode = NodeUtils.createNode(player);

    possibleTargets.clear();
    List<Node> emptySpaces = pathFinder.searchSpaces(hypotheticalBoard, playerNode,
        (int) (WorldConstants.WORLD_HEIGHT * WorldConstants.WORLD_WIDTH));

    possibleTargets.addAll(findTargets(emptySpaces));
    return possibleTargets.size() > 0;
  }

  @Override
  public void act() {
    complete = true;
    onComplete();
  }

  @Override
  public void onComplete() {
    mainTarget = null;
  }
}
