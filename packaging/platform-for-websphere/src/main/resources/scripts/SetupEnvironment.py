#-----------------------------------------------------------------
# SetupEnvironment.py - Jython implementation of Setup Platform Database
#-----------------------------------------------------------------
#
#  The purpose of this script is to add some required configuration and environment entries.
#  
#  This script can be included in the wsadmin command invocation like this:
#     
#     wsadmin -lang jython -f SetupEnvironment.py <WASServerName>
#
#  or the script can be execfiled from the wsadmin command line like this:
#     wsadmin> execfile("SetupEnvironment.py")
#     wsadmin> SetupEnvironment("server1")
#
#  The script expects 1 parameter:
#   
#   wasServerName:	the WAS Server name
#-----------------------------------------------------------------
#
import sys, java

def SetupEnvironment(wasServerName):
	wasCellName = java.lang.System.getenv().get('WAS_CELL')
	wasNodeName = java.lang.System.getenv().get('WAS_NODE')
	wasProfileRelativePath = java.lang.System.getenv().get('USER_INSTALL_ROOT')
	wasProfileRelativePath = wasProfileRelativePath[wasProfileRelativePath.index("/profiles/"):]

	server = AdminConfig.getid("/Cell:"+wasCellName+"/Node:"+wasNodeName+"/Server:"+wasServerName+"/")
	jvm = AdminConfig.list('JavaVirtualMachine', server)
	AdminConfig.modify(jvm, [['initialHeapSize','256'],['maximumHeapSize','1024'],['genericJvmArguments', '-Dexo.conf.dir.name='+wasProfileRelativePath+'/gatein/ -Dgatein.data.dir=${USER_INSTALL_ROOT}/gatein/data/ -Dexo.profiles=default -Dexo.product.developing=false']])

	applicationServer = AdminConfig.list("ApplicationServer", server)
	AdminConfig.modify(applicationServer, [['applicationClassLoaderPolicy','SINGLE']])
	serverAttrs = [["name", "com.ibm.websphere.threadmonitor.interval"], ["value", "-1"], ["description", "Threadmonitor interval"]]
	AdminConfig.create("Property", applicationServer, serverAttrs)

	library = AdminConfig.create('Library', server, [['name', 'Platform shared libraries'], ['classPath', '${USER_INSTALL_ROOT}/gatein/shared']])
	serverClassLoader = AdminConfig.create('Classloader', applicationServer, [['mode',  'PARENT_FIRST']])
	AdminConfig.create('LibraryRef', serverClassLoader, [['libraryName', 'Platform shared libraries']])

	AdminConfig.save()
	

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
if (len(sys.argv) != 1):
   print "SetupEnvironment: this script requires 1 parameter: "
   print "     the WAS Server name"
else:
	SetupEnvironment(sys.argv[0])