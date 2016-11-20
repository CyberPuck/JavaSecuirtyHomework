# shell script for importing a cert
keytool -importcert -file client.cer -keystore serverTruststore.jks

# export
keytool -keystore clientKeystore.jks -exportcert -alias mykey -file client.cer

# script for creating an SSL (X.509) cert
keytool -keystore clientKeystore.jks -genkeypair -keyalg RSA -keysize 2048 -storepass clientPassword