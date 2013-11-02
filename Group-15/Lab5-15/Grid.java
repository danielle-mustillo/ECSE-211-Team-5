import lejos.nxt.comm.RConsole;


public class Grid {
	
	private final int FIELD_WIDTH = 4; 
	private final int FIELD_LENGTH = 8;
	private final int TILE_SIZE = 30;
	
	/*
	 * Tiles array
	 * indexed width,height
	 * eg
	 * 
	 * 0,1 ; 1,1 ; 2,1 ; 3;1
	 * 0,0 ; 1,0 ; 2,0 ; 3;0
	 * 
	 * such that (15,15) would be in title 1,1 
	 * 
	 * CODES:
	 *  0 = empty
	 *  1 = fully blocked
	 *  2 = left 1/2 blocked (width direction)
	 *  3 = right 1/2 blocked (width direction)
	 *  4 = left 1/2 clear (width direction)
	 *  5 = right 1/2 clear (width direction)
	 *  6 = Unknown
	 *  7 = inspection required
	 *  8 = inspect object in tile
	 * 
	 */
	private short[][] tiles = new short[FIELD_WIDTH][FIELD_LENGTH];
	
	public Grid() {
		initalize();
	}
	
	public void initalize() {
		for(int i=0; i<tiles.length; i++) {
			for(int j=0; j<tiles[0].length; j++) {
				if(i < 3 && j < 3) {
					//inital nine tiles are clear of any objects
					tiles[i][j] = 0;
				} else {
					//tile is unknown to us
					tiles[i][j] = 6;
				}
			}
		}
	}
	
	public void setTile(int w, int l, short state) {
		RConsole.println("w;l" + String.valueOf(w) +";" + String.valueOf(l));
		tiles[w][l] = state;
	}
	
	public short getTitle(int w, int l) {
		return tiles[w][l];
	}
	
	public void setTilesScan(int start, int end, int scanPos, int distance, int heading) {
		
		int startTile = getTileIndexWidth(start);
		int endTile = getTileIndexWidth(end);
		
		RConsole.println( "start;end " + String.valueOf(startTile) + ";" + String.valueOf(endTile) );
		
		if(distance == 255) {
			RConsole.println("255");
			for(int i = startTile; i<=endTile; i++) {
				for(int j = getTileIndexLength(scanPos)+1; j<tiles[0].length; j++) {
					if(tiles[i][j] != 1) { 
						tiles[i][j] = 7;
					}
				}
				
			}
			RConsole.println("/255");
		} else {
		
			int freeTiles = ( (distance - ((scanPos+5) % TILE_SIZE)) / TILE_SIZE ) + 1;
			
			if(freeTiles + (scanPos / TILE_SIZE) > FIELD_LENGTH ) {
				freeTiles = FIELD_LENGTH - (scanPos / TILE_SIZE);
			}
			
			boolean startTileHalf;
			
			if(start > 0) {
				startTileHalf= (start % TILE_SIZE > 15) ? true : false;
			} else {
				startTileHalf = ((start+30) % TILE_SIZE > 15) ? true : false;
			}
			boolean endTileHalf = (end % TILE_SIZE < 15) ? true : false;
			int offset = (int) Math.floor(scanPos / TILE_SIZE) + 1;
			
			
			RConsole.println( "free tiles " + String.valueOf(freeTiles) + "; offset " + String.valueOf(offset) );
			RConsole.println( "distance to wall " + String.valueOf(distanceToWall(start, scanPos, heading)) );
		
			for(int j = offset; j<freeTiles+offset; j++) {
				for(int i = startTile; i<=endTile; i++) {
					RConsole.println(String.valueOf(i) + "," + String.valueOf(j));
					if (startTileHalf && i == startTile ) {
						if(tiles[i][j] != 2) {
							tiles[i][j] = 0;
						} 
					} else if(endTileHalf && i == endTile) {
						if(tiles[i][j] != 3) {
							tiles[i][j] = 0;
						} 
					} else {
						tiles[i][j] = 0;
					}
				}
			}
			
			if( Math.abs(distance - distanceToWall(start, scanPos, heading)) > 10) {
				//length is too long for styrofoam
				boolean blocked = (Math.abs(start-end) > 40) ? true : false;
				int j = getTileIndexLength(scanPos+distance);
				for(int i = startTile; i<=endTile; i++) {
					if(blocked) {
						if(startTileHalf && i == startTile) {
							if(tiles[i][j] == 2 || tiles[i][j] == 1) {
								tiles[i][j] = 1;
							} else {
								tiles[i][j] = 3;
							}
						} else if(endTileHalf && i == endTile) {
							if(tiles[i][j] == 3 || tiles[i][j] == 1) {
								tiles[i][j] = 1;
							} else {
								tiles[i][j] = 2;
							}
						} else {
							//block the tile
							tiles[i][j] = 1;
						}
					} else {
						//set it to inspect
						tiles[i][j] = 8;
					}
					
					
				}
			}
		}
		
	}
	
