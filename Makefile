.PHONY: all 
all: 
	rm -rf build || true

	# building release version of libtealeaf
	mkdir -p build/release

	./build_libtealeaf.sh

	cp -aRL modules/native-android/TeaLeaf build/release
	rm -rf build/release/TeaLeaf/jni/core/.git

	# building debug version of libtealeaf
	mkdir -p build/debug

	./build_libtealeaf.sh debug

	cp -aRL modules/native-android/TeaLeaf build/debug
	rm -rf build/debug/TeaLeaf/jni/core/.git

	cp launchClient.js build


