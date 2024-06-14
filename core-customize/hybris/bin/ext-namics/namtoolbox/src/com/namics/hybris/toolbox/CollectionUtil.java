package com.namics.hybris.toolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class CollectionUtil {

    /**
     * Fügt auf die <code>Map</code> <code>destination</code> den Befehl <code>putAll()</code> aus, sofern source nicht <code>null</code>
     * ist.<br>
     * entspricht <code>destination</code>.putAll(<code>source</code>);<br>
     * CollectionUtil.putAllNullSave(destination, source);
     * 
     * @param destination
     *            Die Map, welche die Werte von <code>source</code> erhalten soll.
     * @param source
     *            Die Daten, welche eingefügt werden sollen.
     * @return Gibt <code>destination</code> zurück.
     */
    public static Map<Object, Object> putAllNullSave(final Map<Object, Object> destination, final Map<Object, Object> source) {
        if (source != null && destination != null) {
            destination.putAll(source);
        }
        return destination;
    }

    /**
     * Wandelt eine Map in einen lesbaren Text um z.B. bei einem Logger auszugeben.
     * 
     * @param source
     *            Die umzuwandlende Map.
     * @return Umgewandelte Map
     */
    public static String mapToString(final Map<Object, Object> source) {
        if (source == null) {
            return "Map is null";
        }

        final StringBuffer result = new StringBuffer(50);
        result.append("Map '" + source + "' (" + source.size() + "):\n");

        for (final Iterator<Object> iter = source.keySet().iterator(); iter.hasNext();) {
            try {
                final Object key = iter.next();
                final Object value = source.get(key);

                final String keyString = (key instanceof String) ? (String) key : key.toString();
                final String valueString = (value instanceof String) ? (String) value : value.toString();

                result.append(keyString + ":" + valueString + "\n");
            } catch (final Exception e) {
                result.append("Couldn't read an entry from Map");
            }
        }

        return result.toString();
    }

    /**
     * Wandelt ein kommaseparierter String in eine <code>List</code> um. Alle Listeneinträge werden "getrimt", wenn <code>toLowerCase</code>
     * auf <code>true</code> ist und auf Kleinbuchstaben reduziert wenn <code>trim</code> auf <code>true</code> ist
     * 
     * @see String#toLowerCase()
     * @see String#trim()
     * 
     * @param commaSeparatedString
     *            Der kommaseparierter String
     * @param toLowerCase
     *            true wenn auf Kleinbuchstaben reduziert werden soll
     * @param trim
     *            true wenn die Listeneinträge "getrimt" werden sollen
     * @return Liste der einzelnen Strings
     */
    public static List<String> commaSeparatedStringToList(final String commaSeparatedString, final boolean toLowerCase, final boolean trim) {
        final List<String> resultList = new ArrayList<String>();

        final StringTokenizer tokenizer = new StringTokenizer(commaSeparatedString, ",");

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = toLowerCase ? token.toLowerCase() : token;
            token = trim ? token.trim() : token;
            resultList.add(token);
        }

        return resultList;
    }

    /**
     * Wandelt ein kommaseparierter String in eine <code>Map</code> um. Dabei werden die Kommas als Trennzeichen interpertiert und der
     * Doppelpunkt als Trennzeichen zwischen Key und Value. Alle Listeneinträge werden noch "getrimt". Wenn <code>toLowerCase</code>
     * <code>true</code> ist, werden die einzelnen Listeneinträge noch auf Kleinbuchstaben reduziert.
     * 
     * @param commaSeparatedString
     *            Der kommaseparierte String
     * @param keyToLowerCase
     *            Wenn <code>true</code>, werden alle Keys noch in Kleinbuchstaben umgewandelt.
     * @param valueToLowerCase
     *            Wenn <code>true</code>, werden alle Werte noch in Kleinbuchstaben umgewandelt.
     * @param trim
     *            Wenn <code>true</code>, werden bei allen Werten die vorhandenen Leerzeichen am Anfang und Ende des Strings entfernt. Die
     *            Keys werden in jedem fall getrimmt.
     * @return Map mit den einzelnen Strings
     * @see String#toLowerCase()
     * @see String#trim()
     */
    public static Map<String, String> commaSeparatedStringToMap(final String commaSeparatedString, final boolean keyToLowerCase,
            final boolean valueToLowerCase, final boolean trim) {
        final Map<String, String> resultMap = new HashMap<String, String>();

        final StringTokenizer tokenizer = new StringTokenizer(commaSeparatedString, ",");

        while (tokenizer.hasMoreTokens()) {
            try {
                final String token = tokenizer.nextToken();
                final int posOfSeparator = token.indexOf(':');
                String key = token.substring(0, posOfSeparator);
                String value = token.substring(posOfSeparator + 1);
                key = key.trim();
                key = keyToLowerCase ? key.toLowerCase() : key;
                value = valueToLowerCase ? value.toLowerCase() : value;
                value = trim ? value.trim() : value;
                resultMap.put(key, value);
            } catch (final Exception e) {
                // ignore and continue
            }
        }

        return resultMap;
    }

    /**
     * Konvertiert alle <code>String</code>-Objekte in einer List mit der Funktion <code>String.toLowerCase</code>. Die Reihenfolge und
     * Objekte, welche nichts Strings sind bleiben unver�ndert.
     * 
     * @param originalList
     *            Die zu konvertierende Liste.
     * @return Die konvertierte Liste.
     */
    public static List<Object> stringListToLowerCase(final List<Object> originalList) {
        final List<Object> newList = new ArrayList<Object>();

        for (final Iterator<Object> iter = originalList.iterator(); iter.hasNext();) {
            final Object element = iter.next();
            if (element instanceof String) {
                newList.add(((String) element).toLowerCase());
            } else {
                newList.add(element);
            }
        }

        return newList;
    }

}
