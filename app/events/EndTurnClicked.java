package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Player humanPlayer = gameState.getHumanPlayer();
		Player aiPlayer = gameState.getAiPlayer();
		int m = ++gameState.turn_number+1;
		m = m > 9 ? 9:m;

		BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
		humanPlayer.setMana(m);
		BasicCommands.setPlayer1Mana(out, humanPlayer);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		// Mana
		BasicCommands.addPlayer1Notification(out, "setPlayer2Mana ("+m+")", 1);
		aiPlayer.setMana(m);
		BasicCommands.setPlayer2Mana(out, aiPlayer);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		
		int index = gameState.deck1_index;
		Card card = BasicObjectBuilders.loadCard(gameState.deck1Cards[index], index, Card.class);
		int free_index = gameState.getFreeCard();
		if(free_index != 0){
			gameState.setHumanCard(free_index, card);
			gameState.setHighlightCard(free_index, 0);
			BasicCommands.drawCard(out, gameState.getHumanCard(free_index), free_index, 0);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.deck1_index += 1;
			gameState.deck1_index = gameState.deck1_index % gameState.deck1Cards.length;
		}

		for(Unit unit:gameState.human_unit){
			unit.move = true;
			unit.attack = true;
		}
		
	}

}
