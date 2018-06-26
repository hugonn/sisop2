import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		Gerenciador gerenciador = leitura("teste.txt");
		gerenciador.executa();
	}
	
	/*
	 * Método que faz a leitura do arquivo de entrada e preenche
	 * as estruturas do objeto com os parâmetros informados
	 */
	private static Gerenciador leitura(String arquivo) {
		Gerenciador gerenciador = null;
		Path path = Paths.get(arquivo);
		try (BufferedReader br = Files.newBufferedReader(path, Charset.defaultCharset())) {
			String modo = br.readLine();
			String algoritmo = br.readLine();
			int tamPagina = Integer.parseInt(br.readLine());
			int tamMemoria = Integer.parseInt(br.readLine());
			int tamDisco = Integer.parseInt(br.readLine());
			gerenciador = new Gerenciador(tamPagina, tamMemoria, tamDisco, algoritmo);
			if(modo.equals("sequencial")) {
				gerenciador.inicializaComandos();
				String comando = null;
				while((comando = br.readLine()) != null)
					Gerenciador.addComando(comando);
			}
		}
		catch (IOException x) {
			System.err.format("Erro de E/S: %s%n", x);
		}
		return gerenciador;
	}

}
