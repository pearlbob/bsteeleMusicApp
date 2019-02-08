package com.bsteele.bsteeleMusicApp.client.util;

import com.bsteele.bsteeleMusicApp.shared.util.UndoStack;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */
public class UndoStackTest
        extends TestCase
{

    @Test
    public void testPushUndoRedo()
    {
        int max = 4;

        for (int undoCount = 1; undoCount <= max; undoCount++) {
            UndoStack<String> undoStack = new UndoStack<>(max);

            int index = 0;
            for (int i = 0; i < undoCount; i++) {
                String s = Integer.toString(++index);
                undoStack.push(s);
                assertEquals(s, undoStack.top());
                if (i > 0)
                    assertTrue(undoStack.canUndo());
            }
            for (int i = undoCount - 1; i > 0; i--) {
                String s = Integer.toString(--index);
                String u = undoStack.undo();
                assertEquals(s, u);
                assertEquals(s, undoStack.top());
            }
            {
                String u = undoStack.undo();
                assertEquals(null, u);
            }

            for (int i = 0; i < undoCount - 1; i++) {
                String s = Integer.toString(++index);
                String r = undoStack.redo();
                assertEquals(s, r);
                assertEquals(s, undoStack.top());
                if (i < undoCount - 2)
                    assertTrue(undoStack.canRedo());
            }

            for (int i = undoCount - 1; i > 0; i--) {
                String s = Integer.toString(--index);
                String u = undoStack.undo();
                assertEquals(s, u);
                assertEquals(s, undoStack.top());
            }
            {
                String u = undoStack.undo();
                assertEquals(null, u);
            }

        }
        {
            //  test push after undo
            UndoStack<String> undoStack = new UndoStack<>();

            //  push 8
            int index = 0;
            for (int i = 0; i < 8; i++) {
                String s = Integer.toString(++index);
                undoStack.push(s);
                assertEquals(s, undoStack.top());
                if (i > 0)
                    assertTrue(undoStack.canUndo());
            }
            //  undo 4
            for (int i = 0; i < 4; i++) {
                undoStack.undo();
            }
            //  push 4 different (9-12)
            for (int i = 0; i < 4; i++) {
                String s = Integer.toString(++index);
                undoStack.push(s);
                assertEquals(s, undoStack.top());
                if (i > 0)
                    assertTrue(undoStack.canUndo());
            }
            //  see 12 - 9
            for (int i = 0; i < 4-1; i++) {
                String s = Integer.toString(index - 1 - i);
                assertTrue(undoStack.canUndo());
                String u = undoStack.undo();
                assertEquals(s, u);
            }
            //  see 4 - 1
            for (int i = 4; i >0; i--) {
                String s = Integer.toString(i);
                assertTrue(undoStack.canUndo());
                String u = undoStack.undo();
                assertEquals(s, u);
            }
        }
    }
}