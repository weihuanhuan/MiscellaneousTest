package crypto.gm;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.gm.SM2P256V1Curve;

/**
 * Created by JasonFitch on 8/15/2020.
 */
public class SM2Cipher {

    public static final SM2P256V1Curve CURVE = new SM2P256V1Curve();

    public final static BigInteger SM2_ECC_GX =
            new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
    public final static BigInteger SM2_ECC_GY =
            new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
    public static final ECPoint G_POINT = CURVE.createPoint(SM2_ECC_GX, SM2_ECC_GY);

    public final static BigInteger SM2_ECC_N = CURVE.getOrder();
    public final static BigInteger SM2_ECC_H = CURVE.getCofactor();
    public static final ECDomainParameters EC_DOMAIN_PARAMETERS =
            new ECDomainParameters(CURVE, G_POINT, SM2_ECC_N, SM2_ECC_H);

    public static AsymmetricCipherKeyPair generateKeyPair() {
        ECKeyGenerationParameters generationParameters = new ECKeyGenerationParameters(EC_DOMAIN_PARAMETERS, getSecureRandom());
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        keyPairGenerator.init(generationParameters);
        AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public static byte[] encrypt(CipherParameters parameters, byte[] originalBytes) throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine();
        ParametersWithRandom parametersWithRandom = new ParametersWithRandom(parameters, getSecureRandom());
        engine.init(true, parametersWithRandom);
        byte[] encryptedBytes = engine.processBlock(originalBytes, 0, originalBytes.length);
        return encryptedBytes;
    }

    public static byte[] decrypt(CipherParameters parameters, byte[] encryptedBytes) throws InvalidCipherTextException {
        SM2Engine engine = new SM2Engine();
        engine.init(false, parameters);
        byte[] decryptedBytes = engine.processBlock(encryptedBytes, 0, encryptedBytes.length);
        return decryptedBytes;
    }

    public static SecureRandom getSecureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom;
    }
}
