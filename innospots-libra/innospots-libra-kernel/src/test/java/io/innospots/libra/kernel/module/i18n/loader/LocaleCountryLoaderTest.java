package io.innospots.libra.kernel.module.i18n.loader;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.jupiter.api.Test;

import java.util.*;


/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/1/23
 */

class LocaleCountryLoaderTest {

    @Test
    void testVersion() {
        //List<String> list = Arrays.asList("V0.1_V0.2","V0.1.0_V0.2","V0.1_V0.2.0");
        List<String> list = Arrays.asList("v0.1_V0.2", "V0.1.0_V0.2", "V0.1.9_V0.2.0", "V0.1_V0.2.0", "V0.2.1_V0.2.2");
        for (String version : list) {
            String[] arr = version.split("_");
            System.out.println(version + ":" + (arr[0].compareToIgnoreCase(arr[1])));
        }
    }


    @Test
    void localeList() {
        TreeMap<String, String> tm = LocaleCountryLoader.localeList();
        for (Map.Entry<String, String> entry : tm.entrySet()) {
            System.out.println(entry);
            Locale locale = LocaleUtils.toLocale(entry.getValue());
            System.out.println(locale);
        }
    }

    @Test
    void test() {
        List<String> ss = new ArrayList<>();
        TreeMap<String, String> lang = new TreeMap<>();
        //Locale.setDefault(Locale.US);
        for (Locale locale : Locale.getAvailableLocales()) {
//            ss.add(locale.toString());
            ss.add(locale.getLanguage() + "_" + locale.getCountry() + "," + locale.getDisplayName());
            //System.out.println(locale.toString());
            lang.put(locale.getDisplayName(), locale.getLanguage() + "_" + locale.getCountry());
        }
        Collections.sort(ss);
        for (String s : ss) {
//            System.out.println(s);
        }
        for (Map.Entry<String, String> stringStringEntry : lang.entrySet()) {
            System.out.println(stringStringEntry.toString());
        }
    }

    @Test
    void test2() {
        for (Locale locale : LocaleUtils.availableLocaleList()) {
            System.out.println(locale);
        }
    }
}