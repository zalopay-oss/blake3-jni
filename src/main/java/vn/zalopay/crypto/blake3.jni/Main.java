package vn.zalopay.zas.common.crypto;

import vn.zalopay.zas.common.crypto.exception.InvalidNativeOutput;

import java.util.Base64;

/** Created by phucvt Date: 29/03/2022 */
public class Main {
  public static void main(String[] args) throws InvalidNativeOutput {
    try (NativeBlake3 hasher = new NativeBlake3()) {

      hasher.initDefault();

      // read data
      byte[] data = "123".getBytes();
      hasher.update(data);

      // Finalize the hash. BLAKE3 output lenght defaults to 32 bytes
      byte[] output = hasher.getOutput();

      System.out.println(Base64.getEncoder().encodeToString(output));
    }
  }
}
