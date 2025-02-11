package srcs.service;

public interface AppelDistant extends Service {

    /*
     * This function was already coded in the *Service* interface... I created this interface to follow the exercise,
     * but it doesn't have any purpose at the moment...
     *
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
                    out.writeObject(new MyProtocolException("Method \"" + name + "\"not found"));
                    out.flush();
                    return;
                }

                // Getting the types of the expected arguments
                Class<?>[] argsTypes = method.getParameterTypes();
                Object[] args = new Object[argsTypes.length];

                // Getting the arguments as they are expected in order
                int i = 0;
                for (; i < argsTypes.length && in.available() > 0; i++) {
                    if (argsTypes[i] == byte.class || argsTypes[i] == Byte.class) {
                        args[i] = in.readByte();
                    } else if (argsTypes[i] == short.class || argsTypes[i] == Short.class) {
                        args[i] = in.readShort();
                    } else if (argsTypes[i] == int.class || argsTypes[i] == Integer.class) {
                        args[i] = in.readInt();
                    } else if (argsTypes[i] == long.class || argsTypes[i] == Long.class) {
                        args[i] = in.readLong();
                    } else if (argsTypes[i] == float.class || argsTypes[i] == Float.class) {
                        args[i] = in.readFloat();
                    } else if (argsTypes[i] == double.class || argsTypes[i] == Double.class) {
                        args[i] = in.readDouble();
                    } else if (argsTypes[i] == boolean.class || argsTypes[i] == Boolean.class) {
                        args[i] = in.readBoolean();
                    } else if (argsTypes[i] == char.class || argsTypes[i] == Character.class) {
                        args[i] = in.readChar();
                    } else if (argsTypes[i] == String.class) {
                        args[i] = in.readUTF();
                    } else {
                        args[i] = in.readObject();
                    }
                }

                // The client didn't give the right number of arguments
                if (i != argsTypes.length) {
                    out.writeObject(new MyProtocolException("Wrong number of arguments for the method \"" + name + "\"."));
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
                out.writeObject(new MyProtocolException("A problem occurred during the parsing of the arguments."));
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
    */

}
