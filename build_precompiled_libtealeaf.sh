#!/bin/sh

rm -rf build

echo "Building release version of libtealeaf"
mkdir -p build/release

./build_libtealeaf.sh

cp -ar modules/native-android/TeaLeaf/ build/release

echo "Building debug version of libtealeaf"
mkdir -p build/debug

./build_libtealeaf.sh debug

cp -ar modules/native-android/TeaLeaf/ build/debug

