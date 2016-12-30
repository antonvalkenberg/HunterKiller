package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;

/**
 * Defines the various types of tiles that can be at a location and their string representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@AllArgsConstructor
public enum TileType {
  SPACE("."), FLOOR("_"), WALL("["), DOOR_CLOSED("D"), DOOR_OPEN("O"), SOLDIER("S"), MEDIC("M"), INFECTED("I"), BASE("B");
  
  public String txt;
}
