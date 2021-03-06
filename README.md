# stitching-spark
Reconstruct big images from overlapping tiled images on a Spark cluster.

The code is based on the Stitching plugin for Fiji https://github.com/fiji/Stitching

## Usage

### 0. Prerequisites

Build the required dependencies that have been specifically patched for this application:

```bash
git clone -b serializable-new https://github.com/igorpisarev/n5.git
mvn clean install -f n5
```

### 1. Building the package

Clone the repository with submodules:

```bash
git clone --recursive https://github.com/saalfeldlab/stitching-spark.git
```

The application can be executed on Janelia cluster or locally. Build the package for the desired execution environment:

<details>
<summary><b>Compile for running on Janelia cluster</b></summary>

```bash
python build.py
```
</details>

<details>
<summary><b>Compile for running on local machine</b></summary>

```bash
python build-spark-local.py
```
</details>
<br/>

The scripts for starting the application are located under `startup-scripts/spark-janelia` and `startup-scripts/spark-local`, and their usage is explained in the next steps.

If running locally, you can access the Spark job tracker at http://localhost:4040/ to monitor the progress of the tasks.


### 2. Preparing input tile configuration files

The application requires an input file containing the registered tiles configuration for each channel. It should be a JSON formatted as follows:

```json
[
{
  "index" : 0,
  "file" : "FCF_CSMH__54383_20121206_35_C3_zb15_zt01_63X_0-0-0_R1_L086_20130108192758780.lsm.tif",
  "position" : [0.0, 0.0, 0.0],
  "size" : [991, 992, 880],
  "pixelResolution" : [0.097,0.097,0.18],
  "imageType" : "GRAY16"
},
{
  "index" : 1,
  "file" : "FCF_CSMH__54383_20121206_35_C3_zb15_zt01_63X_0-0-0_R1_L087_20130108192825183.lsm.tif",
  "position" : [716.932762003862, -694.0887500300357, -77.41783189603937],
  "size" : [991, 992, 953],
  "pixelResolution" : [0.097,0.097,0.18],
  "imageType" : "GRAY16"
}
]
```

### 3. Flatfield estimation

<details>
<summary><b>Run on Janelia cluster</b></summary>

```bash
spark-janelia/flatfield.py <number of cluster nodes> -i ch0.json
```
</details>

<details>
<summary><b>Run on local machine</b></summary>

```bash
spark-local/flatfield.py -i ch0.json
```
</details>

This will create a folder named `ch0-flatfield/` near the provided `ch0.json` file. After the application is finished, it will store two files `S.tif` and `T.tif` (the brightfield and the offset respectively).
The next steps will detect the flatfield folder and will automatically use the estimated flatfields for on-the-fly correction.

The full list of available parameters for the flatfield script is available [here](https://github.com/saalfeldlab/stitching-spark/wiki/Flatfield-parameters).

### 4. Stitch

<details>
<summary><b>Run on Janelia cluster</b></summary>

```bash
spark-janelia/stitch.py <number of cluster nodes> -i ch0.json -i ch1.json
```
</details>

<details>
<summary><b>Run on local machine</b></summary>

```bash
spark-local/stitch.py -i ch0.json -i ch1.json
```
</details>

This will run the stitching performing a number of iterations until it cannot improve the solution anymore. The multichannel data will be averaged on-the-fly before computing pairwise shifts in order to get higher correlations because of denser signal.

As a result, it will create files `ch0-final.json` and `ch1-final.json` near the input tile configuration files.
It will also store a file named `optimizer.txt` that will contain the statistics on average and max errors, number of retained tiles and edges in the final graph, and cross correlation and variance threshold values that were used to obtain the final solution.

The current stitching method is iterative translation-based (improving the solution by building the prediction model).
The pipeline incorporating a higher-order model is currently under development in the `split-tiles` branch.

The full list of available parameters for the stitch script is available [here](https://github.com/saalfeldlab/stitching-spark/wiki/Stitching-parameters).

### 5. Export

<details>
<summary><b>Run on Janelia cluster</b></summary>

```bash
spark-janelia/export.py <number of cluster nodes> -i ch0-final.json -i ch1-final.json
```
</details>

<details>
<summary><b>Run on local machine</b></summary>

```bash
spark-local/export.py -i ch0-final.json -i ch1-final.json
```
</details>

This will generate an [N5](https://github.com/saalfeldlab/n5) export under `export.n5/` folder. The export is fully compatible  with [N5 Viewer](https://github.com/saalfeldlab/n5-viewer) for browsing.

The full list of available parameters for the export script is available [here](https://github.com/saalfeldlab/stitching-spark/wiki/Export-parameters).
