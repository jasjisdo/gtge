/*
 * DataEncryption.java
 *
 * Created on May 23, 2007, 3:34 PM
 *
 *
 * Copyright (c) 2008 Golden T Studios.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.golden.gamedev.engine.network.manipulator;

// JFC
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import com.golden.gamedev.engine.network.DataManipulator;

/**
 * 
 * @author Paulus Tuerah
 */
public class DataEncryption implements DataManipulator {
	
	public static final int DESEDE_ENCRYPTION_SCHEME = 1;
	public static final int DES_ENCRYPTION_SCHEME = 2;
	
	private Cipher encryption;
	private Cipher decryption;
	
	/** ************************************************************************* */
	/** ***************************** CONSTRUCTOR ******************************* */
	/** ************************************************************************* */
	
	public DataEncryption(int encryptionScheme, String encryptionKey) {
		try {
			// create the key spec
			String unicodeFormat = "UTF8";
			byte[] keyBytes = encryptionKey.getBytes(unicodeFormat);
			
			String scheme = null;
			KeySpec keySpec = null;
			
			switch (encryptionScheme) {
				case DESEDE_ENCRYPTION_SCHEME:
					scheme = "DESede";
					keySpec = new DESedeKeySpec(keyBytes);
					break;
				
				case DES_ENCRYPTION_SCHEME:
					scheme = "DES";
					keySpec = new DESKeySpec(keyBytes);
					break;
				
				default:
					throw new IllegalArgumentException(
					        "Encryption scheme is not supported.");
			}
			
			// create the secret key
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(scheme);
			SecretKey key = keyFactory.generateSecret(keySpec);
			
			// the encryption cipher
			this.encryption = Cipher.getInstance(scheme);
			this.encryption.init(Cipher.ENCRYPT_MODE, key);
			
			// the decryption cipher
			this.decryption = Cipher.getInstance(scheme);
			this.decryption.init(Cipher.DECRYPT_MODE, key);
			
		}
		catch (Exception ex) {
			// ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	public DataEncryption() {
		this(DataEncryption.DESEDE_ENCRYPTION_SCHEME,
		        "Secure Network Data Packet with Encryption Key");
	}
	
	public byte[] manipulate(byte[] data) throws Exception {
		return this.encryption.doFinal(data);
	}
	
	public byte[] demanipulate(byte[] data) throws Exception {
		return this.decryption.doFinal(data);
	}
	
}
