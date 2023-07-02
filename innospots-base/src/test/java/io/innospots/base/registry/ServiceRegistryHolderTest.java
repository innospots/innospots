package io.innospots.base.registry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author Smars
 * @version 1.0.0
 * @date 2022/4/9
 */
class ServiceRegistryHolderTest {

    @Test
    void currentShardingKeys() {

        for (int i = 1; i <= 16; i++) {
            ServiceRegistryHolder.setAvailableServicesSize(i);
            System.out.println("--------" + i + "--------");
            for (int j = 0; j < i; j++) {
                ServiceRegistryHolder.setPosition(j);
                Integer[] sks = ServiceRegistryHolder.currentShardingKeys();
                System.out.println(i + "-" + j + ":" + Arrays.toString(sks));
            }
        }

    }
}