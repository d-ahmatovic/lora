public class Encryption {
    // Unsere eigene Liste aller Symbole, die mit verschlüsselt werden sollen
    private static final String SYMBOL_LIST = "0123456789.";

    public static String caesarEncrypt(String input, int key)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            int index = SYMBOL_LIST.indexOf(c);

            // Verschiebung mit Modulo, um im Bereich zu bleiben
            int verschoben = (index + key) % SYMBOL_LIST.length();
            if (verschoben < 0) {
                verschoben += SYMBOL_LIST.length();
            }
            result.append(SYMBOL_LIST.charAt(verschoben));
        }

        return result.toString();
    }

    public static String caesarDecrypt(String input, int key)
    {
        return caesarEncrypt(input, (-1)*key);
    }
}
