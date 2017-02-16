# Jdrasil – A Tree Decompositions Library

Jdrasil is a modular Java library for computing tree
decompositions both, exact and heuristically. The goal of Jdrasil is
to allow other projects to add tree decompositions to their
applications as easy as possible. In order to achieve this, Jdrasil
is designed in a very modular way: Every algorithm is implemented as
interchangeable as possible. At the same time, algorithms are
implemented in a clean object oriented manner, making it easy to
understand and extend the implementation. We hope that this approach
makes it easier to study the practical aspects of tree width
algorithms, and that it helps to push the practical usage of tree
decompositions.

## Getting Started
Obtain the latest version of Jdrasil from the panel on the left
site or directly here. Alternatively, download a previous version
below.

If you wish to use Jdrasil as
as standalone solver, we simple do the following:
```
  # this will execute the exact mode
  java -jar Jdrasil.jar
```
Alternatively, you can execute Jdrasil in a specific mode (exact, heuristic, approximation)
with one of the following commands:
```
  java -cp Jdrasil.jar jdrasil.Exact
  java -cp Jdrasil.jar jdrasil.Heuristic
  java -cp Jdrasil.jar jdrasil.Approximation
```
Note, however, that the heuristic mode tries to improve the solution
until a SIGTERM is received, i.e., Jdrasil has to be stopped
manually in this mode. You may alternatively invoke the following command to obtain a quick solution:
```
  java -cp Jdrasil.jar jdrasil.Heuristic -instant
```

## Using Jdrasil as Library
Jdrasil can also be used as
library: simply add the `.jar` to the classpath of the desired
project. For instance, the following simple program uses Jdrasil to
compute an exact tree decomposition:
```
  import jdrasil.graph.*;
  import jdrasil.algorithms.*;

  public class Main {
    public static void main(String[] args) {
      // create your own graph
      Graph<Integer> G = GraphFactory.emptyGraph();
      for (int v = 1; v <= 5; v++) { G.addVertex(v); }
      G.addEdge(1, 2);
      G.addEdge(1, 4);
      G.addEdge(2, 3);
      G.addEdge(2, 4);
      G.addEdge(4, 5);

      // output graph in PACE format
      System.out.println(G);
      
      // compute exact decomposition
      TreeDecomposition<Integer> td = null;
      try {
        td = new ExactDecomposer<Integer>(G).call();
      } catch (Exception e) {
        System.out.println("something went wrong");
      }

      // ouput tree decomposition in PACE format
      System.out.println(td);
    }
  }
```
If the program is stored in a file `Main.java`, it can be
compiled and used with the following commands:
```
  javac -cp Jdrasil.jar: Main.java
  java -cp Jdrasil.jar: Main
