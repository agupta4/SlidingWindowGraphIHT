# SlidingWindowGraphIHT

This project was developed under the copyright of University at Albany, SUNY. Please consult me at agupta4@albany.edu or at @abhisheklokesh@gmail.com for further discussion related to this project. We are currently running various simulations on current version of Graph-IHT.

Sliding window based Graph-IHT works for a single window at a time and evaluates the x^i vector to be used in the next window. The proposed technique uses head and tail projection to generalize the projected gradient descent method to find the optimal set of nodes.
Results can be produced using TestIHT.java, TestLogisticReg.java and ModifiedLogisticReg.java files in the test folder of the project. The results and observations are generated in Graph_Observations folder for proposed technique and one baseline method Graph-MP using simple python script. Graph-MP can be downloaded here: https://github.com/baojianzhou/Graph-MP.git

Since, out of all the cost functions, only Graphs structured sparse logistic regression estimates the results based on offline settings, we have compared the results for this cost function on our proposed method and Graph-MP.

Dependency Information:

1. Maven Installation: https://maven.apache.org/install.html

2. Java 1.8: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

3. Git: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

