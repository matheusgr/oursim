# Sonar execution to extract metrics

mvn clean install -Dtest=false -DfailIfNoTests=false
mvn sonar:sonar