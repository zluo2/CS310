/*************************************************************************
 *  Compilation:  javac Dungeon.java
 *
 *************************************************************************/

import java.util.*;
import java.text.SimpleDateFormat;

public class Dungeon 
{

    private class Room 
    {

      public int W,H; //width and height of the Room
      public Site UL; //upper left corner of the Room

      public boolean isInside(Site s)
      {
        return (s.i()>=UL.i() && s.i()<UL.i()+W && s.j()>=UL.j() && s.j()<UL.j()+H);
      }

      //check if the room "other" overlap with this room
      public boolean overlap (Room other)
      {
        if(UL.i()>other.UL.i()+other.W) return false;
        if(UL.i()+W<other.UL.i())  return false;

        if(UL.j()>other.UL.j()+other.H) return false;
        if(UL.j()+H<other.UL.j()) return false;     
        
        return true;
      }
    }

    private class Corridor 
    {
      public int room1 =0;
      public int room2 =0;
      
      Corridor()
      { 
    	  sites=new LinkedList<Site>(); 
      }
      
      LinkedList<Site> sites;
    }

    private final static String NEWLINE = System.getProperty("line.separator");

    private int N;                         // dimension of dungeon
    private int R;                         // number of rooms
    private int CC;                        // number of connected components, this number is R initially
    private Site monsterSite, rogueSite;   // location of monster and rogue, must be in room
    private char MONSTER = 'M';            // name of the monster (A - Z)
    private char ROGUE = '@';              // name of the rogue

    //your task is to generate the following lists
    private Vector<Room> rooms;
    private LinkedList<Corridor> corridors;

    private Random rng;
    
    //union-find data structure
    private DisjointSets connectedRoom;

    // initialize a new empty dungeon based on the given size
    public Dungeon(int dimension, int room_size) {
        N = dimension;
        R = room_size;
        CC= R;
        rng = new Random(); //you can seed it to create the same dungeon every time
        rooms = new Vector<Room>();
        corridors = new LinkedList<Corridor>();
        
        connectedRoom = new DisjointSets(room_size);
        //create a random dungeon here!
        CreateDisjointRooms();
      
        //connect rooms randomly
        while(CC > 1)
        {
        	
          // R room size（room number)
          int r1=(int)Math.floor(R*rng.nextDouble());  //  from 0 to R
          int r2=(int)Math.floor(R*rng.nextDouble());  // from 0 to R
          if(r1==r2) continue; //same room
          
          int root1 = connectedRoom.find(r1);
          int root2 = connectedRoom.find(r2);
          //get a corridor
          Corridor c=CreateCorridor(root1, root2);
          if(c==null) continue; //not a valid corridor
          
          CC=JointRooms(c);// c != null then CC-1
          corridors.add(c); //remember this corridor
        }

        //get Random Monster and Rogue location
        rogueSite  =GetARandomRoomSite();
        monsterSite=GetARandomRoomSite();
        while(rogueSite.equals(monsterSite))
        {
          rogueSite=GetARandomRoomSite();
        }

    }//end Dungeon()

    // return dimension of dungeon
    public int size() { return N; }
    
    //Finished!!!
    //Task #1: create R rooms and make sure that all rooms are disjoint
    private void CreateDisjointRooms()
    {
    
      int num = 0;
      while(num < R)
      {
        //create a room that is disjoint from all the rooms created so far
        Room room = new Room();
        room.W=Math.max(1,(int)Math.floor(N/2*rng.nextDouble())); // from 1 to N/2
        room.H=Math.max(1,(int)Math.floor(N/2*rng.nextDouble())); // from 1 to N/2
        //Special situation: when R = W = H == 1;
        if(R==1&&room.W==1&&room.H==1)
        {
        	room.W=2;
        	room.H=2;
        }
        
        int x=(int)Math.floor(N*rng.nextDouble()); //UL -x 
        int y=(int)Math.floor(N*rng.nextDouble()); //UL -y
        room.UL=new Site(x,y);
        
        //Check if the random room out of boundary 
        boolean outOfBounary = false;
        if((room.UL.i()+room.W > this.size())||(room.UL.j()+room.H > this.size()))
        		outOfBounary = true;
 
        boolean overlap = false;
        //TODO: check if this random room overlaps with the rooms created
        for(Room temp_room : rooms)
        {
        	if (room.overlap(temp_room))
        	{
        		overlap = true;
        		break;
        	}
        }
        //add room to rooms
        if (!overlap&&!outOfBounary){
        	this.rooms.add(room);
        	num++;
        }
        
      }
    }
    
    //Finished!!!
    //Task #2: return the corridor if r1 and r2 are not already connected
    //otherwise return null
    private Corridor CreateCorridor(int r1, int r2)
    {
    	Corridor corridor = new Corridor();
    	//there isn't the room 
    	if (rooms.size() == 0){
			System.out.println("Error: there are no rooms");
			return null;
		}
    	//create a corridor connecting the center of room r1 and the center of room r2
    	if ((r1 != r2)&&(r1<rooms.size())&&(r2<rooms.size()))
    	{
    		//create the corridor
	    	corridor.sites.addAll(buildCorridor(rooms.elementAt(r1),rooms.elementAt(r2)));
	    	corridor.room1 = r1;
	    	corridor.room2 = r2;
    	}
    	return corridor;
    }
    
