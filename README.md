# Добавление нового сертификата

Создаём пустой JKS
```shell
$ keytool -storetype jks -keyalg RSA -genkeypair -alias boguscert -storepass supersecretpassword -keypass supersecretpassword -keystore lekeystore.jks -dname "CN=Developer, OU=Department, O=Company, L=City, ST=State, C=CA"
$ keytool -delete -alias boguscert -storepass supersecretpassword -keystore lekeystore.jks
```

Импортируем туда root-сертификат (в формате der)
```shell
$ keytool -trustcacerts -importcert -keystore lekeystore.jks -file isrgrootx1.der -storepass supersecretpassword -alias isrg-root-x1
```

Проверяем результат
```shell
$ keytool -list -keystore lekeystore.jks -storepass supersecretpassword
```
