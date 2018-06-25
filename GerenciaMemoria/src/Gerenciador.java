import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Gerenciador {
	
	private static int tamPagina;
	private int numPaginasMemoria;
	private int numPaginasDisco;
	private Queue<Pagina> paginasMemoria; //Fila de páginas na ordem de menos recentemente usada (LRU)
	private ArrayList<Pagina> paginasDisco;
	private ArrayList<Processo> processos;
	private int contador; //Contador de páginas 
	private Algoritmo algoritmo;
	private Random seletor; //Objeto que seleciona uma página aleatoriamente, para o caso do algoritmo aleatório
	
	public Gerenciador(int tamPagina, int tamMemoriaFisica, int tamDisco, String algoritmo) {
		Gerenciador.tamPagina = tamPagina;
		this.numPaginasMemoria = tamMemoriaFisica/tamPagina;
		paginasMemoria = new LinkedList<Pagina>();
		paginasDisco = new ArrayList<Pagina>(numPaginasDisco = tamDisco/tamPagina);
		contador = 0;
		if(algoritmo.equalsIgnoreCase("lru"))
			this.algoritmo = Algoritmo.LRU;
		else {
			this.algoritmo = Algoritmo.ALEATORIO;
			seletor = new Random();
		}
	}
	
	/*
	 * Retorna o número de páginas necessárias para alocar n endereços de memória
	 */
	public int paginasNecessarias(int enderecos) {
		return (int) Math.ceil((double) enderecos/tamPagina);
	}
	
	/*
	 * Retorna o número restante de páginas em memória
	 */
	public int paginasRestantes() { return numPaginasMemoria - paginasMemoria.size(); }
	
	private Pagina vitima() {
		Pagina vitima = null;
		if(algoritmo == Algoritmo.LRU)
			vitima = paginasMemoria.poll();
		else {
			int escolha = seletor.nextInt(paginasMemoria.size());
			int cont = 0;
			for(Pagina pagina: paginasMemoria) {
				if(cont == escolha) {
					vitima = pagina;
					paginasMemoria.remove(pagina);
					break;
				}
				cont++;
			}
		}
		vitima.setEmMemoria(false);
		return vitima;
	}
	
	private boolean alocacao(Processo processo, int espaco) {
		int paginasNecessarias = paginasNecessarias(espaco);
		int paginasRestantes = paginasRestantes();
		if(paginasNecessarias > paginasRestantes) {
			for(int i = 0; i < paginasRestantes; i++) {
				Pagina pagina = criaPagina();
				processo.addPagina(pagina);
				paginasMemoria.add(pagina);
			}
			paginasNecessarias -= paginasRestantes;
			while(paginasDisco.size() < numPaginasDisco) {
				paginasDisco.add(vitima());
				paginasNecessarias--;
			}
			if(paginasNecessarias > 0)
				return false;
		}
		for(int i = 0; i < paginasNecessarias; i++) {
			Pagina pagina = criaPagina();
			processo.addPagina(pagina);
			paginasMemoria.add(pagina);
		}
		return true;
	}
	
	public boolean criaProcesso(String id, int tamanho) {
		Processo processo = new Processo(id, tamanho);
		return alocacao(processo, tamanho);
	}
	
	public boolean aloca(String id, int espaco) {
		Processo processo = null;
		for(Processo p: processos)
			if(p.getId() == id) {
				processo = p;
				break;
			}
		if(processo.isFragmentado()) {
			int enderecosFragmentados = processo.enderecosFragmentados();
			if(enderecosFragmentados >= espaco) {
				processo.aloca(espaco);
				return true;
			}
			processo.aloca(enderecosFragmentados);
			return alocacao(processo, espaco - enderecosFragmentados);
		}
		return alocacao(processo, espaco);
	}
	
	public void terminaProcesso(String id) {
		Processo processo = null;
		for(int i = 0; i < processos.size(); i++)
			if(processos.get(i).getId() == id) {
				processo = processos.remove(i);
				break;
			}
		processo.liberaPaginas();
	}
	
	private Pagina criaPagina() {
		Pagina pagina = new Pagina(contador);
		contador++;
		return pagina;
	}
	
	public static int getTamPagina() { return tamPagina; }
	
}
