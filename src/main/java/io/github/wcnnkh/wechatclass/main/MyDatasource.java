/*
 * package io.github.wcnnkh.wechatclass.main;
 * 
 * import java.beans.PropertyVetoException; import java.sql.Connection; import
 * java.sql.SQLException; import java.util.Properties;
 * 
 * import com.mchange.v2.c3p0.ComboPooledDataSource;
 * 
 * import shuchaowen.config.ConfigManager; import
 * shuchaowen.config.service.DefaultSelectConfigPath; import
 * shuchaowen.db.SimpleMysqlDB;
 * 
 * public class MyDatasource extends SimpleMysqlDB{ private
 * ComboPooledDataSource datasource; public MyDatasource() {
 * DefaultSelectConfigPath defaultSelectConfigPath = new
 * DefaultSelectConfigPath(); Properties properties =
 * ConfigManager.getProperties(defaultSelectConfigPath.execute(
 * "/core/datasource.properties")); datasource = new ComboPooledDataSource();
 * try { datasource.setDriverClass(properties.getProperty("driverClass"));
 * datasource.setUser(properties.getProperty("user"));
 * datasource.setPassword(properties.getProperty("password"));
 * datasource.setJdbcUrl(properties.getProperty("jdbcUrl"));
 * datasource.setInitialPoolSize(Integer.parseInt(properties.getProperty(
 * "initSize", "50")));
 * datasource.setMinPoolSize(Integer.parseInt(properties.getProperty("minSize",
 * "50")));
 * datasource.setMaxPoolSize(Integer.parseInt(properties.getProperty("maxSize",
 * "100"))); datasource.setMaxStatements(0); datasource.setMaxIdleTime(60); }
 * catch (PropertyVetoException e) { e.printStackTrace(); } }
 * 
 * @Override public Connection getConnection() throws SQLException { return
 * datasource.getConnection(); }
 * 
 * @Override public void destroy() { try { datasource.close(); } catch
 * (SQLException e) { e.printStackTrace(); } } }
 */