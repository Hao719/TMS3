package com.hao.test;

public class Test {
    public static void main(String[] args) {
        Object[] src = { "A", "B", "C" };
        String[] dest = new String[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        System.out.println(dest[0]);
    }
}
