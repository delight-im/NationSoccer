<?php
set_time_limit(0);
error_reporting(E_ALL);
define('TARGET_WIDTH', 102);
define('TARGET_HEIGHT', 128);
define('TILE_WIDTH', 86);
define('TILE_HEIGHT', 108);
define('INPUT_DIR', 'RAW/');
define('OUTPUT_DIR', 'OUT/');
define('OUTPUT_DIR_TILES', 'OUT/TILES/');
define('TILES_PER_ROW', 2);
define('TILES_PER_COLUMN', 2);
define('IMAGE_FLIP_HORIZONTAL', 1);
define('IMAGE_FLIP_VERTICAL', 2);
define('IMAGE_FLIP_BOTH', 3);

function copyToHeight128($input, &$output) {
	$input_width = imagesx($input);
	$input_height = imagesy($input);
	$resultWidth = $input_width / ($input_height / TARGET_HEIGHT);
	$padding_leftRight = round((TARGET_WIDTH-$resultWidth)/2);
	if ($padding_leftRight < 0) {
		throw new Exception('Expected result width may not be larger than the defined target width');
	}
	imagecopyresampled($output, $input, $padding_leftRight, 0, 0, 0, $resultWidth, TARGET_HEIGHT, $input_width, $input_height);
}

function copyToTiledImage($input_normal, $input_boing, &$output) {
	$flipped_normal = flipImage($input_normal, IMAGE_FLIP_HORIZONTAL);
	$flipped_boing = flipImage($input_boing, IMAGE_FLIP_HORIZONTAL);
	imagecopyresampled($output, $input_normal, TILE_WIDTH*0, TILE_HEIGHT*0, 0, 0, TILE_WIDTH, TILE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
	imagecopyresampled($output, $input_boing, TILE_WIDTH*0, TILE_HEIGHT*1, 0, 0, TILE_WIDTH, TILE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
	imagecopyresampled($output, $flipped_normal, TILE_WIDTH*1, TILE_HEIGHT*0, 0, 0, TILE_WIDTH, TILE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
	imagecopyresampled($output, $flipped_boing, TILE_WIDTH*1, TILE_HEIGHT*1, 0, 0, TILE_WIDTH, TILE_HEIGHT, TARGET_WIDTH, TARGET_HEIGHT);
	destroyImage($flipped_normal);
	destroyImage($flipped_boing);
}

function &flipImage($input, $mode) {
	$width = imagesx($input);
	$height = imagesy($input);

	$src_x = 0;
	$src_y = 0;
	$src_width = $width;
	$src_height = $height;

	switch ((int) $mode) {
		case IMAGE_FLIP_HORIZONTAL:
			$src_x = $width-1;
			$src_width = -$width;
			break;
		case IMAGE_FLIP_VERTICAL:
			$src_y = $height-1;
			$src_height = -$height;
			break;
		case IMAGE_FLIP_BOTH:
			$src_x = $width-1;
			$src_y = $height-1;
			$src_width = -$width;
			$src_height = -$height;
			break;
		default:
			throw new Exception('Unknown flip mode: '.$mode);
	}

	$output = createTransparentImage($width, $height);
	if (imagecopyresampled($output, $input, 0, 0, $src_x, $src_y, $width, $height, $src_width, $src_height)) {
		return $output;
	}
	else {
		throw new Exception('Could not flip image');
	}
}

function saveImage(&$im, $outputDir, $filename) {
	imagepng($im, str_replace(INPUT_DIR, $outputDir, $filename));
}

function destroyImage(&$im) {
	imagedestroy($im);
}

function &createTransparentImage($width, $height) {
	$im = imagecreatetruecolor($width, $height);
	imagesavealpha($im, true);
	$transparent = imagecolorallocatealpha($im, 0, 0, 0, 127);
	imagefill($im, 0, 0, $transparent);
	return $im;
}

$inputFiles = glob(INPUT_DIR.'*');
$nInputFiles = count($inputFiles);
for ($i = 0; $i < $nInputFiles; $i = $i+2) {
	if (($i+1) < $nInputFiles) {
		$im_normal = createTransparentImage(TARGET_WIDTH, TARGET_HEIGHT);
		$im_boing = createTransparentImage(TARGET_WIDTH, TARGET_HEIGHT);
		$im_tiles = createTransparentImage(TILE_WIDTH*TILES_PER_ROW, TILE_HEIGHT*TILES_PER_COLUMN);

		$raw_normal = imagecreatefrompng($inputFiles[$i]);
		$raw_boing = imagecreatefrompng($inputFiles[$i+1]);

		copyToHeight128($raw_normal, $im_normal);
		copyToHeight128($raw_boing, $im_boing);
		copyToTiledImage($im_normal, $im_boing, $im_tiles);

		saveImage($im_normal, OUTPUT_DIR, $inputFiles[$i]);
		saveImage($im_tiles, OUTPUT_DIR_TILES, $inputFiles[$i]);

		destroyImage($im_normal);
		destroyImage($im_boing);
		destroyImage($im_tiles);
	}
}
?>
