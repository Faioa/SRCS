package srcs.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public interface Service {

    default void execute(Socket connexion) {
        try {
            ObjectInputStream in = new ObjectInputStream(connexion.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(connexion.getOutputStream());

            try {
                // Server reads method name
                String name = in.readUTF();

                // Server Tries to get the corresponding method (assuming the method name is unique)
                Method[] possible_m = this.getClass().getDeclaredMethods();
                Method method = null;
                for (Method m : possible_m) {
                    if (m.getName().equals(name)) {
                        method = m;
                        break;
                    }
                }

                // Server didn't find the method
                if (method == null) {
                    out.writeObject(new MyProtocolException());
                    out.flush();
                    return;
                }

                // Getting the types of the expected arguments
                Class<?>[] argsTypes = method.getParameterTypes();
                Object[] args = new Object[argsTypes.length];

                // Getting the arguments as they are expected in order
                int i = 0;
                for (; i < argsTypes.length && in.available() > 0; i++) {
                    if (argsTypes[i].isPrimitive()) {
                        if (argsTypes[i] == byte.class) {
                            args[i] = in.readByte();
                        } else if (argsTypes[i] == short.class) {
                            args[i] = in.readShort();
                        } else if (argsTypes[i] == int.class) {
                            args[i] = in.readInt();
                        } else if (argsTypes[i] == long.class) {
                            args[i] = in.readLong();
                        } else if (argsTypes[i] == float.class) {
                            args[i] = in.readFloat();
                        } else if (argsTypes[i] == double.class) {
                            args[i] = in.readDouble();
                        } else if (argsTypes[i] == boolean.class) {
                            args[i] = in.readBoolean();
                        } else if (argsTypes[i] == char.class) {
                            args[i] = in.readChar();
                        } else {
                            args[i] = null;
                        }
                    } else {
                        if (argsTypes[i] == String.class) {
                            args[i] = in.readUTF();
                        } else {
                            args[i] = in.readObject();
                        }
                    }
                }

                // The client didn't give the right number of arguments
                if (i != argsTypes.length) {
                    out.writeObject(new MyProtocolException());
                    out.flush();
                    return;
                }

                // Server tries to return the result. If it cannot, returns an exception.
                Object res = method.invoke(this, args);
                if (res == null)
                    out.writeObject(new VoidResponse());
                else
                    out.writeObject(res);
                out.flush();
            } catch (ClassNotFoundException | InvocationTargetException |
                     IllegalAccessException e) {
                out.writeObject(new MyProtocolException());
                out.flush();
                throw new RuntimeException(e);
            } finally {
                // Server closes the connexion
                connexion.close();
            }
        } catch (IOException _) {
            // Ignored : connexion with the client coundn't close
        }
    }

}
