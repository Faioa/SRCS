package srcs.interpretor;

import java.io.*;

public class CustomObjectInputStream extends ObjectInputStream {

    private ClassLoader classLoader;

    public CustomObjectInputStream(InputStream in) throws IOException {
        super(in);
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    protected CustomObjectInputStream() throws IOException, SecurityException {
        super();
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws ClassNotFoundException {
        return Class.forName(objectStreamClass.getName(), false, classLoader);
    }

}
