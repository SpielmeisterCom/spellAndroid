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

cd modules/native-android
make setup
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

