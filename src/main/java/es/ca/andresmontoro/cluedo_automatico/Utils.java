package es.ca.andresmontoro.cluedo_automatico;

import com.vaadin.flow.component.button.Button;

public class Utils {
  public static Button createPositionButton() {
    Button position = new Button(" ");
    position.setWidth("27px");
    position.setHeight("27px");
    position.getStyle().set("padding", "0");
    position.getStyle().set("margin", "1px");
    position.getStyle().set("min-width", "0");
    position.getStyle().set("min-height", "0");
    position.getStyle().set("border", "none");
    position.getStyle().set("background-color", "black");
    return position;
  }
}
