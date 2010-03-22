package br.edu.ufcg.lsd.gridsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.lsd.gridsim.output.DefaultOutput;

public class Peer {

    protected int resources;
    protected int avaliableResources;
    protected String clusterId;
    protected HashSet<Job> runningJobs;
    protected HashSet<Job> runningLocalJobs;
    protected HashMap<Peer, Integer> balances;
    protected HashMap<Peer, Integer> consumingPeers;
    protected HashMap<String, Peer> peersMap;

    public Peer(int size, String clusterId) {
        this.clusterId = clusterId;
        this.resources = size;
        this.avaliableResources = size;
        this.runningJobs = new HashSet<Job>();
        this.runningLocalJobs = new HashSet<Job>();
        this.balances = new HashMap<Peer, Integer>();
        this.consumingPeers = new HashMap<Peer, Integer>();
    }

    public void setPeersMap(HashMap<String, Peer> peersMap) {
        this.peersMap = peersMap;
    }

    public int getBalance(Peer consumingPeer) {
        int balance = 0;
        if (Configuration.getInstance().useNoF()) {
            if (this.balances.containsKey(consumingPeer)) {
                balance = this.balances.get(consumingPeer);
            }
        }
        return balance;
    }

    public void finishOportunisticJob(Job job, boolean preempted) {

        if (this == peersMap.get(getJobOrigSite(job))) {
            boolean removed = this.runningLocalJobs.remove(job);
            assert removed;
        } else {
            boolean removed = this.runningJobs.remove(job);
            assert removed;
        }

        this.avaliableResources++;

        Peer peer = peersMap.get(getJobOrigSite(job));
        int value = this.consumingPeers.get(peer) - 1;
        if (value == 0) {
            this.consumingPeers.remove(peer);
        } else {
            this.consumingPeers.put(peer, value);
        }
        
        if (peer == this) {
        	return; // Don't compute own balance 
        }

        if (Configuration.getInstance().useOGAsCluster() && !getJobOrigSite(job).equals("FAKE")) {
            setBalance(peer, -job.getRunTime());
            peer.setBalance(this, job.getRunTime());
        }

        if (Configuration.getInstance().checkpointEnabled() || !preempted) {
            setBalance(peer, -job.getRunTime());
            peer.setBalance(this, job.getRunTime());
        }

    }

    private String getJobOrigSite(Job job) {
        if (Configuration.getInstance().useOGAsCluster()) {
            return "FAKE";
        } else {
            return job.getOrigSite();
        }
    }

    public boolean addOportunisticJob(Job job, final Peer consumer, HashSet<Peer> newConsumers, int time) {

        // There is available resources.
        if (avaliableResources > 0) {
            startJob(job);
            return true;
        }

        // Consumer is local and peer is full of local jobs
        if (consumer == this && runningLocalJobs.size() == this.resources) {
            return false;
        }

        // This job may need preemption
        HashMap<Peer, Integer> allowedResources = calculateAllowedResources(consumer, time);

        int usedResources = consumingPeers.containsKey(consumer) ? consumingPeers.get(consumer) : 0;

        if (consumer == this || allowedResources.get(consumer) > usedResources) {
            preemptOneJob(allowedResources, time);
            startJob(job);
            return true;
        }
        return false;
    }

