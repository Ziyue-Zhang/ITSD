package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
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

		for(Unit ai_unit : gameState.ai_unit) {
			ai_unit.round_attackable = true;
			ai_unit.round_moveable = true;
		}

		for(Unit human_unit : gameState.human_unit) {
			human_unit.round_attackable = true;
			human_unit.round_moveable = true;
		}
		
		int index = gameState.deck1_index;
		if(gameState.deck1_count == gameState.deck1Cards.length*2)
			return;

		Card card = BasicObjectBuilders.loadCard(gameState.deck1Cards[index], index, Card.class);
		int free_index = gameState.getFreeCard();
		if(free_index != 0){
			gameState.setHumanCard(free_index, card);
			gameState.setHighlightCard(free_index, 0);
			BasicCommands.drawCard(out, gameState.getHumanCard(free_index), free_index, 0);
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.deck1_index += 1;
			gameState.deck1_count += 1;
			gameState.deck1_index = gameState.deck1_index % gameState.deck1Cards.length;
		}
		
	}

}
