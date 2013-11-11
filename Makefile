DEFAULT: all
BUILD_DIR_RELEASE	= build/release
BUILD_DIR_DEBUG		= build/debug

.PHONY: debug
debug:
	rm -Rf $(BUILD_DIR_DEBUG) || true
	mkdir -p $(BUILD_DIR_DEBUG)
	
	./build_libtealeaf.sh debug

	cp -aRL modules/native-android/TeaLeaf $(BUILD_DIR_DEBUG) 
	rm -rf $(BUILD_DIR_DEBUG)/TeaLeaf/jni/core/.git

.PHONY: release
release:
	rm -Rf $(BUILD_DIR_RELEASE) || true
	mkdir -p $(BUILD_DIR_RELEASE)
	
	./build_libtealeaf.sh
	
	cp -aRL modules/native-android/TeaLeaf $(BUILD_DIR_RELEASE) 
	rm -rf $(BUILD_DIR_RELEASE)/TeaLeaf/jni/core/.git

.PHONY: aux
aux:
	cp modules/native-android/AndroidManifest.xsl build
	cp launchClient.js build

.PHONY: all 
all: debug release aux
