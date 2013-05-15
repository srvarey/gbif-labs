/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.service.registry2;

import org.gbif.api.model.registry2.Dataset;
import org.gbif.api.model.registry2.Metadata;
import org.gbif.api.vocabulary.registry2.MetadataType;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public interface DatasetService
  extends NetworkEntityService<Dataset>, ContactService, EndpointService, MachineTagService, TagService,
  IdentifierService, CommentService {

  /**
   * Lists all metadata descriptions available for a dataset and optionally filters them by document type.
   * The list is sorted by priority with the first result ranking highest.
   * Highest priority in this sense means most relevant for augmenting/updating a dataset with EML being the most
   * relevant cause informative type.
   *
   * @return the list of metadata entries sorted by priority
   */
  List<Metadata> listMetadata(UUID datasetKey, @Nullable MetadataType type);

  /**
   * Get a metadata description by its key.
   */
  Metadata getMetadata(int metadataKey);

  /**
   * Removes a metadata entry and its document by its key.
   */
  void deleteMetadata(int metadataKey);

  /**
   * Inserts a metadata document, replacing any previously existing document of the same type.
   * The document type is discovered by the service and returned in the Metadata instance.
   *
   * @throws IllegalArgumentException if document is not parsable
   */
  Metadata insertMetadata(UUID datasetKey, InputStream document);

  /**
   * Retrieves a GBIF generated EML document overlaying GBIF information with any existing metadata document data.
   */
  InputStream getMetadataDocument(UUID datasetKey);

  /**
   * Gets the actual metadata document content by its key.
   */
  InputStream getMetadataDocument(int metadataKey);

}
