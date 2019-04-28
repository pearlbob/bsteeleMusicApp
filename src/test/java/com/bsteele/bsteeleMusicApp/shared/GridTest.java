package com.bsteele.bsteeleMusicApp.shared;

import org.junit.Test;

import static org.junit.Assert.*;

public class GridTest {

    @Test
    public void set() {
        Grid<Integer> grid = new Grid<>();

        assertEquals("Grid{[]}", grid.toString());
        grid.clear();
        assertEquals("Grid{[]}", grid.toString());


        grid.set(0,0, 1);
        assertEquals("Grid{[[1]]}", grid.toString());
        grid.set(0,0, 1);
        assertEquals("Grid{[[1]]}", grid.toString());
        grid.set(0,1, 2);
        assertEquals("Grid{[[1], [2]]}", grid.toString());
        grid.set(0,3, 4);
        assertEquals("Grid{[[1], [2], [], [4]]}", grid.toString());
        grid.set(2,3, 4);
        assertEquals("Grid{[[1], [2], [], [4, null, 4]]}", grid.toString());
        grid.set(-2,3, 444);
        assertEquals("Grid{[[1], [2], [], [444, null, 4]]}", grid.toString());
        grid.set(-2,-3, 555);
        assertEquals("Grid{[[555], [2], [], [444, null, 4]]}", grid.toString());

        grid.clear();
        assertEquals("Grid{[]}", grid.toString());
        grid.set(4,0, 1);
        assertEquals("Grid{[[null, null, null, null, 1]]}", grid.toString());
        grid.clear();
        assertEquals("Grid{[]}", grid.toString());
        grid.set(0,4, 1);
        assertEquals("Grid{[[], [], [], [], [1]]}", grid.toString());

        grid.clear();
        assertEquals("Grid{[]}", grid.toString());
    }

    @Test
    public void get() {
        Grid<Integer> grid = new Grid<>();

        assertEquals("Grid{[]}", grid.toString());
        assertNull(grid.get(0, 0));
        assertNull(grid.get(1000, 0));
        assertNull(grid.get(1000, 2345678));
        assertNull(grid.get(-1, -12));

        grid.set(0,0, 1);
        grid.set(0,1, 5);
        grid.set(0,3, 9);
        grid.set(3,3, 12);
        assertEquals("Grid{[[1], [5], [], [9, null, null, 12]]}", grid.toString());
        assertEquals((Integer) 1,grid.get(0,0));
        assertNull(grid.get(3,0));
        assertEquals((Integer) 5,grid.get(0,1));
        assertNull(grid.get(1,1));

        assertEquals((Integer) 9,grid.get(0,3));
        assertNull(grid.get(1,3));
        assertNull(grid.get(2,3));
        assertEquals((Integer) 12,grid.get(3,3));
        assertNull(grid.get(4,3));
    }
}