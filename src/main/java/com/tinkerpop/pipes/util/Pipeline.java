package com.tinkerpop.pipes.util;

import com.tinkerpop.pipes.Pipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A Pipeline is a linear composite of Pipes.
 * Pipeline takes a List of Pipes and joins them according to their order as specified by their location in the List.
 * It is important to ensure that the provided ordered Pipes can connect together.
 * That is, that the output of the n-1 Pipe is the same as the input to n Pipe.
 * Once all provided Pipes are composed, a Pipeline can be treated like any other Pipe.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Pipeline<S, E> implements Pipe<S, E>, MetaPipe {

    protected Pipe<S, ?> startPipe;
    protected Pipe<?, E> endPipe;
    protected List<Pipe> pipes;
    protected Iterator<S> starts;

    public Pipeline() {
        this.pipes = new ArrayList<Pipe>();
    }

    /**
     * Constructs a pipeline from the provided pipes. The ordered list determines how the pipes will be chained together.
     * When the pipes are chained together, the start of pipe n is the end of pipe n-1.
     *
     * @param pipes the ordered list of pipes to chain together into a pipeline
     */
    public Pipeline(final List<Pipe> pipes) {
        this.pipes = pipes;
        this.setPipes(pipes);
    }


    /**
     * Constructs a pipeline from the provided pipes. The ordered array determines how the pipes will be chained together.
     * When the pipes are chained together, the start of pipe n is the end of pipe n-1.
     *
     * @param pipes the ordered array of pipes to chain together into a pipeline
     */
    public Pipeline(final Pipe... pipes) {
        this(new ArrayList<Pipe>(Arrays.asList(pipes)));
    }

    /**
     * Useful for constructing the pipeline chain without making use of the constructor.
     *
     * @param pipes the ordered list of pipes to chain together into a pipeline
     */
    protected void setPipes(final List<Pipe> pipes) {
        this.startPipe = (Pipe<S, ?>) pipes.get(0);
        this.endPipe = (Pipe<?, E>) pipes.get(pipes.size() - 1);
        for (int i = 1; i < pipes.size(); i++) {
            pipes.get(i).setStarts((Iterator) pipes.get(i - 1));
        }
    }

    /**
     * Useful for constructing the pipeline chain without making use of the constructor.
     *
     * @param pipes the ordered array of pipes to chain together into a pipeline
     */
    protected void setPipes(final Pipe... pipes) {
        this.setPipes(Arrays.asList(pipes));
    }

    /**
     * Adds a new pipe to the end of the pipeline and then reconstructs the pipeline chain.
     *
     * @param pipe the new pipe to add to the pipeline
     */
    public void addPipe(final Pipe pipe) {
        this.pipes.add(pipe);
        this.setPipes(this.pipes);
    }

    public void setStarts(final Iterator<S> starts) {
        this.starts = starts;
        this.startPipe.setStarts(starts);
    }

    public void setStarts(final Iterable<S> starts) {
        this.setStarts(starts.iterator());
    }

    /**
     * An unsupported operation that throws an UnsupportedOperationException.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if there is another object that can be emitted from the pipeline.
     *
     * @return true if an object can be next()'d out of the pipeline
     */
    public boolean hasNext() {
        return this.endPipe.hasNext();
    }

    /**
     * Get the next object emitted from the pipeline.
     * If no such object exists, then a NoSuchElementException is thrown.
     *
     * @return the next emitted object
     */
    public E next() {
        return this.endPipe.next();
    }

    public List getPath() {
        return this.endPipe.getPath();
    }

    /**
     * Get the number of pipes in the pipeline.
     *
     * @return the pipeline length
     */
    public int size() {
        return this.pipes.size();
    }

    public void reset() {
        this.endPipe.reset();
    }

    /**
     * Simply returns this as as a pipeline (more specifically, pipe) implements Iterator.
     *
     * @return returns the iterator representation of this pipeline
     */
    public Iterator<E> iterator() {
        return this;
    }

    public String toString() {
        return this.pipes.toString();
    }

    public List<Pipe> getPipes() {
        return this.pipes;
    }

    /**
     * Clear the pipes in the pipeline.
     * A way to use the same reference to a newly constructed pipeline.
     * Generally safer to create a new Pipeline object.
     */
    public void clear() {
        this.pipes.clear();
    }

    public boolean equals(final Object object) {
        return (object instanceof Pipeline) && PipeHelper.areEqual(this, (Pipeline) object);
    }
}