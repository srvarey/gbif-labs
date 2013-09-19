CREATE OR REPLACE VIEW sub_view AS
SELECT
  t.agent_id, 
  n.namespace AS namespace,
  t.name AS name,
  t.value AS value,
  'ADMIN' AS created_by,
  CASE   
    WHEN t.created IS NULL THEN NOW()
    ELSE t.created END AS created
FROM tag t, namespace n
WHERE t.namespace_id = n.id

UNION

SELECT agent_id, 'metasync.gbif.org' namespace, 'basis_of_record' name,
  CASE WHEN basis_of_record IS NULL THEN 'unknown'
  ELSE basis_of_record END AS value,
    'MetadataSynchronizer' created_by,
  CASE WHEN created IS NULL THEN NOW()
  ELSE created END
FROM extended_property

UNION

SELECT agent_id, 'metasync.gbif.org' namespace, 'record_count' name,
  CASE WHEN record_count IS NULL THEN 'unknown'
  ELSE record_count END AS value,
  'MetadataSynchronizer' created_by,
  CASE WHEN created IS NULL THEN NOW()
  ELSE created END
FROM extended_property

UNION

SELECT agent_id, 'metasync.gbif.org' namespace, name, value, 'MetadataSynchronizer' created_by, NOW() created
FROM metadata_property;
