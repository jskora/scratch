package jfskora;

import com.sun.management.UnixOperatingSystemMXBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class JvmStatus {

    private static final int NUM_FILES = 100;

    public static void main( String[] args ) {
        File[] files = new File[NUM_FILES];
        FileOutputStream[] fout = new FileOutputStream[NUM_FILES];

        dumpOperatingSystemInfo();

        System.out.println("Number of open fd - start      : " + getOpenFileCount());
        for (int i = 0; i < NUM_FILES; i++) {
            files[i] = new File("/tmp/fout" + i);
        }
        System.out.println("Number of open fd - before open: " + getOpenFileCount());
        try {
            for (int x = 0; x < NUM_FILES; x++) {
                fout[x] = new FileOutputStream(files[x]);
            }
            System.out.println("Number of open fd - after open : " + getOpenFileCount());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            for (int x = 0; x < NUM_FILES; x++) {
                fout[x].close();
            }
            System.out.println("Number of open fd - after close: " + getOpenFileCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number of open fd - done       : " + getOpenFileCount());
    }

    private static long getOpenFileCount() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof UnixOperatingSystemMXBean) {
            return ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount();
        } else {
            return 0;
        }
    }

    private static void dumpOperatingSystemInfo() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        dumpMxBeanInfo(OperatingSystemMXBean.class, os);
        if (os instanceof UnixOperatingSystemMXBean) {
            dumpMxBeanInfo(UnixOperatingSystemMXBean.class, os);
        }

        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        dumpMxBeanInfo(RuntimeMXBean.class, rt);
    }

    private static void dumpMxBeanInfo(Class klass, PlatformManagedObject bean) {
        System.out.println(klass.getCanonicalName());
        System.out.println("----------------------------------------");
        HashMap<String, Method> methods = new HashMap<>();
        for (Method method : klass.getMethods()) {
            if (method.getName().startsWith("get")) {
                methods.put(method.getName(), method);
            }
        }
        String[] keys = methods.keySet().toArray(new String[methods.size()]);
        Arrays.sort(keys);
        for (String method : keys) {
            try {
                System.out.println(klass.getSimpleName() + "." + methods.get(method).getName() + " = " + methods.get(method).invoke(bean));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("");
    }

}
