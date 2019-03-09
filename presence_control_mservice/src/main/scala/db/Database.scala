package db

import org.postgresql.ds.PGConnectionPoolDataSource

object Database {
  private lazy val datasource = new PGConnectionPoolDataSource()

  def init(params:DbParams) = {
    datasource.setURL(params.url)
    datasource.setUser(params.user)
    datasource.setPassword(params.pwd)
  }

  def getConnection():java.sql.Connection = {
    val connection = datasource.getPooledConnection.getConnection

    connection
  }

  def terminate() = {
    // datasource.
  }
}
