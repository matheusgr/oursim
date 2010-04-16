package br.edu.ufcg.lsd.gridsim.input;

import br.edu.ufcg.lsd.gridsim.Job;

public interface Workload {

    public abstract Job peek();

    public abstract Job poll();

    public abstract void close();

}