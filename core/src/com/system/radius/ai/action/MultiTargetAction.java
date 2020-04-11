package com.system.radius.ai.action;

import com.system.radius.ai.Ai;
import com.system.radius.ai.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiTargetAction extends Action {

  /**
   * The container for all of the possible targets found using {@link Action#isTargetAcquired()}
   * method.
   */
  protected final List<Node> possibleTargets = new ArrayList<>();

  /**
   * The main target for this action.
   */
  protected Node mainTarget;

  public MultiTargetAction(Ai ai, Action... chained) {
    super(ai, chained);
  }

  /**
   * Chooses the main target from the list of the possible targets.
   * @param source - The origin of action.
   */
  protected abstract void chooseTarget(Node source);

  @Override
  public void onComplete() {

    possibleTargets.clear();
    mainTarget = null;
    actionPath = null;

  }

}
