  This is an eclipse project folder, containing solution of Assignment 2, problem 3

  The main function is in src\Assignment2ProgramStarter.java

  Problem 3-(a) is implemented in src\Perceptron\PerceptronInPrimal.java and can be run by using runPerceptronInPrimal()  in 
src\Assignment2ProgramStarter.java. 
  The information about how many iterations performed until convergence, as well as how many total updates (corresponding to mistakes) 
that occur through each iteration are printed out during running.
  After convergence,the output of the raw weights, as well as the normalized weights corresponding to the linear
classifier w1 x1 + w2 x2 + w3 x3 + w4 x4 = 1 are output in the file output\perceptron\Report of Primal Perceptron.txt


  Problem 3-(b) is implemented in src\Perceptron\PerceptronInDual.java and can be run by using runPerceptronInDual()  in 
src\Assignment2ProgramStarter.java
  In this part, it frist train a kernel percptron with linear kernel and perform the classification on percep1.txt. After converge,it transferd
the result into the primal form. the output result are in the file output\perceptron\Report of Dual Perceptron with Linear Kernel on percep1.txt
  Then, it uses an Gaussian RBF kernel to perform the classification on percep2.txt and converges in 18 iterations.

  Problem 4-(a) is implemented in src\SVM\svmSMO.java and can be run by using runSvmLinearKernelSMO()  in 
src\Assignment2ProgramStarter.java.(this part implements training of a SVM with linear kernel by using SMO)

 Problem 4-(b) is implemented in src\Assignment2ProgramStarter.java.  and can be run by using runSvmPackage()  in 
the same file.(this part implements runing of a SVM with different kernels)

 Problem 5 is implemented in src\MLPMultiPercep.java and can be run by using runMultilayerPerceptron()  in 
src\Assignment2ProgramStarter.java.
 

