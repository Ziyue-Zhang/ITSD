package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;

		//gameInitialization
		BasicCommands.addPlayer1Notification(out, "GameInitialization", 2);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		//drawTile
		for(int i = 0; i <= 8; i++){
			for(int j = 0; j <= 4; j++){
				Tile tile = BasicObjectBuilders.loadTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
			}
		}

		//drawUnit
		Tile tile_human = BasicObjectBuilders.loadTile(1,2);
		Unit unit_human = GameState.return_Unit("humanAvatar");
		unit_human.setPositionByTile(tile_human);
		//GameState.unit.add(unit_human);
		BasicCommands.drawUnit(out, unit_human, tile_human);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitAttack(out, unit_human, 2);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitHealth(out, unit_human, 20);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		Tile tile_ai = BasicObjectBuilders.loadTile(7,2);
		Unit unit_ai = GameState.return_enemy_unit("aiAvatar");
		unit_ai.setPositionByTile(tile_ai);
		//GameState.aiUnit.add(unitai);
		BasicCommands.drawUnit(out, unit_ai, tile_ai);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitAttack(out, unit_ai, 2);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitHealth(out, unit_ai, 20);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		
		// setPlayer1Health
		BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
		Player humanPlayer = new Player(20, 0);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer2Health
		BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
		Player aiPlayer = new Player(20, 0);
		BasicCommands.setPlayer2Health(out, aiPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// Mana
		for (int m = 0; m<10; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
			humanPlayer.setMana(m);
			BasicCommands.setPlayer1Mana(out, humanPlayer);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// Mana
		for (int m = 0; m<10; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer2Mana ("+m+")", 1);
			aiPlayer.setMana(m);
			BasicCommands.setPlayer2Mana(out, aiPlayer);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		//deck1CardsDefinition
		String[] deck1Cards = {
			StaticConfFiles.c_comodo_charger,
			StaticConfFiles.c_pureblade_enforcer,
			StaticConfFiles.c_fire_spitter,
		};

		for (int i = 0; i <= 2; i++) {
			// drawCard
			Card card = BasicObjectBuilders.loadCard(deck1Cards[i], i, Card.class);
			BasicCommands.drawCard(out, card, i, 0);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);
	}

}


