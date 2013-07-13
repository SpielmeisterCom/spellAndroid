#!/bin/sh
ARCH=linux-ia32
MODE=RELEASE

if [ "$1" = "debug" ]; then
        MODE=DEBUG
fi


#ifeq ($(UNAME_S),Darwin)
#        SED = sed -i "" -e
#else

SED="sed -i"

#path to javac
export JAVA_HOME=$PWD/modules/jdk/$ARCH
export PATH=$PATH:$PWD/modules/android-sdk/$ARCH/platform-tools:$PWD/modules/android-sdk/$ARCH/tools:$PWD/modules/android-ndk/$ARCH

TEALEAF_DIR=$PWD/modules/native-android/TeaLeaf

DIR=tmp
TARGET=android-15
NAMESPACE=com.spielmeister
ACTIVITY=xyz
NAME=test

rm -Rf $DIR
android create project --target $TARGET --name $NAME --path $DIR --activity $ACTIVITY --package $NAMESPACE
android update project --target $TARGET --path $DIR --library ../modules/native-android/TeaLeaf

# use default manifest file
xsltproc \
	--stringparam package "$NAMESPACE" \
	--stringparam title "" \
	--stringparam activity "" \
	--stringparam version "" \
	--stringparam versionCode "" \
	--stringparam gameHash "0.0" \
	--stringparam sdkHash "1.0" \
	--stringparam androidHash "1.0" \
	--stringparam develop "false" \
	--stringparam appid "" \
	--stringparam shortname "tealeaf" \
	--stringparam studioName "Acme Inc." \
	--stringparam codeHost "127.0.0.1" \
	--stringparam tcpHost "127.0.0.1" \
	--stringparam codePort "80" \
	--stringparam tcpPort "4747" \
	--stringparam entryPoint "gc.native.launchClient" \
	--stringparam pushUrl "http://127.0.0.1/push/%s/?device=%s&amp;version=%s" \
	--stringparam servicesUrl "http://127.0.0.1" \
	--stringparam disableLogs "true" \
	--stringparam debuggable "false" \
	--stringparam installShortcut "false" \
	--stringparam contactsUrl "" \
	--stringparam orientation "" \
	modules/native-android/AndroidManifest.xsl \
	$TEALEAF_DIR/AndroidManifest.xml \
	>$DIR/AndroidManifest.xml

# patch the activity to start teleaf
JAVA_MAIN=$DIR/src/$(echo $NAMESPACE | tr "." "/")/$ACTIVITY.java
$SED 's/extends Activity/extends com.tealeaf.TeaLeaf/g' $JAVA_MAIN 
$SED 's/setContentView(R.layout.main);/startGame();/g' $JAVA_MAIN

cd $DIR

if [ "$MODE" = "RELEASE" ]; then
	ant release

	jarsigner -sigalg MD5withRSA -digestalg SHA1 \
-keystore $KEYSTORE -storepass $STOREPASS -keypass $KEYPASS \
-signedjar ./bin/$NAME-unaligned.apk ./bin/$NAME-release-unsigned.apk $KEY
else
	ant debug

	adb uninstall $NAMESPACE
	adb install -r ./bin/$NAME-debug.apk
	adb shell am start -n $NAMESPACE/$ACTIVITY 
fi


