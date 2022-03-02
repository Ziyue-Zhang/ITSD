package events;


import javax.xml.stream.EventFilter;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.BasicUtils;
import utils.StaticConfFiles;

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
			int highlight_card_index = gameState.getHighlightCard();
			Card highlight_card = gameState.getHumanCard(highlight_card_index);

			/*
			** spell logic start
			*/
			if(highlight_card.getCardname().equals("Truestrike")){

				if(gameState.highlight_board[tilex][tiley] == 2){
					// find this enemy
					Unit enemy = null;
					for(Unit ai_unit : gameState.ai_unit) {
						if(ai_unit.getPosition().getTilex() == tilex && ai_unit.getPosition().getTiley() == tiley) {
							enemy = ai_unit;
						}
					}

					BasicUtils.highlight_unit_off(out, gameState);
					BasicUtils.highlight_card_off(out, gameState);

					// clear the card 
					BasicCommands.deleteCard(out, highlight_card_index);
					gameState.setHumanCard(highlight_card_index, null);
					
					Card[] cards_left = new Card[7];
					int slot = 1;
					for(int i = 1 ; i < 7 ; i++) {
						if(gameState.getHumanCard(i) != null) {
							cards_left[slot] = gameState.getHumanCard(i);
							BasicCommands.deleteCard(out, i);
							slot++;
						}
					}
					gameState.human_card = cards_left;

					for(int i = 1 ; i < 7 ; i++) {
						if(cards_left[i] != null) {
							BasicCommands.drawCard(out, gameState.getHumanCard(i), i, 0);
						}
					}

					// damage this enemy
					BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.hit);
					EffectAnimation spell_effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
					Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
					BasicCommands.drawTile(out, tile, 0);
					BasicCommands.playEffectAnimation(out, spell_effect, tile);

					if(enemy.getHealth() > 2) {
						// not die
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.idle);
						enemy.setHealth(enemy.getHealth() - 2); 
						BasicCommands.setUnitHealth(out, enemy, enemy.getHealth());

						if(enemy.getId() == gameState.ai_boss_id) {
							gameState.aiPlayer.setHealth(enemy.getHealth());
							BasicCommands.setPlayer2Health(out, gameState.aiPlayer);
						}

					}else {
						// die
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.death);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.deleteUnit(out, enemy);
						gameState.board[enemy.getPosition().getTilex()][enemy.getPosition().getTiley()] = 0;
						gameState.ai_unit.remove(enemy);

						if(enemy.getId() == gameState.ai_boss_id) {
							gameState.aiPlayer.setHealth(0);
							BasicCommands.setPlayer2Health(out, gameState.aiPlayer);
							BasicCommands.addPlayer1Notification(out, "You win this game!", 2);
							gameState.gameEnd = true;
							gameState.humanWin = true;
						}
					}

					//Pureblade Enforcer can gain from spell
					for(Unit humaUnit : gameState.human_unit) {
						if(humaUnit.getId() == 7) {
							humaUnit.setAttack(humaUnit.getAttack() + 1);
							humaUnit.setHealth(humaUnit.getHealth() + 1);
							if(humaUnit.getHealth() > humaUnit.max_health)
								humaUnit.max_health = humaUnit.getHealth();

							BasicCommands.setUnitAttack(out, humaUnit, humaUnit.getAttack());
							BasicCommands.setUnitHealth(out, humaUnit, humaUnit.getHealth());
						}
					}


				}else {
					BasicUtils.highlight_card_off(out, gameState);
					BasicUtils.highlight_unit_off(out, gameState);
					return;
				}

				return;
			}else if(highlight_card.getCardname().equals("Sundrop Elixir")){
				// find this unit
				if(gameState.highlight_board[tilex][tiley] == 1){
					Unit me = null;
					for(Unit human_unit : gameState.human_unit) {
						if(human_unit.getPosition().getTilex() == tilex && human_unit.getPosition().getTiley() == tiley) {
							me = human_unit;
						}
					}

					BasicUtils.highlight_unit_off(out, gameState);
					BasicUtils.highlight_card_off(out, gameState);

					// clear the card 
					BasicCommands.deleteCard(out, highlight_card_index);
					gameState.setHumanCard(highlight_card_index, null);
					
					Card[] cards_left = new Card[7];
					int slot = 1;
					for(int i = 1 ; i < 7 ; i++) {
						if(gameState.getHumanCard(i) != null) {
							cards_left[slot] = gameState.getHumanCard(i);
							BasicCommands.deleteCard(out, i);
							slot++;
						}
					}
					gameState.human_card = cards_left;

					for(int i = 1 ; i < 7 ; i++) {
						if(cards_left[i] != null) {
							BasicCommands.drawCard(out, gameState.getHumanCard(i), i, 0);
						}
					}

					// heal this unit
					BasicCommands.playUnitAnimation(out, me, UnitAnimationType.move);
					EffectAnimation spell_effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
					Tile tile = BasicObjectBuilders.loadTile(tilex, tiley);
					BasicCommands.drawTile(out, tile, 0);
					BasicCommands.playEffectAnimation(out, spell_effect, tile);

					if(me.getHealth() + 5 <= me.max_health) {
						// not overflow
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.playUnitAnimation(out, me, UnitAnimationType.idle);
						me.setHealth(me.getHealth() + 5); 
						BasicCommands.setUnitHealth(out, me, me.getHealth());

						if(me.getId() == gameState.human_boss_id) {
							gameState.humanPlayer.setHealth(me.getHealth());
							BasicCommands.setPlayer1Health(out, gameState.humanPlayer);
						}

					}else {
						// overflow
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						BasicCommands.playUnitAnimation(out, me, UnitAnimationType.idle);
						me.setHealth(me.max_health); 
						BasicCommands.setUnitHealth(out, me, me.getHealth());

						if(me.getId() == gameState.human_boss_id) {
							gameState.humanPlayer.setHealth(me.getHealth());
							BasicCommands.setPlayer1Health(out, gameState.humanPlayer);
						}
					}

					//Pureblade Enforcer can gain from spell
					for(Unit humaUnit : gameState.human_unit) {
						if(humaUnit.getId() == 7) {
							humaUnit.setAttack(humaUnit.getAttack() + 1);
							humaUnit.setHealth(humaUnit.getHealth() + 1);
							if(humaUnit.getHealth() > humaUnit.max_health)
								humaUnit.max_health = humaUnit.getHealth();

							BasicCommands.setUnitAttack(out, humaUnit, humaUnit.getAttack());
							BasicCommands.setUnitHealth(out, humaUnit, humaUnit.getHealth());
						}
					}

			}else {
				BasicUtils.highlight_card_off(out, gameState);
				BasicUtils.highlight_unit_off(out, gameState);
				return;
			}
				return;
			}

			/*
			** spell logic end 
			*/

			BasicUtils.highlight_unit_off(out, gameState);

			for(Unit unit:gameState.human_unit){
				Position position = unit.getPosition();
				int x = position.getTilex();
				int y = position.getTiley();
				if(tilex==x&&tiley==y){
					break;
				}
				int loc = gameState.getHighlightCard();
				Card card = gameState.getHumanCard(loc);
				if((Math.abs(tilex-x)<=1 && Math.abs(tiley-y)<=1) || card.getCardname().equals("Ironcliff Guardian")){
					// play a card 
					BasicUtils.highlight_card_off(out, gameState);
					int m = gameState.humanPlayer.getMana() - card.getManacost();
					

					//drawUnit
					Tile tile1 = BasicObjectBuilders.loadTile(tilex,tiley);
					Unit unit1 = gameState.return_Unit(card.getCardname());

					unit1.setPositionByTile(tile1);

					unit1.setAttack(card.getBigCard().getAttack());
					unit1.setHealth(card.getBigCard().getHealth());
					unit1.max_health = unit1.getHealth();
		
					gameState.human_unit.add(unit1);
					gameState.board[tilex][tiley] = 1;

					EffectAnimation effect = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
					BasicCommands.drawUnit(out, unit1, tile1);
					BasicCommands.playEffectAnimation(out, effect, tile1);
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

					//Azure Herald
					if(unit1.getId() == 2) {
						for(Unit human_unit : gameState.human_unit) {
							if(human_unit.getId() == 0) {
								int health = human_unit.getHealth();
								health = ((health + 3) > 20 ? 20 : health + 3);
								human_unit.setHealth(health);
								gameState.humanPlayer.setHealth(health);
								BasicCommands.setPlayer1Health(out, gameState.humanPlayer);
								BasicCommands.setUnitHealth(out, human_unit, health);
							}
						}
					}

					Card[] cards_left = new Card[7];
					int slot = 1;
					for(int i = 1 ; i < 7 ; i++) {
						if(gameState.getHumanCard(i) != null) {
							cards_left[slot] = gameState.getHumanCard(i);
							BasicCommands.deleteCard(out, i);
							slot++;
						}
					}
					gameState.human_card = cards_left;

					for(int i = 1 ; i < 7 ; i++) {
						if(cards_left[i] != null) {
							BasicCommands.drawCard(out, gameState.getHumanCard(i), i, 0);
						}
					}
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
				gameState.select_unit.round_moveable = false;

				int attack_num = gameState.select_unit.getId() == 3 ? 2 : 1;

				for(int attack_round = 0 ; attack_round < attack_num ; attack_round++) {
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
						if(enemy.getId() == gameState.ai_boss_id) {
							gameState.getAiPlayer().setHealth(enemy.getHealth());
							BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
						}
						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
	
						// 3.3 the enemy fights back only if the enemy can attack 
						if(Math.abs(me.getPosition().getTilex() - enemy.getPosition().getTilex()) <= 1 && Math.abs(me.getPosition().getTiley() - enemy.getPosition().getTiley()) <= 1) {
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
								if(me.getId() == gameState.human_boss_id) {
									gameState.getHumanPlayer().setHealth(me.getHealth());
									BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());

									// Silverguard Knight will benefit when boss is under attack
									for(Unit humaUnit : gameState.human_unit) {
										if(humaUnit.getId() == 8) {
											humaUnit.setAttack(humaUnit.getAttack() + 1);
				
											BasicCommands.setUnitAttack(out, humaUnit, humaUnit.getAttack());
										}
									}
								}
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
								if(me.getId() == gameState.human_boss_id) {
									BasicCommands.addPlayer1Notification(out, "AI wins this game!", 2);
									gameState.gameEnd = true;
									gameState.aiWin = true;
								}
							}
						}else{
							BasicCommands.playUnitAnimation(out, me, UnitAnimationType.idle);
							BasicCommands.playUnitAnimation(out, enemy, UnitAnimationType.idle);
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
						if(enemy.getId() == gameState.ai_boss_id) {
							gameState.getAiPlayer().setHealth(0);
							BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
							BasicCommands.addPlayer1Notification(out, "You win this game!", 2);
							gameState.gameEnd = true;
							gameState.humanWin = true;
						}
					}
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
				if(unit.getId() == 4) {
					//Fire Spitter
					for(Unit ai_unit : gameState.ai_unit) {
							gameState.highlight_board[ai_unit.getPosition().getTilex()][ai_unit.getPosition().getTiley()]=2;
					}
				} else {
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
				}
				gameState.select = true;
				gameState.select_unit = unit;
				BasicUtils.drawHighlightBord(out, gameState);
				return;
			}
		}
	}

}
