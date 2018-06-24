import java.util.Random;

public class MyThread implements Runnable {
	
	private String nome;
	private int s;
	private int[] probabilidade;
	private Random r;
	private int cont;
	
	public MyThread(String nome, int[] probabilidade) {
		this.nome = nome;
		this.probabilidade = probabilidade.clone();
		r = new Random();
		cont = 0;
	}
	
	public String getNome() {
		return nome;
	}

	@Override
	public void run() {
		s = r.nextInt(100);
		System.out.println(nome + " – s: " + s);
		int x;
		do {
			x = probabilidade[r.nextInt(probabilidade.length)];
			System.out.println(nome + " – Número aleatório: " + x);
			switch(x) {
			case 0:
				if(cont < 90) {
					probabilidade[cont] = 2;
					cont++;
				}
				if(s > 1)
					x = r.nextInt(s-1) + 1;
				else
					x = r.nextInt(1) + 1;
				System.out.println(nome + " – Novo número aleatório: " + x);
				
			case 1:
				int div = s/5;
				if(div > 2)
					x = r.nextInt(div - 2) + 2;
				else
					x = 2;
				System.out.println(nome + " – Novo número aleatório: " + x);
			
			}
		} while(x != 2);
		System.out.println(nome + " – FIM");
	}
}
