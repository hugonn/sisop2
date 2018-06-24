
public class Gerador {
	
	private Thread[] threads;
	private int[] probabilidade;
	
	public Gerador(int n) {
		
		threads = new Thread[n];
		
		probabilidade = new int[100];
		int i = 90;
		for(; i < 95; i++)
			probabilidade[i] = 1;
		for(; i < 100; i++)
			probabilidade[i] = 2;
		
	}
	
	public void executar() {
		for(int i = 0; i < threads.length; i++)
			threads[i].start();
	}
	
	public void gerar() {
		for(int i = 0; i < threads.length; i++) {
			String nome = "Thread_" + (i+1);
			threads[i] = new Thread(new MyThread(nome, probabilidade));
		}
	}
	
}
