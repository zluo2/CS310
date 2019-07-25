import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;

public class RangeTree<T extends Comparable<T>> {

    private Node3D<T> root;
    private int coordinate;
    
    public RangeTree(int dimension, List<List<T>> points) {
    	//coordination 0,1,2 and dimension 1,2,3
        this.coordinate = dimension - 1;
        //sort the points array
        List<List<T>> sortedPoint = points
                .stream()
                .sorted((x, y) -> x.get(coordinate).compareTo(y.get(coordinate)))
                .collect(Collectors.toList());
        this.root = construct(sortedPoint, 0, points.size() - 1);
    }

    private Node3D<T> construct(List<List<T>> points, int left, int right) {
        if (left > right || coordinate < 0) {
            return null;
        }
        //mid =  the median D-coordinate of P
        int middle = (right + left) / 2;
        //For innerPoints
        List<List<T>> innerTreePoints = points
                .subList(left, right + 1)
                .stream()
                .collect(Collectors.toList());
        //a node storing mid
        Node3D<T> node = new Node3D<>(coordinate, points.get(middle), innerTreePoints);
        if (left != right) {
        	//P.left
            node.setLeft(construct(points, left, middle));
        }
        //P.right
        node.setRight(construct(points, middle + 1, right));
        return node;
    }

    public List<List<T>> search(List<Pair<T, T>> intervals) {
        return search(intervals, true);
    }

    public List<List<T>> search(List<Pair<T, T>> intervals, boolean Result) {
    	  List<List<T>> result = Result ? new ArrayList<>() : null;
          search(result, intervals);
          return result;
    }

    private void search(List<List<T>> result, List<Pair<T, T>> intervals) {
        Node3D<T> node = root;
        //range.left
        T left = intervals.get(coordinate).getKey();
        //rang.right
        T right = intervals.get(coordinate).getValue();
        
        while (!node.isLeaf() && (left.compareTo(node.getPoint().get(coordinate)) > 0
                || right.compareTo(node.getPoint().get(coordinate)) < 0)) {
            if (left.compareTo(node.getPoint().get(coordinate)) > 0) {
                node = node.getRight();
            } else {
                node = node.getLeft();
            }
        }
        if (node.isLeaf()) { // report
            if (left.compareTo(node.getPoint().get(coordinate)) <= 0
                    && right.compareTo(node.getPoint().get(coordinate)) >= 0) {
                if (coordinate == 0) {  //Z tree
                    if (result != null) {
                        result.add(node.getPoint());
                    }
                } else {
                	//Going to the next_level
                    node.getInnerTree().search(result, intervals);
                }
            }
        } else {
            Node3D<T> current = node.getLeft();
            if (current != null) {
                while (!current.isLeaf()) {
                    if (left.compareTo(current.getPoint().get(coordinate)) <= 0) {
                        if (current.getRight() != null) {
                            if (coordinate == 0) {
                                current.getRight().collectLeaves(result);
                            } else {
                            	//going to right innerTree
                                current.getRight().getInnerTree().search(result, intervals);
                            }
                        }
                        current = current.getLeft();
                    } else {
                        current = current.getRight();
                    }
                }
                if (left.compareTo(current.getPoint().get(coordinate)) <= 0
                        && right.compareTo(current.getPoint().get(coordinate)) >= 0) {
                    if (coordinate == 0) { //Z tree
                        if (result != null) {
                            result.add(current.getPoint());
                        }
                    } else {
                        current.getInnerTree().search(result, intervals);
                    }
                }
            }
            current = node.getRight();
            if (current != null) {
                while (!current.isLeaf()) {
                    if (right.compareTo(current.getPoint().get(coordinate)) >= 0) {
                        if (current.getLeft() != null) {
                            if (coordinate == 0) {  //Z tree
                                current.getLeft().collectLeaves(result);
                            } else {
                            	//going to left innerTree
                                current.getLeft().getInnerTree().search(result, intervals);
                            }
                        }
                        current = current.getRight();
                    } else {
                        current = current.getLeft();
                    }
                }
                if (left.compareTo(current.getPoint().get(coordinate)) <= 0
                        && right.compareTo(current.getPoint().get(coordinate)) >= 0) {
                    if (coordinate == 0) { //Z tree
                        if (result != null) {
                            result.add(current.getPoint());
                        }
                    } else {
                        current.getInnerTree().search(result, intervals);
                    }
                }
            }
        }
    }
}
