package com.mypolice.poo.util.encrypt;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Xml密钥/公钥转换为pem格式
 * @author wangjl
 * @date 2016.10.26
 */
public class Xml2PemUtil {

	private static final int PRIVATE_KEY = 1;  
	private static final int PUBLIC_KEY = 2;  
	private static final String[] PRIVATE_KEY_XML_NODES =  { "Modulus", "Exponent", "P", "Q", "DP", "DQ", "InverseQ", "D" };  
	private static final String[] PUBLIC_KEY_XML_NODES =  { "Modulus", "Exponent" };  
	
	private static int getKeyType(Document xmldoc) {  
		  
	    Node root = xmldoc.getFirstChild();  
	    if (!root.getNodeName().equals("RSAKeyValue")) {  
	      System.out.println("Expecting <RSAKeyValue> node, encountered <" + root.getNodeName() + ">");  
	      return 0;  
	    }  
	    NodeList children = root.getChildNodes();  
	    if (children.getLength() == PUBLIC_KEY_XML_NODES.length) {  
	      return PUBLIC_KEY;  
	    }   
	    return PRIVATE_KEY;  
	  
	  }  
	  
	  private static boolean checkXMLRSAKey(int keyType, Document xmldoc) {  
	  
	    Node root = xmldoc.getFirstChild();  
	    NodeList children = root.getChildNodes();  
	    String[] wantedNodes = {};  
	    if (keyType == PRIVATE_KEY) {  
	      wantedNodes = PRIVATE_KEY_XML_NODES;  
	    } else {  
	      wantedNodes = PUBLIC_KEY_XML_NODES;  
	    }  
	    for (int j = 0; j < wantedNodes.length; j++) {  
	      String wantedNode = wantedNodes[j];  
	      boolean found = false;  
	      for (int i = 0; i < children.getLength(); i++) {  
	        if (children.item(i).getNodeName().equals(wantedNode)) {  
	          found = true;  
	          break;  
	        }  
	      }  
	      if (!found) {  
	        System.out.println("Cannot find node <" + wantedNode + ">");  
	        return false;  
	      }  
	    }  
	    return true;  
	  
	  }  
	  
	  public static String convertXMLRSAPrivateKeyToPEM(Document xmldoc) {  
	  
	    Node root = xmldoc.getFirstChild();  
	    NodeList children = root.getChildNodes();  
	  
	    BigInteger modulus = null, exponent = null, primeP = null, primeQ = null,   
	               primeExponentP = null, primeExponentQ = null,   
	               crtCoefficient = null, privateExponent = null;  
	  
	    for (int i = 0; i < children.getLength(); i++) {  
	  
	      Node node = children.item(i);  
	      String textValue = node.getTextContent();  
	      if (node.getNodeName().equals("Modulus")) {  
	        modulus = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("Exponent")) {  
	        exponent = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("P")) {  
	        primeP = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("Q")) {  
	        primeQ = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("DP")) {  
	        primeExponentP = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("DQ")) {  
	        primeExponentQ = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("InverseQ")) {  
	        crtCoefficient = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("D")) {  
	        privateExponent = new BigInteger(b64decode(textValue));  
	      }  
	  
	    }  
	  
	    try {  
	  
	      RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec (  
	        modulus, exponent, privateExponent, primeP, primeQ,   
	        primeExponentP, primeExponentQ, crtCoefficient);  
	  
	      KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	      PrivateKey key = keyFactory.generatePrivate(keySpec);  
	      return b64encode(key.getEncoded());  
	  
	    } catch (Exception e) {  
	      System.out.println(e);  
	    }  
	    return null;  
	  
	  }  
	  
	  public static String convertXMLRSAPublicKeyToPEM(Document xmldoc) {  
	  
	    Node root = xmldoc.getFirstChild();  
	    NodeList children = root.getChildNodes();  
	  
	    BigInteger modulus = null, exponent = null;  
	  
	    for (int i = 0; i < children.getLength(); i++) {  
	  
	      Node node = children.item(i);  
	      String textValue = node.getTextContent();  
	      if (node.getNodeName().equals("Modulus")) {  
	        modulus = new BigInteger(b64decode(textValue));  
	      } else if (node.getNodeName().equals("Exponent")) {  
	        exponent = new BigInteger(b64decode(textValue));  
	      }  
	  
	    }  
	  
	    try {  
	  
	      RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);  
	  
	      KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	      PublicKey key = keyFactory.generatePublic(keySpec);  
	      return b64encode(key.getEncoded());  
	  
	    } catch (Exception e) {  
	      System.out.println(e);  
	    }  
	    return null;  
	  
	  }  
	  
	  public static PublicKey convertXMLRSAPublicKey(Document xmldoc) {  
	  
	    Node root = xmldoc.getFirstChild();  
	    NodeList children = root.getChildNodes();  
	  
	    BigInteger modulus = null, exponent = null;  
	  
	    for (int i = 0; i < children.getLength(); i++) {  
	  
	      Node node = children.item(i);  
	      String textValue = node.getTextContent();  
	      if (node.getNodeName().equals("Modulus")) {  
	        modulus = new BigInteger(1, b64decode(textValue));  
	      } else if (node.getNodeName().equals("Exponent")) {  
	        exponent = new BigInteger(1, b64decode(textValue));  
	      }  
	  
	    }  
	  
	    try {  
	  
	      RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);  
	  
	      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	      PublicKey key = keyFactory.generatePublic(keySpec);  
	      return key;  
	  
	    } catch (Exception e) {  
	      System.out.println(e);  
	    }  
	    return null;  
	  
	  }  
	  
	  private static Document parseXMLFile(String filename) {  
	    try {  
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	      DocumentBuilder builder = factory.newDocumentBuilder();  
	      Document document = builder.parse( new File(filename) );  
	      return document;  
	    } catch(Exception e) {  
	      System.err.println(e);   
	      return null;  
	    }     
	  }  
	  
	  public static final String b64encode(byte[] data) {
	    sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder() ;  
	    String b64str = enc.encodeBuffer(data).trim();  
	    return b64str;  
	  }

		public static final byte[] b64decode(String data) {
	    try {  
	      sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder() ;  
	      byte[] bytes = dec.decodeBuffer(data.trim());  
	      return bytes;  
	    } catch (IOException e) {  
	      System.out.println("Exception caught when base64 decoding!" + e.toString());  
	    }  
	    return null;  
	  }  
	
}
