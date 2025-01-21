import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException {
    String path = "./examples/teste01.txt";
    System.out.println(leitor(path));
  }
  
  public static String leitor(String path) throws IOException {
		BufferedReader buffRead = new BufferedReader(new FileReader(path));
		String linha = "";
		while (true) {
			if (linha != null) {
				System.out.println(linha);

			} else
				break;
			linha = buffRead.readLine();
		}
		buffRead.close();
        return linha;
	}
}
