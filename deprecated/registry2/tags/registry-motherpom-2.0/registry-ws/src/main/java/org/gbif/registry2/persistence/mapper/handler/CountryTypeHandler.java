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
package org.gbif.registry2.persistence.mapper.handler;

import org.gbif.api.vocabulary.Country;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class CountryTypeHandler implements TypeHandler<Country> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Country parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter == null ? null : parameter.getIso2LetterCode(), Types.CHAR);
  }

  @Override
  public Country getResult(ResultSet rs, String columnName) throws SQLException {
    return Country.fromIsoCode(rs.getString(columnName));
  }

  @Override
  public Country getResult(ResultSet rs, int columnIndex) throws SQLException {
    return Country.fromIsoCode(rs.getString(columnIndex));
  }

  @Override
  public Country getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return Country.fromIsoCode(cs.getString(columnIndex));
  }

}
