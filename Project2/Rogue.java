import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

import java.util.HashSet;
/*************************************************************************
 *  Compilation:  javac Rogue.java
 * 
 *************************************************************************/

public class Rogue implements Life{
    private Game game;
    private Dungeon dungeon;
    private Graph<Site> map;
    
    /**Within corridors and rooms, the number of neighbors per site drops.
    */
    private static final int ROOM_DEPTH = 6;
    private static final int CORRIDOR_DEPTH = 8;
    private ArrayList<Site> corridorStarts;
    private HashSet<Site> safeCorridorStarts = new HashSet<Site>();
    private HashSet<Site> viableCorridors    = new HashSet<Site>();
    private HashSet<Site> inLoop             = new HashSet<Site>();

    /*
     *  Constructor for the Rogue
     */
    public Rogue(Game game) {
        this.game    = game;
        this.dungeon = game.getDungeon();
        this.map   = new Map<Site>(dungeon.size() * dungeon.size());

        // Initialize Graph
        Site[] added = new Site[dungeon.size() * dungeon.size()];
        for(int i = 0; i < dungeon.size(); i += 1)
            for(int j = 0; j < dungeon.size(); j += 1) {
                Site s = new Site(i, j);
                map.addVertex(s);
                added[i * dungeon.size() + j] = s;
        }

        for(int i = 0; i < dungeon.size() * dungeon.size(); i += 1)
            for(int j = 0; j < dungeon.size() * dungeon.size(); j += 1) {
                if(dungeon.isLegalMove(added[i], added[j]))
                    map.addEdge(added[i], added[j]);
        }

        // Gather information about the graph
        findCorridors(added);
        System.out.println(viableCorridors);
        
    }

    public Site move() {
        Site monster = game.getMonsterSite();
        Site rogue   = game.getRogueSite();
        Site move    = null;

        // Generate the set of all possible single turn moves for Rogue
        ArrayList<Site> moves = new ArrayList<Site>(map.neighbors(rogue));
        moves.add(rogue); // Current position

        // Select the move which presents the maximum benefit for the rogue
        int    maxIndex = 0;
        double maxValue = Double.MIN_VALUE;
        int    depth = dungeon.isRoom(rogue) ? ROOM_DEPTH : CORRIDOR_DEPTH;
        for(int i = 0; i < moves.size(); i += 1) {
            double val = minimax(moves.get(i), monster, Double.MIN_VALUE, Double.MAX_VALUE, depth); 
            if(val >= maxValue) {
                maxValue = val;
                maxIndex = i;
            }
        }
        
        // A special case where when the rogue realizes it can't win it just sits still.
        if(maxValue == Double.MIN_VALUE) {
            Site farthest = rogue;
            int distance = 0;
            for(Site neigh : map.neighbors(rogue)) {
                int d2 = neigh.manhattanTo(monster);
                if(d2 >= distance) {
                    farthest = neigh;
                    distance = d2;
                }
            }
            return farthest;
        }
        
        return moves.get(maxIndex);
    } 

    /**
     *  Implementation of the minimax decision making algorithm.
     */
    private double minimax(Site rogue, Site monster, double alpha, double beta, int depth) {
    
        for(Site neigh : map.neighbors(monster)) {
            if(rogue.equals(neigh)) return Double.MIN_VALUE;
        }

        if(dungeon.isCorridor(rogue) && !viableCorridors.contains(rogue))
            return Double.MIN_VALUE;

      
        if(depth <= 0) {
            return score(rogue, monster);
        }

        /* meaning that it tries to get the maximum possible heuristic value when it makes a move.
         */
        if(depth % 2 == 0) {
            Queue<Site> neighbors = map.neighbors(rogue);
            neighbors.add(rogue); // Allow for no move to be made

            for(Site neigh : neighbors) {
                alpha = Math.max(alpha, minimax(neigh, monster, alpha, beta, depth - 1));
                if(beta <= alpha) return beta;
            }

            return alpha;
        } else {
            Queue<Site> neighbors = map.neighbors(monster);
            neighbors.add(monster);

            beta = Double.MAX_VALUE;

            for(Site neigh : neighbors) {
                beta = Math.min(beta, minimax(rogue, neigh, alpha, beta, depth - 1));
                if(beta <= alpha) return beta;
            }

            return beta;
        }
    }