    //Finished!!!
    //Task #3: connect all the rooms that corridor c passes by
    private int JointRooms(Corridor c)
    {
      //find rooms connected by this corridor
      int root1 = connectedRoom.find(c.room1);
      int root2 = connectedRoom.find(c.room2);
      
      //use disjoint set data structure to merge (unite) connected rooms
      if (root1!=root2)
      {
    	  connectedRoom.union(root1, root2);
    	  //return the number of connected components currently in the dungeon
    	  CC--;
      }
      
      //connect all the rooms that corridor c passes by
      ListIterator<Site> listIterator = c.sites.listIterator();
      while (listIterator.hasNext())
      {
    	 Site temp = listIterator.next();
    	 for (int i = 0; i<R; i++)
    	 {
	    	  if (rooms.elementAt(i).isInside(temp))
	    	  {
	    		  int root3 = connectedRoom.find(i);
	    		  int root4 = connectedRoom.find(c.room1);
	    			
	    		  if (root3 != root4)
	    		  {
	    			  connectedRoom.union(root3, root4);
	    			  CC--;
	    		  }
	    		  
	    	  }	 
    	 }
    		 
      }
      
      return CC;
    }
    
    ////create a corridor connecting the center of room r1 and the center of room r2
    private LinkedList<Site> buildCorridor(Room room1, Room room2){
		
    	LinkedList<Site> corridor_list = new LinkedList<Site>();
    	
        int room1CenterX = (int) Math.floor(room1.UL.i()+(double)room1.W/2);
        int room2CenterX = (int)Math.floor(room2.UL.i()+(double)room2.W/2);
        int room1CenterY = (int) Math.floor(room1.UL.j()+(double)room1.H/2);
        int room2CenterY = (int)Math.floor(room2.UL.j()+(double)room2.H/2);
        //find the center
        int maxX = Math.max(room1CenterX, room2CenterX);
        int minX = Math.min(room1CenterX, room2CenterX);
        int maxY = Math.max(room1CenterY, room2CenterY);
        int minY = Math.min(room1CenterY, room2CenterY);
        
        int locationX = 0;
        int locationY = 0;
        
        int situation = 1;
        if ((room1CenterX == minX && room1CenterY == minY) || (room1CenterX == maxX && room1CenterY == maxY))
        	situation = 2;
        //horizontal corridor
        for (int i = 0; i< (maxX-minX); i++)
        {
        	locationX = minX + i;
        	
        	Site temp;
        	if (situation == 1)
        		temp = new Site(locationX, minY);
        	else 
        		temp = new Site(locationX, minY);
        	
        	corridor_list.add(temp);	
        }
         //vertical corridor
        for (int i = 0; i< (maxY-minY); i++)
        {
        	locationY = minY + i;
        	Site temp;
        	if (situation == 1)
        		temp = new Site(minX, locationY);
        	else 
        		temp = new Site(maxX, locationY);
        	
        	corridor_list.add(temp);    	
        }
        return corridor_list;
      
	}
    
    //Finished!!!
    //Task #4: Return a random site in the room
    private Site GetARandomRoomSite()
    {
      //pick a random room
      int randomRoom = (int)Math.floor(R*rng.nextDouble());
      //pick a random site in the room
      int x=(int)Math.floor(rooms.elementAt(randomRoom).W*rng.nextDouble());
      int y=(int)Math.floor(rooms.elementAt(randomRoom).H*rng.nextDouble());

      return new Site(rooms.elementAt(randomRoom).UL.i()+x,rooms.elementAt(randomRoom).UL.j()+y);
    }

    //check if a given site is a room
    private boolean isRoom(Site s)
    {
      for (Room room : rooms)
      {
        if(room.isInside(s)) return true;
      }
      return false;
    }

    //convert this dungeon into string
    public String toString()
    {
      //create an empty room
      char board[][] = new char[N][N];
      for (int i = 0; i < N; i++) {
          for (int j = 0; j < N; j++) {
            board[i][j]=' ';
          }//end j
      }//end i

      //write out the cooridors
      for(Corridor corridor : corridors)
      {
        for(Site s:corridor.sites)
        {
          board[s.i()][s.j()]='+';
        }//end s
      }//end corridor

      //write out the rooms
      
      
      for(Room room : rooms)
      {
        for (int i = room.UL.i(); i < room.UL.i()+room.W; i++) {
            if(i>=N) continue;
            for (int j = room.UL.j(); j < room.UL.j()+room.H; j++) {
              if(j>=N) continue;
              board[i][j]='.';
            }//end j
        }//end i
      }//end room
      

      //write out the monster and rogue
      board[rogueSite.i()][rogueSite.j()]=ROGUE;
      board[monsterSite.i()][monsterSite.j()]=MONSTER;

      //convert to string
      String s = N+NEWLINE;
      for (int i = 0; i < N; i++) {
          for (int j = 0; j < N; j++) {
              s += board[i][j]+" ";
          }//end for j
          s += NEWLINE;
      }//end for i

      //time stamp it
      Calendar cal = Calendar.getInstance();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
      s += NEWLINE +"Copyright: created by "+System.getProperty("user.name")+" on " + sdf.format(cal.getTime()) +NEWLINE;

      return s;
    }   
}
