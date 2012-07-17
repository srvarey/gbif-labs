mvn clean assembly:assembly
java -cp target/occurrence-cube-0.1-SNAPSHOT-jar-with-dependencies.jar org.gbif.cube.gmap.density.backfill.Backfill
