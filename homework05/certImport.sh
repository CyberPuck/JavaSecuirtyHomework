# Imports client cert into server trust store

# Expected format certImport.sh <trust store> <password> <client alias>
keytool -keystore $1 -storepass $2 -importcert -file $3.cer -alias $3