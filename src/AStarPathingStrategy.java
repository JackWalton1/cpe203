import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.hash;

public class AStarPathingStrategy implements PathingStrategy {
    public AStarPathingStrategy(){

    }

    public static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    // removes all invalid neighbors (obstacles, searched, non-adjacent)
    public List<Point> getValidNeighbors(Point currPoint, Predicate<Point> canPassThrough, HashSet<Point> closedList,
                                         Function<Point, Stream<Point>> potentialNeighbors){
        List<Point> neighbors = potentialNeighbors.apply(currPoint)
                .filter(canPassThrough).filter(p->!closedList.contains(p)).collect(Collectors.toList());

        return neighbors;
    }


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        HashMap<Point, Node> openList = new HashMap();
        Node currentNode = new Node(null, start, 0, distanceSquared(start, end), distanceSquared(start, end));
        // initial open list uses start Point as first Key
        // and has currNode w/ null prevNode
        openList.put(start, currentNode);
        // best f value always at the front of list
        PriorityQueue nodeQueueByFValues = new PriorityQueue(Comparator.comparing(Node::getF));
        nodeQueueByFValues.add(currentNode);
        // quick lookup for the points using .contains()
        HashSet<Point> closedList = new HashSet();
        List<Point> path = new LinkedList<>();

        while (openList.size() > 0) {
            // removes all invalid neighbors (obstacles, searched, non-adjacent)
            if (withinReach.test(currentNode.currPoint, end)) {
                while (currentNode.prevNode != null) {
                    path.add(0, currentNode.currPoint);
                    currentNode = currentNode.prevNode;
                }
                return path;
            }
            List<Point> neighbors = getValidNeighbors(currentNode.currPoint, canPassThrough, closedList, potentialNeighbors);
            // for the neighbors not in the openList already:
            for (Point neighbor : neighbors) {
                // made a neighborNode with currNode as prevNode
                Node neighborNode = new Node(currentNode,
                        neighbor,
                        currentNode.g + 1,
                        distanceSquared(neighbor, end),
                        currentNode.g + 1 + distanceSquared(neighbor, end));

                // making the (List<Point>) path
                if (!openList.containsKey(neighbor)) {
                    // add Node to openList and to the PriorityQueue (by f-val)
                    openList.put(neighbor, neighborNode);
                    nodeQueueByFValues.add(neighborNode);

                } else {// else if the neighbor is on the open list, check g value and add if lesser
                    if (openList.get(neighbor).f > neighborNode.f) {
                        // and add node to the PriorityQueue and openList
                        openList.put(neighbor, neighborNode);
                        nodeQueueByFValues.add(neighborNode);
                    }
                }

            }
            // remove currentNode from priorityQueue and add currentNode to closed list
            closedList.add(currentNode.currPoint);
            nodeQueueByFValues.remove(currentNode);
            openList.remove(currentNode.currPoint, currentNode);
            // choose smallest f val in PriorityQueue and make it's Node currentNode
            currentNode = (Node) nodeQueueByFValues.peek();
        }

        return path;
    }

    /* each Node keeps track of:
     previous Node,
     current Point,
     distance from start (g),
     distance traveled (h),
     sum distance from start + distance traveled (f) */

    private class Node {

        private Node prevNode;
        private Point currPoint;

        private int g;
        private int h;
        private int f;

        public Node(Node prevNode, Point currPoint, int g, int h, int f) {
            this.prevNode = prevNode;
            this.currPoint = currPoint;
            this.g = g;
            this.h = h;
            this.f = f;
        }
        public int getF(){
            return this.f;
        }

        public boolean equals(Object o) {
            if ((o == null) || (this.getClass() != o.getClass())) {
                return false;
            }
            Node n = (Node) o;
            return this.currPoint.x == n.currPoint.x && this.currPoint.y == n.currPoint.y;
        }

        public int hashCode() {
            int prime = 31;
            if (this.currPoint == null)
                return prime;
            int result = this.currPoint.x + this.currPoint.y;
            return prime + hash(result);
        }

    }

    class FValueComparator implements Comparator<AStarPathingStrategy.Node> {
        public int compare(Node n1, Node n2) {
            if (n1.f > n2.f)
                return 1;
            else if (n1.f < n2.f)
                return -1;
            return 0;
        }

    }
}


