package vn.zalopay.zas.common.crypto;

import java.nio.ByteBuffer;
/**
 * Created by phucvt Date: 17/03/2022
 */
class JNI {
    static native long create_hasher();

    static native void destroy_hasher(long hasher);

    static native void blake3_hasher_init(long hasher);

    static native void blake3_hasher_init_keyed(long hasher, ByteBuffer byteBuff);

    static native void blake3_hasher_init_derive_key(long hasher, String context);

    static native void blake3_hasher_update(long hasher, ByteBuffer byteBuff, int input_len);

    static native void blake3_hasher_finalize(long hasher, ByteBuffer byteBuff, int out_len);
}
