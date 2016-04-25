This is an eclipse project directory, you can import it into eclipse-Mars
The output result of KNN and logistic regression can be found in"KNN-output.txt"
and "Logistic Regression-output.txt"


File discription :

HW2ProgramStarter.java:
The starter of the program.
The Method runKNN() implements 5-fold cross validation on "data.txt" using KNN with different values of k from 1 to 21
The Method runLogisticRegression() implements 5-fold cross validation on "data.txt" using logistic regression 

Logistic.java:
In this implementation of logistic regression, instead of using log liklihood function, I used L2
regularization on the log liklihood function. Corresponding explanation of calculation of 
gradient and hessian are in the solution pdf file, please refer to it if needed.

KNN.java:
implement the KNN classifier