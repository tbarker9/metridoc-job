#!/bin/sh

systemCall() {
    echo "running $1"
    if eval $1; then
		echo "command [$1] ran successfully"
	else
		echo "command [$1] failed with exit status [$?]"
		exit 1
	fi
}

if grep -q "\-SNAPSHOT" "VERSION"; then
    echo "VERSION file has SNAPSHOT in it, skipping release"
    exit 0
fi

PROJECT_VERSION=`cat VERSION`
MDOC_VERSION=`cat metridoc-job-cli/src/main/resources/MDOC_VERSION`

if [ $PROJECT_VERSION -ne $MDOC_VERSION ]; then
    echo "project version and mdoc version are not equal, run [./gradlew updateVersionFile] to update"
    exit 1
fi

#releases to github
echo ""
echo "Releasing ${PROJECT_VERSION} to GitHub"
echo ""

systemCall "git tag -a v${PROJECT_VERSION} -m 'tagging release'"
systemCall "git push origin v${PROJECT_VERSION}"

#release to bintray
echo ""
echo "Releasing ${PROJECT_VERSION} to BinTray"
echo ""

systemCall "./gradlew publishArchives publishDistribution bumpVersion"
#fires off all the tasks for updating version according to new SNAPSHOT version
systemCall "./gradlew compileGroovy"
systemCall "git add VERSION"
systemCall "git add metridoc-job-cli/src/main/resources/MDOC_VERSION"
#this will update the DEPENDENCY_URLS file
systemCall "git add metridoc-job-cli/src/main/resources/DEPENDENCY_URLS"
systemCall "git commit -m 'committing a new version'"
systemCall "git push origin master"