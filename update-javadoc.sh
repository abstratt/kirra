# Credit: https://vaadin.com/blog/host-your-javadoc-s-online-in-github
set -x 
mvn clean javadoc:javadoc -f com.abstratt.kirra.api -Dmaven.javadoc.failOnError=false \
   "-Dfooter=Abstratt Technologies and contributors" \
   "-Dheader=Kirra API - Reference Implementation"
cd com.abstratt.kirra.api/target/site/apidocs/
git init
git remote add javadoc https://github.com/abstratt/kirra.git
git fetch --depth=1 javadoc gh-pages 
git add --all
git commit -m sync
git merge --allow-unrelated-histories --no-edit -s ours remotes/javadoc/gh-pages
git push --set-upstream javadoc master:gh-pages --force
rm -Rf .git