    protected HashMap<Peer, Integer> calculateAllowedResources(Peer newConsumer, int currentTime) {
        Set<Peer> consumers = consumingPeers.keySet();

        // Only use resources that are not local
        int resourcesToShare = this.getRemoteShareSize(currentTime);

        // If its local, remove one resource from remote
        if (newConsumer == this) {
            resourcesToShare -= 1;
        }

        int totalBalance = 0;

        HashSet<Peer> consumersSet = new HashSet<Peer>(consumers);
        consumersSet.add(newConsumer);
        consumersSet.remove(this);

        HashMap<Peer, Integer> allowedResources = new HashMap<Peer, Integer>();

        for (Peer p : consumersSet) {
            totalBalance += getBalance(p);
        }

        ArrayList<Peer> consumersList = new ArrayList<Peer>(consumersSet);

        int resourcesLeft = resourcesToShare;

        // Set minimum resources allowed for each peer
        for (Peer p : consumersList) {
            int resourcesForPeer = 0;
            double share;
            if (totalBalance == 0) {
                share = (1.0d / consumersList.size());
            } else {
                share = ((double) getBalance(p)) / totalBalance;
            }
            resourcesForPeer = (int) Math.floor(share * resourcesToShare);
            allowedResources.put(p, resourcesForPeer);
            resourcesLeft -= resourcesForPeer;
        }

        if (resourcesLeft == 0) {
            return allowedResources;
        }

        final Peer actual = this;
        Collections.shuffle(consumersList);
        Collections.sort(consumersList, new Comparator<Peer>() {

            @Override
            public int compare(Peer o1, Peer o2) {
                // Best balance first
                int balanceDiff = actual.getBalance(o1) - actual.getBalance(o2);
                if (balanceDiff != 0) {
                    return balanceDiff > 0 ? -3 : 3;
                }
                int p1Consumer = consumingPeers.containsKey(o1) ? consumingPeers.get(o1) : 0;
                int p2Consumer = consumingPeers.containsKey(o2) ? consumingPeers.get(o2) : 0;
                int usingDiff = p1Consumer - p2Consumer;
                // Consuming size second
                if (usingDiff != 0) {
                    return usingDiff > 0 ? -2 : 2;
                }

                assert (p2Consumer != 0 && p1Consumer != 0);

                // Recently jobs last
                List<Job> jobs = new ArrayList<Job>();
                for (Job j : runningJobs) {
                    if (peersMap.get(getJobOrigSite(j)) == o1) {
                        jobs.add(j);
                    }
                    if (peersMap.get(getJobOrigSite(j)) == o2) {
                        jobs.add(j);
                    }
                }

                Collections.sort(jobs, new Comparator<Job>() {

                    @Override
                    public int compare(Job o1, Job o2) {
                        return o1.getStartTime() - o2.getStartTime();
                    }
                });
                return peersMap.get(getJobOrigSite(jobs.get(0))) == o1 ? -1 : 1;
            }
        });

        // distribute resources left using reluctant strategy
        while (resourcesLeft > 0) {
            for (Peer p : consumersList) {
                allowedResources.put(p, allowedResources.get(p) + 1);
                resourcesLeft--;
                if (resourcesLeft == 0) {
                    break;
                }
            }
        }

        return allowedResources;
    }

    protected void preemptOneJob(HashMap<Peer, Integer> allowedResources, int time) {
        Peer choosen = null;

        LinkedList<Peer> peerList = new LinkedList<Peer>(consumingPeers.keySet());
        Collections.shuffle(peerList);

        for (Peer p : peerList) {
            if (p == this) {
                continue;
            }
            int usedResources = consumingPeers.get(p);
            if (usedResources > allowedResources.get(p)) {
                choosen = p;
                break;
            }
        }

        assert choosen != null;

        List<Job> jobs = new LinkedList<Job>();
        for (Job j : runningJobs) {
            if (peersMap.get(getJobOrigSite(j)) == choosen) {
                jobs.add(j);
            }
        }

        // get recently start job first
        Collections.sort(jobs, new Comparator<Job>() {

            @Override
            public int compare(Job o1, Job o2) {
                return o2.getStartTime() - o1.getStartTime();
            }
        });
//        if (jobs.size() == 0) { // DEBUG!
//            System.out.println(resources);
//            System.out.println(jobs);
//            System.out.println(allowedResources);
//            System.out.println(peersMap);
//            System.out.println(avaliableResources);
//            System.out.println(this);
//            System.out.println(getRemoteShareSize(time));
//        }
        Job j = jobs.get(0);
        j.preemptJob(time);
        finishOportunisticJob(j, true);
        j.setPeer(null);
        rescheduleJob(j);
    }

    private void startJob(Job job) {
        avaliableResources--;
        if (peersMap.get(getJobOrigSite(job)) == this) {
            runningLocalJobs.add(job);
        } else {
            runningJobs.add(job);
        }
        int consumedResources = 0;
        Peer peer = peersMap.get(getJobOrigSite(job));
        if (this.consumingPeers.containsKey(peer)) {
            consumedResources = this.consumingPeers.get(peer);
        }
        this.consumingPeers.put(peer, consumedResources + 1);
    }

    protected void setBalance(Peer peer, int time) {
        int curBalance = 0;
        if (this.balances.containsKey(peer)) {
            curBalance = this.balances.get(peer);
        }
        int finalBalance = curBalance + time;
        if (finalBalance < 0) {
            this.balances.remove(peer);
        } else {
            this.balances.put(peer, curBalance + time);
        }
    }

    public String toString() {
        return clusterId;
    }

    public double getUtilization() {
        return ((double) (resources - avaliableResources)) / resources;
    }

    protected int getRemoteShareSize(int currentTime) {
        return this.resources - this.runningLocalJobs.size();
    }

    private void rescheduleJob(Job j) {
        GlobalScheduler.getInstance().schedule(j);
    }

    public String getId() {
        return clusterId;
    }
}
