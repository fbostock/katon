package fjdb.interviews.algos;

import com.google.common.collect.Lists;

import java.util.*;

public class MazeSolver {

    private static class Vertex {
        private List<Vertex> adjacents = Lists.newArrayList();
        private String name;

        public Vertex(String name) {
            this.name = name;
        }

        public void addNeighours(Vertex ... vertices ) {
            adjacents.addAll(Arrays.asList(vertices));
        }

        public String getName() {
            return name;
        }

        public List<Vertex> getAdjacents() {
            return adjacents;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(name, vertex.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
/*
A    - B   - C
.      .     .
D      E     F
.      .     .
G      H     I
 */
        Vertex a = new Vertex("A");
        Vertex b = new Vertex("B");
        Vertex c = new Vertex("C");
        Vertex d = new Vertex("D");
        Vertex e = new Vertex("E");
        Vertex f = new Vertex("F");
        Vertex g = new Vertex("G");
        Vertex h = new Vertex("H");
        Vertex i = new Vertex("I");
        a.addNeighours(b, d);
        d.addNeighours(a,g);
        g.addNeighours(d);
        b.addNeighours(a,c,e);
        e.addNeighours(b,h);
        h.addNeighours(e);
        c.addNeighours(b,f);
        f.addNeighours(c,i);
        i.addNeighours(f, h);
        LinkedList<Vertex> path = findPath(a, h);
        System.out.println(path);
    }

    public static LinkedList<Vertex> findPath(Vertex start, Vertex target) {
        Queue<Vertex> queue = new ArrayDeque<>();
        Map<Vertex, Vertex> visited = new HashMap<>();
        LinkedList<Vertex> path = new LinkedList<>();

        queue.add(start);

        Vertex current = start;
        visited.put(start, null);
        while (!current.getName().equals(target.getName())) {
            List<Vertex> adjacents = current.getAdjacents();
            for (Vertex adjacent : adjacents) {
                if (visited.containsKey(adjacent)) continue;
                visited.put(adjacent, current);
                queue.add(adjacent);
            }
            current = queue.poll();
        }

        Vertex vertex = visited.get(target);
        if (vertex != null) {
            path.add(vertex);
            path.add(target);
            while (!vertex.getName().equals(start.getName())) {
                vertex = visited.get(vertex);
                path.addFirst(vertex);
            }
        }
        return path;
    }
}
