package io.innospots.libra.kernel.module.extension.registry;

import io.innospots.libra.base.extension.LibraClassPathExtPropertiesLoader;
import io.innospots.libra.base.extension.LibraExtensionProperties;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Smars
 * @date 2022/1/17
 */
class LibraExtensionRegistryStarterTest {

    @Test
    void run() {
        List<LibraExtensionProperties> appProperties = LibraClassPathExtPropertiesLoader.loadFromClassPath();
        for (LibraExtensionProperties appProperty : appProperties) {
            System.out.println(appProperty);

        }
    }

    @Test
    void resource() throws IOException {
        /*
        URL url  = LibraApplicationRegistryStarter.class.getClassLoader().getResource("META-INF/innospots-extension-meta.json");
//        URL url  = LibraApplicationRegistryStarter.class.getResource("/META-INF/innospots-extension-meta.json");
        System.out.println(url);
        Assert.notNull(url,"not null");
        UrlResource resource = new UrlResource(url);
        System.out.println(resource.exists());

        Enumeration<URL> urls = LibraApplicationRegistryStarter.class.getClassLoader().getResources("META-INF/innospots-extension-meta.json");

        while (urls.hasMoreElements()){
            System.out.println(urls.nextElement());
        }

         */
    }
}