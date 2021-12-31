package concurrentcube;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class Cube {

    private final int size;
    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;
    private AtomicInteger[][][] cube;
    private int rotating;
    private int showing;
    private Semaphore defence;
    private Semaphore first;
    private Semaphore show;
    private boolean[] frontCross;
    private boolean[] sideCross;
    private boolean[] upperCross;
    private int waiting;
    private int who;
    private int howMuch;
    private int running;
    private int finishing;

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        this.size = size;
        this.cube = prepareCube();
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;
        this.rotating = 0;
        this.showing = 0;
        this.defence = new Semaphore(1);
        this.first = new Semaphore(1);
        this.show = new Semaphore(1);
        this.upperCross = prepareTempArray(size);
        this.frontCross = prepareTempArray(size);
        this.sideCross = prepareTempArray(size);
        this.waiting = 0;
        this.who = 0;
        this.howMuch = 0;
        this.running = 0;
        this.finishing = 0;
    }

    private AtomicInteger[][][] prepareCube() {
        AtomicInteger[][][] cube = new AtomicInteger[6][this.size][this.size];
        for (int i = 0; i < 6; i++) {
            for (int x = 0; x < this.size; x++) {
                for (int y = 0; y < this.size; y++) {
                    cube[i][x][y] = new AtomicInteger(i);
                }
            }
        }
        return cube;
    }

    private void matrixClockwiseRotate(int side) {
        for (int i = 0; i < this.size - 1 / 2; i++) {
            for (int j = i; j < this.size - i - 1; j++) {

                AtomicInteger temp = this.cube[side][i][j];
                this.cube[side][i][j] = this.cube[side][this.size - 1 - j][i];
                this.cube[side][this.size - 1 - j][i] = this.cube[side][this.size - 1 - i][this.size - 1 - j];
                this.cube[side][this.size - 1 - i][this.size - 1 - j] = this.cube[side][j][this.size - 1 - i];
                this.cube[side][j][this.size - 1 - i] = temp;
            }
        }
    }

    private void matrixUnclockwiseRotate(int side) {
        for (int x = 0; x < this.size / 2; x++) {
            for (int y = x; y < this.size - x - 1; y++) {

                AtomicInteger temp = this.cube[side][x][y];
                this.cube[side][x][y] = this.cube[side][y][this.size - 1 - x];
                this.cube[side][y][this.size - 1 - x] = this.cube[side][this.size - 1 - x][this.size - 1 - y];
                this.cube[side][this.size - 1 - x][this.size - 1 - y] = this.cube[side][this.size - 1 - y][x];
                this.cube[side][this.size - 1 - y][x] = temp;
            }
        }
    }

    private boolean[] prepareTempArray(int size) {
        boolean[] tempArray = new boolean[size];
        for (int i = 0; i < size; i++) {
            tempArray[i] = false;
        }
        return tempArray;
    }

    private void rotateTop(int layer) {
        //1, 2, 3, 4 scianka y=0 x->0 gut
        //1->2->3->4
        for (int i = this.size - 1; i >= 0; i--) {
            AtomicInteger temp = this.cube[1][i][layer];
            this.cube[1][i][layer] = this.cube[2][i][layer];
            this.cube[2][i][layer] = this.cube[3][i][layer];
            this.cube[3][i][layer] = this.cube[4][i][layer];
            this.cube[4][i][layer] = temp;
        }
    }

    private void rotateBottom(int layer) {
        //1, 2, 3, 4 scianka y=size-1 x->size-1 gut
        for (int i = 0; i < this.size; i++) {
            AtomicInteger temp = this.cube[1][i][this.size - 1 - layer];
            this.cube[1][i][this.size - 1 - layer] = this.cube[4][i][this.size - 1 - layer];
            this.cube[4][i][this.size - 1 - layer] = this.cube[3][i][this.size - 1 - layer];
            this.cube[3][i][this.size - 1 - layer] = this.cube[2][i][this.size - 1 - layer];
            this.cube[2][i][this.size - 1 - layer] = temp;
        }
    }

    private void rotateLeft(int layer) {
        //0, 2, 5 scianka y->size-1 x=0
        //4 scianka x=size-1 y -> size-1  0->2->5->4
        for (int i = 0; i < this.size; i++) {
            AtomicInteger temp = this.cube[0][layer][i];
            this.cube[0][layer][i] = this.cube[4][this.size - 1 - layer][this.size - 1 - i];
            this.cube[4][this.size - 1 - layer][this.size - 1 - i] = this.cube[5][layer][i];
            this.cube[5][layer][i] = this.cube[2][layer][i];
            this.cube[2][layer][i] = temp;
        }
    }

    private void rotateRight(int layer) {
        //0, 2, 5 scianka y->0 x=size-1
        //4 scianka x=0 y -> 0  0->2->5->4
        for (int i = this.size - 1; i >= 0; i--) {
            AtomicInteger temp = this.cube[0][this.size - 1 - layer][i];
            this.cube[0][this.size - 1 - layer][i] = this.cube[2][this.size - 1 - layer][i];
            this.cube[2][this.size - 1 - layer][i] = this.cube[5][this.size - 1 - layer][i];
            this.cube[5][this.size - 1 - layer][i] = this.cube[4][layer][this.size - 1 - i];
            this.cube[4][layer][this.size - 1 - i] = temp;
        }
    }

    private void rotateBack(int layer) {
        //0 y=0 x->0
        //1 x=0 y->size-1
        //3 x=size-1 y->0
        //5 y=size-1 x->size-1
        //0->1->5->3
        for (int i = this.size - 1; i >= 0; i--) {
            AtomicInteger temp = this.cube[0][this.size - 1 - i][layer];
            this.cube[0][this.size - 1 - i][layer] = this.cube[3][this.size - 1 - layer][this.size - 1 - i];
            this.cube[3][this.size - 1 - layer][this.size - 1 - i] = this.cube[5][i][this.size - 1 - layer];
            this.cube[5][i][this.size - 1 - layer] = this.cube[1][layer][i];
            this.cube[1][layer][i] = temp;
        }
    }

    private void rotateFront(int layer) {
        //0 y=size-1 x->size-1
        //1 x=size-1 y->0 gut
        //3 x=0 y->size-1
        //5 y=0 x->0
        //0->3->5->1
        for (int i = 0; i < this.size; i++) {
            AtomicInteger temp = this.cube[0][i][this.size - 1 - layer];
            this.cube[0][i][this.size - 1 - layer] = this.cube[1][this.size - 1 - layer][this.size - 1 - i];
            this.cube[1][this.size - 1 - layer][this.size - 1 - i] = this.cube[5][this.size - 1 - i][layer];
            this.cube[5][this.size - 1 - i][layer] = this.cube[3][layer][i];
            this.cube[3][layer][i] = temp;
        }
    }

    int oppositeSide(int side) {
        switch (side) {
            case 0:
                return 5;
            case 5:
                return 0;
            case 1:
                return 3;
            case 3:
                return 1;
            case 2:
                return 4;
            default:
                return 2;
        }
    }

    private boolean isMyCrossFree(int side, int layer) {
        switch (side) {
            case 1:
                if (this.sideCross[layer]) {
                    return false;
                }
                break;
            case 3:
                if (this.sideCross[this.size - 1 - layer]) {
                    return false;
                }
                break;
            case 0:
                if (this.frontCross[layer]) {
                    return false;
                }
                break;
            case 5:
                if (this.frontCross[this.size - 1 - layer]) {
                    return false;
                }
                break;
            case 2:
                if (this.upperCross[layer]) {
                    return false;
                }
                break;
            default:
                if (this.upperCross[this.size - 1 - layer]) {
                    return false;
                }
                break;
        }
        ;
        return true;
    }

    private void makeMyCross(int side, int layer, boolean statement) {
        switch (side) {
            case 1:
                this.sideCross[layer] = statement;
                break;
            case 3:
                this.sideCross[this.size - 1 - layer] = statement;
                break;
            case 0:
                this.frontCross[layer] = statement;
                break;
            case 5:
                this.frontCross[this.size - 1 - layer] = statement;
                break;
            case 2:
                this.upperCross[layer] = statement;
                break;
            default:
                this.upperCross[this.size - 1 - layer] = statement;
                break;
        }
    }

    private int returnMuCross(int side) {
        int number;
        switch (side) {
            case 3:
                number = 4;
                break;
            case 1:
                number = 4;
                break;
            case 4:
                number = 2;
                break;
            case 2:
                number = 2;
                break;
            default:
                number = 3;
                break;
        }
        return number;
    }

    private void rotateExact(int side, int layer) {
        if (layer == 0) {
            matrixClockwiseRotate(side);
        } else if (layer == this.size - 1) {
            matrixUnclockwiseRotate(oppositeSide(side));
        }
        switch (side) {
            case 0:
                rotateTop(layer);
                break;
            case 1:
                rotateLeft(layer);
                break;
            case 2:
                rotateFront(layer);
                break;
            case 3:
                rotateRight(layer);
                break;
            case 4:
                rotateBack(layer);
                break;
            case 5:
                rotateBottom(layer);
                break;
        }
    }


    public void rotate(int side, int layer) throws InterruptedException {

        int myCross = returnMuCross(side);

        this.defence.acquire();
        if (this.who == 0) {
            this.rotating++;
            this.defence.release();
        } else if (this.waiting == 0 && this.who == myCross && isMyCrossFree(side, layer)) {
            this.rotating++;
            this.defence.release();
        } else {
            this.waiting++;
            this.defence.release();
            this.first.acquire();
            this.defence.acquire();
            this.waiting--;
            this.defence.release();
        }

        this.defence.acquire();
        this.who = myCross;
        this.rotating++;
        makeMyCross(side, layer, true);
        this.beforeRotation.accept(side, layer);
        this.defence.release();

        rotateExact(side, layer);

        this.defence.acquire();
        this.afterRotation.accept(side, layer);
        this.rotating--;
        makeMyCross(side, layer, false);
        if (this.showing == 0) {
            this.who = 0;
            if (this.waiting > 0) {
                this.first.release();
            }
        }
        this.defence.release();

    }

    private String collectCubeToPrint() {
        String cubeState = "";
        for (int i = 0; i < 6; i++) {
            for (int y = 0; y < this.size; y++) {
                for (int x = 0; x < this.size; x++) {
                    cubeState += (this.cube[i][x][y]).toString();
                }
            }
        }
        return cubeState;
    }

    public String show() throws InterruptedException {

        this.defence.acquire();
        if (this.who == 0 || (this.who == 1 && this.waiting == 0)) {
            this.defence.release();
        } else {
            this.waiting++;
            this.defence.release();
            this.first.acquire();
            this.defence.acquire();
            this.waiting--;
            this.defence.release();
        }

        this.defence.acquire();
        this.who = 1;
        this.showing++;
        this.beforeShowing.run();
        this.defence.release();

        String cubeState = collectCubeToPrint();

        this.defence.acquire();
        this.afterShowing.run();
        this.showing--;
        if (this.showing == 0) {
            this.who = 0;
            if (this.waiting > 0) {
                this.first.release();
            }
        }
        this.defence.release();

        return cubeState;
    }

}