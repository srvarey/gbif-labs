/*
 * Copyright 2013 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.registry2.ims;

import org.gbif.api.vocabulary.registry2.ContactType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class ContactTypeHandler implements TypeHandler<ContactType> {
  private static final Map<String, ContactType> DICT = ImmutableMap.<String, ContactType>builder()
    .put("nodes staff", ContactType.NODE_STAFF)
    .build();

  @Override
  public void setParameter(PreparedStatement ps, int i, ContactType parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter == null ? null : parameter.name(), Types.CHAR);
  }

  @Override
  public ContactType getResult(ResultSet rs, String columnName) throws SQLException {
    return lookup(rs.getString(columnName));
  }

  @Override
  public ContactType getResult(ResultSet rs, int columnIndex) throws SQLException {
    return lookup(rs.getString(columnIndex));
  }

  @Override
  public ContactType getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return lookup(cs.getString(columnIndex));
  }

  private ContactType lookup(String val) {
    try {
      return ContactType.fromString(val);
    } catch (IllegalArgumentException e) {
      if (!Strings.isNullOrEmpty(val)) {
        return DICT.get(StringUtils.normalizeSpace(val.toLowerCase()));
      }
    }
    return null;
  }
}
