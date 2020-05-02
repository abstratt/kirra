# Remove snapshot and create release tag
mvn \
  org.codehaus.mojo:build-helper-maven-plugin:3.1.0:parse-version \
  versions:set "-DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}" -DgenerateBackupPoms=false \
  org.eclipse.tycho:tycho-versions-plugin:1.7.0::update-eclipse-metadata \
  scm:tag "-Dtag=v\${newVersion}"
  