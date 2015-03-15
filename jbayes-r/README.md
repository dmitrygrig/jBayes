# jBayes R
The project contains implementation of algorithms for computation of Bayesian probabilities in R language. It uses JRI and pre-installed (not embedded) R environment.

It needs the installation of R Environment (http://cran.r-project.org/) and JRI (https://rforge.net/JRI/).

# Installation of JRI via maven
Go to the directory libs/rJava.
mvn install:install-file -Dfile=JRIEngine.jar -DgroupId=net.rforge -DartifactId=JRIEngine -Dversion=0.9.7 -Dpackaging=jar
mvn install:install-file -Dfile=REngine.jar -DgroupId=net.rforge -DartifactId=REngine -Dversion=0.9.7 -Dpackaging=jar
mvn install:install-file -Dfile=JRI.jar -DgroupId=net.rforge -DartifactId=JRI -Dversion=0.9.7 -Dpackaging=jar

# Installation and configuration of R
*Go to http://cran.r-project.org/ and download the latest version of R.
* Install R
* Set up a environment variable R_HOME to the installation of R: “C:\Program Files\R\R-{version}″
* Append R’s bin directory to path, in my case is: “%R_HOME%\bin;”
* Put the directory containing r.dll into your system’s PATH variable, for 64bit system, put the directory match your JDK’s version, if your JDK is 32bit, then put %R_HOME%\bin\i386 to your PATH
* To install rJava do the following, install rJava directly in R or download rJava from http://www.rforge.net/rJava/files/, then copy JRI related file (jri.dll) located in the c:\Users\%user%\Documents\R\win-library\3.1\rJava\jri\x64 to %R_HOME%\bin, then to %JAVA_HOME%\jre\bin.
* After that, restart your computer, then you should be able to run JRI/rJava in Java, there are 2 examples in the installed/downloaded rJava package.
* execute {code}install.packages("rJava"){code} in R environment. The following info should be received:
{code}
> install.packages("rJava")
{code}
* execute {code}system.file("jri",package="rJava"){code} in R Environment and obtain the path to rJava. After that add this path to PATH. (if x64 is used add concerning directory)