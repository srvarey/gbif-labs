package org.gbif.registry.persistence.mapper.handler;

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
  public Country getResult(ResultSet rs, String columnName) throws SQLException {
    return Country.fromIsoCode(rs.getString(columnName));
  }

  @Override
  public Country getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return Country.fromIsoCode(cs.getString(columnIndex));
  }

  @Override
  public Country getResult(ResultSet rs, int columnIndex) throws SQLException {
    return Country.fromIsoCode(rs.getString(columnIndex));
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, Country parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter.getIso2LetterCode(), Types.CHAR);

  }
}
