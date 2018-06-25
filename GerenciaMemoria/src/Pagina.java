
public class Pagina {
	
	private int posicao; //A posição 0 representa os enderços de 0 até tamPagina - 1;
	//a posição 1, os endereços de tamPagina até 2*tamPagina - 1, e assim por diante
	//(tamPagina é definido na classe Gerenciado)
	private boolean emMemoria; //true se está em memória, e false se está em disco 
	
	public Pagina(int posicao) {
		setPosicao(posicao);
		setEmMemoria(true);
	}
	
	public void setPosicao(int posicao) { this.posicao = posicao; }
	
	public int getPosicao() { return posicao; }
	
	public boolean isEmMemoria() { return emMemoria; }
	
	public void setEmMemoria(boolean emMemoria) { this.emMemoria = emMemoria; }
	
}
