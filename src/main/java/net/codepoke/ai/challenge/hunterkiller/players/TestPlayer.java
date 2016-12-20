package main.java.net.codepoke.ai.challenge.hunterkiller.players;

import java.util.List;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import main.java.net.codepoke.ai.challenge.hunterkiller.Player;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.GameRules.Action;

/**
 * A player to test with, does mostly random things.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class TestPlayer extends Player {
  
  /**
   * @param id
   * @param base
   */
  public TestPlayer(int id, String name, Base base) {
    super(id, name, base);
    // TODO Auto-generated constructor stub
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * main.java.net.codepoke.ai.challenge.hunterkiller.Player#act(main.java.net.codepoke.ai.challenge
   * .hunterkiller.HunterKillerState)
   */
  @Override
  public List<Action> act(HunterKillerState state) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Player copy() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
