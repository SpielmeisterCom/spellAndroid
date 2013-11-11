DEFAULT: all
BUILD_DIR_RELEASE	= build/release
BUILD_DIR_DEBUG		= build/debug

.PHONY: prepare
prepare:
	. ./setup_env.sh && \
cd modules/native-android && \
android update project -p TeaLeaf --target android-15 --subprojects && \
android update project -p GCTestApp --target android-15 --subprojects && \
make clean

.PHONY: debug
debug: prepare
	rm -Rf $(BUILD_DIR_DEBUG) || true
	mkdir -p $(BUILD_DIR_DEBUG)
	
	#copy native-android and native-core into one location
	cp -aR modules/native-android/TeaLeaf $(BUILD_DIR_DEBUG)
	mkdir $(BUILD_DIR_DEBUG)/TeaLeaf/jni/core
	cp -aR modules/native-core/* $(BUILD_DIR_DEBUG)/TeaLeaf/jni/core

	. ./setup_env.sh && \
cd $(BUILD_DIR_DEBUG)/TeaLeaf && \
ndk-build -j 8 DEBUG=1 && \
ant debug


.PHONY: release
release: prepare
	rm -Rf $(BUILD_DIR_RELEASE) || true
	mkdir -p $(BUILD_DIR_RELEASE)
	
	#copy native-android and native-core into one location
	cp -aR modules/native-android/TeaLeaf $(BUILD_DIR_RELEASE)
	mkdir $(BUILD_DIR_RELEASE)/TeaLeaf/jni/core
	cp -aR modules/native-core/* $(BUILD_DIR_RELEASE)/TeaLeaf/jni/core
	
	cp -aRL modules/native-android/TeaLeaf $(BUILD_DIR_RELEASE) 
	rm -rf $(BUILD_DIR_RELEASE)/TeaLeaf/jni/core/.git

	. ./setup_env.sh && \
cd $(BUILD_DIR_RELEASE)/TeaLeaf && \
ndk-build -j 8 RELEASE=1 && \
ant release


.PHONY: aux
aux:
	cp modules/native-android/AndroidManifest.xsl build
	cp launchClient.js build

.PHONY: all 
all: debug release aux
