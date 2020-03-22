package com.system.radius.ai.action;

import com.system.radius.ai.Node;
import com.system.radius.objects.board.WorldConstants;
import com.system.radius.objects.bombs.Bomb;
import com.system.radius.objects.players.Player;
import com.system.radius.utils.AStarUtils;

import java.util.ArrayList;
import java.util.List;

public class DefenseAction extends Action {

  private List<Node> possibleTargets = new ArrayList<>();

  private boolean safe;

  public DefenseAction(Player player) {
    super(player);
  }

  private void chooseTarget(Node source) {

    safe = false;
    target = null;
    for (Node node : possibleTargets) {
      if (source.toString().equals(node.toString())) {
        // There will be no need to update the action path as the current target node is reached.
        // Once the AI is safe, the current values for the fire path in the hypothetical board
        // will be turned into blockers. Further added bombs after this should not be affected.
        safe = true;
        actionPath = new ArrayList<>();
        break;
      }

      List<Node> path = AStarUtils.findShortestPath(hypotheticalBoard, source, node);
      // Get the last node from the path.

      if (path == null) {
        continue;
      }

      node = path.get(path.size() - 1);
      if (target == null || node.getCost() < target.getCost()) {
        target = node;
        actionPath = path;
      }
    }

  }

  private void blockFirePaths() {


  }

  @Override
  public void updateBombCost(int[][] board, Bomb bomb) {
    if (!safe) {
      super.updateBombCost(board, bomb);
      return;
    }

    // If the player is already safe, all fire paths will become blocked.
    bomb.updateBoardCostSetCost(board, -1);
  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node source) {

    hypotheticalBoard = constructBoardRep();
    adaptBoard(parentBoard);

    isTargetAcquired();

    chooseTarget(source);

    System.out.println("Current path: " + actionPath.size());
    return true;
  }

  @Override
  public boolean isTargetAcquired() {

    possibleTargets.clear();
    int detectionRange = (int) Math.max(WorldConstants.WORLD_WIDTH, WorldConstants.WORLD_HEIGHT);

    int playerX = boardState.getExactX(player);
    int playerY = boardState.getExactY(player);

    int movementCost = (int) Player.SPEED_COUNTER - (int) player.getSpeedLevel();

    possibleTargets = new ArrayList<>();

    // k represents the range from player.
    // This will not stop from expanding until at least one area surrounding the player is
    // available.
    for (int k = 0; k < detectionRange && possibleTargets.size() == 0; k += 2) {

      for (int i = -k; i <= k; i++) {
        int y = playerY + i;

        if (y < 0 || y >= WorldConstants.WORLD_HEIGHT) {
          continue;
        }

        for (int j = -k; j <= k; j++) {

          int x = playerX + j;
          if (x < 0 || x >= WorldConstants.WORLD_WIDTH ||
              (Math.abs(i) != k && Math.abs(j) != j) || hypotheticalBoard[y][x] > movementCost ||
              hypotheticalBoard[y][x] < 0) {
            // Ignore checking costs that are out-of-bounds.
            // Also ignore spaces where the cost exceed the normal cost (along the fire path).
            continue;
          }

          possibleTargets.add(new Node(null, x, y, 0, 0));

        }

      }

    }

    return possibleTargets.size() > 0;
  }

  @Override
  public void act() {
    // Empty action because there is no need to act, the AI will just wait.
    // Well, checking for another action possibility could be done here.
    // Especially since only in this action are the boards tagged with blocked fire paths.

    // Fore the AI to check if another action is doable?

    if (safe) {
      complete = true;
    }
  }
}
