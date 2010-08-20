package br.edu.ufcg.lsd.binpacking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.jfree.data.category.CategoryDataset;

import samples.tutorials.scheduling.pack.binpacking.CPpack;
import choco.visu.components.chart.ChocoDatasetFactory;

public class BinPacking {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(new File(args[0]));
		BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
		int taskId = 0;

		Collection<Integer> onlyOneTask = new ArrayList<Integer>();

		while (sc.hasNextLine()) {
			String jobRecord = sc.nextLine();
			Scanner scLine = new Scanner(jobRecord);
			scLine.useDelimiter(":");
			scLine.next();
			long finishTime = scLine.nextLong();
			long jobId = scLine.nextLong();
			long submissionTime = scLine.nextLong();
			long startTime = scLine.nextLong();
			long duration = scLine.nextLong();
			long runTimeDuration = scLine.nextLong();
			int makeSpan = scLine.nextInt();
			String taskStructure = scLine.next();
			long queuingTime = scLine.nextLong();
			long numberOfPreemptions = scLine.nextLong();

			int[] tasks = parseTasks(taskStructure);

			onlyOneTask.add(tasks[0]);

			Collection<Integer> rebuiltJob = tasks.length > 1 ? rebuildJob(tasks, makeSpan) : onlyOneTask;

			for (Integer taskLength : rebuiltJob) {
				// 1125553223 468282 1 67 U200
				// /C=RU/O=DataGrid/CN=host/nordugrid.spbu.ru
				writer.append(asRecord(jobId, taskId, submissionTime, taskLength, "userId", "siteId")).append("\n");
				taskId++;
			}
			onlyOneTask.clear();
		}
		sc.close();
		writer.close();
	}

	static String asRecord(long jobId, long taskId, long submitTime, long runTime, String userId, String siteId) {
		StringBuilder sb = new StringBuilder();
		sb.append(submitTime).append("\t");
		sb.append(jobId).append("\t");
		sb.append(taskId).append("\t");
		sb.append(runTime).append("\t");
		sb.append(userId).append("\t");
		sb.append(siteId);
		return sb.toString();
	}

	static Collection<Integer> rebuildJob(int[] tasks, int makeSpan) {

		CPpack cppack = new CPpack();
		cppack.setUp(new Object[] { tasks, makeSpan });
		cppack.setTimelimit(1000);
		int result = cppack.cpPack();

		CategoryDataset packDataset = ChocoDatasetFactory.createPackDataset(cppack.solver, cppack.getModeler());

		Map<String, List<Integer>> allocations = new TreeMap<String, List<Integer>>();
		Map<String, Integer> groupedAllocations = new TreeMap<String, Integer>();

		for (int machine = 0; machine < packDataset.getColumnCount(); machine++) {
			String columnKey = (String) packDataset.getColumnKey(machine);
			allocations.put(columnKey, new ArrayList<Integer>());
			for (int task = 0; task < packDataset.getRowCount(); task++) {
				Number value = packDataset.getValue(task, machine);
				if (value != null) {
					allocations.get(columnKey).add(value.intValue());
				}
			}
		}
		for (String machine : allocations.keySet()) {
			groupedAllocations.put(machine, 0);
			for (Integer taskLength : allocations.get(machine)) {
				groupedAllocations.put(machine, groupedAllocations.get(machine) + taskLength);
			}
		}

		return groupedAllocations.values();

	}

	private static int[] parseTasks(String taskStructure) {
		String onlyTheTasks = taskStructure.substring(1, taskStructure.length() - 1);
		Scanner scTasks = new Scanner(onlyTheTasks);
		scTasks.useDelimiter(",");
		List<Integer> taskDuration = new ArrayList<Integer>();
		while (scTasks.hasNext()) {
			taskDuration.add(scTasks.nextInt());
		}

		int[] tasks = new int[taskDuration.size()];
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = taskDuration.get(i);
		}
		return tasks;
	}

}
