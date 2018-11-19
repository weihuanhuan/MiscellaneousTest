/*
 * Copyright (c) 1996, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */


package gm.security.ssl;

import gm.security.util.KeyUtil;
import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLProtocolException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import gm.security.internal.spec.TlsRsaPremasterSecretParameterSpec;

/**
 * This is the client key exchange message (CLIENT --> SERVER) used with
 * all RSA key exchanges; it holds the RSA-encrypted pre-master secret.
 *
 * The message is encrypted using PKCS #1 block type 02 encryption with the
 * server's public key.  The padding and resulting message size is a function
 * of this server's public key modulus size, but the pre-master secret is
 * always exactly 48 bytes.
 *
 */
final class ECCClientKeyExchange extends HandshakeMessage {

    /*
     * The following field values were encrypted with the server's public
     * key (or temp key from server key exchange msg) and are presented
     * here in DECRYPTED form.
     */
    private ProtocolVersion protocolVersion; // preMaster [0,1]
    SecretKey preMaster;
    private byte[] encrypted;           // same size as public modulus

    /*
     * Client randomly creates a pre-master secret and encrypts it
     * using the server's RSA public key; only the server can decrypt
     * it, using its RSA private key.  Result is the same size as the
     * server's public key, and uses PKCS #1 block format 02.
     */
    //JF 客户端处理
    ECCClientKeyExchange(ProtocolVersion protocolVersion,
                         ProtocolVersion maxVersion,
                         SecureRandom secureRandom, PublicKey enPublicKey) throws IOException {
        if (enPublicKey.getAlgorithm().equals("EC") == false) {
            throw new SSLKeyException("Public key not of type EC: " +
                    enPublicKey.getAlgorithm());
        }
        this.protocolVersion = protocolVersion;

        try {
            /*JF
             *  在不使用SunJCE的时候
             *  如下方法可以生成预主密钥，但是应为使用了未签名的KeyGenerator，导致无法加载。
             *  这KeyGenerator本来是 com.gm.crypto.provider.SunJCE 提供的
             *  我将其加载到了 gm.security.ssl.SunJSSE 中在 doRegister 方法中注册。
             *  并包装SunJSSE为 gm.security.provider.internal.GMProvider
             *
             */
            //JF 参考 https://blog.csdn.net/upset_ming/article/details/79880688#comments 4.11
            String s = ((protocolVersion.v == ProtocolVersion.GMSSL10.v) ?
                    "TlsRsaPremasterSecretGenerator" : "");
            KeyGenerator kg = JsseJce.getKeyGenerator(s);
            kg.init(new TlsRsaPremasterSecretParameterSpec(
                    maxVersion.v, protocolVersion.v), secureRandom);
            preMaster = kg.generateKey();

            ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(enPublicKey);
            SM2Engine             sm2Engine       = new SM2Engine();
            ParametersWithRandom  parametersWithRandom          = new ParametersWithRandom(pubKeyParams, secureRandom);
            sm2Engine.init(true, parametersWithRandom);
            byte[] preMasterEncoded = preMaster.getEncoded();
            encrypted = sm2Engine.processBlock(preMasterEncoded, 0, preMasterEncoded.length);
        } catch (GeneralSecurityException e) {
            throw (SSLKeyException) new SSLKeyException
                    ("ECC premaster secret error").initCause(e);
        } catch (InvalidCipherTextException e) {
            throw (SSLKeyException) new SSLKeyException
                    ("ECC premaster secret error").initCause(e);
        }
    }

    /*
     * Retrieving the cipher's provider name for the debug purposes
     * can throw an exception by itself.
     */
    private static String safeProviderName(Cipher cipher) {
        try {
            return cipher.getProvider().toString();
        } catch (Exception e) {
            if (debug != null && Debug.isOn("handshake")) {
                System.out.println("Retrieving The Cipher provider name" +
                        " caused exception " + e.getMessage());
            }
        }
        try {
            return cipher.toString() + " (provider name not available)";
        } catch (Exception e) {
            if (debug != null && Debug.isOn("handshake")) {
                System.out.println("Retrieving The Cipher name" +
                        " caused exception " + e.getMessage());
            }
        }
        return "(cipher/provider names not available)";
    }

