
public class Pagina {
	
	private int id;
	private boolean emMemoria; //true se está em memória, e false se está em disco 
	private boolean alocada; //true se pertence a algum processo, e false caso contrário
	
	public Pagina(int id) {
		this.id = id;
		setEmMemoria(true);
		setAlocada(true);
	}
	
	public int getId() { return id; }
	
	public boolean isEmMemoria() { return emMemoria; }
	
	public boolean isAlocada() { return alocada; }
	
	public void setEmMemoria(boolean emMemoria) { this.emMemoria = emMemoria; }
	
	public void setAlocada(boolean alocada) { this.alocada = alocada; }
	
}