    /*
    * It's the weighing mechanism for my rogue,and allows the rogue to distinguish between good and bad positions to be in, allowing it to make good decisions.
    */
    private double score(Site rogue, Site monster) {
        if(inLoop.contains(rogue))
            return 1000 * (monster.manhattanTo(rogue) - 1);
        if(safeCorridorStarts.contains(rogue))
            return 500 * (monster.manhattanTo(rogue) - 1);
        if(viableCorridors.contains(rogue))
            return 250 * monster.manhattanTo(rogue);
        if(dungeon.isRoom(rogue) && !viableCorridors.contains(rogue))
            return Double.MIN_VALUE;
        if(dungeon.isRoom(rogue) && dungeon.isWall(rogue))
            return 0;
        if(dungeon.isRoom(rogue))
            return (monster.manhattanTo(rogue) - 1);

        return 0;     
    }

   
    /*
     * Similar to the above function in pretty much every respect
     */
    private void findConnectedStarts(Site current, Site start, ArrayList<Site> visited) {
        int index = visited.indexOf(current);
        if((index != -1) && visited.size() > 0) {
            if(corridorStarts.indexOf(current) != -1 && visited.size() > 2)  {
                safeCorridorStarts.add(visited.get(0));
                viableCorridors.addAll(visited);
                return;
            }
        }
        else {
            visited.add(current);
            for(Site s : map.neighbors(current)) {
                if(dungeon.isCorridor(s)) findConnectedStarts(s, start, visited);
            }
            visited.remove(visited.size() - 1);
        }
    }
    
    /*
     *  An easy iteration through all the corridor starts to see it any of them
     *  are one long corridors between two rooms.
     */
    private void findSinglePassageways(ArrayList<Site> corridorStarts) {
        for(Site start : corridorStarts) {
            int i = 0;
            for(Site neigh : map.neighbors(start)) {
                if(dungeon.isRoom(neigh)) i += 1;
            }
            if(i > 1) {
                viableCorridors.add(start);
                safeCorridorStarts.add(start);
            }

        }
    }
    
    /*
     * this algorithm is the starting point from which the rogue does research about the board, doing it once as opposed to each turn greatly speeds up the process, and allows for more levels of depth
     */
    private void findCorridors(Site[] vertices) {
        // Find the nodes from which corridors start
        corridorStarts = new ArrayList<Site>();
        for(Site site : vertices) {
            if(dungeon.isCorridor(site))
                for(Site neighbor : map.neighbors(site)) {
                    if(dungeon.isRoom(neighbor)) {
                        corridorStarts.add(site);
                        break;
                    }
            }
        }

        // Find loops within corridors (good example is dungeon O)
        for(Site start : corridorStarts) {
            findCorridorLoops(start, start, new ArrayList<Site>());
        }

        // Find corridors which aren't dead ends, in general
        for(Site start : corridorStarts) {
            findConnectedStarts(start, start, new ArrayList<Site>());
        }

        // Another special case, if a corridor connects to rooms
        findSinglePassageways(corridorStarts);
    }

    /*
     * Using a depth first search to loops within the corridor.
     */
    private void findCorridorLoops(Site current, Site start, ArrayList<Site> visited) {
        int index = visited.indexOf(current);
        if((index != -1) && visited.size() > 0) {
            if((visited.size() - index) > 2 && visited.size() > 2)  {
                visited.add(current);
                safeCorridorStarts.add(visited.get(0));
                viableCorridors.addAll(visited);

                inLoop.addAll(visited.subList(visited.indexOf(visited.get(visited.size() - 1)), 
                              visited.size() - 1));
                visited.remove(visited.size() - 1);
                return;
            }
        }
        else {
            visited.add(current);
            for(Site s : map.neighbors(current)) {
                if(dungeon.isCorridor(s)) findCorridorLoops(s, start, visited);
            }
            visited.remove(visited.size() - 1);
        }
    }

}