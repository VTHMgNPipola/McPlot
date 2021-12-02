# McPlot

McPlot is a lightweight graphing calculator written in Java 17, free for anyone to use.

My goal with McPlot is to create a reliable, powerful and lightweight graphing calculator that is also free and
open-source, so that anyone can use it, even with the most basic of computers (provided it supports Java 17).

## Compiling and Running

If you want to compile McPlot into a jar file, you can easily do so using the command in the root folder of the project:

```
mvn clean compile assembly:single
```

Assuming you have Maven installed, this will compile McPlot and create an executable jar file inside ```./target/```.
You then just need to run the jar file using Java 17:

```
java -jar <generated-jar-file>
```

## Supported Features

* Definition of constants
* Usage of your functions in other functions
* Custom independent units for each axis
* Show function area
* Quickly zoom in and out and pan through the graph with minimal lag
* Almost fully customize how to graph is displayed
* Relatively small memory footprint
* Save and load your functions and constants to a file
* Export data to text files

## Planned Features

* Reduce the memory footprint even more
* Export graph and function data to spreadsheets, PGFPlots graphs and pictures
* Improve overall customization and flexibility
* Gather and present data from the functions
* Show functions as splines instead of straight lines from each sample point
* Option to use a "speed" standard expression evaluator and a "precision" slower evaluator

And some features which I want to implement, but may never do so:

* 3D graphing
* Computing expressions in the GPU
