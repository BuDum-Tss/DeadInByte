package ru.nsu.fit.dib.projectdib;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import ru.nsu.fit.dib.projectdib.initapp.GameInitializer;
import ru.nsu.fit.dib.projectdib.initapp.InputListener;
import ru.nsu.fit.dib.projectdib.initapp.PhysicsLoader;
import ru.nsu.fit.dib.projectdib.initapp.SettingsLoader;


/**
 * Основной класс игры.
 */
public class App extends GameApplication {
  Entity player;
  InputListener inputListener;

  /**
   * Main-метод.
   *
   * @param args - аргументы командной строки
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Инициализация настроек игры.
   *
   * @param settings - настройки
   */
  @Override
  protected void initSettings(GameSettings settings) {
    SettingsLoader settingsLoader = new SettingsLoader(settings);
    settingsLoader.run();
  }

  /**
   * Инициализация действий со входными данными(например, с клавиатуры)
   */
  @Override
  protected void initInput() {
    inputListener = new InputListener();
    inputListener.run();
  }

  /**
   * Инициализация физики игры.
   */
  @Override
  protected void initPhysics() {
    PhysicsLoader physicsLoader = new PhysicsLoader();
    physicsLoader.run();
  }

  /**
   * Инициализация мультиплейера.
   */
  @Override
  protected void onPreInit() {
    /*EntityData data = new EntityData(HeroType.Elf.getName(), Weapons.Sword.getName(),10,8,6,3,
        new int[]{8, 20, 16, 4,4, 6}, new int[]{0, 4, 0, 0, 0});
    */
    //int strength, int agility, int durability,
    //int intelligence, int wisdom, int charisma
    //EntityData.serializeTo("src/main/resources/saves/character.json",data);
  }
  @Override
  protected void initUI(){
    //ImageView hpBar = ImageView(playerData);
    AnchorPane pane = new AnchorPane();
    FXGL.addUINode(pane);
  }

  /**
   * Инициализация игры.
   */
  @Override
  protected void initGame() {
    GameInitializer gameInitializer = new GameInitializer();
    gameInitializer.run();
    inputListener.initialize(gameInitializer.getPlayer());
    player = gameInitializer.getPlayer();
    player=gameInitializer.getPlayer();
/*    AStarGrid grid = AStarGrid.fromWorld(FXGL.getGameWorld(), FXGLForKtKt.getAppWidth(), getAppHeight(), 160, 160,
            (type) -> {
              if (type == EntityType.WALL || type == EntityType.CLOSED_DOOR) {
                return CellState.NOT_WALKABLE;
              }

              return CellState.WALKABLE;
            });
    set("grid", grid);*/
  }
  @Override
  protected void onUpdate(double tpf){
    List<Entity> list = FXGL.getGameScene().getGameWorld().getEntitiesByType(EntityType.PLAYER,EntityType.ENEMY,EntityType.WEAPON);
    list.sort((e1,e2)-> (int) (e1.getY()-e2.getY()));
    list.stream().map(entity ->entity.getViewComponent().getParent()).forEach(Node::toFront);
  }
}
