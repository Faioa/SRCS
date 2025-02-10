package srcs.service.annuaire;

public interface Annuaire {

    String lookup(String name);

    void bind(String name, String value);

    void unbind(String name);

}
