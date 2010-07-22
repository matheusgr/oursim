import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.util.GWAFormat;
import br.edu.ufcg.lsd.oursim.util.GWAJobDescription;

public class GWASplit {

	public static void main(String[] args) throws ParseException, IOException {

		args = "resources/trace_filtrado1.txt 01/01/2006 01/01/2006 resources/nordugrid_ano_2006.txt".split("\\s+");

		int i = 0;
		String workloadFilePath = args[i++];
		String begin = args[i++];
		String end = args[i++];
		String outputFilePath = args[i++];

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Date beginDate = formatter.parse(begin);
		Date endDate = formatter.parse(end);
		
		long beginDateTimeMillis = beginDate.getTime() / 1000;
		long endDateTimeMillis = endDate.getTime() / 1000;

		assert beginDateTimeMillis < endDateTimeMillis;
		
		performSplit(workloadFilePath, outputFilePath, beginDateTimeMillis, endDateTimeMillis);

	}

	private static void performSplit(String workloadFilePath, String outputFilePath, long beginDateTimeMillis, long endDateTimeMillis)
			throws FileNotFoundException, IOException {
		Scanner sc = new Scanner(new File(workloadFilePath));

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		String line = null;

		while (sc.hasNextLine()) {

			if (!(line = sc.nextLine()).trim().startsWith("#")) {
				GWAJobDescription gwaJob = GWAFormat.createGWAJobDescription(line);
				if (isBetween(gwaJob.SubmitTime, beginDateTimeMillis, endDateTimeMillis)) {
					writer.append(line).append("\n");
				}
			}
		}

		writer.close();
	}

	private static boolean isBetween(long value, long begin, long end) {
		return value >= begin && value < end;
	}

}
