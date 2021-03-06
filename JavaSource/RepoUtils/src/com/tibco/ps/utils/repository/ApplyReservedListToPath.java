package com.tibco.ps.utils.repository;

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
    applyReservedListToPath:

    -- CIS Repository Helper Procedure --
    This procedure is used to fix the leading characters and CIS reserved words used in a folder path.  
    Any path that contains a reserved word, leading underscore '_' or a number '0123456789', or special
    character must have double quotes inserted around that portion of the folder.  This procedure would 
    be called in conjunction with other procedures.  For example, when generating a view based off of 
    another view, the SELECT statement's FROM clause would require that the path to the underlying view 
    be fixed with double quotes if it meets any of the quoting criteria above.
 
    Dependencies:
    =============
    $CIS_INSTALL_HOME/conf/customjars/RepoUtils.properties - This properties file on the CIS host filesystem 
        provides a list of reserved words and characters that are used to match with the various parts 
        of a folder path.  Add any new CIS reserved words that appear from release to release in this list. This
        file can be updated without a restart of CIS.

    Input:
        resourcePath - CIS source folder path to assess and fix
            Values: e.g. /shared/tmp/1folder/_folder/XML

    Output:
        fixedResourcePath - Fixed CIS source folder path
            Values: e.g. /shared/tmp/"1folder"/"_folder"/"XML"

    Exceptions:  none

    Author:      Calvin Goodrich
    Date:        6/10/2011
    CSW Version: 5.1.0

 */

// public API's
//

import com.compositesw.extension.*;

import java.sql.SQLException;
import java.sql.Types;

public class ApplyReservedListToPath implements CustomProcedure {

    private ExecutionEnvironment qenv;
    private String result = null;
    private String cjpName = "ApplyReservedListToPath";

    public ApplyReservedListToPath() {
    }

    /**
     * This is called once just after constructing the class.  The
     * environment contains methods used to interact with the server.
     */
    public void initialize (ExecutionEnvironment qenv) throws SQLException {
        this.qenv = qenv;
    }

    /**
     * Called during introspection to get the description of the input
     * and output parameters.  Should not return null.
     */
    public ParameterInfo[] getParameterInfo() {
        return new ParameterInfo[] { 
            new ParameterInfo ("inPath", Types.VARCHAR, DIRECTION_IN),
            new ParameterInfo ("debug", Types.VARCHAR, DIRECTION_IN),
            new ParameterInfo ("result", Types.VARCHAR, DIRECTION_OUT) 
        };
    }

    /**
     * Called to invoke the stored procedure.  Will only be called a
     * single time per instance.  Can throw CustomProcedureException or
     * SQLException if there is an error during invoke.
     */
    public void invoke (Object[] inputValues) throws CustomProcedureException, SQLException {
        String inPath;
        String debug; // deprecated

        if (inputValues == null || inputValues.length != 2)
            throw new CustomProcedureException (cjpName + ": Must receive two arguments.");
        
        inPath = (String) inputValues[0];
        debug = (String) inputValues[1];

        RepoUtilsPropertiesFactory.setCisLogger();
        try {
            result = CisPathQuoter.quotePath (inPath);
        } catch (CisPathQuoterException cpqe) {
            throw new CustomProcedureException (cpqe);
        }
    }

    /**
     * Called to retrieve the number of rows that were inserted,
     * updated, or deleted during the execution of the procedure. A
     * return value of -1 indicates that the number of affected rows is
     * unknown.  Can throw CustomProcedureException or SQLException if
     * there is an error when getting the number of affected rows.
     */
    public int getNumAffectedRows() {
        return 0;
    }

    /**
     * Called to retrieve the output values.  The returned objects
     * should obey the Java to SQL typing conventions as defined in the
     * table above.  Output cursors can be returned as either
     * CustomCursor or java.sql.ResultSet.  Can throw
     * CustomProcedureException or SQLException if there is an error
     * when getting the output values.  Should not return null.
     */
    public Object[] getOutputValues() {
        return new Object[] { result };
    }

    /**
     * Called when the procedure reference is no longer needed.  Close
     * may be called without retrieving any of the output values (such
     * as cursors) or even invoking, so this needs to do any remaining
     * cleanup.  Close may be called concurrently with any other call
     * such as "invoke" or "getOutputValues".  In this case, any pending
     * methods should immediately throw a CustomProcedureException.
     */
    public void close() throws CustomProcedureException, SQLException {
    }

    //
    // Introspection methods
    //

    /**
     * Called during introspection to get the short name of the stored
     * procedure.  This name may be overridden during configuration.
     * Should not return null.
     */
    public String getName() {
        return "applyReservedListToPath";
    }

    /**
     * Called during introspection to get the description of the stored
     * procedure.  Should not return null.
     */
    public String getDescription() {
        return "Applies CIS resource quoting rules to a path.";
    }

    //
    // Transaction methods
    //

    /**
     * Returns true if the custom procedure uses transactions.  If this
     * method returns false then commit and rollback will not be called.
     */
    public boolean canCommit() {
        return false;
    }

    /**
     * Commit any open transactions.
     */
    public void commit() throws SQLException {
    }

    /**
     * Rollback any open transactions.
     */
    public void rollback() throws SQLException {
    }

    /**
     * Returns true if the transaction can be compensated.
     */
    public boolean canCompensate() {
        return false;
    }

    /**
     * Compensate any committed transactions (if supported).
     */
    public void compensate (ExecutionEnvironment qenv) throws SQLException {
    }
}
