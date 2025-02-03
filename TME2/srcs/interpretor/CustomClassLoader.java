package srcs.interpretor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class CustomClassLoader extends ClassLoader{

    private Map<String, String> cmds_paths;
    private Map<String, ClassLoader> loaded;

    public CustomClassLoader(Map<String, String>cmds_paths, ClassLoader parent) {
        super(parent);
        this.cmds_paths = cmds_paths;
        loaded = new HashMap<String, ClassLoader>();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = cmds_paths.get(name);
        if (path != null) {
            ClassLoader loader = loaded.get(path);
            if (loader == null) {
                try {
                    loader = new URLClassLoader(new URL[]{new File(path).toURI().toURL()});
                    loaded.put(path, loader);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            return loader.loadClass(name);
        }
        return super.findClass(name);
    }

}
