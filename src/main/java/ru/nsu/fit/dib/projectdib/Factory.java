package ru.nsu.fit.dib.projectdib;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;

import static java.lang.Character.toUpperCase;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.Pair;
import ru.nsu.fit.dib.projectdib.data.ProjectConfig;
import ru.nsu.fit.dib.projectdib.data.RandomCharacterSystem;
import ru.nsu.fit.dib.projectdib.entity.components.*;
import ru.nsu.fit.dib.projectdib.entity.creatures.Creature;
import ru.nsu.fit.dib.projectdib.data.Projectiles;
import ru.nsu.fit.dib.projectdib.entity.creatures.EnemiesFactory;
import ru.nsu.fit.dib.projectdib.entity.creatures.HeroesFactory;
import ru.nsu.fit.dib.projectdib.entity.creatures.HeroesFactory.HeroType;
import ru.nsu.fit.dib.projectdib.entity.creatures.modules.CreatureWeaponModule;
import ru.nsu.fit.dib.projectdib.entity.creatures.modules.JFXModule;
import ru.nsu.fit.dib.projectdib.entity.weapons.Weapon;
import ru.nsu.fit.dib.projectdib.entity.weapons.WeaponFactory;
import ru.nsu.fit.dib.projectdib.entity.weapons.WeaponFactory.Weapons;
import ru.nsu.fit.dib.projectdib.entity.weapons.enums.modules.TextureModule;
import ru.nsu.fit.dib.projectdib.entity.weapons.enums.modules.WeaponModule;

/**
 * Class Factory for making Entities.
 */
public class Factory implements EntityFactory {

  /**
   * Creates Hero and heroes weapon.
   *
   * @param heroType type of hero
   * @param position position
   * @param isClientHero true if client hero
   * @param seed hero seed
   * @return pair of entity Hero and Weapon
   */
  public static Pair<Entity,Entity> spawnHero(HeroType heroType,Point2D position, Boolean isClientHero,Integer seed){
    SpawnData sd = new SpawnData(position);
    sd.put("clientHero",isClientHero);
    sd.put("creature", HeroesFactory.newHero(heroType,seed));
    Entity hero = spawn("player", sd);

    hero.setScaleUniform(0.75);
    SpawnData weaponSD = new SpawnData(position);
    weaponSD.put("weapon",  hero.getComponent(HeroComponent.class).getCreature().getModule(
        CreatureWeaponModule.class).getActiveWeapon());
    Entity weapon = spawn("weapon",weaponSD);
    return new Pair(hero,weapon);
  };

  public static Pair<Entity,Entity> spawnEnemy(EnemiesFactory.EnemyType enemyType, Point2D position, Integer seed){
    SpawnData sd = new SpawnData(position);
    sd.put("creature", EnemiesFactory.newEnemy(enemyType, seed));

    //enemy = getGameWorld().create("enemy", sd);
    Entity enemy = spawn("enemy", sd);
    enemy.setScaleUniform(0.75);
    SpawnData weaponSD = new SpawnData(position);
    weaponSD.put("weapon",  enemy.getComponent(EnemyComponent.class).getCreature().getModule(
            CreatureWeaponModule.class).getActiveWeapon());
    Entity weapon = spawn("weapon", weaponSD);
    return new Pair(enemy, weapon);
  }

  public static Entity spawnWeapon(Weapons weaponType,Point2D position){
    SpawnData sd = new SpawnData(position);
    sd.put("weapon", WeaponFactory.getWeapon(weaponType));
    return spawn("weapon", sd);
  };
  @Spawns("player")
  public Entity newPlayer(SpawnData data) {
    Creature creature = data.get("creature");
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(0.3f));
    HeroComponent heroComponent = new HeroComponent(creature,new Point2D(50,180));
    if (data.get("clientHero")) heroComponent.bindDirectionView(entity -> getInput().getVectorToMouse(entity.getPosition().add(new Point2D(80, 160))));
    else heroComponent.bindDirectionView(entity ->new Point2D(0,0));
    creature.getModule(JFXModule.class).setComponent(heroComponent);
    //HeroSpecs specs = new HeroSpecs("1", "bow", "ak", 450.0, "player.png");
    return entityBuilder()
        .from(data)
        .type(EntityType.PLAYER)
        //.viewWithBBox(texture("weapon_" + playerMovingComponent.getCurrentWeapon()  + ".png", 150,200))
        .bbox(new HitBox(new Point2D(30, 220), BoundingShape.box(100, 100)))
        .anchorFromCenter()
        .with(physics)
        .with(new HeroViewComponent(creature.getModule(JFXModule.class).getImageID()))
        .with(heroComponent)
        .with(new CellMoveComponent(30, 30, 85))
        .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
        //.with(new ChunkLoaderComponent(new ChunkLoader(wallMapper)))
        .collidable()
        .build();
  }
  @Spawns("enemy")
  public Entity newEnemy(SpawnData data) {
    Creature creature = data.get("creature");
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(0.3f));
    EnemyComponent enemyComponent = new EnemyComponent(creature,new Point2D(50,180));
    enemyComponent.bindDirectionView(entity ->new Point2D(0,0));
    creature.getModule(JFXModule.class).setEnemyComponent(enemyComponent);
    AStarMoveComponent move = new AStarMoveComponent(new LazyValue<>(() -> geto("grid")));
    //HeroSpecs specs = new HeroSpecs("1", "bow", "ak", 450.0, "player.png");
    EntityBuilder builder= entityBuilder()
        .from(data)
        .type(EntityType.ENEMY)
        //.viewWithBBox(texture("weapon_" + playerMovingComponent.getCurrentWeapon()  + ".png", 150,200))
        .bbox(new HitBox(new Point2D(30, 220), BoundingShape.box(100, 100)))
        .anchorFromCenter()
        .with(physics)
        .with(new EnemyViewComponent(creature.getModule(JFXModule.class).getImageID()))
        .with(enemyComponent)
        .collidable();
    // TODO: 26.04.2023 поменять на проверку: клиент - сервер или нет
    if (false) return builder
            .with(new CellMoveComponent(160, 160, 20))
        //.with(new HealthIntComponent(10))
        .with(new CellMoveComponent(160, 160, 300).allowRotation(true))
        //.with(new AIComponent()); todo сделать позже
            .collidable()
            .build();
    return builder.build();
  }
