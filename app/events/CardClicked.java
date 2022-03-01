package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{
	public int[][] highlight = new int[9][5];
	int []dx = {0,1,1,1,0,-1,-1,-1};
	int []dy = {1,1,0,-1,-1,-1,0,1};

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();

		int loc = gameState.getHighlightCard();
		
		if(loc!=0){
			Card last_card = gameState.getHumanCard(loc);
			BasicCommands.drawCard(out, last_card, loc, 0);
			gameState.setHighlightCard(loc, 0);
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
			for(int i = 0; i < 9; i++){
				for(int j = 0; j < 5; j++){
					if(highlight[i][j]==1){
						highlight[i][j] = 0;
						Tile tile = BasicObjectBuilders.loadTile(i, j);
						BasicCommands.drawTile(out, tile, 0);
					}
				}
			}
		}
		
		Card card = gameState.getHumanCard(handPosition);
		BasicCommands.drawCard(out, card, handPosition, 1);
		gameState.setHighlightCard(handPosition, 1);
		try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
		
		for(Unit unit:gameState.human_unit){
			Position position = unit.getPosition();
			int x = position.getTilex();
			int y = position.getTiley();
			for(int i = 0; i < 8; i++){
				int xx=x+dx[i];
				int yy=y+dy[i];
				if(gameState.board[xx][yy]==0){
					highlight[xx][yy]=1;
				}
			}
		}

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 5; j++){
				if(highlight[i][j]==1){
					Tile tile = BasicObjectBuilders.loadTile(i, j);
					BasicCommands.drawTile(out, tile, 1);
				}
			}
		}
		
	}

}
