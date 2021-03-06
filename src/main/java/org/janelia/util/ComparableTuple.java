package org.janelia.util;

import java.util.Arrays;

/**
 * Represents a tuple of objects of the same type that can be compared with each other.
 * Defines the ascending sorting order by the first element of the tuple, or by the second if the first elements are equal, and so on.
 *
 * @author Igor Pisarev
 */

public class ComparableTuple< T extends Comparable< ? super T > > implements Comparable< ComparableTuple< ? extends T > >
{
	private final T[] values;

	public ComparableTuple( final T... values )
	{
		this.values = values;
	}

	public T[] getValues()
	{
		return values;
	}

	public T getValue( final int index )
	{
		return values[ index ];
	}

	@Override
	public int compareTo( final ComparableTuple< ? extends T > other ) throws IllegalArgumentException
	{
		assert values.length == other.getValues().length;
		if ( values.length != other.getValues().length )
			throw new IllegalArgumentException( "Cannot compare tuples of different length" );

		int ret = 0;
		for ( int i = 0; i < Math.max( values.length, other.getValues().length ); ++i )
			if ( ( ret = values[ i ].compareTo( other.getValue( i ) ) ) != 0 )
				break;
		return ret;
	}

	@Override
	public String toString()
	{
		return Arrays.toString( values );
	}
}
