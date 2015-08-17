/**
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2015
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.ibm.bi.dml.test.integration.functions.unary.matrix;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.ibm.bi.dml.api.DMLScript;
import com.ibm.bi.dml.api.DMLScript.RUNTIME_PLATFORM;
import com.ibm.bi.dml.hops.OptimizerUtils;
import com.ibm.bi.dml.lops.LopProperties.ExecType;
import com.ibm.bi.dml.runtime.matrix.data.MatrixValue.CellIndex;
import com.ibm.bi.dml.test.integration.AutomatedTestBase;
import com.ibm.bi.dml.test.integration.TestConfiguration;
import com.ibm.bi.dml.test.utils.TestUtils;
import com.ibm.bi.dml.utils.Statistics;

/**
 * 
 * 
 */
public class FullCumprodTest extends AutomatedTestBase 
{
	@SuppressWarnings("unused")
	private static final String _COPYRIGHT = "Licensed Materials - Property of IBM\n(C) Copyright IBM Corp. 2010, 2015\n" +
                                             "US Government Users Restricted Rights - Use, duplication  disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
	
	private final static String TEST_NAME = "Cumprod";
	private final static String TEST_DIR = "functions/unary/matrix/";
	
	private final static double eps = 1e-10;
	
	private final static int rowsMatrix = 1201;
	private final static int colsMatrix = 1103;
	private final static double spSparse = 0.1;
	private final static double spDense = 0.9;
	
	private enum InputType {
		COL_VECTOR,
		ROW_VECTOR,
		MATRIX
	}
	
	@Override
	public void setUp() 
	{
		addTestConfiguration(TEST_NAME,new TestConfiguration(TEST_DIR, TEST_NAME,new String[]{"B"})); 
	}

		
	@Test
	public void testCumprodColVectorDenseCP() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, false, ExecType.CP);
	}
	
	@Test
	public void testCumprodRowVectorDenseCP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.CP);
	}
	
	@Test
	public void testCumprodRowVectorDenseNoRewritesCP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.CP, false);
	}
	
	@Test
	public void testCumprodMatrixDenseCP() 
	{
		runColAggregateOperationTest(InputType.MATRIX, false, ExecType.CP);
	}
	
	@Test
	public void testCumprodColVectorSparseCP() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, true, ExecType.CP);
	}
	
	@Test
	public void testCumprodRowVectorSparseCP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, true, ExecType.CP);
	}
	
	@Test
	public void testCumprodRowVectorSparseNoRewritesCP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, true, ExecType.CP, false);
	}
	
	@Test
	public void testCumprodMatrixSparseCP() 
	{
		runColAggregateOperationTest(InputType.MATRIX, true, ExecType.CP);
	}
	
	@Test
	public void testCumprodColVectorDenseMR() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, false, ExecType.MR);
	}
	
	@Test
	public void testCumprodRowVectorDenseMR() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.MR);
	}
	
	@Test
	public void testCumprodRowVectorDenseNoRewritesMR() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.MR, false);
	}
	
	@Test
	public void testCumprodMatrixDenseMR() 
	{
		runColAggregateOperationTest(InputType.MATRIX, false, ExecType.MR);
	}
	
	@Test
	public void testCumprodColVectorSparseMR() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, true, ExecType.MR);
	}
	
	@Test
	public void testCumprodRowVectorSparseNoRewritesMR() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, true, ExecType.MR, false);
	}
	
	@Test
	public void testCumprodMatrixSparseMR() 
	{
		runColAggregateOperationTest(InputType.MATRIX, true, ExecType.MR);
	}
	
	@Test
	public void testCumprodColVectorDenseSP() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, false, ExecType.SPARK);
	}
	
	@Test
	public void testCumprodRowVectorDenseSP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.SPARK);
	}
	
	@Test
	public void testCumprodRowVectorDenseNoRewritesSP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, false, ExecType.SPARK, false);
	}
	
	@Test
	public void testCumprodMatrixDenseSP() 
	{
		runColAggregateOperationTest(InputType.MATRIX, false, ExecType.SPARK);
	}
	
	@Test
	public void testCumprodColVectorSparseSP() 
	{
		runColAggregateOperationTest(InputType.COL_VECTOR, true, ExecType.SPARK);
	}
	
	@Test
	public void testCumprodRowVectorSparseSP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, true, ExecType.SPARK);
	}
	
	@Test
	public void testCumprodRowVectorSparseNoRewritesSP() 
	{
		runColAggregateOperationTest(InputType.ROW_VECTOR, true, ExecType.SPARK, false);
	}
	
	@Test
	public void testCumprodMatrixSparseSP() 
	{
		runColAggregateOperationTest(InputType.MATRIX, true, ExecType.SPARK);
	}
	
	
	/**
	 * 
	 * @param type
	 * @param sparse
	 * @param instType
	 */
	private void runColAggregateOperationTest( InputType type, boolean sparse, ExecType instType)
	{
		//by default we apply algebraic simplification rewrites
		runColAggregateOperationTest(type, sparse, instType, true);
	}
	
	/**
	 * 
	 * @param sparseM1
	 * @param sparseM2
	 * @param instType
	 */
	private void runColAggregateOperationTest( InputType type, boolean sparse, ExecType instType, boolean rewrites)
	{
		RUNTIME_PLATFORM platformOld = rtplatform;
		switch( instType ){
			case MR: rtplatform = RUNTIME_PLATFORM.HADOOP; break;
			case SPARK: rtplatform = RUNTIME_PLATFORM.SPARK; break;
			default: rtplatform = RUNTIME_PLATFORM.HYBRID; break;
		}
	
		boolean sparkConfigOld = DMLScript.USE_LOCAL_SPARK_CONFIG;
		if( rtplatform == RUNTIME_PLATFORM.SPARK )
			DMLScript.USE_LOCAL_SPARK_CONFIG = true;
	
		//rewrites
		boolean oldFlagRewrites = OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION;
		OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION = rewrites;
		
		try
		{
			int cols = (type==InputType.COL_VECTOR) ? 1 : colsMatrix;
			int rows = (type==InputType.ROW_VECTOR) ? 1 : rowsMatrix;
			double sparsity = (sparse) ? spSparse : spDense;
			
			TestConfiguration config = getTestConfiguration(TEST_NAME);
			
			// This is for running the junit test the new way, i.e., construct the arguments directly
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			programArgs = new String[]{"-explain", "-args", HOME + INPUT_DIR + "A",
					                        HOME + OUTPUT_DIR + "B"    };
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
			       HOME + INPUT_DIR + " " + HOME + EXPECTED_DIR;
			
			loadTestConfiguration(config);
	
			//generate actual dataset 
			double[][] A = getRandomMatrix(rows, cols, 0.99, 1, sparsity, 7); 
			writeInputMatrixWithMTD("A", A, true);
	
			runTest(true, false, null, -1); 
			if( instType==ExecType.CP || instType==ExecType.SPARK  ) //in CP no MR jobs should be executed
				Assert.assertEquals("Unexpected number of executed MR jobs.", 0, Statistics.getNoOfExecutedMRJobs());
			
			runRScript(true); 
		
			//compare matrices 
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("B");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("B");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
		}
		finally
		{
			rtplatform = platformOld;
			DMLScript.USE_LOCAL_SPARK_CONFIG = sparkConfigOld;
			OptimizerUtils.ALLOW_ALGEBRAIC_SIMPLIFICATION = oldFlagRewrites;
		}
	}	
}