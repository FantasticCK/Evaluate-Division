package com.CK;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<List<String>> equations = new ArrayList<>();
        equations.add(new ArrayList<>() {{
            add("a");
            add("b");
        }});
        equations.add(new ArrayList<>() {{
            add("b");
            add("c");
        }});
        double[] values = {2.0, 3.0};
        List<List<String>> queries = new ArrayList<>();
        queries.add(new ArrayList<>() {{
            add("a");
            add("c");
        }});
        queries.add(new ArrayList<>() {{
            add("b");
            add("a");
        }});
        queries.add(new ArrayList<>() {{
            add("a");
            add("e");
        }});
        queries.add(new ArrayList<>() {{
            add("a");
            add("a");
        }});
        queries.add(new ArrayList<>() {{
            add("x");
            add("x");
        }});
        System.out.println(Arrays.toString(new Solution().calcEquation(equations, values, queries)));
    }
}

//DFS
class Solution {
    Map<String, Double> valueMap = new HashMap<>();
    Map<String, List<String>> graph = new HashMap<>();

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        // build graph
        for (int i = 0; i < values.length; i++) {
            String x = equations.get(i).get(0), y = equations.get(i).get(1);
            valueMap.put(getKey(x, y), values[i]);
            valueMap.put(getKey(y, x), 1.0 / values[i]);
            graph.putIfAbsent(x, new ArrayList<>());
            graph.putIfAbsent(y, new ArrayList<>());
            graph.get(x).add(y);
            graph.get(y).add(x);
        }

        // dfs on each item
        int n = queries.size();
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            List<String> query = queries.get(i);
            result[i] = dfs(new HashSet<>(), query.get(0), query.get(1), 1.0);
            if (result[i] == 0.0) result[i] = -1.0;
        }

        return result;
    }

    private double dfs(Set<String> visited, String start, String end, double value) {
        if (visited.contains(start)) return 0.0;
        if (!graph.containsKey(start)) return 0.0;
        if (start.equals(end)) return value;
        visited.add(start);

        double temp = 0.0;
        for (String next : graph.get(start)) {
            String nextKey = getKey(start, next);
            temp = dfs(visited, next, end, value * valueMap.get(nextKey));
            if (temp != 0.0) break;
        }
        return temp;
    }

    private String getKey(String a, String b) {
        return a + "/" + b;
    }
}

//Union Find
class Solution {
    class Node {
        public String parent;
        public double ratio;
        public Node(String parent, double ratio) {
            this.parent = parent;
            this.ratio = ratio;
        }
    }

    class UnionFindSet {
        private Map<String, Node> parents = new HashMap<>();

        public Node find(String s) {
            if (!parents.containsKey(s)) return null;
            Node n = parents.get(s);
            if (!n.parent.equals(s)) {
                Node p = find(n.parent);
                n.parent = p.parent;
                n.ratio *= p.ratio;
            }
            return n;
        }

        public void union(String s, String p, double ratio) {
            boolean hasS = parents.containsKey(s);
            boolean hasP = parents.containsKey(p);
            if (!hasS && !hasP) {
                parents.put(s, new Node(p, ratio));
                parents.put(p, new Node(p, 1.0));
            } else if (!hasP) {
                parents.put(p, new Node(s, 1.0 / ratio));
            } else if (!hasS) {
                parents.put(s, new Node(p, ratio));
            } else {
                Node rS = find(s);
                Node rP = find(p);
                rS.parent = rP.parent;
                rS.ratio = ratio / rS.ratio * rP.ratio;
            }
        }
    }

    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        UnionFindSet u = new UnionFindSet();

        for (int i = 0; i < equations.size(); ++i)
            u.union(equations.get(i).get(0), equations.get(i).get(1), values[i]);

        double[] ans = new double[queries.size()];

        for (int i = 0; i < queries.size(); ++i) {
            Node rx = u.find(queries.get(i).get(0));
            Node ry = u.find(queries.get(i).get(1));
            if (rx == null || ry == null || !rx.parent.equals(ry.parent))
                ans[i] = -1.0;
            else
                ans[i] = rx.ratio / ry.ratio;
        }

        return ans;
    }
}

// Floyd
class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        HashMap<String, HashMap<String, Double>> map = new HashMap<>();
        int count = 0;
        for (List<String> e : equations) {
            map.putIfAbsent(e.get(0), new HashMap<>());
            map.putIfAbsent(e.get(1), new HashMap<>());
            map.get(e.get(0)).put(e.get(0), 1.0);
            map.get(e.get(1)).put(e.get(1), 1.0);
            map.get(e.get(0)).put(e.get(1), values[count]);
            map.get(e.get(1)).put(e.get(0), 1.0 / values[count]);
            count++;
        }

        for (String mid : map.keySet()) {
            for (String start : map.get(mid).keySet())
                for (String end : map.get(mid).keySet()) {
                    double val = map.get(start).get(mid) * map.get(mid).get(end);
                    map.get(start).put(end, val);
                }
        }

        double[] ans = new double[queries.size()];
        for (int i = 0; i < ans.length; i++) {
            List<String> e = queries.get(i);
            if (!map.containsKey(e.get(0)) || !map.get(e.get(0)).containsKey(e.get(1)))
                ans[i] = -1.0;
            else
                ans[i] = map.get(e.get(0)).get(e.get(1));
        }
        return ans;
    }
}