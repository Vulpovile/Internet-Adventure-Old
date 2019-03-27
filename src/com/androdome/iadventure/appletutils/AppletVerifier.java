package com.androdome.iadventure.appletutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AppletVerifier
{
	public static enum Signage {
		UNSIGNED, SELFSIGNED, SIGNED, CORRUPT, EXPIRED
	}

	public static Signage verifySignage(URL archive) {
		URL jarUrl;
		try
		{
			jarUrl = new URL("jar:" + archive.toString() + "!/");
		}
		catch (MalformedURLException e1)
		{
			e1.printStackTrace();
			return Signage.CORRUPT;
		}
		JarFile jf = null;
		try
		{
			Vector<JarEntry> entriesVec = new Vector<JarEntry>();
			jf = ((JarURLConnection) jarUrl.openConnection()).getJarFile();
			Manifest man = jf.getManifest();
			if (man == null) return Signage.UNSIGNED;
			else
			{
				try
				{
					byte[] buffer = new byte[8192];
					Enumeration<JarEntry> entries = jf.entries();

					while (entries.hasMoreElements())
					{
						JarEntry je = (JarEntry) entries.nextElement();

						// Skip directories.
						if (je.isDirectory()) continue;

						entriesVec.addElement(je);
						InputStream is = jf.getInputStream(je);

						// Read in each jar entry. A security exception will
						// be thrown if a signature/digest check fails.
						int n;
						while ((n = is.read(buffer, 0, buffer.length)) != -1)
						{
							// Don't care
						}
						is.close();
					}
					
					
					Enumeration<JarEntry> e = entriesVec.elements();
					ArrayList<X509Certificate> certList = getTrustedCerts();
				    while (e.hasMoreElements()) {
					JarEntry je = (JarEntry) e.nextElement();

					// Every file must be signed except files in META-INF.
					Certificate[] certs = je.getCertificates();
					if ((certs == null) || (certs.length == 0)) {
					    if (!je.getName().startsWith("META-INF"))
						return Signage.UNSIGNED;
					} else {
					    // Check whether the file is signed by the expected
					    // signer. The jar may be signed by multiple signers.
					    // See if one of the signers is 'targetCert'.
					    int startIndex = 0;
					    X509Certificate[] certChain;

					    while ((certChain = getAChain(certs, startIndex)) != null) {
					    	for(int i = 0; i < certChain.length; i++)
					    	{
						    	System.out.println(certChain[i].getIssuerDN().getName());
						    	for(X509Certificate tCert : certList)
						    	{
						    		System.out.println(tCert.getType());
						    		try{
						    			certChain[i].checkValidity();
						    		}
						    		catch(CertificateExpiredException ex)
						    		{
						    			return Signage.EXPIRED;
						    		}
									if (tCert == certChain[i]) {
									    return Signage.SIGNED;
								}
					    	}
						}
						// Proceed to the next chain.
						startIndex += certChain.length;
					    }
					}
				    }  
					
					return Signage.SELFSIGNED;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return Signage.CORRUPT;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Signage.CORRUPT;
	}

	
	public static ArrayList<X509Certificate> getTrustedCerts() {
		ArrayList<X509Certificate> certs = new ArrayList<X509Certificate>();
        try {
            // Load the JDK's cacerts keystore file
            String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
            FileInputStream is = new FileInputStream(filename);
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "changeit";
            keystore.load(is, password.toCharArray());

            // This class retrieves the most-trusted CAs from the keystore
            PKIXParameters params = new PKIXParameters(keystore);

            // Get the set of trust anchors, which contain the most-trusted CA certificates
            Iterator<TrustAnchor> it = params.getTrustAnchors().iterator();
            while( it.hasNext() ) {
                TrustAnchor ta = (TrustAnchor)it.next();
                // Get certificate
                X509Certificate cert = ta.getTrustedCert();
                certs.add(cert);
                //if(cert.getIssuerDN().getName().contains("Thawte"))
                	//System.out.println(cert.getIssuerDN().getName());
            }
            return certs;
        } catch (Exception e) {
        	return null;
        } 
    }
	
	private static X509Certificate[] getAChain(Certificate[] certs, int startIndex) {
		if (startIndex > certs.length - 1) return null;

		int i;
		// Keep going until the next certificate is not the
		// issuer of this certificate.
		for (i = startIndex; i < certs.length - 1; i++)
		{
			if (!((X509Certificate) certs[i + 1]).getSubjectDN().equals(((X509Certificate) certs[i]).getIssuerDN()))
			{
				break;
			}
		}
		// Construct and return the found certificate chain.
		int certChainSize = (i - startIndex) + 1;
		X509Certificate[] ret = new X509Certificate[certChainSize];
		for (int j = 0; j < certChainSize; j++)
		{
			ret[j] = (X509Certificate) certs[startIndex + j];
		}
		return ret;
	}

}
