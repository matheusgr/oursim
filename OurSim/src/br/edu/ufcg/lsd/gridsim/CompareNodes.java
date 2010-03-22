package br.edu.ufcg.lsd.gridsim;

import java.util.Comparator;

public class CompareNodes implements Comparator<Job> {

    public int compare(Job o1, Job o2) {
        int diffStartTime = o2.getStartTime() - o1.getStartTime();
        if (diffStartTime == 0) {
        	return -o1.compareTo(o2);
        } else if (diffStartTime > 0) {
            return 4;
        } else {
            return -4;
        }
    }
}
