package dzung.trie.spell_checker;

import org.openjdk.jmh.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

@SpringBootApplication
public class SpellCheckerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpellCheckerApplication.class, args);

//        ClassLoader classLoader = SpellCheckerApplication.class.getClassLoader();
//        StringBuilder classpath = new StringBuilder();
//
//        if (classLoader instanceof URLClassLoader) {
//            for (URL url : ((URLClassLoader) classLoader).getURLs()) {
//                classpath.append(url.getPath()).append(File.pathSeparator);
//            }
//        } else {
//            String[] classpathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
//            for (String entry : classpathEntries) {
//                classpath.append(entry).append(File.pathSeparator);
//            }
//        }
//
//        System.setProperty("java.class.path", classpath.toString());
//        Main.main(args);
    }
}
// When initial Tries, we have 1.467.206 nodes in two tries.
