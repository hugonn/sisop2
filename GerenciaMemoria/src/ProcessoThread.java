import java.util.Random;

public class ProcessoThread implements Runnable {
	
	private String id;
	private Random seletor; //Seletor aleatório de comandos
	private Comando[] probabilidade; //Array que representa a probabilidade de cada comando
	private final int chanceAlocar = 5; //Probabilidade (%) de alocar mais memória
	private final int chanceTerminar = 5; //Probabilidade (%) de terminar o processo
	
	public ProcessoThread(String id) {
		this.id = id;
		seletor = new Random();
		probabilidade = new Comando[100];
		int limite = chanceAlocar;
		int contador = 0;
		for(; contador < limite; contador++)
			probabilidade[contador] = Comando.ALOCAR;
		limite += chanceTerminar;
		for(; contador < limite; contador++)
			probabilidade[contador] = Comando.TERMINAR;
		limite = probabilidade.length;
		for(; contador < limite; contador++)
			probabilidade[contador] = Comando.ACESSAR;
	}

	/*
	 * Método que escolhe aleatoriamente o comando a ser executado
	 * bem como o endereço a ser acessado ou o tanto de memória a ser alocado
	 */
	@Override
	public void run() {
		Comando escolha;
		do {
			escolha = probabilidade[seletor.nextInt(probabilidade.length)];
			switch(escolha) {
			case ACESSAR:
				Gerenciador.addComando("A " + id + " " + (seletor.nextInt(Gerenciador.enderecoLimite) + 1));
				break;
			case ALOCAR:
				Gerenciador.addComando("M " + id + " " + (seletor.nextInt(Gerenciador.enderecoLimite) + 1));
				break;
			case TERMINAR:
				Gerenciador.addComando("T " + id);
				Gerenciador.terminaThread();
			}
		} while(escolha != Comando.TERMINAR);
	}

}
