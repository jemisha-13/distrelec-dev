Firewall settings prohibit access to SAP-PI from localhost.


-------------------------------------------------------------------------------


If you want to connect SAP-PI from localhost you have to create an SSH tunnel.
The following command will tunnel your traffic from localhost via hybris d00 (or q00) to SAP-PI:


ssh tunnel localhost -> hybris d00 -> SAP-PI D/Q
    ssh -L 50000:198.18.2.141:50000 daesup_hybris@198.18.7.85


ssh tunnel localhost -> hybris q00 -> SAP-PI D/Q
    ssh -L 50000:198.18.2.141:50000 daesup_hybris@198.18.7.89

ssh tunnel localhost -> hybris q01 -> SAP-PI D/Q
    ssh -L 50000:198.18.2.141:50000 daesup_hybris@198.18.7.90

ssh tunnel localhost -> hybris q02 -> SAP-PI D/Q
    ssh -L 50000:198.18.2.141:50000 daesup_hybris@198.18.7.91


ssh tunnel localhost -> hybris p00 -> SAP-PI P
    ???

ssh tunnel localhost -> hybris p01 -> SAP-PI P
    ???
    
ssh tunnel localhost -> hybris p02 -> SAP-PI P
    ???


-------------------------------------------------------------------------------


Add the following entry to you hosts file to be able to use the original WSDL endpoint:

# Workaround to send SOAP calls via SSH tunnel to PI
127.0.0.1  daechs062u.dae.datwyler.biz


-------------------------------------------------------------------------------


How to use FactFinder soapUI projects

FactFinder authentication must be initialized before sending a soap request:
* Open "AuthInit" Test Suite within the project which contains the request (double click)
* Open and run "Setup Script"
