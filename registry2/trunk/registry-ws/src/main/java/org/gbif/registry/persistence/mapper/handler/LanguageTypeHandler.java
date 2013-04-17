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
package org.gbif.registry.persistence.mapper.handler;

import org.gbif.api.vocabulary.Language;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class LanguageTypeHandler implements TypeHandler<Language> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Language parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter.getIso2LetterCode(), Types.CHAR);

  }

  @Override
  public Language getResult(ResultSet rs, String columnName) throws SQLException {
    return Language.fromIsoCode(rs.getString(columnName));
  }

  @Override
  public Language getResult(ResultSet rs, int columnIndex) throws SQLException {
    return Language.fromIsoCode(rs.getString(columnIndex));
  }

  @Override
  public Language getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return Language.fromIsoCode(cs.getString(columnIndex));
  }

}
