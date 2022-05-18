#!bin/sh
cd ..

RESOURCE_PATH=src/main/resources

git submodule init
git submodule update

javac src/main/java/vn/zalopay/crypto/JNI.java -h src/main/c

if uname -a | grep -q -i darwin; then
  echo -e "🤘 Build the shared library for DARWIN !!!"

  # darwin (arm-64) - Mac M1
  gcc -O3 -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -I${JAVA_HOME}/include/darwin -IBLAKE3/c -fPIC -shared -o ${RESOURCE_PATH}/libblake3.so -DBLAKE3_USE_NEON=1 \
    src/main/c/vn_zalopay_crypto_JNI.c \
    BLAKE3/c/blake3.c \
    BLAKE3/c/blake3_dispatch.c \
    BLAKE3/c/blake3_portable.c \
    BLAKE3/c/blake3_neon.c

  mv ${RESOURCE_PATH}/libblake3.so ${RESOURCE_PATH}/libblake3.dylib

else
  echo -e "🤘 Build the shared library for LINUX !!!"

  # linux
  gcc -z noexecstack -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -I${JAVA_HOME}/include/darwin -IBLAKE3/c -fPIC -shared -o ${RESOURCE_PATH}/libblake3.so \
    src/main/c/vn_zalopay_crypto_JNI.c \
    BLAKE3/c/blake3.c \
    BLAKE3/c/blake3_dispatch.c \
    BLAKE3/c/blake3_portable.c \
    BLAKE3/c/blake3_sse41_x86-64_unix.S \
    BLAKE3/c/blake3_avx2_x86-64_unix.S \
    BLAKE3/c/blake3_avx512_x86-64_unix.S
fi
