#-----------------------------------------------------------------
# SetupDB.py - Jython implementation of Setup Platform Database
#-----------------------------------------------------------------
#
#  The purpose of this script is to create a JDBCProvider and two DataSources.
#  
#  This script can be included in the wsadmin command invocation like this:
#     
#     wsadmin -lang jython -f SetupDB.py <DBServerURL> <DBServerPort> <JCR_DB_Name> <IDM_DB_Name> <DBUserName> <DBPassword>
#
#  or the script can be execfiled from the wsadmin command line like this:
#     wsadmin> execfile("SetupDB.py")
#     wsadmin> SetupDB("localhost", "3306", "exo_jcr", "exo_idm", "root", "secret")
#
#  The script expects 6 parameters:
#   
#   dbServerURL		the name of the DB Server or IP, like 127.0.0.1 or localhost
#   dbServerPort	the DB Server port
#   jcrDBName	   	the jcr database name
#   idmDBName	   	the idm database name
#   dbUserName	   	the DB user name
#   dbPassword	   	the DB password
#-----------------------------------------------------------------
#
import sys, java

def SetupDB(dbServerURL, dbServerPort, jcrDBName, idmDBName, dbUserName, dbPassword):
	wasCellName = java.lang.System.getenv().get('WAS_CELL')
	wasNodeName = java.lang.System.getenv().get('WAS_NODE')

	node = AdminConfig.getid("/Cell:"+wasCellName+"/Node:"+wasNodeName+"/")
	name = ['name', 'MySQLJDBCProvider']
	description = ['name', 'MySQL JDBC Provider']
	implementationClassName = ['implementationClassName', 'com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource']
	classpath = ['classpath', '${USER_INSTALL_ROOT}/gatein/lib/mysql-connector-java-5.1.12-bin.jar']
	xa = ['xa', 'false']
	jdbcAttrs = [name,  description, implementationClassName, classpath, xa]
	jdbcProvider = AdminConfig.create('JDBCProvider', node, jdbcAttrs)

	jcrDSName = ['name', 'eXo JCR']
	jcrJNDIName = ['jndiName', 'exo-jcr_portal']
	jcrDSDescription = ['description', 'eXo JCR Datasource']
	jcrDSHelper = ['datasourceHelperClassname', 'com.ibm.websphere.rsadapter.ConnectJDBCDataStoreHelper']
	jcrDSAttributes = [jcrDSName, jcrJNDIName, jcrDSDescription, jcrDSHelper]
	jcrDataSource = AdminConfig.create('DataSource', jdbcProvider, jcrDSAttributes)

	# Set Cloudscape custom property for databaseName.
	jcrDSPropSet = AdminConfig.create('J2EEResourcePropertySet', jcrDataSource, [])

	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'serverName'],['type','java.lang.String'],['value', dbServerURL]])
	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'port'],['type','java.lang.String'],['value',dbServerPort]])
	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'databaseName'],['type','java.lang.String'],['value',jcrDBName+"?autoReconnect=true"]])
	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'user'],['type','java.lang.String'],['value',dbUserName]])
	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'password'],['type','java.lang.String'],['value',dbPassword]])
	AdminConfig.create('J2EEResourceProperty', jcrDSPropSet, [['name', 'nonTransactionalDataSource'],['type','java.lang.Boolean'],['value','true']])

	idmDSName = ['name', 'eXo IDM']
	idmJNDIName = ['jndiName', 'exo-idm_portal']
	idmDSDescription = ['description', 'eXo IDM Datasource']
	idmDSHelper = ['datasourceHelperClassname', 'com.ibm.websphere.rsadapter.ConnectJDBCDataStoreHelper']
	idmDSAttributes = [idmDSName, idmJNDIName, idmDSDescription, idmDSHelper]
	idmDataSource = AdminConfig.create('DataSource', jdbcProvider, idmDSAttributes)

	# Set Cloudscape custom property for databaseName.
	idmDSPropSet = AdminConfig.create('J2EEResourcePropertySet', idmDataSource, [])

	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'serverName'],['type','java.lang.String'],['value', dbServerURL]])
	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'port'],['type','java.lang.String'],['value',dbServerPort]])
	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'databaseName'],['type','java.lang.String'],['value',idmDBName+"?autoReconnect=true"]])
	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'user'],['type','java.lang.String'],['value',dbUserName]])
	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'password'],['type','java.lang.String'],['value',dbPassword]])
	AdminConfig.create('J2EEResourceProperty', idmDSPropSet, [['name', 'nonTransactionalDataSource'],['type','java.lang.Boolean'],['value','true']])

	AdminConfig.save()
	print 'Testing JCR datasource connection'
	print AdminControl.testConnection(jcrDataSource)

	print 'Testing IDM datasource connection'
	print AdminControl.testConnection(idmDataSource)



#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
if (len(sys.argv) != 6):
   print "SetupDB: this script requires 6 parameters: "
   print "     the name of the DB Server or IP, like 127.0.0.1 or localhost"
   print "	   the DB Server port"
   print "	   the jcr database name"
   print "	   the idm database name"
   print "	   the DB user name"
   print "	   the DB password"
else:
	SetupDB(sys.argv[0], sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5])