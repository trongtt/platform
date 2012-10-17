Welcome to Platform
==================

This document explains how to build and package Platform for Websphere

Prerequisites
=============

- Java Development Kit 1.6
- Recent Git client
- Recent Maven 3

Build configuration
===================

1) Profile configuration

Platform build uses several profiles to configure which packaging  to be generated, the profile pkg-websphere will be used to generate all stuffs related to websphere

- docs           : contains installation_guide in which we describe steps to follow to install platform.ear under Websphere 7.0
- platform.ear   : platform application to be installed on Websphere 7.0
- gatein.zip     : external configuration files used by platform engine

2) Database configuration

By default the build uses a HSQLDB database. However, it is possible to use MySQL5


Build instructions
==================

1) Clone Platform
--------------------------

git clone git@github.com:exoplatform/platform.git
cd platform

2) Build and package platform
----------------------------------

You can build platform without packaging (only entreprise Tomcat bundle)it by using the following command:

mvn clean install -Dmaven.test.skip=true

But that's only usable for development since in order to be able to run platform you have to package it.

Platform can be packaged with different web / application servers. The specific server to use is selected by using an appropriate profile.

  Packaging with Websphere
  --------------------------------

mvn install -Ppkg-websphere -Dmaven.test.skip=true

The packaged Platform is available in packaging/platform-for-websphere/target/

To start it please refer to the install guide.

Troubleshooting
===============

Going Further
=============
Your next stop will depend on who you are:

   * Developers: learn how to build your own portal, gadgets, REST services or eXo-based applications in the Developer Guide [http://docs.exoplatform.com/PLF35/topic/org.exoplatform.doc.35/DeveloperGuide.html] and the Reference Documentation [http://docs.exoplatform.com/PLF35/topic/org.exoplatform.doc.35/GateInReferenceGuide.html]
   * Administrators: learn how to install eXo Platform on a server in the Administrator Guide: http://docs.exoplatform.com/PLF35/topic/org.exoplatform.doc.35/AdministratorGuide.html
   * End Users: learn more about using the features in the User Manuals: http://docs.exoplatform.com/PLF35/topic/org.exoplatform.doc.35/UserGuide.html
