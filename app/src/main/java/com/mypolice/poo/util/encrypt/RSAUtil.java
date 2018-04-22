package com.mypolice.poo.util.encrypt;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA加密方法
 *
 * @author wangjl
 * @version V4.0
 * @date 2016-10-25
 */
public class RSAUtil {

    //密钥对
    private KeyPair keyPair = null;
    //RSA是加密方式 ECB是工作模式 PKCS1Padding是填充模式
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    //    private static final String TRANSFORMATION = "RSA/ECB/NoPadding";
//    public static final String SIGNATURE_ALGORITHM="MD5withRSA";  	// MD5签名
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";    // SHA1签名

    /**
     * 初始化密钥对
     */
    public RSAUtil() {
        try {
            this.keyPair = this.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对
     *
     * @return KeyPair
     * @throws Exception
     */
    private KeyPair generateKeyPair() throws Exception {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(TRANSFORMATION,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低  
            final int KEY_SIZE = 1024;
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            KeyPair keyPair = keyPairGen.genKeyPair();
            return keyPair;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
//          keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        keyBytes = Xml2PemUtil.b64decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//          KeyFactory keyFactory = KeyFactory.getInstance(TRANSFORMATION,
//        		  new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//          RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
//          keyBytes = Base64Utils.decode(key);
        keyBytes = Xml2PemUtil.b64decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(TRANSFORMATION,
                new org.bouncycastle.jce.provider.BouncyCastleProvider());
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 生成公钥
     *
     * @param modulus
     * @param publicExponent
     * @return RSAPublicKey
     * @throws Exception
     */
    public RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) throws Exception {

        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance(TRANSFORMATION,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger(modulus), new BigInteger(publicExponent));
        try {
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }

    }

    /**
     * 生成私钥
     *
     * @param modulus
     * @param privateExponent
     * @return RSAPrivateKey
     * @throws Exception
     */
    private RSAPrivateKey generateRSAPrivateKey(byte[] modulus,
                                                byte[] privateExponent) throws Exception {
        KeyFactory keyFac = null;
        try {
            keyFac = KeyFactory.getInstance(TRANSFORMATION,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            throw new Exception(ex.getMessage());
        }
        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(
                new BigInteger(modulus), new BigInteger(privateExponent));
        try {
            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
        } catch (InvalidKeySpecException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * 加密
     *
     * @param key  加密的密钥
     * @param data 待加密的明文数据
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(Key key, byte[] data) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //获得加密块大小，如:加密前数据为128个byte，而key_size=1024 加密块大小为127 byte,加密后为128个byte;  
            //因此共有2个加密块，第一个127 byte第二个为1个byte  
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(data.length);//获得加密块加密后块大小  
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize)
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
                else
                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
                //这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到ByteArrayOutputStream中  
                //，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了OutputSize所以只好用dofinal方法。  
                i++;
            }
            return raw;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 解密
     *
     * @param key 解密的密钥
     * @param raw 已经加密的数据
     * @return 解密后的明文
     * @throws Exception
     */
    public byte[] decrypt(Key key, byte[] raw) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION,
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(cipher.DECRYPT_MODE, key);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (raw.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //***************************签名和验证*******************************  
    public static byte[] sign(byte[] data, String str_priK) throws Exception {
        PrivateKey priK = getPrivateKey(str_priK);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM,
                new org.bouncycastle.jce.provider.BouncyCastleProvider());
        sig.initSign(priK);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verify(byte[] data, byte[] sign, String str_pubK) throws Exception {
        PublicKey pubK = getPublicKey(str_pubK);
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM,
                new org.bouncycastle.jce.provider.BouncyCastleProvider());
        sig.initVerify(pubK);
        sig.update(data);
        return sig.verify(sign);
    }

    public static boolean verify(byte[] data, byte[] sign, PublicKey pubK) throws Exception {
        Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM,
                new org.bouncycastle.jce.provider.BouncyCastleProvider());
        sig.initVerify(pubK);
        sig.update(data);
        return sig.verify(sign);
    }

    /**
     * 返回公钥
     *
     * @return
     * @throws Exception
     */
    public RSAPublicKey getRSAPublicKey() throws Exception {

        //获取公钥  
        RSAPublicKey pubKey = (RSAPublicKey) keyPair.getPublic();
        //获取公钥系数(字节数组形式)  
        byte[] pubModBytes = pubKey.getModulus().toByteArray();
        //返回公钥公用指数(字节数组形式)  
        byte[] pubPubExpBytes = pubKey.getPublicExponent().toByteArray();
        //生成公钥  
        RSAPublicKey recoveryPubKey = this.generateRSAPublicKey(pubModBytes, pubPubExpBytes);
        return recoveryPubKey;
    }

    /**
     * 获取私钥
     *
     * @return
     * @throws Exception
     */
    public RSAPrivateKey getRSAPrivateKey() throws Exception {

        //获取私钥  
        RSAPrivateKey priKey = (RSAPrivateKey) keyPair.getPrivate();
        //返回私钥系数(字节数组形式)  
        byte[] priModBytes = priKey.getModulus().toByteArray();
        //返回私钥专用指数(字节数组形式)  
        byte[] priPriExpBytes = priKey.getPrivateExponent().toByteArray();
        //生成私钥  
        RSAPrivateKey recoveryPriKey = this.generateRSAPrivateKey(priModBytes, priPriExpBytes);
        return recoveryPriKey;
    }

}
