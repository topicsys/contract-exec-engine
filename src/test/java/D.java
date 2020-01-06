/*
 * Copyright (C) 2020-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 11/01/2020
 */
public class D {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> System.out.println("a"));
        Thread t2 = new Thread(() -> {
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("b");
        });
        Thread t3 = new Thread(() -> {
            t2.start();
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("c");
        });
        t3.start();

////////////////////////////////////////////////////////////////

        Object o4 = new Object();
        Object o5 = new Object();

        Thread t4 = new Thread(() -> {
            System.out.println("4");
            done4 = true;
            synchronized (o4) {
                o4.notifyAll();
            }
        });
        Thread t5 = new Thread(() -> {
            while (!done4) {
                Thread.yield();
                /*synchronized (o4) {
                    if (!done4)
                        try {
                            o4.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }*/
            }
            System.out.println("5");
            done5 = true;
            synchronized (o5) {
                o5.notifyAll();
            }
        });
        Thread t6 = new Thread(() -> {
            while (!done5) {
                Thread.yield();
                /*synchronized (o5) {
                    if (!done5)
                        try {
                            o5.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }*/
            }
            System.out.println("6");
        });
        t6.start();
        t5.start();
        t4.start();
    }

    static volatile boolean done4, done5 = false;
}
