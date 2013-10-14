.DEFAULT: release

.PHONY: clean
	rm -rf build || true

.PHONY: release
release: clean
	# building release version of libtealeaf
	mkdir -p build

	./build_libtealeaf.sh

	cp -aRL modules/native-android/TeaLeaf build
	rm -rf build/TeaLeaf/jni/core/.git

.PHONY: debug
debug: clean
	# building debug version of libtealeaf
	mkdir -p build

	./build_libtealeaf.sh debug

	cp -aRL modules/native-android/TeaLeaf build
	rm -rf build/TeaLeaf/jni/core/.git

	cp launchClient.js build/

