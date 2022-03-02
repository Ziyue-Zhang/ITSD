package structures;

import java.util.ArrayList;
import java.util.List;

import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;
	public boolean select_card = false;
	public boolean select = false;

	public Unit select_unit = null;

	public Player humanPlayer;
	public Player aiPlayer;

	public int[][] board = new int[9][5];
	public int[][] highlight_board = new int[9][5];
	public int turn_number;

	public List<Unit> human_unit = new ArrayList<Unit>();
	public List<Unit> ai_unit = new ArrayList<Unit>();

	public Card[] human_card = new Card[7];
	public int[] highlight_card = new int[7];

	public int deck1_index = 0;

	public int human_boss_id = 0;
	public int ai_boss_id = 0;

	public boolean gameEnd = false;
	public boolean aiWin = false;
	public boolean humanWin = false;

	public String[] deck1Cards = {
		StaticConfFiles.c_azure_herald,
		StaticConfFiles.c_azurite_lion,
		StaticConfFiles.c_comodo_charger,
		StaticConfFiles.c_fire_spitter,
		StaticConfFiles.c_hailstone_golem,
		StaticConfFiles.c_ironcliff_guardian,
		StaticConfFiles.c_pureblade_enforcer,
		StaticConfFiles.c_silverguard_knight,
		StaticConfFiles.c_sundrop_elixir,
		StaticConfFiles.c_truestrike
	};

	public String[] deck2Cards = {
		StaticConfFiles.c_blaze_hound,
		StaticConfFiles.c_bloodshard_golem,
		StaticConfFiles.c_entropic_decay,
		StaticConfFiles.c_hailstone_golem,
		StaticConfFiles.c_planar_scout,
		StaticConfFiles.c_pyromancer,
		StaticConfFiles.c_serpenti,
		StaticConfFiles.c_rock_pulveriser,
		StaticConfFiles.c_staff_of_ykir,
		StaticConfFiles.c_windshrike,
	};

	public Player getHumanPlayer() {return humanPlayer;}
	public Player getAiPlayer() {return aiPlayer;}

	public void setHumanCard(int loc, Card card) {human_card[loc]=card;}
	public void setHighlightCard(int loc, int mode) {highlight_card[loc]=mode;}

	public Card getHumanCard(int loc) {return human_card[loc];}
	public int getFreeCard(){
		for(int i = 1; i < 7; i++){
			if(human_card[i] == null)
				return i;
		}
		return 0;
	}
	public int getHighlightCard() {
		for(int i = 1; i < 7; i++){
			if(highlight_card[i] == 1)
				return i;
		}
		return 0;
	}
	
	public Unit return_Unit(String name){
		switch (name) {
			case "humanAvatar" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
			case "Comodo Charger" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger, 1, Unit.class);
			case "Azure Herald" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 2, Unit.class);
			case "Azurite Lion" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, 3, Unit.class);
			case "Fire Spitter" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter, 4, Unit.class);
			case "Hailstone Golem" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, 5, Unit.class);
			case "Ironcliff Guardian" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, 6, Unit.class);
			case "Pureblade Enforcer" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer, 7, Unit.class);
			case "Silverguard Knight" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 8, Unit.class);
			default :
		}
		return null;
	}


	public Unit return_enemy_unit(String name){
		switch (name) {
			case "aiAvatar" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 9, Unit.class);
			case "Blaze Hound" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_blaze_hound, 10, Unit.class);
			case "Bloodshard Golem" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_bloodshard_golem, 11, Unit.class);
			case "Hailstone GolemR" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golemR, 12, Unit.class);
			case "Planar Scout" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_planar_scout, 13, Unit.class);
			case "Pyromancer" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, 14, Unit.class);
			case "Rock Pulveriser" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_rock_pulveriser, 15, Unit.class);
			case "Serpenti" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, 16, Unit.class);
			case "Windshrike" : 
				return BasicObjectBuilders.loadUnit(StaticConfFiles.u_windshrike, 17, Unit.class);
			default :
		}
		return null;
	}

	public GameState(){
		humanPlayer = new Player(20, 2);
		aiPlayer = new Player(20, 2);
		turn_number = 1;

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 5; j++){
				board[i][j]=0;
			}
		}

		for(int i = 0; i < 7; i++){
			human_card[i] = null;
			highlight_card[i] = 0;
		}
	}

}
