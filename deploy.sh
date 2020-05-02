mvn \
  clean \
  package \
  org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy \
  --settings deploy.settings.xml \
  -Dmaven.test.skip=true \
  -DdeployAtEnd=true