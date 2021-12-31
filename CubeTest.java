package concurrentcube;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CubeTest {

    private static final String EXPECTED1 =
            "0000"
                    + "0000"
                    + "0000"
                    + "0000"

                    + "2222"
                    + "1111"
                    + "1111"
                    + "1111"

                    + "3333"
                    + "2222"
                    + "2222"
                    + "2222"

                    + "4444"
                    + "3333"
                    + "3333"
                    + "3333"

                    + "1111"
                    + "4444"
                    + "4444"
                    + "4444"

                    + "5555"
                    + "5555"
                    + "5555"
                    + "5555";
    private static final String EXPECTED2 =
            "0000"
                    + "0000"
                    + "0000"
                    + "1111"

                    + "1115"
                    + "1115"
                    + "4444"
                    + "1115"

                    + "2222"
                    + "2222"
                    + "1115"
                    + "2222"

                    + "0333"
                    + "0333"
                    + "2222"
                    + "0333"

                    + "4444"
                    + "4444"
                    + "0333"
                    + "4444"

                    + "3333"
                    + "5555"
                    + "5555"
                    + "5555";
    private static final String COMPLETED =
            "0000"
                    + "0000"
                    + "0000"
                    + "0000"

                    + "1111"
                    + "1111"
                    + "1111"
                    + "1111"

                    + "2222"
                    + "2222"
                    + "2222"
                    + "2222"

                    + "3333"
                    + "3333"
                    + "3333"
                    + "3333"

                    + "4444"
                    + "4444"
                    + "4444"
                    + "4444"

                    + "5555"
                    + "5555"
                    + "5555"
                    + "5555";
    static int value = 0;

    private static void error(int test) {
        System.out.println("ERROR in" + test + "\n");
    }

    @Test
    void correctSimpleRotation() {

        var counter = new Object() {
            int value = 0;
        };

        Cube cube = new Cube(4,
                (x, y) -> {
                    ++counter.value;
                },
                (x, y) -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                }
        );

        try {
            cube.rotate(0, 0);
            assertTrue(counter.value == 2);

            String state = cube.show();
            assertTrue(counter.value == 4 && state.equals(EXPECTED1));

        } catch (InterruptedException e) {
            error(1);
        }
    }

    @Test
    void correctExample() {

        var counter = new Object() {
            int value = 0;
        };

        Cube cube = new Cube(4,
                (x, y) -> {
                    ++counter.value;
                },
                (x, y) -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                }
        );

        try {
            cube.rotate(2, 0);
            cube.rotate(5, 1);
            assertTrue(counter.value == 4);

            String state = cube.show();
            assertTrue(counter.value == 6 && state.equals(EXPECTED2));

        } catch (InterruptedException e) {
            error(2);
        }

    }

    @Test
    void correctDoubleShowOperation() {

        var counter = new Object() {
            int value = 0;
        };

        Cube cube = new Cube(4,
                (x, y) -> {
                    ++counter.value;
                },
                (x, y) -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                }
        );

        try {
            String state = cube.show();
            state = cube.show();

            assertTrue(counter.value == 4 && state.equals(COMPLETED));

        } catch (InterruptedException e) {
            error(3);
        }
    }

    @Test
    void permutationsTest() {

        var counter = new Object() {
            int value = 0;
        };

        Cube cube = new Cube(4,
                (x, y) -> {
                    ++counter.value;
                },
                (x, y) -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                }
        );

        try {
            for (int i = 0; i < 6; i++) {
                cube.rotate(3, 0);
                cube.rotate(3, 0);
                cube.rotate(4, 0);
                cube.rotate(4, 0);
                cube.rotate(1, 0);
                cube.rotate(1, 0);
                cube.rotate(2, 0);
                cube.rotate(2, 0);
            }
            String state = cube.show();
            assertTrue(counter.value == 16 * 6 + 2 && state.equals(COMPLETED));

        } catch (InterruptedException e) {
            error(4);
        }

        try {
            for (int i = 0; i < 6; i++) {
                cube.rotate(3, 0);
                cube.rotate(3, 0);
                cube.rotate(0, 0);
                cube.rotate(0, 0);
                cube.rotate(1, 0);
                cube.rotate(1, 0);
                cube.rotate(5, 0);
                cube.rotate(5, 0);
            }
            String state = cube.show();
            assertTrue(counter.value == (16 * 6 + 2) * 2 && state.equals(COMPLETED));

        } catch (InterruptedException e) {
            error(5);
        }

        try {
            for (int i = 0; i < 6; i++) {
                cube.rotate(0, 0);
                cube.rotate(0, 0);
                cube.rotate(2, 0);
                cube.rotate(2, 0);
                cube.rotate(5, 0);
                cube.rotate(5, 0);
                cube.rotate(4, 0);
                cube.rotate(4, 0);
            }
            String state = cube.show();
            assertTrue(counter.value == (16 * 6 + 2) * 3 && state.equals(COMPLETED));

        } catch (InterruptedException e) {
            error(6);
        }

    }

    @Test
    void sameSideOtherLayersRotations() {
        var counter = new Object() {
            int value = 0;
        };

        Cube cube = new Cube(4,
                (x, y) -> {
                    ++counter.value;
                },
                (x, y) -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                },
                () -> {
                    ++counter.value;
                }
        );

        try {

            for (int i = 0; i < 4; i++) {
                cube.rotate(0, 0);
                cube.rotate(0, 1);
                cube.rotate(0, 2);
                cube.rotate(0, 3);
            }
            assertTrue(counter.value == 32);

            String state = cube.show();
            assertTrue(counter.value == 34 && state.equals(COMPLETED));

        } catch (InterruptedException e) {
            error(7);
        }
    }

    @Test
    void concurrentPermutation() {
        value = 0;
        Cube tempCube = new Cube(4,
                (x, y) -> {
                    ++value;
                },
                (x, y) -> {
                    ++value;
                },
                () -> {
                    ++value;
                },
                () -> {
                    ++value;
                }
        );
        concurrentTesting1 temp = new concurrentTesting1(tempCube, 100);

        try {
            Thread thread = new Thread(temp);
            thread.start();
            Thread thread2 = new Thread(temp);
            thread2.start();
            Thread thread3 = new Thread(temp);
            thread3.start();
            Thread thread4 = new Thread(temp);
            thread4.start();
            Thread thread5 = new Thread(temp);
            thread5.start();
            Thread thread6 = new Thread(temp);
            thread6.start();

            try {
                thread.join();
                thread2.join();
                thread3.join();
                thread4.join();
                thread5.join();
                thread6.join();
            } catch (InterruptedException e) {
                error(8);
            }

            assertTrue(value == 6 * 32 * 100 + 6 * 2);

            tempCube.show();

        } catch (InterruptedException e) {
            error(8);
        }
    }

    @Test
    void concurrentOtherLayers() {
        value = 0;
        Cube tempCube = new Cube(4,
                (x, y) -> {
                    ++value;
                },
                (x, y) -> {
                    ++value;
                },
                () -> {
                    ++value;
                },
                () -> {
                    ++value;
                }
        );
        concurrentTesting2 temp = new concurrentTesting2(tempCube, 10000);

        Thread thread = new Thread(temp);
        thread.start();
        Thread thread2 = new Thread(temp);
        thread2.start();
        Thread thread3 = new Thread(temp);
        thread3.start();
        Thread thread4 = new Thread(temp);
        thread4.start();
        Thread thread5 = new Thread(temp);
        thread5.start();
        Thread thread6 = new Thread(temp);
        thread6.start();

        try {
            thread.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
        } catch (InterruptedException e) {
            error(9);
        }

        assertTrue(value == 96 * 6 * 10000 + 2 * 6);

    }

    @Test
    void concurrentMix() {
        value = 0;
        Cube tempCube = new Cube(4,
                (x, y) -> {
                    ++value;
                },
                (x, y) -> {
                    ++value;
                },
                () -> {
                    ++value;
                },
                () -> {
                    ++value;
                }
        );
        concurrentTesting1 temp1 = new concurrentTesting1(tempCube, 10000);
        concurrentTesting2 temp2 = new concurrentTesting2(tempCube, 10000);

        Thread thread = new Thread(temp1);
        thread.start();
        Thread thread2 = new Thread(temp2);
        thread2.start();
        Thread thread3 = new Thread(temp2);
        thread3.start();
        Thread thread4 = new Thread(temp2);
        thread4.start();
        Thread thread5 = new Thread(temp1);
        thread5.start();
        Thread thread6 = new Thread(temp1);
        thread6.start();

        try {
            thread.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
        } catch (InterruptedException e) {
            error(10);
        }

        assertTrue(value == 10000 * 2 * 32 * 3 + 10000 * 2 * 8 * 2 * 6 + 6 * 2);
        try {
            tempCube.show();

        } catch (InterruptedException e) {
            error(10);
        }
    }

    @Test
    void concurrentOneThousandThreadsMixedRotations() {
        value = 0;
        Cube tempCube = new Cube(4,
                (x, y) -> {
                    ++value;
                },
                (x, y) -> {
                    ++value;
                },
                () -> {
                    ++value;
                },
                () -> {
                    ++value;
                }
        );
        concurrentTesting1 temp1 = new concurrentTesting1(tempCube, 100);
        concurrentTesting2 temp2 = new concurrentTesting2(tempCube, 100);

        Thread[] threads = new Thread[1000];

        for(int i = 0; i < 500; i++) {
            threads[i] = new Thread(temp1);
            threads[500 + i] = new Thread((temp2));
        }

        for(int i = 0; i < 1000; i++) {
            threads[i].start();
        }

        try {
            for(int i = 0; i < 1000; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            error(11);
        }

        assertTrue(value == 100 * 2 * 32 * 500 + 100 * 2 * 8 * 2 * 2 * 500 + 1000 * 2);
    }

    private static class concurrentTesting1 implements Runnable {

        private final Cube cube;
        private final int times;

        public concurrentTesting1(Cube cube, int times) {
            this.cube = cube;
            this.times = times;
        }

        @Override
        public void run() {

            try {

                for (int i = 0; i < 4 * this.times; i++) {
                    cube.rotate(0, 0);
                    cube.rotate(0, 1);
                    cube.rotate(0, 2);
                    cube.rotate(0, 3);
                }

                cube.show();

            } catch (InterruptedException e) {
                error(8);
            }
        }
    }

    private static class concurrentTesting2 implements Runnable {

        private final Cube cube;
        private final int times;

        public concurrentTesting2(Cube cube, int times) {
            this.cube = cube;
            this.times = times;
        }

        @Override
        public void run() {

            try {

                for (int i = 0; i < 6 * this.times; i++) {
                    cube.rotate(0, 0);
                    cube.rotate(0, 0);
                    cube.rotate(2, 0);
                    cube.rotate(2, 0);
                    cube.rotate(5, 0);
                    cube.rotate(5, 0);
                    cube.rotate(4, 0);
                    cube.rotate(4, 0);
                }

                cube.show();

            } catch (InterruptedException e) {
                error(8);
            }
        }
    }

}
