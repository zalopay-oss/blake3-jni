package vn.zalopay.zas.common.crypto;

import vn.zalopay.zas.common.crypto.exception.InvalidNativeOutput;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** Created by phucvt Date: 17/03/2022 */
public class NativeBlake3 implements AutoCloseable {
  public static final int KEY_LEN = 32;
  public static final int OUT_LEN = 32;
  public static final int BLOCK_LEN = 64;
  public static final int CHUNK_LEN = 1024;
  public static final int MAX_DEPTH = 54;
  public static final int MAX_SIMD_DEGREE = 16;

  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock r = rwl.readLock();
  private final Lock w = rwl.writeLock();
  private static ThreadLocal<ByteBuffer> nativeByteBuffer = new ThreadLocal<ByteBuffer>();

  private static final boolean enabled;

  private long hasher = -1;

  static {
    boolean isEnabled = false;
    try {
      //            NativeLoader.loadLibrary("blake3");
      Blake3LibLoader.loadLibrary();
      isEnabled = true;
    } catch (java.io.IOException e) {
      System.out.println("UnsatisfiedLinkError: " + e.toString());
    } finally {
      enabled = isEnabled;
    }
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public NativeBlake3() throws IllegalStateException {
    NativeBlake3Util.checkState(enabled);
    long initHasher;
    initHasher = JNI.create_hasher();
    NativeBlake3Util.checkState(initHasher != 0);
    hasher = initHasher;
  }

  public boolean isValid() {
    return hasher != -1;
  }

  @Override
  public void close() {
    if (isValid()) {
      cleanUp();
    }
  }

  public void initDefault() {
    r.lock();
    try {
      JNI.blake3_hasher_init(getHasher());
    } finally {
      r.unlock();
    }
  }

  public void initKeyed(byte[] key) {
    NativeBlake3Util.checkArgument(key.length == KEY_LEN);
    ByteBuffer byteBuff = nativeByteBuffer.get();
    if (byteBuff == null || byteBuff.capacity() < key.length) {
      byteBuff = ByteBuffer.allocateDirect(key.length);
      byteBuff.order(ByteOrder.nativeOrder());
      nativeByteBuffer.set(byteBuff);
    }
    byteBuff.rewind();
    byteBuff.put(key);

    w.lock();
    try {
      JNI.blake3_hasher_init_keyed(getHasher(), byteBuff);
    } finally {
      w.unlock();
    }
  }

  public void initDeriveKey(String context) {
    r.lock();
    try {
      JNI.blake3_hasher_init_derive_key(getHasher(), context);
    } finally {
      r.unlock();
    }
  }

  public void update(byte[] data) {
    ByteBuffer byteBuff = nativeByteBuffer.get();

    if (byteBuff == null || byteBuff.capacity() < data.length) {
      byteBuff = ByteBuffer.allocateDirect(data.length);
      byteBuff.order(ByteOrder.nativeOrder());
      nativeByteBuffer.set(byteBuff);
    }
    byteBuff.rewind();
    byteBuff.put(data);
    w.lock();
    try {
      JNI.blake3_hasher_update(getHasher(), byteBuff, data.length);
    } finally {
      w.unlock();
    }
  }

  public byte[] getOutput() throws InvalidNativeOutput {
    return getOutput(OUT_LEN);
  }

  public byte[] getOutput(int outputLength) throws  InvalidNativeOutput {
    ByteBuffer byteBuff = nativeByteBuffer.get();

    if (byteBuff == null || byteBuff.capacity() < outputLength) {
      byteBuff = ByteBuffer.allocateDirect(outputLength);
      byteBuff.order(ByteOrder.nativeOrder());
      nativeByteBuffer.set(byteBuff);
    }
    byteBuff.rewind();

    w.lock();
    try {
      JNI.blake3_hasher_finalize(getHasher(), byteBuff, outputLength);
    } finally {
      w.unlock();
    }

    byte[] retByteArray = new byte[outputLength];
    byteBuff.get(retByteArray);

    NativeBlake3Util.checkOutput(
        retByteArray.length == outputLength,
        "Output size produced by lib doesnt match:"
            + retByteArray.length
            + " expected:"
            + outputLength);

    return retByteArray;
  }

  private long getHasher() throws IllegalStateException {
    NativeBlake3Util.checkState(isValid());
    return hasher;
  }

  private void cleanUp() {
    w.lock();
    try {
      JNI.destroy_hasher(getHasher());
    } finally {
      hasher = -1;
      w.unlock();
    }
  }
}
