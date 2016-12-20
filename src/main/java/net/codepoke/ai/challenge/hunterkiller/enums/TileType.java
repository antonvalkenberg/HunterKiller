package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Defines the various types of tiles that can be at a location and their String representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum TileType {
  SPACE("."), FLOOR("_"), WALL("["), DOOR_CLOSED("D"), DOOR_OPEN("O"), SOLDIER("S"), MEDIC("M"), INFECTED("I"), BASE("B");
  
  private TileType(String txt) {
    this.txt = txt;
  }
  
  public final String txt;
}
