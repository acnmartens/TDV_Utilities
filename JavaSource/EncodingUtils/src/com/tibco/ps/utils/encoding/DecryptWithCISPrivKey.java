package com.tibco.ps.utils.encoding;

/**
 * (c) 2017 TIBCO Software Inc. All rights reserved.
 * 
 * Except as specified below, this software is licensed pursuant to the Eclipse Public License v. 1.0.
 * The details can be found in the file LICENSE.
 * 
 * The following proprietary files are included as a convenience, and may not be used except pursuant
 * to valid license to Composite Information Server or TIBCO(R) Data Virtualization Server:
 * csadmin-XXXX.jar, csarchive-XXXX.jar, csbase-XXXX.jar, csclient-XXXX.jar, cscommon-XXXX.jar,
 * csext-XXXX.jar, csjdbc-XXXX.jar, csserverutil-XXXX.jar, csserver-XXXX.jar, cswebapi-XXXX.jar,
 * and customproc-XXXX.jar (where -XXXX is an optional version number).  Any included third party files
 * are licensed under the terms contained in their own accompanying LICENSE files, generally named .LICENSE.txt.
 * 
 * This software is licensed AS-IS. Support for this software is not covered by standard maintenance agreements with TIBCO.
 * If you would like to obtain assistance with this software, such assistance may be obtained through a separate paid consulting
 * agreement with TIBCO.
 * 
 */


/*
DecryptWithCISPrivKey() 

    Description:
        Decrypts a string encrypted using CIS's built in SSL certificate. 

    Input:
        "encrypted hex string" - The encrypted string to decrypt.
            Values - Any text string

        "keystore password" - The password of the CIS SSL keystore.
            Values - Any text string


    Output:
        "plain text" - The decrypted string
            Values - Any text string


    Exceptions:  none


    Modified Date:  Modified By:        CSW Version:    Reason:
    10/27/2014      Alex Dedov          6.2.6           Created new

 */

import java.sql.SQLException;
import java.sql.Types;

import com.compositesw.extension.CustomProcedure;
import com.compositesw.extension.CustomProcedureException;
import com.compositesw.extension.ParameterInfo;

public class DecryptWithCISPrivKey extends EncodingUtilTemplate {

	private String decMessage = null;

	@Override
	public String getName() {
		procName = getClass().getSimpleName() ;
		return procName ;
	}

	@Override
	public String getDescription() {
		return "Tries to decrypt input (a hexadecimal representation of bin array) using CIS private key (from keystore file)";
	}
	@Override
    public ParameterInfo[] getParameterInfo() {
    	return new ParameterInfo[] {
    			new ParameterInfo("encrypted hex string", Types.VARCHAR, DIRECTION_IN),
    			new ParameterInfo("keystore password", Types.VARCHAR, DIRECTION_IN),
    			new ParameterInfo("plain text", Types.VARCHAR, DIRECTION_OUT)
    	} ;
    }
	@Override
	public Object[] getOutputValues() throws CustomProcedureException, SQLException {
		return new Object[] { decMessage };
	}
	 	
	@Override
	public int execute(Object[] args) throws Exception {
		String encText = args.length > 0 ? (String)args[0] : null ;
		String password = args.length > 1 && args[1].toString().trim().length() > 0 ? (String)args[1] : EncryptDecryptX509.DEFAULT_CIS_PASSWORD ;

		byte[] bytes = EncryptDecryptX509.bytes(encText) ;
		decMessage = new String(new EncryptDecryptX509(cjpenv).decrypt(bytes,password)) ;
		return 0 ;
	}
	
	public static void main(String[] args) {
		
		CustomProcedure cp = new DecryptWithCISPrivKey();
		/*
		 * Parameters:
    			new ParameterInfo("encrypted hex string", Types.VARCHAR, DIRECTION_IN),
    			new ParameterInfo("keystore password", Types.VARCHAR, DIRECTION_IN),
    			new ParameterInfo("plain text", Types.VARCHAR, DIRECTION_OUT)
		 */
		String encrypted_string = "50BF439C250EEC9A39E90C4578392AFE2B6850B00DBC0E6C5829902837AD635A6DAB033DB5387C6FCBC131CC93802C569FC8FCB18FD4DB377AADF385B687F9BEACC24C2015FA55EFF781E4DBA532C3A7460A3F6734412AF9F21A18C069D0226225CE2DE453D5701CFCB67BC671BB4AE85F69C7956BA0883786DCE869D68CB7E5"; 
		String keystore_password = "changeit";

        try {
	        cp.initialize(null);
	        System.out.println("invoke "+cp.getName());
	        cp.invoke(new Object[] {
	        		new String (encrypted_string),
	        		new String (keystore_password)
	        });
       
	        String result = cp.getOutputValues()[0].toString();
	        System.out.println("DecryptWithCISPrivKey Result:");
	        System.out.println(result);
	        
		} catch (Exception ex) {
			System.out.print(ex.toString());
		}
	}
}
