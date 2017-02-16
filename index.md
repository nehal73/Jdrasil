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
or alternatively
```
  java -cp Jdrasil.jar jdrasil.Exact
```
In addition, you can execute Jdrasil in the heuristic or
approximation mode:
```
  java -cp Jdrasil.jar jdrasil.Heuristic
  java -cp Jdrasil.jar jdrasil.Approximation
```
Note, however, that the heuristic mode tries to improve the solution
until a SIGTERM is received, i.e., Jdrasil has to be stopped
manually in this mode. You may alternatively invoke the following command to obtain a quick solution:
```
  java -cp Jdrasil.jar jdrasil.Heuristic -instant
```
