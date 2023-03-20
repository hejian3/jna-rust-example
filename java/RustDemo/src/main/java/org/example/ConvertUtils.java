package org.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.JarFile;


public class ConvertUtils {

    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static final String TEMP_DIR = System.getProperty(JAVA_IO_TMPDIR);
    private static final String JNA_LIBRARY_PATH = "jna.library.path";

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.setProperty("jna.encoding", "UTF-8");
        String jnaLibraryPath = getJnaLibraryPath();
        System.out.println("jnaLibraryPath = " + jnaLibraryPath);
        System.out.println(ConvertUtils.DLL.INSTANCE.foo(1, 2));
        System.out.println(ConvertUtils.DLL.INSTANCE.boo("1...world...1"));
        System.out.println(ConvertUtils.DLL.INSTANCE.boo("world"));
        System.out.println(ConvertUtils.DLL.INSTANCE.boo("1world1"));
        System.out.println(ConvertUtils.DLL.INSTANCE.boo("world"));

        DLL.INSTANCE.convert_osgb(jnaLibraryPath, "C:\\Users\\hejian\\Desktop\\OSGB", "D:\\osgb2", "");
    }

    private static String getJnaLibraryPath() throws URISyntaxException, IOException {
        System.out.println(Platform.RESOURCE_PREFIX);
        URL current_jar_dir = ConvertUtils.class.getProtectionDomain().getCodeSource().getLocation();
        Path jar_path = Paths.get(current_jar_dir.toURI());
        String folderContainingJar = jar_path.getParent().toString();
        ResourceCopy r = new ResourceCopy();
        Optional<JarFile> jar = r.jar(ConvertUtils.class);
        if (jar.isPresent()) {
            try {
                System.out.println("JAR detected");
                File target_dir = new File(folderContainingJar);
                System.out.println(String.format("Trying copy from %s %s to %s", jar.get().getName(), Platform.RESOURCE_PREFIX, target_dir));
                // perform dir copy
                r.copyResourceDirectory(jar.get(), Platform.RESOURCE_PREFIX, target_dir);
                // add created folders to JNA lib loading path
                System.setProperty(JNA_LIBRARY_PATH, target_dir.getCanonicalPath().toString());
                return target_dir.getCanonicalPath().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1) + Platform.RESOURCE_PREFIX;
    }
    public interface DLL extends Library {
        DLL INSTANCE = Native.load("_3dtile", DLL.class);

        String boo(String a);

        int foo(int a, int b);

        void convert_b3dm(String libPath, String input, String output);

        void convert_gltf(String libPath, String input, String output);

        void convert_osgb(String libPath, String input, String output, String tileConfig);

        void convert_shapefile(String libPath, String input, String output, String heightField);

    }
}
