# Shading DadaGUI inside another plugin

DadaGUI exposes two different build outputs:

| Output | Purpose |
|---|---|
| `dadagui-dist-universal` | Standalone demo/test plugin. Contains `plugin.yml` and example commands. Do **not** shade this into another plugin. |
| `dadagui-bundle-universal` | Library bundle for other plugins. Contains API, Bukkit runtime and version adapters, but no `plugin.yml`. Shade this into your plugin. |

## 1. Install DadaGUI locally

The artifact is not available on public repositories unless you publish it yourself.
From the DadaGUI root project run:

```bash
mvn clean install -pl dadagui-bundle-universal -am
```

This installs the artifact in your local Maven repository:

```text
~/.m2/repository/it/dadagui/dadagui-bundle-universal/2.8.0-SNAPSHOT/
```

After that, your plugin can depend on it.

## 2. Add the dependency to your plugin

```xml
<dependency>
    <groupId>it.dadagui</groupId>
    <artifactId>dadagui-bundle-universal</artifactId>
    <version>2.8.0-SNAPSHOT</version>
</dependency>
```

Do not use `dadagui-dist-universal` as a dependency for another plugin.

## 3. Shade it into your final plugin jar

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.3</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <minimizeJar>false</minimizeJar>

                        <relocations>
                            <relocation>
                                <pattern>me.mrbast.dadagui</pattern>
                                <shadedPattern>com.yourplugin.libs.dadagui</shadedPattern>
                            </relocation>
                        </relocations>

                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                        </transformers>

                        <filters>
                            <filter>
                                <artifact>*:*</artifact>
                                <excludes>
                                    <exclude>plugin.yml</exclude>
                                    <exclude>META-INF/*.SF</exclude>
                                    <exclude>META-INF/*.DSA</exclude>
                                    <exclude>META-INF/*.RSA</exclude>
                                </excludes>
                            </filter>
                        </filters>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

`ServicesResourceTransformer` is mandatory because DadaGUI discovers Minecraft version adapters through `ServiceLoader`.

## 4. Common error

If Maven shows:

```text
Could not find artifact it.dadagui:dadagui-bundle-universal:pom:2.8.0-SNAPSHOT
```

then the bundle has not been installed or published. Fix it with:

```bash
cd DadaGUI-universal
mvn clean install -pl dadagui-bundle-universal -am
```

If Maven searches only a remote repository such as `auxilor-public`, the local artifact is still used automatically after `mvn install`, unless your Maven configuration is forcing mirrors for every repository. In that case, publish the artifact to your private repository or disable the mirror for local development.

## 5. Do not minimize the jar

Avoid:

```xml
<minimizeJar>true</minimizeJar>
```

DadaGUI uses `ServiceLoader` and version adapters. Minification can remove classes that are only loaded at runtime.