	public int distanceToWall(int x, int y, int heading) {
		if(Math.sin(Math.toRadians(heading)) > 0.95) {
			return (int) ( (FIELD_LENGTH - 1) * TILE_SIZE - y );
		} else {
			return (int) ( (FIELD_WIDTH - 1) * TILE_SIZE - x );
		}
	}
	
	public int getTileIndexWidth(int x) {
			
		return  (int) Math.floor( (double) x / (double) TILE_SIZE) + 1;
	}
	
	public int getTileIndexLength(int y) {
		return (int) Math.floor( (double) y / (double) TILE_SIZE) + 1;
	}
	
	public String printGrid() {
		String result = "";
		for(int i=0; i<tiles.length; i++) {
			for(int j=0; j<tiles[0].length; j++) {
				result += String.valueOf(tiles[i][j] + ",");
			}
			result += "\n";
		}
		
		return result;
	}
	
	public boolean contains(short value) {
		for(int i=0; i<tiles.length; i++) {
			for(int j=0; j<tiles[0].length; j++) {
				if(tiles[i][j] == value) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean getClosestTile(int[] data, short value) {
				
		data[0] = getTileIndexWidth(data[0]);
		data[1] = getTileIndexWidth(data[1]);
		
		double distance = 1000;
		int w = -1;
		int l = -1;
		
		for(int i=0; i<tiles.length; i++) {
			for(int j=0; j<tiles[0].length; j++) {
				if(tiles[i][j] == value) {
					if ( Math.pow( (i - data[0]) , 2) + Math.pow( (i - data[0]) , 2) < distance) {
						w = i;
						l = j;
						distance = Math.pow( (i - data[0]) , 2) + Math.pow( (i - data[0]) , 2);
					}
				}
			}
		}
		
		if(w == -1) {
			return false;
		} else {
			
			data[0] = w;
			data[1] = l;
			
			return true;
		}
		
	}
	
	public boolean getClosestTile2(int[] data, short value) {
		
		data[0] = getTileIndexWidth(data[0]);
		data[1] = getTileIndexWidth(data[1]);
		
		double distance = 1000;
		int l = -1;
		int w = -1;
		
		for(int i=0; i<tiles.length; i++) {
			for(int j=3; j<tiles[0].length; j++) {
				if(tiles[i][j] == value) {
					if ( j < distance) {
						l = j;
						w = i;
						distance = j;
					}
				}
			}
		}
		
		if(l == -1) {
			return false;
		} else {
			
			data[0] = w;
			data[1] = l;
			
			return true;
		}
		
	}
	
	
	
	public int getX(int i) {
		if(i==0) {
			return i*30-10;
		} else if (i == FIELD_WIDTH - 1) {
			return i*30-20;
		}
		return i*30-15;
	}
	
	public int getY(int j) {
		return j*30-15;
	}
	

}
