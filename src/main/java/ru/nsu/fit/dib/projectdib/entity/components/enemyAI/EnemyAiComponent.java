package ru.nsu.fit.dib.projectdib.entity.components.enemyAI;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.google.javascript.jscomp.jarjar.javax.annotation.CheckForNull;
import javafx.geometry.Point2D;
import ru.nsu.fit.dib.projectdib.entity.components.fight.WeaponInventoryComponent;
import ru.nsu.fit.dib.projectdib.entity.components.view.HeroViewComponent;
import ru.nsu.fit.dib.projectdib.entity.weapons.WeaponViewComponent;
import ru.nsu.fit.dib.projectdib.newMultiplayer.context.client.MCClient;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.core.math.FXGLMath.sqrt;
import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class EnemyAiComponent extends Component {

  private Map<Integer, Entity> gameMapOfEntities = MCClient.getClientState().getIdHashTable();
  private Entity currentEnemy;
  private List<Entity> heroList;
  private List<Point2D> memoryOfLastHeroesPositions;

  @Override
  public void onAdded() {
    currentEnemy = getEntity();
  }

  @Override
  public void onUpdate(double tpf) {

    int hp = 15; // TODO entity.getComponent(HealthDoubleComponent.class). ... .getHP();
    int maxHp = 200;

    if (hp <= maxHp * 0.15) {
      // TODO RUNNING AWAY
      return;
    }
    Entity target = findNearestHero();
    if (target == null) {
      return;
    } else {
      // move(target.getPosition())
      // attack();
    }

    // Если нет оружия, тогда ищет упавшее оружие в зоне видимости и подбирает
    if (!entity.getComponent(WeaponInventoryComponent.class).hasWeapon()) {
      Entity droppedWeapon = findNearestDroppedWeapon();
      // TODO SEARCHING DROPPED WEAPON
      // move(dropped.Weapon.getPosition);
      // take();
    }
  }

  @CheckForNull
  private Entity findNearestHero() {
    List<Entity> heroList =
        gameMapOfEntities.values().stream()
            .filter(entities -> entities.hasComponent(HeroViewComponent.class))
            .filter(heroes -> distanceBetweenEntities(currentEnemy, heroes) < 500)
            .toList();

    if (heroList.isEmpty()) {
      return null;
    }
    double nearestHeroDist = Double.MAX_VALUE;
    Entity nearestHero = heroList.get(0);

    for (Entity hr : heroList) {
      double dist =
          sqrt(
              hr.getPosition().getX() * hr.getPosition().getX()
                  + hr.getPosition().getY() * hr.getPosition().getY());
      if (dist < nearestHeroDist) {
        nearestHero = hr;
        nearestHeroDist = dist;
      }
    }
    return nearestHero;
  }

  @CheckForNull
  private Entity findNearestDroppedWeapon() {
    List<Entity> droppedWp =
        gameMapOfEntities.values().stream()
            .filter(x -> x.hasComponent(WeaponViewComponent.class))
            .filter(weapon -> distanceBetweenEntities(currentEnemy, weapon) < 500)
            .toList();
    if (droppedWp.isEmpty()) {
      return null;
    }
    double nearestWpDist = Double.MAX_VALUE;
    Entity nearestWp = droppedWp.get(0);
    for (Entity ent : droppedWp) {
      double dist =
          sqrt(
              ent.getPosition().getX() * ent.getPosition().getX()
                  + ent.getPosition().getY() * ent.getPosition().getY());
      if (dist < nearestWpDist) {
        nearestWp = ent;
        nearestWpDist = dist;
      }
    }
    return nearestWp;
  }

  private void takeNearestDroppedWeapon() {
    Entity nearestWp = findNearestDroppedWeapon();
    if (nearestWp == null) {
      // running away
    }
    // move to nearestWp.getPosition;
    // collidable ? take : move()
  }

  private double distanceBetweenEntities(Entity a, Entity b) {
    double Xa = a.getPosition().getX();
    double Ya = a.getPosition().getY();
    double Xb = b.getPosition().getX();
    double Yb = b.getPosition().getY();
    return sqrt((Xa - Xb) * (Xa - Xb) + (Ya - Yb) * (Ya - Yb));
  }

  // TODO  No lasers?
  //    private List<Entity> heroesInFieldOfView(List<Entity> heroesInRangeOfVisibility){
  //    }
}
