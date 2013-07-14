#!/bin/sh

MODE=RELEASE
UNAME=$(uname -sm)

if [ "$UNAME" = "Linux i386" ]; then
	ARCH="linux-ia32"

elif [ "$UNAME" = "Linux x86_64" ]; then
	ARCH="linux-ia32"

elif [ "$UNAME" = "Darwin x86_64" ]; then
	ARCH="osx-x64"
fi

if [ "$1" = "debug" ]; then
	MODE=DEBUG
fi

#path to javac
export JAVA_HOME=$PWD/modules/jdk/$ARCH
export PATH=$PATH:$PWD/modules/android-sdk/$ARCH/platform-tools:$PWD/modules/android-sdk/$ARCH/tools:$PWD/modules/android-ndk/$ARCH

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
	ant release
else
	echo "Creating DEBUG build..."
	
	ndk-build -j 8 DEBUG=1
	ant debug 
fi

