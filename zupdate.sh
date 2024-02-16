#!/bin/bash

# Behold my perfect creation, grabs the latest version of zeta and updates the zeta entry in dependencies.properties to said version

# Zeta's XML from Jared's Maven
xml_url="https://maven.blamejared.com/org/violetmoon/zeta/Zeta/maven-metadata.xml"

# Find the latest version
latest_version=$(curl -s "$xml_url" | grep -oP '<(latest)>\K[^<]+' | sort -V | tail -n 1)

# Grab the current version of zeta being used
current_version=$(grep "^zeta=" dependencies.properties | cut -d '=' -f 2)

if [ "$current_version" == "$latest_version" ]; then
  echo "Zeta is already up to date."; exit
fi

# Print what its updating from and to
echo "Updating Zeta: $current_version -> $latest_version"

# Update the dependency
sed -i "s/^zeta=.*/zeta=$latest_version/" dependencies.properties

echo "Updated Zeta, Please resync gradle now."
