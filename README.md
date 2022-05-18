# Blake3 JNI

## Overview

- C Bindings for the API of [BLAKE3](https://github.com/BLAKE3-team/BLAKE3) cryptographic hash function in Java.

## Usage

- Import maven from [Artifactory](https://artifactory.zalopay.com.vn/artifactory/paycon/vn/zalopay/zas/common/crypto/blake3-jni/).

```java
<dependency>
  <groupId>vn.zalopay.zas.common.crypto</groupId>
  <artifactId>blake3-jni</artifactId>
  <version>1.0.0</version>
</dependency>
```

## API

> The library has the same [c api](https://github.com/BLAKE3-team/BLAKE3/tree/master/c):

```java
  // Verifies the library is connected
  public static boolean isEnabled() 
  
  // Creates a NativeBlake3 instance and a equivalent one as `blake3_hasher` in c.
  public NativeBlake3() throws IllegalStateException 
  
  // Initializers
  public void initDefault()
  public void initKeyed(byte[] key)
  public void initDeriveKey(String context)
  
  // Add input to the hasher. This can be called any number of times.
  public void update(byte[] data)
  
  // Equivalent to blake3_hasher_finalize in C. you can keep adding data after calling this. 
  public byte[] getOutput() throws InvalidNativeOutput 
  public byte[] getOutput(int outputLength) throws InvalidNativeOutput
```

## Example

- _Note_: Using in __try-with-resources__ block to auto-close resource allocated in
  memory in C.

```java
  // Initialize the hasher
  try (NativeBlake3 hasher = new NativeBlake3()) {
      hasher.initDefault();
  
      // read data
      byte[] data = "example data".getBytes();
      hasher.update(data);
      
      // more data
      byte[] moredata = "more data".getBytes();
      hasher.update(moredata);
  
      // Finalize the hash. BLAKE3 output length defaults to 32 bytes
      byte[] output = hasher.getOutput();
  }
```

## Build from scratch

- Currently, this library is build for __Linux__ and __Mac OS (M1)__. detect the OS and load the correct binary.

- Linux's library is integrated build with Gitlab Runner pipeline. For MacOS build, follow the following guides.

- _For more environment, see [here](https://github.com/BLAKE3-team/BLAKE3/tree/master/c#building)._


#### Mac OS M1

- Requirements:
  - Java
  - Maven
  - GCC


- Run the command to build native shared library.

```bash
$ ./scripts/make.sh

# Output will be native library binaries in `resources` folder.
# libblake3.sylib
```

- Build jar with Maven.

```bash
$ ./scripts/build.sh
```

