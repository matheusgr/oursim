# deploy.sh installs JARs that are not distributed in maven to a local repository

mvn install:install-file -DgroupId=oursim -DartifactId=jahmm -Dversion=0.6.1 -Dpackaging=jar -Dfile=lib/jahmm-0.6.1.jar
mvn install:install-file -DgroupId=oursim -DartifactId=ssj -Dversion=default -Dpackaging=jar -Dfile=lib/ssj.jar