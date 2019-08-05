package com.ljw.gataway.core;

import com.ljw.gateway.anno.ProcessReflect;
import com.ljw.gateway.anno.Reflect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: DemoAnnoTests
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/4 15:47
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoAnnoTests {

    @Reflect
    public static void sayHello(final String name) {
        System.out.println("==>> Hi, " + name + " [sayHello]");
    }

    @Reflect(name = "ljw")
    public static void sayHelloToSomeone(final String name) {
        System.out.println("==>> Hi, " + name + " [sayHelloToSomeone]");
    }

    @Test
    public void testAnno() throws Exception {
        final ProcessReflect relectProcessor = new ProcessReflect();
        relectProcessor.parseMethod(DemoAnnoTests.class);
    }
}
