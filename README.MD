Plugins to integrate jasper reports and pentaho
===============================================

This repository  contains the code of a pentaho plugin to integrate jasperreports inside pentaho bussiness server, a Jasper Reports plugin with a datasource to access [Pentaho Data Access](http://community.pentaho.com/ctools/cda/) datasources, and a Jasper Studio Plugin to include a CDA datasource inside Jasper Design

## Features:
  - Load a CDA datasource inside Jasper Studio
  - Execute a CDA from a Pentaho Bussiness Server
  - Map the CDA Parameters from any expression
  - Load jrxml and jasper Files inside Pendaho Bussiness Server

## Build

### Jasper Reports CDA Datasource

  Before build the other plugins, is necessary to build the datasource plugin. You can do it executing _maven install_ from the folder pentaho-cda-datasource

### Jasper Studio Plugin
  1. Follow the instructions to download the code from [Build Sources](http://community.jaspersoft.com/wiki/contributing-jaspersoft-studio-and-building-sources). The minimum version to compile are Jasper Studio 6.4.1 and Eclipse 4.6+
  2. Clone the code from this repository inside your eclipse workspace
  3. Download the required libraries executing _maven process-resources_, to make this Tycho should be properly configured.
  3. You can use Tycho to compile the plugin or you can debug directly from eclipse workspace

### Pentaho Plugin

  From the pentaho-jasperreports-plugin folder execute _maven package_ to build a zip. To install the plugin you can extract the zip file inside pentaho solutions folder. PENTAHO_SERVER_HOME/pentaho-solutions/system

## Jasper Studio Usage  

  1. Create a new Data Adapter  using the Pentaho CDA Connection option

  2. Set the configuration, if your pentaho server is 5.x+ the 5.x option should be checked. You can test the connection clicking the test button. The cda plugin should be installed inside the pentaho bussiness server

  3. When you edit a datasource, select the connection you've created and **PentahoCdaQuery** as language

  4. Click on browse to select a CDA File. Using double click, you can see a folder's content

  5. Select a Query, clicking inside the Query table.

  6. To edit the parameter values, you can click inside the value cell and then click on "..." button

  7. Click on Read Fields to load all the columns from the datasource

