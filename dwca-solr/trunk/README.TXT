This is an extension to solr that requires a couple of jars to exist and 2 extra lines to the default schema & configuration.
The handler will index all data files in an entire zipped dwc archives based on the meta.xml.
Fields are right now named like:

core file:
simple term name in lower case, e.g. scientificname

for extension files:
ext + ":" + simple rowType name in lower case +":"+ simple term name in lower case, e.g. ext:distribution:countrycode

To enable the dwca handler please do:

(1) add a new dwca contrib folder with jars:
 cd solr/contrib
 mkdir dwca
 
copy all jars from lib into this folder

(2) insert these lines into the fields section of the schema.xml
   <field name="decimallatitude" type="tdouble"  indexed="true"  stored="true" />
   <field name="decimallongitude" type="tdouble"  indexed="true"  stored="true" />
   <field name="individualcount" type="int"  indexed="true"  stored="true" />
   <field name="year" type="int"  indexed="true"  stored="true" />
   <field name="month" type="int"  indexed="true"  stored="true" />
   <field name="day" type="int"  indexed="true"  stored="true" />
   <dynamicField name="*:*remarks" type="text"  indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*:subject" type="text"  indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*:description" type="text"  indexed="true"  stored="true" multiValued="true"/>
   <dynamicField name="*:*" type="string"  indexed="true"  stored="true" multiValued="true"/>   
   <dynamicField name="*remarks" type="text"  indexed="true"  stored="true" />
   <dynamicField name="*inmeters" type="int"  indexed="true"  stored="true" />   
   <dynamicField name="*" type="string"  indexed="true"  stored="true" />
   
   
   If you wanna enable simple search across all fields also add this line to the copyField statements at the bottom of the schema.xml
   
   <copyField source="*" dest="text" />

(3) insert these lines anywhere into the solrconfig.xml
  <lib dir="../../contrib/dwca" />
  <requestHandler name="/update/dwca" class="org.gbif.solr.DwcaRequestHandler" startup="lazy" />
  



WEB CLIENT:
see this neat library for ajax based web clients 
http://evolvingweb.github.com/ajax-solr/