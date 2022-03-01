package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.BasicUtils;

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

	int []dx_attack = {0,1,1,1,0,-1,-1,-1};
	int []dy_attack = {1,1,0,-1,-1,-1,0,1};


	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		if (gameState.select_card == true) {
			// do some logic
			BasicUtils.highlight_unit_off(out, gameState);

			for(Unit unit:gameState.human_unit){
				Position position = unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				if(tilex==x&&tiley==y){
					break;
				}
				if(Math.abs(tilex-x)<=1 && Math.abs(tiley-y)<=1){
					int loc = gameState.getHighlightCard();
					BasicUtils.highlight_card_off(out, gameState);

					Card card = gameState.getHumanCard(loc);
					int m = gameState.humanPlayer.getMana() - card.getManacost();
					

					//drawUnit
					Tile tile1 = BasicObjectBuilders.loadTile(tilex,tiley);
					Unit unit1 = gameState.return_Unit(card.getCardname());

					unit1.setPositionByTile(tile1);

					unit1.setAttack(card.getBigCard().getAttack());
					unit1.setHealth(card.getBigCard().getHealth());
		
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
			BasicUtils.highlight_card_off(out, gameState);
		}
		else if(gameState.select == true) {
			if(gameState.highlight_board[tilex][tiley] == 1){
				// move

				if(!gameState.select_unit.round_moveable){
					BasicUtils.highlight_unit_off(out, gameState);
					BasicCommands.addPlayer1Notification(out, "You can't move in this round!", 2);
					return;
				}

				gameState.select_unit.round_moveable = false;

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
			}else if (gameState.highlight_board[tilex][tiley] == 2){
				// attack

				if(!gameState.select_unit.round_attackable){
					BasicCommands.addPlayer1Notification(out, "You can't attack in this round!", 2);
					return;
				}

				gameState.select_unit.round_attackable = false;

				// 1. get the attacked enemy
				Unit enemy = null;
				for(Unit ai_unit : gameState.ai_unit) {
					if(ai_unit.getPosition().getTilex() == tilex && ai_unit.getPosition().getTiley() == tiley)
						enemy = ai_unit;
				}
				if(enemy == null) return;

				Unit me = gameState.select_unit;

				// 2. calculate whether enemy will die in this round
				if(enemy.getHealth() > me.getAttack()) {
					// enemy not die

					// 3.1 show the attack
					BasicCommands.playUnitAnimation(out, me, UnitAnimationType.attack);
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.hit);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

					// 3.2 decrease the health of enemy
					enemy.setHealth(enemy.getHealth() - me.getAttack());
					BasicCommands.setUnitHealth(out, enemy, enemy.getHealth());
					try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

					// 3.3 the enemy fights back
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.attack);
					BasicCommands.playUnitAnimation(out, me, UnitAnimationType.hit);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.idle);

					if(me.getHealth() > enemy.getAttack()) {
						// me not die
						BasicCommands.playUnitAnimation(out, me, UnitAnimationType.idle);
						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}

						// 4. decrease the health of me
						me.setHealth(me.getHealth() - enemy.getAttack());
						BasicCommands.setUnitHealth(out, me, me.getHealth());
						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
					}else {
						// me die
						BasicCommands.playUnitAnimation(out, me, UnitAnimationType.death);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

						// 4.1 del me
						BasicCommands.deleteUnit(out, me);
						gameState.board[me.getPosition().getTilex()][me.getPosition().getTiley()] = 0;
						gameState.human_unit.remove(me);

						// 4.2 if me is boss , ai wins
						gameState.gameEnd = true;
						gameState.aiWin = true;
					}


				}else {
					// enemy die

					// 3.1 show the attack
					BasicCommands.playUnitAnimation(out, me, UnitAnimationType.attack);
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.hit);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					BasicCommands.playUnitAnimation(out, me, UnitAnimationType.idle);
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.death);
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

					// 3.2 del the enemy
					BasicCommands.deleteUnit(out, enemy);
					gameState.board[enemy.getPosition().getTilex()][enemy.getPosition().getTiley()] = 0;
					gameState.ai_unit.remove(enemy);

					// 3.3 if the enemy is boos , human wins
					gameState.gameEnd = true;
					gameState.humanWin = true;
				}
			}

			BasicUtils.highlight_unit_off(out, gameState);
		}
		else{
			BasicUtils.highlight_card_off(out, gameState);
			for(Unit unit:gameState.human_unit){
				Position position = unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				if(x!=tilex || y!=tiley){
					continue;
				}
				// get achievable position and highlight as 1
				for(int i = 0; i < 12; i++){
					int xx=x+dx[i];
					int yy=y+dy[i];
					if(xx<0||yy<0||xx>8||yy>4)
						continue;
					if(gameState.board[xx][yy]==0){
						gameState.highlight_board[xx][yy]=1;
					}
				}
				// get attackable position and highlight as 2
				for(int i = 0; i < 8; i++){
					int xx=x+dx_attack[i];
					int yy=y+dy_attack[i];
					if(xx<0||yy<0||xx>8||yy>4)
						continue;
					for(Unit ai_unit : gameState.ai_unit) {
						if(ai_unit.getPosition().getTilex() == xx && ai_unit.getPosition().getTiley() == yy)
							gameState.highlight_board[xx][yy]=2;
					}
				}
				gameState.select = true;
				gameState.select_unit = unit;
				BasicUtils.drawHighlightBord(out, gameState);
				return;
			}
		}
	}

}
