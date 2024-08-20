package io.seqera.debug;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeTest {
    public static void main(String[] args) throws Exception {

        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        long memoryAddress = unsafe.allocateMemory(1024);
        System.out.println("Memory allocated at: " + memoryAddress);

        unsafe.freeMemory(memoryAddress);
        System.out.println("Memory freed.");
    }
}
