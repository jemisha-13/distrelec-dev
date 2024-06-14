#!/bin/bash

# Config, Paths
TC_SRCDIR="resources/terrific-module-tcdefault"
TC_MOD_DIR="web/webroot/WEB-INF/terrific/modules"
TC_TAG_DIR="web/webroot/WEB-INF/tags/terrific/modules"
TC_STYLES_DIR="css"
TC_SCRIPTS_DIR="js"
TC_DEFAULTFILE="tcdefault"
TC_DEFAULT_MODNAME_LOWER="tcdefault"
TC_DEFAULT_MODNAME_UCFIRST="Tcdefault"
TC_DEFAULT_SKINNAME_LOWER="skindefault"
TC_DEFAULT_SKINNAME_UCFIRST="Skindefault"
TC_TRIGGER="web/webroot/WEB-INF/terrific/triggerfile"

# Arguments
TC_MODNAME="$1"
TC_SKINNAME="$2"

# Sanitize inputs
TC_MODNAME_LOWER=$(echo $TC_MODNAME | awk '{ print tolower($0); } ')
TC_MODNAME_UCFIRST=$(echo $TC_MODNAME_LOWER | awk '{ printf("%s%s", toupper( substr($0,1,1) ), substr($0,2)); } ')
TC_SKINNAME_LOWER=$(echo $TC_SKINNAME | awk '{ print tolower($0); } ')
TC_SKINNAME_UCFIRST=$(echo $TC_SKINNAME_LOWER | awk '{ printf("%s%s", toupper( substr($0,1,1) ), substr($0,2)); } ')

TC_TARGET_TAG="${TC_TAG_DIR}/${TC_MODNAME_LOWER}.tag"
TC_TARGET_DIR="${TC_MOD_DIR}/${TC_MODNAME_LOWER}"
TC_TARGET_TPL="${TC_TARGET_DIR}/${TC_MODNAME_LOWER}.jsp"
TC_TARGET_MOD_STYLES="${TC_TARGET_DIR}/${TC_STYLES_DIR}/${TC_MODNAME_LOWER}.less"
TC_TARGET_MOD_SCRIPTS="${TC_TARGET_DIR}/${TC_SCRIPTS_DIR}/${TC_MODNAME_LOWER}.js"


echo ".........................................................................."
echo ""

echo "# Create Module Files:"

# Create Module Directories
mkdir -v -p $TC_TARGET_DIR/$TC_STYLES_DIR $TC_TARGET_DIR/$TC_SCRIPTS_DIR

# Copy Module Files and Tag
cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.tag  $TC_TARGET_TAG
cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.jsp  $TC_TARGET_TPL
cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.less $TC_TARGET_MOD_STYLES
cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.js   $TC_TARGET_MOD_SCRIPTS
echo "-> done"
echo ""

# Replace Module Name
echo "# Replace Module Name:"
sed -i '' "s/${TC_DEFAULT_MODNAME_LOWER}/${TC_MODNAME_LOWER}/g" ${TC_TARGET_TAG}
sed -i '' "s/${TC_DEFAULT_MODNAME_UCFIRST}/${TC_MODNAME_UCFIRST}/g" ${TC_TARGET_TPL}
sed -i '' "s/${TC_DEFAULT_MODNAME_LOWER}/${TC_MODNAME_LOWER}/g" ${TC_TARGET_MOD_STYLES}
sed -i '' "s/${TC_DEFAULT_MODNAME_UCFIRST}/${TC_MODNAME_UCFIRST}/g" ${TC_TARGET_MOD_SCRIPTS}
echo "-> done"
echo ""

# Skin
if [ ! -z "$TC_SKINNAME_LOWER" ]
then
	TC_TARGET_SKIN_STYLES="${TC_TARGET_DIR}/${TC_STYLES_DIR}/${TC_MODNAME_LOWER}.skin.${TC_SKINNAME_LOWER}.less"
	TC_TARGET_SKIN_SCRIPTS="${TC_TARGET_DIR}/${TC_SCRIPTS_DIR}/${TC_MODNAME_LOWER}.skin.${TC_SKINNAME_LOWER}.js"

	echo "# Create Skin Files:"
	# Copy Skin Files
	cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.skin.$TC_DEFAULT_SKINNAME_LOWER.less $TC_TARGET_SKIN_STYLES
	cp -n -v $TC_SRCDIR/$TC_DEFAULTFILE.skin.$TC_DEFAULT_SKINNAME_LOWER.js   $TC_TARGET_SKIN_SCRIPTS
	echo "-> done"
	echo ""

	# Replace Skin Name
	echo "# Replace Skin Name:"
	sed -i '' "s/${TC_DEFAULT_MODNAME_LOWER}/${TC_MODNAME_LOWER}/g" ${TC_TARGET_SKIN_STYLES}
	sed -i '' "s/${TC_DEFAULT_SKINNAME_LOWER}/${TC_SKINNAME_LOWER}/g" ${TC_TARGET_SKIN_STYLES}
	sed -i '' "s/${TC_DEFAULT_MODNAME_UCFIRST}/${TC_MODNAME_UCFIRST}/g" ${TC_TARGET_SKIN_SCRIPTS}
	sed -i '' "s/${TC_DEFAULT_SKINNAME_UCFIRST}/${TC_SKINNAME_UCFIRST}/g" ${TC_TARGET_SKIN_SCRIPTS}
	echo "-> done"
	echo ""
fi

# Touch trigger
echo "# Touch Trigger"
touch $TC_TRIGGER
echo "-> done"
echo ""
