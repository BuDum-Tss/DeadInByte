package ru.nsu.fit.dib.projectdib.entity.components.data;

import com.almasb.fxgl.entity.component.Component;
import ru.nsu.fit.dib.projectdib.entity.creatures.Creature;

/**
 * Only Data
 */
public class CreatureComponent extends Component {

  protected Creature creature;

  public CreatureComponent(Creature creature) {
    this.creature=creature;
  }


  public Creature getCreature() {
    return creature;
  }

  //Actions
}
