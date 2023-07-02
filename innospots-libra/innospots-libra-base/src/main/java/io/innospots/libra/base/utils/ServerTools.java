/*
 *  Copyright © 2021-2023 Innospots (http://www.innospots.com)
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.innospots.libra.base.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Server Info
 *
 * @author chenc
 * @date 2022/4/16
 */
public class ServerTools {

    private static final Logger logger = LoggerFactory.getLogger(ServerTools.class);

    private static final SystemInfo systemInfo;

    private static final HardwareAbstractionLayer hardware;

    private static final OperatingSystem os;

    static {
        systemInfo = new SystemInfo();
        hardware = systemInfo.getHardware();
        os = systemInfo.getOperatingSystem();
    }

    public static HardwareAbstractionLayer getHardware() {
        return hardware;
    }

    public static OperatingSystem getOs() {
        return os;
    }

    public static NetworkParams getNetworkParams() {
        return os.getNetworkParams();
    }

    public static OperatingSystem.OSVersionInfo getOsVersion() {
        return os.getVersionInfo();
    }

    public static ComputerSystem getComputer() {
        return hardware.getComputerSystem();
    }

    public static CentralProcessor getCpu() {
        return hardware.getProcessor();
    }

    public static GlobalMemory getMemory() {
        return hardware.getMemory();
    }

    public static List<HWDiskStore> getDisk() {
        return hardware.getDiskStores();
    }

    public static String getMemoryTotal() {
        return FormatUtil.formatBytes(getMemory().getTotal());
    }

    public static String getMemoryUse() {
        GlobalMemory memory = getMemory();
        return new DecimalFormat("#.##%").format((memory.getTotal() - memory.getAvailable()) * 1.0 / memory.getTotal());
    }

    public static String getCpuUse() throws InterruptedException {
        CentralProcessor processor = hardware.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long system = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = nice + irq + softirq + steal + system + user + iowait + idle;
        return new DecimalFormat("#.##%").format(1.0 - idle * 1.0 / totalCpu);
    }

    public static String getTotalDisk() {
        File file = new File("/");
        long total = 0;
        if (file.exists()) {
            total = file.getTotalSpace();
        }
        return FormatUtil.formatBytes(total);
    }

    public static String getAvailableDisk() {
        File file = new File("/");
        long free = 0;
        if (file.exists()) {
            free = file.getFreeSpace();
        }
        return FormatUtil.formatBytes(free);
    }
}
