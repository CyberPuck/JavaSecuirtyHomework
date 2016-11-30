# Generate DSA key pair
# genKey.sh <key store> <key store password> <alias of key>
keytool -keystore $1 -storepass $2 -genkeypair -alias $3 -keyalg DSA -keysize 1024 -keypass $2 -dname "o=jhu.edu, cn=Kyle Travers"