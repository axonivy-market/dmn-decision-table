# LDAP Beans
Provides simple Axon.ivy process element extensions that allow to write or query LDAP objects.

## Query Bean
Allows to query LDAP objects. In this example we query all mail adresses of users within our organisation unit.

First: Add an Program Element to your process and select the PI class `ch.ivyteam.ivy.ldap.beans.LdapQueryBean`

![PiClassSelection](samples/screenshots/queryBean_piClass.png)

Second: define the connection to your LDAP server

![QueryServerDefinition](samples/screenshots/queryBean_serverDefinition.png)

Third: setup the filter criteria. In this case we filter for users
![QueryFilterDefinition](samples/screenshots/queryBean_queryDefinition.png)

Fourth: map the data back to process data


Full example: 
![AccessResult](samples/screenshots/queryBean_accessResultInProcess.png)
![LdapBrowser](samples/screenshots/queryBean_browseLdap_usersWithEMail.png)


