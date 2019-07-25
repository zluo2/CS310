import java.util.Queue;
/**
 * interface Graph is an interface
 */
public interface Graph<E>
{
    /*
     * @return true if the graph has 0 vertices, false otherwise
     */
    public boolean isEmpty ();
    /*
     * @return true if the graph is full and no more vertices can be added
     */
    public boolean isFull ();
    /*
     * @return true if vertex is in this graph, false otherwise
     */
    public boolean hasVertex (E vertex);
    
    /*
     * @return a Queue of the neighboring vertices 1 edge away from vertex
     */
    public Queue<E> neighbors(E vertex);
    /*
     * add vertex to the graph if the graph is NOT full
     */
    public void addVertex (E vertex);
    /*
     * add an edge from fromVertex to toVertex
     */
    public void addEdge(E fromVertex, E toVertex);
    
    /*
     * mark related methods for managing the graph
     */
    /*
     * unmark all vertices in the graph
     */
    public void clearMarks ();
    /*
     * @return true if vertex is marked, false otherwise
     */
    public boolean isMarked(E vertex);
    /*
     * mark vertex
     */
    public void mark (E vertex);
}
