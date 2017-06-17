package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Data;

@Data
public class OrderStatistics {

	public int issued;

	public int success;

	public int failed;

	public int spawnSoldier;

	public int spawnMedic;

	public int spawnInfected;

	public int move;

	public int rotateClockwise;

	public int rotateCounter;

	public int attack;

	public int heal;

	public int grenade;

	public int attackUnit;

	public int attackStructure;

	public int attackAlly;

	public OrderStatistics() {
		issued = 0;
		success = 0;
		failed = 0;
		spawnSoldier = 0;
		spawnMedic = 0;
		spawnInfected = 0;
		move = 0;
		rotateClockwise = 0;
		rotateCounter = 0;
		attack = 0;
		heal = 0;
		grenade = 0;
		attackUnit = 0;
		attackStructure = 0;
		attackAlly = 0;
	}

	@Override
	public String toString() {
		return issued + "\t" + success + "\t" + failed + "\t" + spawnSoldier + "\t" + spawnMedic + "\t" + spawnInfected + "\t" + move
				+ "\t" + rotateClockwise + "\t" + rotateCounter + "\t" + attack + "\t" + heal + "\t" + grenade + "\t" + attackUnit + "\t"
				+ attackStructure + "\t" + attackAlly;
	}

}
