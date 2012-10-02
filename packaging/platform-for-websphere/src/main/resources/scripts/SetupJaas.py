#-----------------------------------------------------------------
# SetupJaas.py - Jython implementation of Setup Platform Database
#-----------------------------------------------------------------
#
#  The purpose of this script is to create a JAAS Login module Realm.
#  
#  This script can be included in the wsadmin command invocation like this:
#     
#     wsadmin -lang jython -f SetupJaas.py <WASServerName>
#
#  or the script can be execfiled from the wsadmin command line like this:
#     wsadmin> execfile("SetupJaas.py")
#     wsadmin> SetupJaas("server1")
#
#  The script expects 1 parameter:
#   
#   wasServerName:	the WAS Server name
#-----------------------------------------------------------------
#
import sys

def SetupJaas(wasServerName):
	wasCellName = java.lang.System.getenv().get('WAS_CELL')
	wasNodeName = java.lang.System.getenv().get('WAS_NODE')

	AdminTask.setAdminActiveSecuritySettings(['-appSecurityEnabled', 'true'])
	AdminTask.createSecurityDomain('-securityDomainName gatein-domain')
	AdminConfig.save()

	AdminTask.mapResourceToSecurityDomain('-securityDomainName gatein-domain -resourceName -resourceName Cell=:Node='+wasNodeName+':Server='+wasServerName)
	AdminTask.configureJAASLoginEntry('-securityDomainName gatein-domain -loginType system -loginEntryAlias WEB_INBOUND -loginModules org.exoplatform.web.security.PortalLoginModule,org.exoplatform.services.security.jaas.SharedStateLoginModule,org.exoplatform.services.security.j2ee.websphere.WebsphereJAASLoginModule,com.ibm.ws.security.server.lm.ltpaLoginModule,com.ibm.ws.security.server.lm.wsMapDefaultInboundLoginModule -authStrategies OPTIONAL,OPTIONAL,OPTIONAL,REQUIRED,REQUIRED')
	AdminTask.setAppActiveSecuritySettings('-securityDomainName gatein-domain -enforceJava2Security false -useDomainQualifiedUserNames false -appSecurityEnabled true  -customProperties ["com.ibm.websphere.security.util.fullyQualifiedURL=true"]')
	AdminConfig.save()

#-----------------------------------------------------------------
# Main
#-----------------------------------------------------------------
if (len(sys.argv) != 1):
   print "SetupJaas: this script requires 1 parameter: "
   print "     the WAS Server name"
else:
	SetupJaas(sys.argv[0])