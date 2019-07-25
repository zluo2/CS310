import java.util.*;

public class MakeDungeon {

  public static void main(String[] args)
  {

    if(args.length<2)
    {
      System.err.println("Usage: java MakeDungeon N R");
      System.exit(1);
    }

    int N=Integer.parseInt(args[0]); //dimension
    int R=Integer.parseInt(args[1]); //room size
	
	/*
	Scanner input = new Scanner(System.in);
	int N = input.nextInt();
	int R = input.nextInt();
	*/
    
	Dungeon dungeon = new Dungeon(N,R);
	System.out.println(dungeon);

  }

}
