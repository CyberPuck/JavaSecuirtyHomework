# import certs
sh certImport.sh serverTruststore.jks server client1
sh certImport.sh serverTruststore.jks server client2
sh certImport.sh serverTruststore.jks server client3
# clients need the server
sh certImport.sh client1Truststore.jks client server
sh certImport.sh client2Truststore.jks client server
sh certImport.sh client3Truststore.jks client server