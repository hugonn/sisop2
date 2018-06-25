import java.util.ArrayList;

public class Processo {
	
	private String id;
	private int tamanho;
	private ArrayList<Pagina> paginas;
	private boolean fragmentado; //true se o tamanho do processo é múltiplo do tamanho da página
	
	public Processo(String id) {
		this.id = id;
		tamanho = 0;
		paginas = new ArrayList<Pagina>();
		fragmentado = fragmentacao();
	}
	
	/*
	 * Retorna a página correspondente ao endereço informado
	 */
	public Pagina getPagina(int endereco) {
		return paginas.get(endereco/Gerenciador.getTamPagina());
	}
	
	public void addPagina(Pagina pagina) { paginas.add(pagina); }
	
	/*
	 * Retorna o número de endereços que estão alocados para o processo
	 * mas que não estão sendo utilizados
	 */
	public int enderecosFragmentados() {
		return Gerenciador.getTamPagina() - (tamanho % Gerenciador.getTamPagina());
	}
	
	public void aloca(int espaco) {
		tamanho += espaco;
		fragmentado = fragmentacao();
	}
	
	public String getId() { return id; }
	
	public int getTamanho() { return tamanho; }
	
	public ArrayList<Pagina> getPaginas() { return new ArrayList<Pagina>(paginas); }
	
	/*
	 * Retorna true se o tamanho do processo é múltiplo do tamanho da página,
	 * e false caso contrário
	 */
	private boolean fragmentacao() {
		return (tamanho % Gerenciador.getTamPagina()) != 0;
	}
	
	public boolean isFragmentado() { return fragmentado; }

}
