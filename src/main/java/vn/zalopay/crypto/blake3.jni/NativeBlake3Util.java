package vn.zalopay.zas.common.crypto;

import vn.zalopay.zas.common.crypto.exception.AssertFailException;
import vn.zalopay.zas.common.crypto.exception.InvalidNativeOutput;

/** Created by phucvt Date: 17/03/2022 */
public class NativeBlake3Util {

  private NativeBlake3Util() {}

  public static void assertEquals(boolean val, boolean val2, String message)
      throws AssertFailException {
    if (val != val2) throw new AssertFailException("FAIL: " + message);
    else System.out.println("PASS: " + message);
  }

  public static void checkState(boolean expression) {
    if (!expression) {
      throw new IllegalStateException();
    }
  }

  public static void checkArgument(boolean expression) {
    if (!expression) {
      throw new IllegalArgumentException();
    }
  }

  public static void checkOutput(boolean expression, String message) throws InvalidNativeOutput {
    if (!expression) {
      throw new InvalidNativeOutput(message);
    }
  }
}
