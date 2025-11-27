import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFileTest {
  public static void main(String[] args) {
    try {
      var buffer = new BufferedReader(new FileReader("test"));
      var builder = new StringBuilder();

      String line;
      while ((line = buffer.readLine()) != null) {
        builder.append(line).append("\n");
      }
      buffer.close();
      System.out.println(builder.toString());
    } catch (IOException e) {

    }
  }
}