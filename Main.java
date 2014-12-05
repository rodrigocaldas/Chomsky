import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String args[]){
		Scanner entrada = new Scanner(System.in);
		String caminho;
		ArrayList<Producao> gramatica;
		do{
			System.out.println("Entre com o caminho do arquivo:");
			caminho = entrada.nextLine();
			gramatica = ManipulaArquivo.arquivoLeitura(caminho);
		}while(gramatica == null);
		entrada.close();
				
		Chomsky chomsky = new Chomsky(gramatica);
		chomsky.inicialNaoRecursivo();
		chomsky.eliminarRegrasLambda();
		chomsky.eliminarRegrasDaCadeia();
		chomsky.eliminarSimbolosInuteis();
		chomsky.formaNormalChomsky();
		
		int qtdRegras;
		for (Producao producao : chomsky.getGramatica()){
			System.out.print("Produ��o [" + producao.getVariavel() + "]: ");
			qtdRegras = producao.getRegras().size();
			for (String regra : producao.getRegras().subList(0, qtdRegras - 1)){
				System.out.print(regra + " | ");
			}
			System.out.println(producao.getRegras().get(qtdRegras - 1));
		}
	}
}