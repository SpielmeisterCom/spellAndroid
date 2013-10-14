#!/bin/sh

NDK_ROOT=/opt/android-ndk
SDK_ROOT=/opt/android-sdk
MODE=RELEASE
UNAME=$(uname -sm)

if [ "$UNAME" = "Linux i386" ]; then
	echo "Your arch is not supported!"
	exit 1

elif [ "$UNAME" = "Linux x86_64" ]; then
	ARCH="linux-x64"

elif [ "$UNAME" = "Darwin x86_64" ]; then
	ARCH="osx-x64"
fi

if [ "$1" = "debug" ]; then
	MODE=DEBUG
fi

if [ ! -d /opt/android-ndk ]; then
	echo "The android ndk was not found in $NDK_ROOT"
	echo "You need at least version r9 and can download it under"
	echo "http://developer.android.com/tools/sdk/ndk/index.html"
	exit 1;
fi

if [ ! -d /opt/android-sdk ]; then
	echo "The android sdk was not found in $SDK_ROOT"
	echo "You need at least version 22.2.1 and can download it under"
	echo "http://developer.android.com/sdk/index.html"
	echo "You can find the SDK Tools Only version under DOWNLOAD FOR OTHER PLATFORMS"

	exit 1;
fi

#path to javac
export JAVA_HOME=$PWD/modules/jdk/$ARCH
export PATH=$PATH:$SDK_ROOT/platform-tools:$SDK_ROOT/tools:$NDK_ROOT

#symlink our own native-core repository
rm modules/native-android/TeaLeaf/jni/core
ln -sf ../../../native-core modules/native-android/TeaLeaf/jni/core

cd modules/native-android

android update project -p TeaLeaf --target android-15 --subprojects
android update project -p GCTestApp --target android-15 --subprojects
#node plugins/updatePlugins.js

make clean

cd TeaLeaf
make clean

if [ "$MODE" = "RELEASE" ]; then
	echo "Creating RELEASE build..."

	ndk-build -j 8 RELEASE=1
	# APP_ABI="armeabi armeabi-v7a"
	ant release
else
	echo "Creating DEBUG build..."
	
	ndk-build -j 8 DEBUG=1
	#APP_ABI="armeabi armeabi-v7a"
	ant debug 
fi

