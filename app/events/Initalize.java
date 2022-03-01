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
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 5; j++){
				Tile tile = BasicObjectBuilders.loadTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
			}
		}

		//drawUnit
		Tile tile_human = BasicObjectBuilders.loadTile(1,2);
		Unit unit_human = gameState.return_Unit("humanAvatar");
		unit_human.setPositionByTile(tile_human);

		unit_human.setAttack(2);
		unit_human.setHealth(20);
		
		gameState.human_unit.add(unit_human);
		gameState.board[1][2] = 1;

		BasicCommands.drawUnit(out, unit_human, tile_human);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitAttack(out, unit_human, 2);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitHealth(out, unit_human, 20);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		Tile tile_ai = BasicObjectBuilders.loadTile(7,2);
		Unit unit_ai = gameState.return_enemy_unit("aiAvatar");
		unit_ai.setPositionByTile(tile_ai);

		unit_ai.setAttack(2);
		unit_ai.setHealth(20);
		
		gameState.ai_unit.add(unit_ai);
		gameState.board[7][2] = 2;

		BasicCommands.drawUnit(out, unit_ai, tile_ai);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitAttack(out, unit_ai, 2);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		BasicCommands.setUnitHealth(out, unit_ai, 20);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}	
		
		Player humanPlayer = gameState.getHumanPlayer();
		Player aiPlayer = gameState.getAiPlayer();

		// setPlayer1Health
		BasicCommands.addPlayer1Notification(out, "setPlayer1Health", 2);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer2Health
		BasicCommands.addPlayer1Notification(out, "setPlayer2Health", 2);
		BasicCommands.setPlayer2Health(out, aiPlayer);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		int m = gameState.turn_number+1;
		
		// Mana
		BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
		humanPlayer.setMana(m);
		BasicCommands.setPlayer1Mana(out, humanPlayer);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		// Mana
		BasicCommands.addPlayer1Notification(out, "setPlayer2Mana ("+m+")", 1);
		aiPlayer.setMana(m);
		BasicCommands.setPlayer2Mana(out, aiPlayer);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		
		for (int i = 0; i < 3; i++) {
			// drawCard
			gameState.deck1_index += 1;
			Card card = BasicObjectBuilders.loadCard(gameState.deck1Cards[i], i, Card.class);
			gameState.setHumanCard(i+1, card);
			gameState.setHighlightCard(i+1, 0);
			BasicCommands.drawCard(out, gameState.getHumanCard(i+1), i+1, 0);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// moveUnitToTile
		/*BasicCommands.addPlayer1Notification(out, "Deck 1 Units Test", 2);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		String[] deck1Units = {
				StaticConfFiles.u_comodo_charger,
				StaticConfFiles.u_hailstone_golem,
				StaticConfFiles.u_azure_herald,
				StaticConfFiles.u_azurite_lion,
				StaticConfFiles.u_pureblade_enforcer,
				StaticConfFiles.u_ironcliff_guardian,
				StaticConfFiles.u_silverguard_knight,
				StaticConfFiles.u_fire_spitter
		};

		Tile tile = BasicObjectBuilders.loadTile(3, 2);

		int unitID = 3;
		for (String deck1CardFile : deck1Units) {
			BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
			Unit unit = BasicObjectBuilders.loadUnit(deck1CardFile, unitID, Unit.class);
			unit.setPositionByTile(tile); 
			BasicCommands.drawUnit(out, unit, tile);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			// playUnitAnimation [Move]
			BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Move]", 2);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			// playUnitAnimation [Attack]
			BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Attack]", 2);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			// playUnitAnimation [Death]
			BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Death]", 3);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
			try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}

			// deleteUnit
			BasicCommands.addPlayer1Notification(out, "deleteUnit", 2);
			BasicCommands.deleteUnit(out, unit);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

			unitID++;
		}

		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);*/
	}

}


