import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Node3D<T extends Comparable<T>> {

    private List<T> point; 
    private Node3D<T> left;
    private Node3D<T> right;
    private RangeTree<T> next_level;
    private int coordinate;

    //build the root
    Node3D(int coordinate, List<T> point, List<List<T>> points) {
        this.point = point;
        this.coordinate = coordinate;
        if (coordinate > 0) {
            this.next_level = new RangeTree<>(coordinate, points);
        }
    }
    
    //using the reportsubtree for search
    void collectLeaves(List<List<T>> result) {
        if (result == null) {
            return;
        }
        Deque<Node3D<T>> nodesToVisit = new ArrayDeque<>();
        nodesToVisit.addLast(this);
        while (!nodesToVisit.isEmpty()) {
            Node3D<T> node = nodesToVisit.removeLast();
            if (node.isLeaf()) {
                result.add(node.getPoint());
            } else {
                if (node.getLeft() != null) {
                    nodesToVisit.addLast(node.getLeft());
                }
                if (node.getRight() != null) {
                    nodesToVisit.addLast(node.getRight());
                }
            }
        }
    }

    List<T> getPoint() {
        return point;
    }
    
    boolean isLeaf() {
        return left == null && right == null;
    }

    void setPoint(List<T> point) {
        this.point = point;
    }

    Node3D<T> getLeft() {
        return left;
    }

    void setLeft(Node3D<T> left) {
        this.left = left;
    }

    Node3D<T> getRight() {
        return right;
    }

    void setRight(Node3D<T> right) {
        this.right = right;
    }

    RangeTree<T> getInnerTree() {
        return next_level;
    }

    void setInnerTree(RangeTree<T> innerTree) {
        this.next_level = innerTree;
    }

    int getCoordinate() {
        return coordinate;
    }

    void setCoordinate(int coordinate) {
        this.coordinate = coordinate;
    }
    
}
