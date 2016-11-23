# Generates a key store with a public private key pair
# exports the public key

# format: genKey.sh <key store> <password> <alias> <key password>
# Remove the existing key store
rm -f $1
keytool -keystore $1 -storepass $2 -genkeypair -alias $3 -keyalg RSA -keysize 2048 -keypass $4 -dname "o=jhu.edu, cn=Kyle Travers"
keytool -keystore $1 -storepass $2 -exportcert -alias $3 -file $3.cer