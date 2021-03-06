package org.bgu.nlp.wicket.behaviour;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

public class PlaceholderBehaviour extends Behavior{
		  private final String placeholder;

		  public PlaceholderBehaviour(String placeholder) {
		    this.placeholder = placeholder;
		  }

		  @Override
		  public void onComponentTag(Component component, ComponentTag tag) {
		    tag.put("placeholder", this.placeholder);
		  }

}