    /*
     * Server gets the PKCS #1 (block format 02) data, decrypts
     * it with its private key.
     */
    //JF 服务端处理
    ECCClientKeyExchange(ProtocolVersion currentVersion,
                         ProtocolVersion clientRequestedVersion,
                         SecureRandom secureRandom, HandshakeInStream input,
                         int messageSize, PrivateKey enPrivateKey) throws IOException {

        if (enPrivateKey.getAlgorithm().equals("EC") == false) {
            throw new SSLKeyException("Private key not of type EC: " +
                    enPrivateKey.getAlgorithm());
        }

        if (currentVersion.v >= ProtocolVersion.TLS10.v) {
            encrypted = input.getBytes16();
        } else if (currentVersion.v == ProtocolVersion.GMSSL10.v) {
            //JF GMSSL
            encrypted = input.getBytes16();
        } else {
            encrypted = new byte [messageSize];
            if (input.read(encrypted) != messageSize) {
                throw new SSLProtocolException(
                        "SSL: read PreMasterSecret: short read");
            }
        }

        byte[] decrypted = null;
        try {
            ECPrivateKeyParameters priKeyParams         = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(enPrivateKey);
            SM2Engine              sm2Engine            = new SM2Engine();
            sm2Engine.init(false, priKeyParams);

            boolean failed = false;
            try {
                decrypted = sm2Engine.processBlock(encrypted, 0, encrypted.length);
            } catch (InvalidCipherTextException e) {
                // Note: decrypted == null
                failed = true;
            }

            //JF 初步检查解密后的数据是否正确，不正确则生成一份替代书籍，避免decrypted == null 的问题。
            decrypted = KeyUtil.checkTlsPreMasterSecretKey(
                    clientRequestedVersion.v, currentVersion.v,
                    secureRandom, decrypted, failed);

            preMaster = generatePreMasterSecret(
                    clientRequestedVersion.v, currentVersion.v,
                    decrypted, secureRandom);
        } catch (InvalidKeyException ibk) {
            // the message is too big to process with RSA
            throw new SSLException(
                "Unable to process PreMasterSecret", ibk);
        } catch (Exception e) {
            // unlikely to happen, otherwise, must be a provider exception
            if (debug != null && Debug.isOn("handshake")) {
                System.out.println("ECC premaster secret decryption error:");
                e.printStackTrace(System.out);
            }
            throw new RuntimeException("Could not generate dummy secret", e);
        }
    }

    // generate a premaster secret with the specified version number
    @SuppressWarnings("deprecation")
    private static SecretKey generatePreMasterSecret(
            int clientVersion, int serverVersion,
            byte[] encodedSecret, SecureRandom secureRandom) {

        if (debug != null && Debug.isOn("handshake")) {
            System.out.println("Generating a premaster secret");
        }

        try {
            String s = ((clientVersion == ProtocolVersion.GMSSL10.v) ?
                    "TlsRsaPremasterSecretGenerator" : "");
            KeyGenerator kg = JsseJce.getKeyGenerator(s);
            kg.init(new TlsRsaPremasterSecretParameterSpec(
                    clientVersion, serverVersion, encodedSecret),
                    secureRandom);
            return kg.generateKey();
        } catch (InvalidAlgorithmParameterException |
                NoSuchAlgorithmException iae) {
            // unlikely to happen, otherwise, must be a provider exception
            if (debug != null && Debug.isOn("handshake")) {
                System.out.println("ECC premaster secret generation error:");
                iae.printStackTrace(System.out);
            }
            throw new RuntimeException("Could not generate premaster secret", iae);
        }
    }

    @Override
    int messageType() {
        return ht_client_key_exchange;
    }

    @Override
    int messageLength() {
        if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
            return encrypted.length + 2;
        } else if (protocolVersion.v == ProtocolVersion.GMSSL10.v){
            //JF 数据本身 + 数据域长度
            return encrypted.length + 2;
        } else {
            return encrypted.length;
        }
    }

    @Override
    void send(HandshakeOutStream s) throws IOException {
        if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
            s.putBytes16(encrypted);
        } else if (protocolVersion.v == ProtocolVersion.GMSSL10.v){
            //JF 先写消息域长度，再写消息本身
            s.putBytes16(encrypted);
        } else {
            s.write(encrypted);
        }
    }

    @Override
    void print(PrintStream s) throws IOException {
        String version = "version not available/extractable";

        byte[] ba = preMaster.getEncoded();
        if (ba != null && ba.length >= 2) {
            version = ProtocolVersion.valueOf(ba[0], ba[1]).name;
        }

        s.println("*** ClientKeyExchange, ECC PreMasterSecret, " + version);
    }
}
