package ru.nsu.fit.dib.projectdib.data;

/**
 * Типы снарядов.
 */
public enum Projectiles {
  ARROW("arrow", 250),
  BULLET("bullet", 500);


  private final String name;
  private final Integer speed;

  Projectiles(String name, Integer speed) {
    this.name = name;
    this.speed = speed;
  }

  public String getName() {
    return name;
  }

  public Integer getSpeed() {
    return speed;
  }

}
