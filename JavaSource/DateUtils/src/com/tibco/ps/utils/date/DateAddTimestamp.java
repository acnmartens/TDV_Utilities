package com.tibco.ps.utils.date;

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
	Description:
	  Returns a new Date value based on adding a datePart to the specified Date.
	
	
	Inputs:
	  datePart   - Unit of measure for the output "dateLength".
	    values: 'second', 'minute', 'hour',  'day', 'week', 'month' and 'year' (not case sensitive)
	
	  dateLength - The number of dateParts to add
	    values: 
	
	  startDate  - The specified start timestamp
	    values: Any valid timestamp value.
	
	
	Outputs:
	  endDate - The end timestamp resulting from adding dateLength number of datePart's
	            to the start timestamp.
	
	Exceptions:
	  None
	
	
	Author:      Jerry Joplin
	Date:        3/1/2012
	CSW Version: 5.2.0
	
*/

import org.joda.time.DateTime;
import java.util.Calendar;
import com.compositesw.extension.*;
import java.sql.*;

public class DateAddTimestamp implements CustomProcedure {

    private ExecutionEnvironment qenv;
    private Object result;


    /**
     * This is called once just after constructing the class.  The
     * environment contains methods used to interact with the server.
     */
     @Override
	public void initialize(ExecutionEnvironment qenv) throws SQLException
     {
         this.qenv = qenv;
     }


    /**
     * Called during introspection to get the description of the input
     * and output parameters.  Should not return null.
     */
    @Override
	public ParameterInfo[] getParameterInfo() {

        return new ParameterInfo[] {
            new ParameterInfo("datePart", Types.VARCHAR, DIRECTION_IN),
            new ParameterInfo("dateLength", Types.INTEGER, DIRECTION_IN),
            new ParameterInfo("startDate", Types.TIMESTAMP, DIRECTION_IN),
            new ParameterInfo("endDate", Types.TIMESTAMP, DIRECTION_OUT)
        };
    }


    /**
     * Called to invoke the stored procedure.  Will only be called a
     * single time per instance.  Can throw CustomProcedureException or
     * SQLException if there is an error during invoke.
     */
    @Override
	public void invoke(Object[] inputValues) throws CustomProcedureException, SQLException
    {
        Timestamp startDate = null;
        Calendar startCal = null;
        DateTime startDateTime = null;
        DateTime endDateTime = null;
        String datePart = null;
        int dateLength = 0;

        try
        {
            result = null;
            if(inputValues[0] == null || inputValues[1] == null || inputValues[2] == null)
            {
                return;
            }

            datePart = (String)inputValues[0];
            dateLength = (Integer)inputValues[1];

            startDate = (Timestamp)inputValues[2];
            startCal = Calendar.getInstance();
            startCal.setTime(startDate);
            startDateTime = new DateTime(
            		startCal.get(Calendar.YEAR),
            		startCal.get(Calendar.MONTH)+1, 
            		startCal.get(Calendar.DAY_OF_MONTH), 
                startCal.get(Calendar.HOUR_OF_DAY), 
            		startCal.get(Calendar.MINUTE), 
            		startCal.get(Calendar.SECOND), 
            		startCal.get(Calendar.MILLISECOND));
            
            if(datePart.equalsIgnoreCase("second"))
            {
            	endDateTime = startDateTime.plusSeconds(dateLength);
            }

            if(datePart.equalsIgnoreCase("minute"))
            {
            	endDateTime = startDateTime.plusMinutes(dateLength);
            }
    
            if(datePart.equalsIgnoreCase("hour"))
            {
            	endDateTime = startDateTime.plusHours(dateLength);
            }

            if(datePart.equalsIgnoreCase("day"))
            {
            	endDateTime = startDateTime.plusDays(dateLength);
            }
    
            if(datePart.equalsIgnoreCase("week"))
            {
            	endDateTime = startDateTime.plusWeeks(dateLength);
            }
    
            if(datePart.equalsIgnoreCase("month"))
            {
            	endDateTime = startDateTime.plusMonths(dateLength);
            }
    
            if(datePart.equalsIgnoreCase("year"))
            {
            	endDateTime = startDateTime.plusYears(dateLength);
            }

            result = new Timestamp(endDateTime.toDate().getTime());
        }
        catch(Throwable t)
        {
            throw new CustomProcedureException(t);
        }
    }



    /**
     * Called to retrieve the number of rows that were inserted,
     * updated, or deleted during the execution of the procedure. A
     * return value of -1 indicates that the number of affected rows is
     * unknown.  Can throw CustomProcedureException or SQLException if
     * there is an error when getting the number of affected rows.
     */
    @Override
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
    @Override
	public Object[] getOutputValues() {

        Object[] outputValues = new Object[1];

        outputValues[0] = result;

        return outputValues;
    }

    /**
     * Called when the procedure reference is no longer needed.  Close
     * may be called without retrieving any of the output values (such
     * as cursors) or even invoking, so this needs to do any remaining
     * cleanup.  Close may be called concurrently with any other call
     * such as "invoke" or "getOutputValues".  In this case, any pending
     * methods should immediately throw a CustomProcedureException.
     */
    @Override
	public void close()
        throws SQLException
    {
    }

    //
    // Introspection methods
    //

    /**
     * Called during introspection to get the short name of the stored
     * procedure.  This name may be overridden during configuration.
     * Should not return null.
     */
    @Override
	public String getName() {
        return "DateAddTimestamp";
    }

    /**
     * Called during introspection to get the description of the stored
     * procedure.  Should not return null.
     */
    @Override
	public String getDescription() {
        return "DateAddTimestamp";
    }

    //
    // Transaction methods
    //

    /**
     * Commit any open transactions.
     */
    @Override
	public void commit()
        throws SQLException
    { }

    /**
     * Rollback any open transactions.
     */
    @Override
	public void rollback()
        throws SQLException
    { }

    /**
     * Returns true if the transaction can be compensated.
     */
    @Override
	public boolean canCompensate() {
        return false;
    }

    @Override
	public boolean canCommit(){
        return false;
    }

    /**
     * Compensate any committed transactions (if supported).
     */
    @Override
	public void compensate(ExecutionEnvironment qenv)
        throws CustomProcedureException, SQLException {
    }



    public static void main(String[] args)
    {
    }

}