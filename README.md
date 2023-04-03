# CAFixer

<p>
    <a href="https://central.sonatype.com/search?q=ca-fixer&namespace=com.turikhay">
        <img src="https://img.shields.io/maven-central/v/com.turikhay/ca-fixer" />
    </a>
</p>

Zero-dependency utility that adds root certificate authority. In our case it only contains [ISRG Root X1](https://letsencrypt.org/certificates/) for older Java 8 versions (before Update 101)<sup>[1](https://letsencrypt.org/docs/certificate-compatibility/)</sup>.

## How to use?

### In Minecraft launcher
You can use tweakers:
* `com.turikhay.caf.Tweaker` or `com.turikhay.caf.Tweaker2`
* `com.turikhay.caf.LegacyTweaker` for legacy Minecraft releases (1.5.2 and older)

### As a Java agent
```shell
$ java -javaagent:"ca-fixer.jar" -jar ...
```

### In the code
```java
static {
    CAFixer.fix();
}
```
```java
public static void main(String[] args) {
    CAFixer.fix();
}
```

## Include as a dependency

### Maven

```xml
<dependency>
    <groupId>com.turikhay</groupId>
    <artifactId>ca-fixer</artifactId>
    <version>1.6</version>
</dependency>
```

### Gradle

```gradle
implementation 'com.turikhay:ca-fixer:1.6'
```

## Note: How to create my own .jks file

1. Create an empty .jks
```shell
$ keytool -storetype jks -keyalg RSA -genkeypair -alias boguscert -storepass supersecretpassword -keypass supersecretpassword -keystore lekeystore.jks -dname "CN=Developer, OU=Department, O=Company, L=City, ST=State, C=CA"
$ keytool -delete -alias boguscert -storepass supersecretpassword -keystore lekeystore.jks
```

2. Import target root certificate in DER format
```shell
$ keytool -trustcacerts -importcert -keystore lekeystore.jks -file isrgrootx1.der -storepass supersecretpassword -alias isrg-root-x1
```

3. Check the result
```shell
$ keytool -list -keystore lekeystore.jks -storepass supersecretpassword
```
