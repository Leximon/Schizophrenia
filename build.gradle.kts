import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import java.net.URI

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.12"
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "dev.leximon"
version = "1.0"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

repositories {
    mavenCentral()
    maven { url = URI("https://repo.codemc.org/repository/maven-public/") }
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    compileOnly("dev.jorel:commandapi-bukkit-core:9.3.0")
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release = 17
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
      // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
      outputJar = layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar")
    }
     */
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
bukkitPluginYaml {
    main = "dev.leximon.schizophrenia.SchizophreniaPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Leximon")
    depend.add("CommandAPI")
    apiVersion = "1.20"
}