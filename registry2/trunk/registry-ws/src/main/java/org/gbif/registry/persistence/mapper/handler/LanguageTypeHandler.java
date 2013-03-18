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
  public Language getResult(ResultSet rs, String columnName) throws SQLException {
    return Language.fromIsoCode(rs.getString(columnName));
  }

  @Override
  public Language getResult(CallableStatement cs, int columnIndex) throws SQLException {
    return Language.fromIsoCode(cs.getString(columnIndex));
  }

  @Override
  public Language getResult(ResultSet rs, int columnIndex) throws SQLException {
    return Language.fromIsoCode(rs.getString(columnIndex));
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, Language parameter, JdbcType jdbcType) throws SQLException {
    ps.setObject(i, parameter.getIso2LetterCode(), Types.CHAR);

  }
}
