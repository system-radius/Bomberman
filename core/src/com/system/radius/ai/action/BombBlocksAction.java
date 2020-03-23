package com.system.radius.ai.action;

import com.system.radius.ai.Node;
import com.system.radius.objects.players.Player;

public class BombBlocksAction extends Action {

  public BombBlocksAction(Player player) {
    super(player);
  }

  @Override
  public boolean isDoable(int[][] parentBoard, Node sourceNode) {
    return false;
  }

  @Override
  public boolean isTargetAcquired() {
    return false;
  }

  @Override
  public void act() {

    // The act of placing bomb completes this action.
    player.plantBomb();
    complete = true;

  }
}
