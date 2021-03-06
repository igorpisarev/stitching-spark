package org.janelia.flatfield;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import net.imglib2.Interval;
import net.imglib2.SerializableFinalInterval;

/**
 * Command line arguments parser for flatfield correction.
 *
 * @author Igor Pisarev
 */

public class FlatfieldCorrectionArguments
{
	@Option(name = "-i", aliases = { "--input" }, required = true,
			usage = "Path/link to a tile configuration JSON file")
	private String inputFilePath;

	@Option(name = "--crop", required = false,
			usage = "Crop interval in a form of xMin,yMin,zMin,xMax,yMax,zMax")
	private String cropMinMaxInterval = null;

	@Option(name = "-b", aliases = { "--bins" }, required = false,
			usage = "Number of bins to use")
	private int bins = 256;

	@Option(name = "-p", aliases = { "--pivot" }, required = false,
			usage = "Pivot value which is to subtract from all histograms (usually the background intensity value)")
	private double pivotValue = 101;

	@Option(name = "--min", required = false,
			usage = "Min value of a histogram")
	private double histMinValue = 80;

	@Option(name = "--max", required = false,
			usage = "Max value of a histogram")
	private double histMaxValue = 500;

	@Option(name = "--2d", required = false,
			usage = "Estimate 2D flatfield (slices are used as additional data points)")
	private boolean use2D = false;


	private boolean parsedSuccessfully = false;

	public FlatfieldCorrectionArguments( final String[] args ) throws CmdLineException
	{
		final CmdLineParser parser = new CmdLineParser( this );
		try
		{
			parser.parseArgument( args );
			parsedSuccessfully = true;
		}
		catch ( final CmdLineException e )
		{
			System.err.println( e.getMessage() );
			parser.printUsage( System.err );
		}
	}

	public boolean parsedSuccessfully() { return parsedSuccessfully; }

	public String inputFilePath() { return inputFilePath; }
	public int bins() { return bins; }
	public double pivotValue() { return pivotValue; }
	public Double histMinValue() { return histMinValue; }
	public Double histMaxValue() { return histMaxValue; }
	public String cropMinMaxIntervalStr() { return cropMinMaxInterval; };
	public boolean use2D() { return use2D; }

	public Interval cropMinMaxInterval( final long[] fullTileSize )
	{
		if ( cropMinMaxInterval == null )
		{
			final long[] minSize = new long[ fullTileSize.length * 2 ];
			System.arraycopy( fullTileSize, 0, minSize, fullTileSize.length, fullTileSize.length );
			return SerializableFinalInterval.createMinSize( minSize );
		}

		final String[] intervalStrSplit = cropMinMaxInterval.trim().split(",");
		final long[] intervalMinMax = new long[ intervalStrSplit.length ];
		for ( int i = 0; i < intervalMinMax.length; i++ )
			intervalMinMax[ i ] = Long.parseLong( intervalStrSplit[ i ] );
		return SerializableFinalInterval.createMinMax( intervalMinMax );
	}
}
