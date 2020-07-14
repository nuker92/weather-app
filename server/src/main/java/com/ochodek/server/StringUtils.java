package com.ochodek.server;

public interface StringUtils {

    /**
     * Change typical polish characters like ą, ę to the version without tail.
     * @param source Source string
     * @return Filtered String
     */
    static String filterDiacriticChars(String source) {
        return source
                .replace("ą", "a").replace("Ą", "A")
                .replace("ć", "c").replace("Ć", "C")
                .replace("ę", "e").replace("Ę", "E")
                .replace("ł", "l").replace("Ł", "L")
                .replace("ń", "n").replace("Ń", "N")
                .replace("ó", "o").replace("Ó", "O")
                .replace("ś", "s").replace("Ś", "S")
                .replace("ż", "z").replace("Ż", "Z")
                .replace("ź", "z").replace("Ź", "Z");
    }



}
