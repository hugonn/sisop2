import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Gerenciador {
	
	private static int tamPagina; //Número de endereços que cada página possui 
	private int numPaginasMemoria;
	private int numPaginasDisco;
	private Queue<Pagina> paginasMemoria; //Fila de páginas na ordem de menos recentemente usada (LRU)
	private ArrayList<Pagina> paginasDisco;
	private ArrayList<Processo> processos;
	private Algoritmo algoritmo; //LRU ou aleatório
	private Random seletor; //Objeto que seleciona uma página aleatoriamente, para o caso do algoritmo aleatório
	private boolean[] posicoes; //Representa a ordem das páginas na memória física
	//Se a posição 0 é true, então a primeira página da memória está ocupada, e assim por diante
	
	public Gerenciador(int tamPagina, int tamMemoriaFisica, int tamDisco, String algoritmo) {
		Gerenciador.tamPagina = tamPagina;
		this.numPaginasMemoria = tamMemoriaFisica/tamPagina;
		paginasMemoria = new LinkedList<Pagina>();
		paginasDisco = new ArrayList<Pagina>(numPaginasDisco = tamDisco/tamPagina);
		if(algoritmo.equalsIgnoreCase("lru"))
			this.algoritmo = Algoritmo.LRU;
		else {
			this.algoritmo = Algoritmo.ALEATORIO;
			seletor = new Random();
		}
		posicoes = new boolean[numPaginasMemoria];
	}
	
	public Resultado acessa(String id, int endereco) {
		Processo processo = getProcesso(id);
		if(endereco >= processo.getTamanho()) //Endereço fora da área de acesso
			return Resultado.SEGMENTATION_FAULT;
		Pagina pagina = processo.getPagina(endereco);
		if(pagina.isEmMemoria()) { //Se página está em memória, acessa endereço e atualiza fila,
			paginasMemoria.remove(pagina); //realocando a página para o fim da fila
			paginasMemoria.add(pagina);
			return Resultado.SUCESSO;
		}
		paginasDisco.remove(pagina); //Se a página está em disco, retira a vítima da memória,
		paginasDisco.add(vitima()); //coloca a vítima em disco, e carrega a página para a memória,
		paginasMemoria.add(pagina); //efetuando o acesso
		return Resultado.PAGE_FAULT;
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
	
	/*
	 * Seleciona e remove a vítima da fila de páginas, com base ou no LRU, ou no algoritmo
	 * aleatório, dependendo do que tiver sido determinado inicialmente
	 */
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
		posicoes[vitima.getPosicao()] = false;
		return vitima;
	}
	
	private Resultado alocacao(Processo processo, int espaco) {
		int paginasNecessarias = paginasNecessarias(espaco);
		int paginasRestantes = paginasRestantes();
		if(paginasNecessarias > paginasRestantes) { //Se não há páginas suficientes em memória
			for(int i = 0; i < paginasRestantes; i++) { //Aloca todas as páginas disponíveis em memória
				Pagina pagina = criaPagina();
				processo.addPagina(pagina);
				paginasMemoria.add(pagina);
			}
			paginasNecessarias -= paginasRestantes;
			while(paginasDisco.size() < numPaginasDisco) { //Seleciona vítima e manda para o disco
				paginasDisco.add(vitima()); //até encher o disco
				Pagina pagina = criaPagina();
				processo.addPagina(pagina);
				paginasMemoria.add(pagina);
				paginasNecessarias--;
				if(paginasNecessarias == 0) { //ou até alocar o número necessário de páginas
					processo.aloca(espaco); //Atualiza o espaço alocado e
					return Resultado.PAGE_FAULT; //avisa page fault
				}
			} //Se não alocou todas as páginas necessária, então faltou memória
			processo.aloca(espaco - paginasNecessarias * tamPagina); //Aloca o máximo que foi possível
			return Resultado.NO_MEMORY; //e avisa que faltou memória
		}
		for(int i = 0; i < paginasNecessarias; i++) { //Se há páginas suficientes em memória,
			Pagina pagina = criaPagina(); //apenas as aloca e avisa que a operação foi bem-sucedida
			processo.addPagina(pagina);
			paginasMemoria.add(pagina);
		}
		processo.aloca(espaco);
		return Resultado.SUCESSO;
	}
	
	public Resultado criaProcesso(String id, int tamanho) {
		Processo processo = new Processo(id, tamanho);
		return alocacao(processo, tamanho);
	}
	
	/*
	 * Retorna o processo com o ID informado
	 */
	private Processo getProcesso(String id) {
		for(Processo processo: processos)
			if(processo.getId() == id) {
				return processo;
			}
		return null;
	}
	
	public Resultado aloca(String id, int espaco) {
		Processo processo = getProcesso(id);
		if(processo.isFragmentado()) { //Se o número de endereços alocados pelo processo não é múltiplo do 
			int enderecosFragmentados = processo.enderecosFragmentados(); //tamanho da página, então aloca
			if(enderecosFragmentados >= espaco) { //memória até que seja, ou até alocar todo o espaço desejado
				processo.aloca(espaco);
				return Resultado.SUCESSO;
			}
			processo.aloca(enderecosFragmentados);
			return alocacao(processo, espaco - enderecosFragmentados);
		}
		return alocacao(processo, espaco);
	}
	
	/*
	 * Retira o processo da lista de processos, bem como suas respectivas páginas
	 */
	public void terminaProcesso(String id) {
		Processo processo = null;
		for(int i = 0; i < processos.size(); i++)
			if(processos.get(i).getId() == id) {
				processo = processos.remove(i);
				break;
			}
		for(Pagina pagina: processo.getPaginas()) {
			if(paginasMemoria.remove(pagina))
				posicoes[pagina.getPosicao()] = false;
			paginasDisco.remove(pagina);
		}
	}
	
	/*
	 * Cria nova página na primeira posição de memória física que estiver livre
	 */
	private Pagina criaPagina() {
		int posicao;
		for(posicao = 0; posicao < posicoes.length; posicao++)
			if(posicoes[posicao] == false)
				break;
		Pagina pagina = new Pagina(posicao);
		return pagina;
	}
	
	public static int getTamPagina() { return tamPagina; }
	
}
