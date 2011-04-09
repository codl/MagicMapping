package net.aquageek.codl.MagicMapping;

//import java.awt.Image;
import java.awt.Color;
import java.io.FileWriter;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.lang.NullPointerException;

import javax.imageio.ImageIO;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MagicMappingRenderer implements Runnable {
	private MagicMapping plugin;
	
	public MagicMappingRenderer(MagicMapping plugin) {
		this.plugin = plugin;
	}
	
	public int slowcounter = 0;
	
	private void drawCube(int x, int y, Graphics2D graphics, double dsize) {
		int size = (int) (dsize*6);
		graphics.fill(new Rectangle(x+5, y+0+6-size, 2, 1));
		graphics.fill(new Rectangle(x+3, y+1+6-size, 6, 1));
		graphics.fill(new Rectangle(x+1, y+2+6-size, 10, 1));
		graphics.fill(new Rectangle(x+0, y+3+6-size, 12, size));
		graphics.fill(new Rectangle(x+1, y+9, 10, 1));
		graphics.fill(new Rectangle(x+3, y+10, 6, 1));
		graphics.fill(new Rectangle(x+5, y+11, 2, 1));
	}
	private void drawShadow(int x, int y, Graphics2D graphics, double dsize) {
		int size = (int) (dsize*6);
		graphics.setPaint(new Color(0,0,0,15));
		graphics.fill(new Rectangle(x+0,  y+3+6-size, 1, size));
		graphics.fill(new Rectangle(x+1,  y+3+6-size, 1, size+1));
		graphics.fill(new Rectangle(x+2,  y+4+6-size, 1, size));
		graphics.fill(new Rectangle(x+3,  y+4+6-size, 1, size+1));
		graphics.fill(new Rectangle(x+4,  y+5+6-size, 1, size));
		graphics.fill(new Rectangle(x+5,  y+5+6-size, 1, size+1));

		graphics.setPaint(new Color(0,0,0,10));
		graphics.fill(new Rectangle(x+7,  y+6+6-size, 1, size-1));
		graphics.fill(new Rectangle(x+8,  y+5+6-size, 1, size));
		graphics.fill(new Rectangle(x+9,  y+5+6-size, 1, size-1));
		graphics.fill(new Rectangle(x+10,  y+4+6-size, 1, size));
		graphics.fill(new Rectangle(x+11, y+4+6-size, 1, size-1));
	}
	private void drawOutline(int x, int y, Graphics2D graphics, double dsize, Block block) {
		int size = (int) (dsize*6);
		boolean continueLeft = true;
		boolean continueRight = true;
		boolean continueTopLeft = true;
		boolean continueTopRight = true;
		boolean continueBotLeft = true;
		boolean continueBotRight = true;
		int i = 0;
		while (
				i < 256 && (
					continueLeft == true ||
					continueRight == true ||
					continueTopLeft == true ||
					continueTopRight == true ||
					continueBotLeft == true ||
					continueBotRight == true
					)
				){
			Block centerblock = block.getRelative(i, -i, -i);
			if (i > 0 && !isEmpty(centerblock.getType())){
				continueLeft = false;
				continueRight = false;
				continueTopLeft = false;
				continueTopRight = false;
				continueBotLeft = false;
				continueTopRight = false;
			}
			if (!isEmpty(centerblock.getRelative(-1, 0, 0).getType())){
				continueLeft = false;
				continueBotLeft = false;
			}
			if (!isEmpty(centerblock.getRelative(-1, -1, 0).getType())){
				continueBotLeft = false;
			}
			if (!isEmpty(centerblock.getRelative(0, -1, 0).getType())){
				continueBotLeft = false;
				continueBotRight = false;
			}
			if (!isEmpty(centerblock.getRelative(0, -1, 1).getType())){
				continueBotRight = false;
			}
			if (!isEmpty(centerblock.getRelative(0, 0, 1).getType())){
				continueBotRight = false;
				continueRight = false;
			}
			if (!isEmpty(centerblock.getRelative(1, 0, 1).getType())){
				continueRight = false;
			}
			if (!isEmpty(centerblock.getRelative(1, 0, 0).getType())){
				continueRight = false;
				continueTopRight = false;
			}
			if (!isEmpty(centerblock.getRelative(1, 1, 0).getType())){
				continueTopRight = false;
			}
			if (!isEmpty(centerblock.getRelative(0, 1, 0).getType())){
				continueTopRight = false;
				continueTopLeft = false;
			}
			if (!isEmpty(centerblock.getRelative(0, 1, -1).getType())){
				continueTopLeft = false;
			}
			if (!isEmpty(centerblock.getRelative(0, 0, -1).getType())){
				continueTopLeft = false;
				continueLeft = false;
			}
			if (!isEmpty(centerblock.getRelative(-1, 0, -1).getType())){
				continueLeft = false;
			}
			
			graphics.setPaint(new Color(0, 0, 0, 15));
			if(continueLeft){
				graphics.fill(new Rectangle(x-1, y+3+6-size, 1, size));
			}
			if(continueRight){
				graphics.fill(new Rectangle(x+12, y+3+6-size, 1, size));
			}
			if(continueTopLeft){
				graphics.fill(new Rectangle(x+0, y+2+6-size, 1, 1));
				graphics.fill(new Rectangle(x+1, y+1+6-size, 2, 1));
				graphics.fill(new Rectangle(x+3, y+0+6-size, 2, 1));
				graphics.fill(new Rectangle(x+5, y-1+6-size, 1, 1));
			}
			if(continueTopRight){
				graphics.fill(new Rectangle(x+6, y-1+6-size, 1, 1));
				graphics.fill(new Rectangle(x+7, y+0+6-size, 2, 1));
				graphics.fill(new Rectangle(x+9, y+1+6-size, 2, 1));
				graphics.fill(new Rectangle(x+11, y+2+6-size, 1, 1));
			}
			if(continueBotLeft){
				graphics.fill(new Rectangle(x+0, y+9, 1, 1));
				graphics.fill(new Rectangle(x+1, y+10, 2, 1));
				graphics.fill(new Rectangle(x+3, y+11, 2, 1));
				graphics.fill(new Rectangle(x+5, y+12, 1, 1));
			}
			if(continueBotRight){
				graphics.fill(new Rectangle(x+6, y+12, 1, 1));
				graphics.fill(new Rectangle(x+7, y+11, 2, 1));
				graphics.fill(new Rectangle(x+9, y+10, 2, 1));
				graphics.fill(new Rectangle(x+11, y+9, 1, 1));
			}
			
			i++;
		}
	}
	private void drawTopFace(int x, int y, Graphics2D graphics, double dsize) {
		int size = (int) (dsize*6);
		graphics.fill(new Rectangle(x+5, y+0+6-size, 2, 1));
		graphics.fill(new Rectangle(x+3, y+1+6-size, 6, 1));
		graphics.fill(new Rectangle(x+1, y+2+6-size, 10, 1));
		graphics.fill(new Rectangle(x+0, y+3+6-size, 12, 1));
		graphics.fill(new Rectangle(x+1, y+4+6-size, 10, 1));
		graphics.fill(new Rectangle(x+3, y+5+6-size, 6, 1));
		graphics.fill(new Rectangle(x+5, y+6+6-size, 2, 1));
	}
	private void drawOre(int x, int y, Graphics2D graphics, int number){
		Random rand = new Random();
		for( int i = 0; i < number; i++) {
			int orex = rand.nextInt(9);
			int orey = (orex-4)*4/10 + rand.nextInt(5) + 4;
			graphics.fill(new Rectangle(x+orex, y+orey, 1+rand.nextInt(1), 1+rand.nextInt(1)));
		}
	}
	private void drawLadder(int x, int y, Graphics2D graphics, byte orientation){
		graphics.setPaint(new Color(150,118,84));
		if(orientation == 3){ // West
			graphics.fill(new Rectangle(x+1, y+4, 1, 7));
			graphics.fill(new Rectangle(x+2, y+5, 1, 1));
			graphics.fill(new Rectangle(x+2, y+8, 1, 1));
			graphics.fill(new Rectangle(x+3, y+4, 1, 1));
			graphics.fill(new Rectangle(x+3, y+7, 1, 1));
			graphics.fill(new Rectangle(x+4, y+3, 1, 6));
		} else if (orientation == 4){ // North
			graphics.fill(new Rectangle(x+7, y+3, 1, 6));
			graphics.fill(new Rectangle(x+8, y+4, 1, 1));
			graphics.fill(new Rectangle(x+8, y+7, 1, 1));
			graphics.fill(new Rectangle(x+9, y+8, 1, 1));
			graphics.fill(new Rectangle(x+9, y+5, 1, 1));
			graphics.fill(new Rectangle(x+10, y+4, 1, 7));
		}
	}
	private void drawStairs(int x, int y, Graphics2D graphics, byte orientation){
		if(orientation == 0){ // South
			graphics.fill(new Rectangle(x+0, y+6, 1, 3));
			graphics.fill(new Rectangle(x+1, y+5, 2, 5));
			graphics.fill(new Rectangle(x+3, y+1, 2, 10));
			graphics.fill(new Rectangle(x+5, y+0, 2, 12));
			graphics.fill(new Rectangle(x+7, y+1, 2, 10));
			graphics.fill(new Rectangle(x+9, y+2, 2, 8));
			graphics.fill(new Rectangle(x+11, y+3, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,15));
			graphics.fill(new Rectangle(x+0, y+6, 1, 3));
			graphics.fill(new Rectangle(x+1, y+7, 2, 3));
			graphics.fill(new Rectangle(x+3, y+2, 2, 3));
			graphics.fill(new Rectangle(x+3, y+8, 2, 3));
			graphics.fill(new Rectangle(x+5, y+3, 2, 3));
			graphics.fill(new Rectangle(x+5, y+9, 1, 3));
			graphics.fill(new Rectangle(x+7, y+4, 2, 3));
			
			graphics.setPaint(new Color(0,0,0,10));
			graphics.fill(new Rectangle(x+6, y+9, 1, 3));
			graphics.fill(new Rectangle(x+7, y+8, 2, 3));
			graphics.fill(new Rectangle(x+9, y+4, 2, 6));
			graphics.fill(new Rectangle(x+11, y+3, 1, 6));
		}
		else if(orientation == 3){ // East
			graphics.fill(new Rectangle(x+11, y+6, 1, 3));
			graphics.fill(new Rectangle(x+9, y+5, 2, 5));
			graphics.fill(new Rectangle(x+7, y+1, 2, 10));
			graphics.fill(new Rectangle(x+5, y+0, 2, 12));
			graphics.fill(new Rectangle(x+3, y+1, 2, 10));
			graphics.fill(new Rectangle(x+1, y+2, 2, 8));
			graphics.fill(new Rectangle(x+0, y+3, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,15));
			graphics.fill(new Rectangle(x+11, y+6, 1, 3));
			graphics.fill(new Rectangle(x+9, y+7, 2, 3));
			graphics.fill(new Rectangle(x+7, y+2, 2, 3));
			graphics.fill(new Rectangle(x+7, y+8, 2, 3));
			graphics.fill(new Rectangle(x+5, y+3, 2, 3));
			graphics.fill(new Rectangle(x+5, y+9, 1, 3));
			graphics.fill(new Rectangle(x+4, y+4, 2, 3));
			
			graphics.setPaint(new Color(0,0,0,10));
			graphics.fill(new Rectangle(x+5, y+9, 1, 3));
			graphics.fill(new Rectangle(x+3, y+8, 2, 3));
			graphics.fill(new Rectangle(x+1, y+4, 2, 6));
			graphics.fill(new Rectangle(x+0, y+3, 1, 6));
		}
		else if (orientation == 2){ // West
			graphics.fill(new Rectangle(x+0, y+6, 1, 3));
			graphics.fill(new Rectangle(x+1, y+5, 2, 5));
			graphics.fill(new Rectangle(x+3, y+4, 2, 7));
			graphics.fill(new Rectangle(x+5, y+3, 2, 9));
			graphics.fill(new Rectangle(x+7, y+2, 2, 9));
			graphics.fill(new Rectangle(x+9, y+2, 2, 8));
			graphics.fill(new Rectangle(x+11, y+3, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,15));
			graphics.fill(new Rectangle(x+0, y+6, 1, 3));
			graphics.fill(new Rectangle(x+1, y+7, 2, 3));
			graphics.fill(new Rectangle(x+3, y+5, 2, 6));
			graphics.fill(new Rectangle(x+5, y+6, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,10));
			graphics.fill(new Rectangle(x+6, y+6, 1, 6));
			graphics.fill(new Rectangle(x+7, y+5, 2, 6));
			graphics.fill(new Rectangle(x+9, y+4, 2, 6));
			graphics.fill(new Rectangle(x+11, y+3, 1, 6));
		}
		else if (orientation == 1){ // North
			graphics.fill(new Rectangle(x+11, y+6, 1, 3));
			graphics.fill(new Rectangle(x+9, y+5, 2, 5));
			graphics.fill(new Rectangle(x+7, y+4, 2, 7));
			graphics.fill(new Rectangle(x+5, y+3, 2, 9));
			graphics.fill(new Rectangle(x+3, y+2, 2, 9));
			graphics.fill(new Rectangle(x+1, y+2, 2, 8));
			graphics.fill(new Rectangle(x+0, y+3, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,15));
			graphics.fill(new Rectangle(x+11, y+6, 1, 3));
			graphics.fill(new Rectangle(x+9, y+7, 2, 3));
			graphics.fill(new Rectangle(x+7, y+5, 2, 6));
			graphics.fill(new Rectangle(x+6, y+6, 1, 6));
			
			graphics.setPaint(new Color(0,0,0,10));
			graphics.fill(new Rectangle(x+5, y+6, 1, 6));
			graphics.fill(new Rectangle(x+3, y+5, 2, 6));
			graphics.fill(new Rectangle(x+1, y+4, 2, 6));
			graphics.fill(new Rectangle(x+0, y+3, 1, 6));
		}
	}
	private void drawTorch(int x, int y, Graphics2D graphics, byte orientation){
		switch(orientation){
		case 5:// Top
			graphics.setPaint(new Color(255, 200, 0));
			graphics.fill(new Rectangle(x+5, y+6, 1, 1));
			graphics.setPaint(new Color(207, 179, 124));
			graphics.fill(new Rectangle(x+5, y+7, 1, 2));
			break;
		case 2: // North
			graphics.setPaint(new Color(255, 200, 0));
			graphics.fill(new Rectangle(x+7, y+2, 1, 1));
			graphics.setPaint(new Color(207, 179, 124));
			graphics.fill(new Rectangle(x+7, y+3, 1, 1));
			graphics.fill(new Rectangle(x+8, y+4, 1, 1));
			break;
		case 3: // West
			graphics.setPaint(new Color(255, 200, 0));
			graphics.fill(new Rectangle(x+3, y+2, 1, 1));
			graphics.setPaint(new Color(207, 179, 124));
			graphics.fill(new Rectangle(x+3, y+3, 1, 1));
			graphics.fill(new Rectangle(x+2, y+4, 1, 1));
			break;
		}
	}
	private void drawDoor(int x, int y, Graphics2D graphics, byte blockdata){
		int orientation = 0;
		int posx = x;
		int posy = y;
		if(blockdata%8 >= 4){
			blockdata++;
		}
		switch(blockdata%4){
		case 0: // North
			orientation = 0; // Oriented N-S
			posy = y+6;
			break;
		case 1: // East
			orientation = 1; // Oriented E-W
			posy = y+3;
			break;
		case 2: // South
			orientation = 0; // Oriented N-S
			posx = x+6;
			posy = y+3;
			break;
		case 3: // West
			orientation = 1; // Oriented E-W
			posx = x+6;
			posy = y+6;
			break;
		}
		if(orientation == 0){
			graphics.draw(new Rectangle(posx+0, posy-9, 1, 12));
			graphics.draw(new Rectangle(posx+1, posy-8, 1, 12));
			graphics.draw(new Rectangle(posx+2, posy-8, 1, 1));
			graphics.draw(new Rectangle(posx+2, posy-3, 1, 7));
			graphics.draw(new Rectangle(posx+3, posy-7, 1, 1));
			graphics.draw(new Rectangle(posx+3, posy-2, 1, 7));
			graphics.draw(new Rectangle(posx+4, posy-7, 1, 12));
			graphics.draw(new Rectangle(posx+5, posy-6, 1, 12));
		} else {
			graphics.draw(new Rectangle(posx+5, posy-9, 1, 12));
			graphics.draw(new Rectangle(posx+4, posy-8, 1, 12));
			graphics.draw(new Rectangle(posx+3, posy-8, 1, 1));
			graphics.draw(new Rectangle(posx+3, posy-3, 1, 7));
			graphics.draw(new Rectangle(posx+2, posy-7, 1, 1));
			graphics.draw(new Rectangle(posx+2, posy-2, 1, 7));
			graphics.draw(new Rectangle(posx+1, posy-7, 1, 12));
			graphics.draw(new Rectangle(posx+0, posy-6, 1, 12));
		}
	}
	private boolean isTransparent(Material mat){
		if (
				isEmpty(mat) ||
				mat == Material.WATER ||
				mat == Material.STATIONARY_WATER ||
				mat == Material.GLASS ||
				mat == Material.LEAVES ||
				mat == Material.COBBLESTONE_STAIRS ||
				mat == Material.WOOD_STAIRS ||
				mat == Material.STEP){
			return true;
		} else { return false; }
	}
	private boolean isEmpty(Material mat){
		if (
				mat == Material.AIR ||
				mat == Material.LADDER ||
				mat == Material.TORCH ||
				mat == Material.BEDROCK //blocks with y>127 or y<0 are bedrock 
				){
			return true;
		} else { return false; }
	}
	private boolean isVisible(Block block) {
		if(
				isTransparent(block.getRelative(-1,0,0).getType()) ||
				isTransparent(block.getRelative(0,1,0).getType()) ||
				isTransparent(block.getRelative(0,0,1).getType()) ||
				isTransparent(block.getRelative(-1,0,1).getType()) ||
				isTransparent(block.getRelative(-1,1,0).getType()) ||
				isTransparent(block.getRelative(0,1,1).getType())
				){
			return true;
		} else { return false; }
	}
	private void render(Chunk chunk) {
		BufferedImage image = new BufferedImage(16*6+16*6+2, 128*6 +6 + 2*3*15, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(33,33,33,0));
		for(int z=0; z<16; z++) {
			for(int x=15; x>=0; x--){
				for(int y=0; y<128; y++){
					///System.out.println(String.valueOf(x)+" "+String.valueOf(y)+" "+String.valueOf(z));
					Block block = chunk.getBlock(x, y, z);
					if (x == 0 || z == 15 || isVisible(block)) {
						Material blockType = block.getType();
						int picx = x*6 + z*6 + 1;
						int picy = (127-y)*6 + 3*15 + z*3 - x*3;
						if(blockType == Material.TORCH){
							byte orientation = block.getData();
							drawTorch(picx, picy, graphics, orientation);
						}
						else if(blockType == Material.LADDER){
							byte orientation = block.getData();
							drawLadder(picx, picy, graphics, orientation);
						}
						else if(blockType == Material.WOODEN_DOOR){
							byte data = block.getData();
							if(data < 8){
								graphics.setPaint(new Color(207, 179, 124));
								drawDoor(picx, picy, graphics, data);
							}
						}
						else if(blockType == Material.IRON_DOOR_BLOCK){
							byte data = block.getData();
							if(data < 8){
								graphics.setPaint(new Color(200,200,200));
								drawDoor(picx, picy, graphics, data);
							}
						}
						else if(blockType == Material.WOOD_STAIRS){
							graphics.setPaint(new Color(207, 179, 124));
							byte orientation = block.getData();
							drawStairs(picx, picy, graphics, orientation);
						}
						else if(blockType == Material.COBBLESTONE_STAIRS){
							graphics.setPaint(new Color(120,120,120));
							byte orientation = block.getData();
							drawStairs(picx, picy, graphics, orientation);
						}
						else if(blockType == Material.WATER || blockType == Material.STATIONARY_WATER){
							graphics.setPaint(new Color(45, 45, 230, 70)); //TODO add sloped water and lava
							drawCube( picx, picy, graphics, 1);
						}
						else if(blockType == Material.LAVA || blockType == Material.STATIONARY_LAVA){
							drawOutline( picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(255,100,0));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(255,255,0, 50));
							drawOre(picx, picy, graphics, 30);
						}
						else if(blockType == Material.WOOL){
							drawOutline( picx, picy, graphics, 1, block);
							byte color = block.getData();
							switch (color) {
							case 1:
								graphics.setPaint(new Color(233,121,44));
								break;
							case 2:
								graphics.setPaint(new Color(187,121,198));
								break;
							case 3:
								graphics.setPaint(new Color(57,111,225));
								break;
							case 4:
								graphics.setPaint(new Color(214,220,35));
								break;
							case 5:
								graphics.setPaint(new Color(54,178,43));
								break;
							case 6:
								graphics.setPaint(new Color(205,90,121));
								break;
							case 7:
								graphics.setPaint(new Color(60,60,60));
								break;
							case 8:
								graphics.setPaint(new Color(160,160,160));
								break;
							case 9:
								graphics.setPaint(new Color(32,139,183));
								break;
							case 10:
								graphics.setPaint(new Color(119,46,183));
								break;
							case 11:
								graphics.setPaint(new Color(37,56,208));
								break;
							case 12:
								graphics.setPaint(new Color(83,34,0));
								break;
							case 13:
								graphics.setPaint(new Color(36,60,0));
								break;
							case 14:
								graphics.setPaint(new Color(135,0,0));
								break;
							case 15:
								graphics.setPaint(new Color(10,10,10));
								break;
							default:
								graphics.setPaint(new Color(255,255,255));
							}
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.SNOW){
							drawOutline( picx, picy, graphics, 0.2, block);
							graphics.setPaint(new Color(255,255,255));
							drawCube( picx, picy, graphics, 0.2);
							drawShadow(picx, picy, graphics, 0.2);
						}
						else if(blockType == Material.CACTUS){
							drawOutline( picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(31,177,27));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
							graphics.setPaint(new Color(30,58,29));
							drawOre(picx, picy, graphics, 1);
						}
						else if(blockType == Material.SNOW_BLOCK){
							drawOutline( picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(255,255,255));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.ICE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(150,255,255, 50));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.OBSIDIAN){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(30,10,30));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.DIAMOND_BLOCK){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100,255,255));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.IRON_BLOCK){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(200,200,200));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.GOLD_BLOCK){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(255,230,46));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.DIAMOND_ORE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(100,255,255));
							drawOre(picx, picy, graphics, 5);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.IRON_ORE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(200,200,100));
							drawOre(picx, picy, graphics, 8);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.COAL_ORE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(30,30,30));
							drawOre(picx, picy, graphics, 10);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.REDSTONE_ORE || blockType == Material.GLOWING_REDSTONE_ORE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(200,0,0));
							drawOre(picx, picy, graphics, 8);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.GOLD_ORE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(235,211,46));
							drawOre(picx, picy, graphics, 5);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.TNT){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(236,33,17));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.GLASS){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(255,255,255, 30));
							drawCube(picx, picy, graphics, 1);
						}
						else if(blockType == Material.WOOD){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(207, 179, 124));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.CLAY){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(197,189,160));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.BRICK){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(125,0,0));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.LEAVES){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(20, 130, 18));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.LOG){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(120, 70, 0));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.STONE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.STEP){
							drawOutline(picx, picy, graphics, 0.5, block);
							
							switch (block.getData()){
							case 0: //Stone
								graphics.setPaint(new Color(100, 100, 100));
								break;
							case 1: //Sandstone
								graphics.setPaint(new Color(250, 250, 100));
								break;
							case 2: //Wood
								graphics.setPaint(new Color(207, 179, 124));
								break;
							case 3: //Cobblestone
								graphics.setPaint(new Color(120,120,120));
								break;
							}
							
							drawCube(picx, picy, graphics, 0.5);
							drawShadow(picx, picy, graphics, 0.5);
						}
						else if(blockType == Material.DOUBLE_STEP){
							drawOutline(picx, picy, graphics, 1, block);
							
							switch (block.getData()){
							case 0: //Stone
								graphics.setPaint(new Color(100, 100, 100));
								break;
							case 1: //Sandstone
								graphics.setPaint(new Color(250, 250, 100));
								break;
							case 2: //Wood
								graphics.setPaint(new Color(207, 179, 124));
								break;
							case 3: //Cobblestone
								graphics.setPaint(new Color(120,120,120));
								break;
							}
							
							graphics.setPaint(new Color(100, 100, 100));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 0.5);
							drawShadow(picx, picy-3, graphics, 0.5);
						}
						else if(blockType == Material.COBBLESTONE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(120, 120, 120));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.GRAVEL){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(120, 110, 110));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.SAND || blockType == Material.SANDSTONE){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(250, 250, 100));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.BEDROCK){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(50, 50, 50));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.DIRT){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(133, 100, 62));
							drawCube(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						else if(blockType == Material.GRASS){
							drawOutline(picx, picy, graphics, 1, block);
							graphics.setPaint(new Color(133, 100, 62));
							drawCube(picx, picy, graphics, 1);
							graphics.setPaint(new Color(80, 229, 34));
							drawTopFace(picx, picy, graphics, 1);
							drawShadow(picx, picy, graphics, 1);
						}
						/*else if (blockType == Material.AIR){
							// Do nothing
						}
						else {
							System.out.println("MagicMapping: "+String.valueOf(blockType.getId())+" : BlockType unknown");
						}*/
					}
				}
			}
			try {
	            Thread.sleep(0, 1);
			} catch (InterruptedException e) {
	    	}
		}
		try {
			File out = new File(plugin.output+File.separator+"chunks"+File.separator+String.valueOf(chunk.getX())+ "_" + String.valueOf(chunk.getZ())+ ".png");
			ImageIO.write(image, "png", out);
			FileWriter recent = new FileWriter(plugin.output+File.separator+"recent", true);
			recent.write(String.valueOf(new Date().getTime())+" "+String.valueOf(chunk.getX())+ " " + String.valueOf(chunk.getZ())+"\n");
			recent.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	LinkedList<int[]> queue = new LinkedList<int[]>();
	LinkedList<int[]> slowQueue = new LinkedList<int[]>();
	
	@SuppressWarnings("unchecked")
	public boolean isIn (LinkedList<int[]> list, int[] item){
		list = (LinkedList<int[]>) list.clone();
		for(int i = list.size()-1; i>=0; i--){
			int[] listitem = list.get(i);
			if(listitem[0] == item[0] && listitem[1] == item[1]){
				return true;
			}
		}
		return false;
	}

	public boolean enqueue(int x, int z){
		int[] pos = {x, z};
		if(!isIn(queue, pos)){
			queue.add(pos);

			slowcounter = 0;
			return true;
		} else {return false;}
	}
	public boolean slowEnqueue(int x, int z){
		int[] pos = {x, z};
		if(!isIn(queue, pos) && !isIn(slowQueue, pos)){
			slowQueue.add(pos);
			slowcounter = 0;
			return true;
		} else {return false;}
	}
	@Override
	public void run() {
		while(true){
			try {
				while(!queue.isEmpty()){
					try {
			            Thread.sleep(1000);
			        } catch (InterruptedException e) {
			        }
					int[] pos = queue.poll();
					try {
						slowcounter = 0;
						Chunk chunk = this.plugin.world.getChunkAt(pos[0], pos[1]);
						render(chunk);
					} catch (NullPointerException e) {
					}
					}
				try {
		            Thread.sleep(1000);
		        } catch (InterruptedException e) {
		        }
		        slowcounter++;
			    if(!slowQueue.isEmpty() && slowcounter > 30){
			        try {
			        	slowcounter=0;
						int[] pos = slowQueue.removeLast();
						Chunk chunk = this.plugin.world.getChunkAt(pos[0], pos[1]);
						render(chunk);
					} catch (NullPointerException e) {
					}
		        }
			} catch (ConcurrentModificationException e){}
		}
	}
}
