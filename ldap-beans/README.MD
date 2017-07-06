# LDAP Beans
Provides simple Axon.ivy process element extensions that allow to write or query LDAP objects.

## Query Bean
Allows to query LDAP objects. In this example we query all mail adresses of users within our organisation unit.

### Add a Program Element and select the PI class 
`ch.ivyteam.ivy.ldap.beans.LdapQueryBean`
![PiClassSelection](samples/screenshots/queryBean_piClass.png)

### Define the connection to your LDAP server
![QueryServerDefinition](samples/screenshots/queryBean_serverDefinition.png)

### Setup the filter criteria
In this case we filter for users

![QueryFilterDefinition](samples/screenshots/queryBean_queryDefinition.png)

### Map the data back to process data


### Full example
Rough overview over the LDAP organisation unit. We have three users that own a 'mail' attribute.
![LdapBrowser](samples/screenshots/queryBean_browseLdap_usersWithEMail.png)

We're able to log and display the users mail adresses:
![AccessResult](samples/screenshots/queryBean_accessResultInProcess.png)



