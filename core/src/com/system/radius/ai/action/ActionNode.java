package com.system.radius.ai.action;

import com.system.radius.ai.Node;
import com.system.radius.enums.AiAction;

public class ActionNode extends Node {

  private AiAction action;

  private ActionNode chainedAction;

  public ActionNode(AiAction action, Node parent, int x, int y, float g, float h) {
    super(parent, x, y, g, h);

    this.action = action;

  }

  public void setChainedAction(ActionNode node) {
    this.chainedAction = node;
  }

  public ActionNode getChainedAction() {
    return chainedAction;
  }

  public AiAction getAction() {
    return action;
  }

}
