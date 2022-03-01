package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	public int[][] highlight = new int[9][5];
	int []dx = {0,1,1,1,0,-1,-1,-1,2,0,-2,0};
	int []dy = {1,1,0,-1,-1,-1,0,1,0,-2,0,2};

	public void highlight_card_off(ActorRef out, GameState gameState){
		int loc = gameState.getHighlightCard();
		if(loc!=0){
			Card last_card = gameState.getHumanCard(loc);
			BasicCommands.drawCard(out, last_card, loc, 0);
			gameState.setHighlightCard(loc, 0);
			try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
			for(int i = 0; i < 9; i++){
				for(int j = 0; j < 5; j++){
					if(gameState.highlight_board[i][j]==1){
						gameState.highlight_board[i][j] = 0;
						Tile tile = BasicObjectBuilders.loadTile(i, j);
						BasicCommands.drawTile(out, tile, 0);
					}
				}
			}
		}
		gameState.select_card = false;
	}

	public void highlight_unit_off(ActorRef out, GameState gameState){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 5; j++){
				if(gameState.highlight_board[i][j]==1){
					gameState.highlight_board[i][j] = 0;
					Tile tile = BasicObjectBuilders.loadTile(i, j);
					BasicCommands.drawTile(out, tile, 0);
				}
			}
		}
		gameState.select = false;
	}

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		if (gameState.select_card == true) {
			// do some logic
			highlight_unit_off(out, gameState);

			for(Unit unit:gameState.human_unit){
				Position position = unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				if(tilex==x&&tiley==y){
					break;
				}
				if(Math.abs(tilex-x)<=1 && Math.abs(tiley-y)<=1){
					int loc = gameState.getHighlightCard();
					highlight_card_off(out, gameState);

					Card card = gameState.getHumanCard(loc);
					int m = gameState.humanPlayer.getMana() - card.getManacost();
					

					//drawUnit
					Tile tile1 = BasicObjectBuilders.loadTile(tilex,tiley);
					Unit unit1 = gameState.return_Unit(card.getCardname());

					unit1.setPositionByTile(tile1);
		
					gameState.human_unit.add(unit1);
					gameState.board[tilex][tiley] = 1;

					BasicCommands.drawUnit(out, unit1, tile1);
					try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}	
					BasicCommands.setUnitAttack(out, unit1, card.getBigCard().getAttack());
					try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}	
					BasicCommands.setUnitHealth(out, unit1, card.getBigCard().getHealth());
					try {Thread.sleep(50);} catch (InterruptedException e) {e.printStackTrace();}
				
					BasicCommands.deleteCard(out, loc);
					gameState.setHumanCard(loc, null);
					
					gameState.humanPlayer.setMana(m);
					BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
					try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
					return;
				}
			}
			highlight_card_off(out, gameState);
		}
		else if(gameState.select == true) {
			if(gameState.highlight_board[tilex][tiley] == 1){
				Unit select_unit = gameState.select_unit;
				Position position = select_unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
				select_unit.setPositionByTile(tile);
				BasicCommands.addPlayer1Notification(out, "move", 2);
                BasicCommands.moveUnitToTile(out, select_unit, tile);
				gameState.board[x][y] = 0;
				gameState.board[tilex][tiley] = 1;
			}

			highlight_unit_off(out, gameState);
		}
		else{
			highlight_card_off(out, gameState);
			for(Unit unit:gameState.human_unit){
				Position position = unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				if(x!=tilex || y!=tiley){
					continue;
				}
				for(int i = 0; i < 12; i++){
					int xx=x+dx[i];
					int yy=y+dy[i];
					if(xx<0||yy<0||xx>8||yy>4)
						continue;
					if(gameState.board[xx][yy]==0){
						gameState.highlight_board[xx][yy]=1;
					}
				}
				gameState.select = true;
				gameState.select_unit = unit;
				for(int i = 0; i < 9; i++){
					for(int j = 0; j < 5; j++){
						if(gameState.highlight_board[i][j]==1){
							Tile tile = BasicObjectBuilders.loadTile(i, j);
							BasicCommands.drawTile(out, tile, 1);
						}
					}
				}
				return;
			}
		}
	}

}