/*
  @Spawns("enemy")
  public Entity newEnemy(SpawnData data) {
    var hp = new HealthIntComponent(10);
    var hpView = new ProgressBar(false);
    hpView.setFill(Color.LIGHTGREEN);
    hpView.setMaxValue(10);
    hpView.setWidth(40);
    hpView.setTranslateY(-10);
    hpView.currentValueProperty().bind(hp.valueProperty());

    Circle circle = new Circle(10, 10, 10, Color.RED);
    circle.setStroke(Color.BROWN);
    circle.setStrokeWidth(2.0);
    return entityBuilder()
            .from(data)
            .type(EntityType.ENEMY)
            .viewWithBBox(texture("skull.png", 50, 50))
            .anchorFromCenter()
            .collidable()
            .with(hp)
            .view(hpView)
            .with(new CellMoveComponent(25, 25, 100))
            .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
            .with(new PlayerChaseComponent())
            .build();
  }
*/

  @Spawns("platform")
  public Entity platform(SpawnData data) {
    return entityBuilder(data)
        .from(data)
        .type(EntityType.PLATFORM)
        .bbox(
            new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
        .with(new CollidableComponent(true))
        .build();
  }

  @Spawns("wall")
  public Entity wall(SpawnData data) {
    return entityBuilder(data)
        .from(data)
        .type(EntityType.WALL)
        .bbox(
            new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
        .with(new PhysicsComponent())
        .collidable()
        .build();
  }

  @Spawns("box")
  public Entity newBox(SpawnData data) {
    var hp = new HealthIntComponent(3);
    var hpView = new ProgressBar(false);
    hpView.setFill(Color.LIGHTGREEN);
    hpView.setMaxValue(3);
    hpView.setWidth(40);
    hpView.setTranslateY(-10);
    hpView.currentValueProperty().bind(hp.valueProperty());

    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(0.3f));

    return entityBuilder()
        .from(data)
        .type(EntityType.BOX)
        .viewWithBBox(FXGL.texture("box.png", 40, 40))
        .bbox(new HitBox(new Point2D(25, 30), BoundingShape.box(20, 10)))
        .view(hpView)
        .with(hp)
        .with(physics)
        .with(new BoxMovingComponent())
        .collidable()
        .build();
  }

  @Spawns("chest")
  public Entity newChest(SpawnData data) {
    var hp = new HealthIntComponent(3);
    var hpView = new ProgressBar(false);
    hpView.setFill(Color.LIGHTGREEN);
    hpView.setMaxValue(3);
    hpView.setWidth(40);
    hpView.setTranslateY(-10);
    hpView.currentValueProperty().bind(hp.valueProperty());

    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(0.3f));

    return entityBuilder()
        .from(data)
        .type(EntityType.CHEST)
        .viewWithBBox(FXGL.texture("chest.png", 40, 40))
        .view(hpView)
        .with(hp)
        .with(physics)
        .with(new BoxMovingComponent())
        .collidable()
        .build();
  }


  @Spawns("coin")
  public Entity newCoin(SpawnData data) {
    return entityBuilder()
        .from(data)
        .type(EntityType.COIN)
        .viewWithBBox(new Circle(5, 5, 5, Color.YELLOW))
        .collidable()
        .build();
  }


  @Spawns("projectile")
  public Entity newProjectile(SpawnData data) {
    Entity player = FXGLForKtKt.getGameWorld().getSingleton(EntityType.PLAYER);
    Point2D direction = getInput().getMousePositionWorld()
        .subtract(player.getCenter().subtract(new Point2D(60, 90)));
    Projectiles projectile = data.get("typeProj");
    return entityBuilder()
        .from(data)
        .type(EntityType.PROJECTILE)
        .viewWithBBox(texture("projectile_" + projectile.getName() + ".png", 40, 15))
        .with(new ProjectileComponent(direction, projectile.getSpeed()))
        .with(new OffscreenCleanComponent())
        .collidable()
        .build();
  }
  /*
  @Spawns("bow")
  public Entity newBow(SpawnData data) {
    return entityBuilder(data)
        .from(data)
        .type(EntityType.BOW)
        .viewWithBBox(texture("red_bow.png", 15, 50))
        .bbox(new HitBox(BoundingShape.box(50, 15)))
        .with(new CollidableComponent(true))
        .build();
  }

  @Spawns("ak")
  public Entity newAK(SpawnData data) {
    return entityBuilder(data)
        .from(data)
        .type(EntityType.AK)
        .viewWithBBox(texture("weapon_ak.png", 75, 20))
        .bbox(new HitBox(BoundingShape.box(75, 20)))
        .with(new CollidableComponent(true))
        .build();
  }
    @Spawns("player")
  public Entity newPlayer(SpawnData data) {
    PhysicsComponent physics = new PhysicsComponent();
    physics.setBodyType(BodyType.DYNAMIC);
    physics.setFixtureDef(new FixtureDef().friction(0.3f));
    HeroSpecs specs = new HeroSpecs("1", "bow", "ak", 450.0, "player.png");

    return entityBuilder()
        .from(data)
        .type(EntityType.PLAYER)
        //.viewWithBBox(texture("weapon_" + playerMovingComponent.getCurrentWeapon()  + ".png", 150,200))
        .bbox(new HitBox(new Point2D(30, 220), BoundingShape.box(100, 100)))
        .anchorFromCenter()
        .with(physics)
        .with(new PlayerMovingComponent(specs,new Point2D(50,180)))
        .with(new CellMoveComponent(30, 30, 85))
        .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
        //.with(new ChunkLoaderComponent(new ChunkLoader(wallMapper)))
        .collidable()
        .build();
  }
  */
  @Spawns("weapon")
  public Entity Weapon(SpawnData data) {
    Weapon weapon = data.get("weapon");
    ImageView iv = imageViewFromSpriteSheet(weapon.getModule(TextureModule.class).getTexturePath(),weapon.getModule(TextureModule.class).getWeaponID(),
        weapon.getModule(TextureModule.class).getImageWidht(),weapon.getModule(TextureModule.class).getImageHeight(), ProjectConfig._WEAPON_COLUMNS);
    //iv.setRotate(90);
    WeaponComponent weaponComponent = new WeaponComponent(weapon);
    weapon.getModule(TextureModule.class).setComponent(weaponComponent);
    return entityBuilder(data)
        .from(data)
        .type(EntityType.WEAPON)
        .viewWithBBox(iv)
        .bbox(new HitBox(BoundingShape.box(75, 20)))
        .with(new CollidableComponent(true))
        .with(weaponComponent)
        .build();
  }
  ImageView imageViewFromSpriteSheet(String path, int number, int spriteWidth, int spriteHeight,int columns){
    ImageView iv = new ImageView(new Image(path));
    iv.setViewport(new Rectangle2D(spriteWidth*(number%columns),spriteHeight*(number/columns),spriteWidth,spriteHeight));
    return iv;
  }

  @Spawns("button")
  public Entity newButton(SpawnData data) {
    return entityBuilder()
        .from(data)
        .type(EntityType.BUTTON)
        .viewWithBBox(texture("button.png", 16, 16))
        .with(new CollidableComponent(true))
        .with("closedDoor", data.get("closedDoor"))
        .build();
  }

  @Spawns("explosion")
  public Entity newExplosion(SpawnData data) {
    Image image = image("explosion.png");
    AnimatedTexture texture = new AnimatedTexture(
        new AnimationChannel(image, 4, 960 / 5, 768 / 4, Duration.seconds(0.66),
            0, 3));
    return entityBuilder()
        .from(data)
        .type(EntityType.EXPLOSION)
        .viewWithBBox(texture("explosion.png", 16, 16))
        .build();
  }

  @Spawns("closedDoor")
  public Entity newClosedDoor(SpawnData data) {
    return entityBuilder()
        .from(data)
        .type(EntityType.CLOSED_DOOR)
        .viewWithBBox(texture("closedDoor.png", 32, 32))
        .with(new PhysicsComponent())
        .build();
  }

  @Spawns("openedDoor")
  public Entity newOpenedDoor(SpawnData data) {
    return entityBuilder()
        .from(data)
        .type(EntityType.OPENED_DOOR)
        .viewWithBBox(texture("openedDoor.png", 32, 32))
        .build();
  }

  @Spawns("doorTrigger")
  public Entity newDoorTrigger(SpawnData data) {
    return entityBuilder()
        .from(data)
        .type(EntityType.DOOR_TRIGGER)
        .bbox(
            new HitBox(BoundingShape.box(32, 32)))
        .with(new CollidableComponent(true))
        .with("openedDoor", data.get("openedDoor"))
        .build();
  }
}
