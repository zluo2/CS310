import java.util.*;

/**
 * Provides various information about the provided graph helpful to a Monster.
 */

public class GraphSearch {
    private Graph<Site> graph;

    /**
     * Constructs a SiteGraphSearcher from the given Graph graph.
     */
    public GraphSearch(Graph<Site> graph) {
        this.graph = graph;
    }

    /**
     * Returns the site on the shortest path to target from node. Seems to be of quadratic complexity.
     */
    public Site siteOnShortestPath(Site node, Site target) {
        return pathFromStack(node, shortestSiteStack(node, target)).pop();
    }

    /**
     * Implements a bidirectional depth-limited iterative deepening depth-first search to find the 
     * next best move for a Monster starting on node.
     */
    public Site bestMovebidiIDDFS(Site node, Site target, int maxDepth) {
        for(int i= 0; i<maxDepth; i++) {
            for(Site from: graph.neighbors(node)) {
                if(isBestMovebidiDLS(from, target, i))
                    return from;
            }
        }
        return null;
    }

    /**
     * Implements a bidirectional depth-limited search to find if there is a way to always catch in
     * under depth moves a Rogue starting on target if the Monster starts at node.
     */
    public boolean isBestMovebidiDLS(Site node, Site target, int depth) {
        if(depth >= 0) {
            boolean worked = true;
            for(Site tChild: graph.neighbors(target)) //base case
                worked = worked && graph.neighbors(node).contains(tChild);

            if(worked) return worked;
            else {
                for(Site nChild: graph.neighbors(node)) {
                    boolean worked2= true;
                    for(Site tChild: graph.neighbors(target)) {
                        boolean temp= isBestMovebidiDLS(nChild, tChild, depth-1);
                        if(!temp) {
                            worked2= temp;
                            break;
                        }
                    }
                    if(worked2) return worked2;
                }
            }
        }
        return false;
    }

    private Stack<Site[]> shortestSiteStack(Site from, Site to) {
        Queue<Site[]> temp = new LinkedList<Site[]>();
        Stack<Site[]> moves = new Stack<Site[]>();

        graph.mark(from);
        for(Site site: graph.neighbors(from)) {
            if(!graph.isMarked(site)) {
                temp.add(new Site[] {from,site});
                graph.mark(site);
            }
        }

        while(!temp.isEmpty()) {
            Site[] sites = temp.poll();
            moves.push(sites);
            if(sites[1].equals(to)) break;
            for(Site site: graph.neighbors(sites[1])) {
                if(!graph.isMarked(site)) {
                    temp.add(new Site[] {sites[1],site});
                    graph.mark(site);
                }
            }
        }
        graph.clearMarks();

        return moves;
    }

    /**
     * Implements a bidirectional breadth-first search to find the shortest path between Sites
     * from and to.
     */
    public Stack<Site> bidiShortestSiteStack(Site from, Site to) {
        boolean fFound= false, tFound= false;  
        Site fConnectSite = null, tConnectSite = null;
        Queue<Site[]> fTemp = new LinkedList<Site[]>(), tTemp = new LinkedList<Site[]>();  
        ArrayList<Site> fromList = new ArrayList<Site>(), toList = new ArrayList<Site>(); 
        Stack<Site[]> fMoves = new Stack<Site[]>(), tMoves = new Stack<Site[]>();
        Stack<Site> moves = new Stack<Site>();  

        graph.mark(from);
        for(Site site: graph.neighbors(from)) {
            if(site.equals(to)) {}
            fTemp.add(new Site[] {from,site});
            graph.mark(site);
            fromList.add(site);
        }   

        graph.mark(to);
        for(Site site: graph.neighbors(to)) {
            tTemp.add(new Site[] {to,site});
            if(graph.isMarked(site) && fromList.contains(site)) {
                tFound= true;
                break;
            }
            toList.add(site);
            graph.mark(site);
        } 

        if(tFound) {
            moves.add(to);
            return moves;
        }

        bigBreak:
        while(!fTemp.isEmpty() && !tTemp.isEmpty()) {
            Site[] fSites = fTemp.poll();
            fMoves.push(fSites);
            
            Site[] tSites = tTemp.poll();
            tMoves.push(tSites);
            
            for(Site site: graph.neighbors(fSites[1])) {    
                fTemp.add(new Site[] {fSites[1],site});
                if(graph.isMarked(site) && toList.contains(site)) {
                    fromList.add(site);
                    fConnectSite = site;
                    fFound = true;
                    break bigBreak;
                }
                else 
                    graph.mark(site); 
            }
            
            for(Site site: graph.neighbors(tSites[1])) {
                tTemp.add(new Site[] {tSites[1],site});
                if(graph.isMarked(site) && fromList.contains(site)) {
                    toList.add(site);
                    tConnectSite = site;
                    tFound = true;
                    break bigBreak;
                }
                else 
                    graph.mark(site);
            }
        }
        if(fFound) {
            Stack<Site> fTempMoves = pathFromStack(from, fMoves);
            Site[] sites = null;
            while(!tMoves.isEmpty()) {
                sites = tMoves.pop();
                if(sites[1].equals(fConnectSite)) break;
            }
            fMoves.push(sites);
            Stack<Site> tTempMoves = pathFromStack(to, tMoves);
            //need to combine them
        }
        else if(tFound) {
            Stack<Site> tTempMoves = pathFromStack(to, tMoves);
            Site[] sites = null;
            while(!fMoves.isEmpty()) {
                sites = fMoves.pop();
                if(sites[1].equals(tConnectSite)) break;
            }
            tMoves.push(sites);
            Stack<Site> fTempMoves = pathFromStack(from, fMoves);
            //need to combine them
        }
        return moves;
    }

    private Stack<Site> pathFromStack(Site node, Stack<Site[]> moves) {
        Stack<Site> path = new Stack<Site>();
        
        Site[] tempMoves = moves.pop();
        Site[] tempMoves2 = null;   

        path.add(tempMoves[1]);
        while(!moves.isEmpty()) {   
            tempMoves2 = moves.pop();
            if(tempMoves[0].equals(node)) {
                path.push(tempMoves[0]);
                break;
            }
            if(tempMoves2[0].equals(node) && tempMoves[0].equals(tempMoves2[1])) {
                path.push(tempMoves[0]);
                break;
            }
            if(tempMoves[0].equals(tempMoves2[1])) {
                path.push(tempMoves[0]);
                tempMoves = tempMoves2;  
            }
        }
        return path;
    }
}