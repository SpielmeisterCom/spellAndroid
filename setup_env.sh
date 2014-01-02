#!/bin/sh

NDK_ROOT=/opt/android-ndk
SDK_ROOT=/opt/android-sdk
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

if [ ! -x "$JAVA_HOME"/bin/java -o ! -x "$JAVA_HOME"/bin/javac ]; then 
	echo "The JAVA_HOME environment variable is not defined correctly"
	echo "Please set your JAVA_HOME variable to a valid JDK"	
	exit 1
fi

export PATH=$PATH:$SDK_ROOT/platform-tools:$SDK_ROOT/tools:$NDK_ROOT

echo Enviroment setup:
echo JAVA_HOME: $JAVA_HOME
echo PATH: $PATH

