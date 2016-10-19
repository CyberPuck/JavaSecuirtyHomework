# Runs the invalid class files in order to break the class verifer
cd ./homework03/bin
cp -fr ./classVerifier/modifiedClasses/*.class .
echo -e "\n----Pass11.class executing----"
java classVerifier.Pass11
echo -e "\n----Pass12.class executing----"
java classVerifier.Pass12
echo -e "\n----Pass21.class executing----"
java classVerifier.Pass21
echo -e "\n----Pass22.class executing----"
java classVerifier.Pass22
echo -e "\n----Pass31.class executing----"
java classVerifier.Pass31
echo -e "\n----Pass32.class executing----"
java classVerifier.Pass32
echo -e "\n----Pass41.class executing----"
java classVerifier.Pass41
echo -e "\n----Pass42.class executing----"
java classVerifier.Pass42

echo -e " \n+++ Scipt Complete +++"