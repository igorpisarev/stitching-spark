package org.janelia.stitching;

import java.io.Serializable;

import org.apache.spark.api.java.JavaSparkContext;

/**
 * Base class for all tasks implementations that should be executed as a part of the pipeline.
 *
 * @author Igor Pisarev
 */

public abstract class PipelineStepExecutor implements Serializable
{
	private static final long serialVersionUID = 3546355803511705943L;

	protected final StitchingJob job;
	protected final transient JavaSparkContext sparkContext;

	public PipelineStepExecutor( final StitchingJob job, final JavaSparkContext sparkContext )
	{
		this.job = job;
		this.sparkContext = sparkContext;
	}
	
	public abstract void run() throws PipelineExecutionException;
}
