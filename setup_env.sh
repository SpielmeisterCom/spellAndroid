#!/bin/sh

if [ "$1" = "debug" ]; then
	MODE=DEBUG
fi

if [ ! -x "$ANDROID_NDK_PATH"/ndk-build ]; then
	echo "The android ndk was not found in $ANDROID_NDK_PATH"
	echo "Please set your ANDROID_NDK_PATH environment vairable to a valid android ndk"
	echo "You need at least version r9 and can download it under"
	echo "http://developer.android.com/tools/sdk/ndk/index.html"
	exit 1;
fi

if [ ! -x "$ANDROID_SDK_PATH"/tools/android ]; then
	echo "The android sdk was not found in $ANDROID_SDK_PATH"
	echo "Please set your ANDROID_SDK_PATH environment vairable to a valid android sdk"
	echo "You need at least version 22.2.1 and can download it under"
	echo "http://developer.android.com/sdk/index.html"
	echo "You can find the SDK Tools Only version under DOWNLOAD FOR OTHER PLATFORMS"

	exit 1;
fi

if [ ! -x "$JAVA_HOME"/bin/java -o ! -x "$JAVA_HOME"/bin/javac ]; then 
	echo "The JAVA_HOME environment variable is not defined correctly"
	echo "Please set your JAVA_HOME variable to a valid JDK"	
	exit 1
fi

export PATH=$PATH:$ANDROID_SDK_PATH/platform-tools:$ANDROID_SDK_PATH/tools:$ANDROID_NDK_PATH

echo Enviroment setup:
echo JAVA_HOME: $JAVA_HOME
echo PATH: $PATH

