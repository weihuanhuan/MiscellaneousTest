#####cert store
---server store
keytool -genkey -v -alias bluedash-ssl-demo-server -keyalg RSA -keystore ./server_ks -dname "CN=localhost,OU=cn,O=cn,L=cn,ST=cn,C=cn" -storepass server -keypass 123123

---client store
keytool -genkey -v -alias bluedash-ssl-demo-client -keyalg RSA -keystore ./client_ks -dname "CN=localhost,OU=cn,O=cn,L=cn,ST=cn,C=cn" -storepass client -keypass 456456

#####server auth
---server cert
keytool -export -alias bluedash-ssl-demo-server -keystore ./server_ks -file server_key.cer

---client trust server
keytool -import -trustcacerts -alias bluedash-ssl-demo-server -file ./server_key.cer -keystore ./client_ks

#####client auth
---client cert
keytool -export -alias bluedash-ssl-demo-client -keystore ./client_ks -file client_key.cer

---server trust client
keytool -import -trustcacerts -alias bluedash-ssl-demo-client -file ./client_key.cer -keystore ./server_ks
