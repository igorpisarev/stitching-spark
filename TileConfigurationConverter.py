import argparse
import re
import json

if __name__ == '__main__':
	parser = argparse.ArgumentParser()
	parser.add_argument('path', type=str, help='path to initial tile configuration file')
	args = parser.parse_args()
	
	out_path = args.path + '.json'
	sep_ind = out_path.rindex('/') + 1
	abs_path_prefix = out_path[ : sep_ind ]
	try:
		out_file = open( out_path, 'w' )
	except IOError:
		# non-writable path, store the output file in the current directory as a fallback
		out_path = out_path[sep_ind : ]
		out_file = open( out_path, 'w' )
	
	coords_pattern = re.compile('.*(\d{3}x_\d{3}y_\d{3}z).*');

	groups = {}
	tiles = []
	with open(args.path) as in_file:
		for line in in_file.readlines():
			tokens = [t.strip() for t in line.strip().split()]
			
			match = coords_pattern.match(tokens[0])
			index =	len(tiles)

			# workaround for datasets from betzigcollab
			wrong_suffix = '_decon.tif'
			if tokens[0].endswith(wrong_suffix):
				tokens[0] = tokens[0][ : len(tokens[0]) - len(wrong_suffix) ] + '.tif'

			# try to group by (x,y,z) coordinates obtained from filenames
			if match:
				grouping = match.group(1)
			else:
				grouping = index

			if not grouping in groups:
				groups[grouping] = index
				tiles.append({ 'index': index, 'position': [float(tokens[2]), float(tokens[1]), float(tokens[3])], 'channels':[] })
			
			tiles[ groups[grouping] ]['channels'].append(abs_path_prefix + tokens[0])

	
	out_file.write(json.dumps(tiles))
	out_file.close()