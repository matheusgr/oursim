import java.io.* ;
import java.util.*;

public class MyIo {
  private BufferedReader fin;
  private StringTokenizer s;
  private String b;
  private boolean moreData;
  private String delimiter;

  public MyIo(String fname, String delim) 
    throws FileNotFoundException, IOException {
      delimiter = delim;
    fin = new BufferedReader(new FileReader(fname));
    moreData = (b = fin.readLine()) != null;
    if (moreData)
      s = new StringTokenizer(b,delimiter);
  }

  public MyIo(String fname) 
    throws FileNotFoundException, IOException {
      delimiter = " ";
    fin = new BufferedReader(new FileReader(fname));
    moreData = (b = fin.readLine()) != null;
    if (moreData)
      s = new StringTokenizer(b,delimiter);
  }

  public boolean hasMoreData() throws IOException {
    if (moreData && s.hasMoreTokens())
      return true;
    if (moreData && (b = fin.readLine()) != null){
	s = new StringTokenizer(b,delimiter);
	return hasMoreData();}
    else {
      moreData = false;
      return false;}
  }

  public String getNextString() throws IOException {
    if (hasMoreData())
      return s.nextToken();
    else throw new MyIoException("No more Data while reading string");
  }

  public int getNextInt() throws IOException {
    if (hasMoreData())
      return Integer.parseInt(s.nextToken());
    else throw new MyIoException("No more Data while reading int");
  }

  public void close() throws IOException {
    fin.close(); }
}
  
