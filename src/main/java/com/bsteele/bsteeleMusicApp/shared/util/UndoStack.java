package com.bsteele.bsteeleMusicApp.shared.util;

import java.util.ArrayList;

/**
 * CopyRight 2018 bsteele.com
 * User: bob
 */

/**
 * An undo stack utility that assumes the contents are immutable.
 *
 * @param <T> stack class
 */
public class UndoStack<T>
{
    public UndoStack()
    {
        this(defaultSize);
    }

    public UndoStack(int max)
    {
        if (max <= 0)
            max = defaultSize;
        this.max = max;
    }

    public final void push(T value)
    {
        //  remove the dead edits in the redo top
        if (canRedo())
            while (!undoStack.isEmpty() && undoStack.size() > undoStackPointer+1)
                undoStack.remove(undoStack.size() - 1);

        //  cut the maxed out undo's off the stack
        if (undoStackPointer >= max - 1)
            undoStack.remove(0);


        //  store the value in the undo stack

        undoStack.add(value);

        undoStackPointer = undoStack.size()-1;
        undoStackCount = undoStackPointer;
    }

    public final boolean canUndo()
    {
        return undoStackPointer > 0;
    }

    public final T undo()
    {
        if (!canUndo())
            return null;
        undoStackPointer--;
        return top();
    }

    public final boolean canRedo()
    {
        return undoStackPointer < undoStackCount;
    }

    public final T redo()
    {
        if (!canRedo())
            return undoStack.get(undoStackCount);
        undoStackPointer++;
        return top();
    }

    public final T top()
    {
        return undoStack.get(undoStackPointer);
    }

    private final int max;
    private static final int defaultSize = 100;
    private ArrayList<T> undoStack = new ArrayList<T>();
    private int undoStackPointer = 0;
    private int undoStackCount = 0;
}
