package org.janelia.stitching.analysis;

import java.util.Arrays;
import java.util.List;

import org.janelia.stitching.Boundaries;
import org.janelia.stitching.SerializablePairWiseStitchingResult;
import org.janelia.stitching.TileInfo;
import org.janelia.stitching.TileInfoJSONProvider;
import org.janelia.stitching.TileOperations;
import org.janelia.stitching.Utils;

public class FindParticularShift
{
	public static void main( final String[] args ) throws Exception
	{
		final List< SerializablePairWiseStitchingResult > shifts = TileInfoJSONProvider.loadPairwiseShifts( args[ 0 ] );

		final int i1 = 33, i2 = 177;

		int hits = 0;
		SerializablePairWiseStitchingResult shift = null;
		for ( final SerializablePairWiseStitchingResult s : shifts ) {
			if ( (s.getPairOfTiles()._1.getIndex() == i1 && s.getPairOfTiles()._2.getIndex() == i2) ||
					(s.getPairOfTiles()._1.getIndex() == i2 && s.getPairOfTiles()._2.getIndex() == i1) )
			{
				shift = s;
				hits++;
			}
		}

		if ( hits == 0 )
			throw new Exception( "Not found" );
		else if ( hits != 1)
			throw new Exception( "Impossible: present more than once" );

		System.out.println( "Found, offset=" + Arrays.toString( shift.getOffset() ) );

		final TileInfo t1 = shift.getPairOfTiles()._1;
		final TileInfo t2 = shift.getPairOfTiles()._2;

		Boundaries overlap = TileOperations.getOverlappingRegionGlobal( t1, t2 );
		System.out.println( "Initial overlap at " + Arrays.toString( overlap.getMin() ) + " with dimensions " + Arrays.toString( overlap.getDimensions() ) );

		TileInfoJSONProvider.saveTilesConfiguration( new TileInfo[] { t1, t2 }, Utils.addFilenameSuffix( args[0], "_ORIGINAL" ) );

		for ( int d = 0; d < shift.getNumDimensions(); d++ )
			t2.setPosition( d, t1.getPosition( d ) + shift.getOffset( d ) );

		overlap = TileOperations.getOverlappingRegionGlobal( t1, t2 );
		if ( overlap != null)
			System.out.println( "Overlap after applying the offset at " + Arrays.toString( overlap.getMin() ) + " with dimensions " + Arrays.toString( overlap.getDimensions() ) );
		else
			System.out.println( "*** No overlap after applying the offset! ***" );

		TileInfoJSONProvider.saveTilesConfiguration( new TileInfo[] { t1, t2 }, Utils.addFilenameSuffix( args[0], "_SHIFTED" ) );
	}
}