import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ManipulaArquivo {
	/*Classe respons�vel por manipular arquivos*/	
	
	private static int buscarProducao(String variavel, ArrayList<Producao> gramatica){
		/*Retorna o �ndice da primeira ocorr�ncia de uma produ��o se encontrada 
		  ou -1 caso contr�rio.*/
		int indice = 0;
		for (Producao producao : gramatica){
			if(producao.getVariavel().equals(variavel)){
				return indice;
			}
			indice++;
		}
		return -1;
	}
	
	public static ArrayList<Producao> arquivoLeitura(String caminho){
		/*M�todo respons�vel pela abertura e leitura do arquivo de entrada*/
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(caminho));
			String linha;
			ArrayList<Producao> gramatica = new ArrayList<Producao>();
			String variavel;
			List<String> regras;
			int indiceProducao;
			do {
				linha = buffRead.readLine();
				if (linha != null) { 
					//L� a vari�vel e as regras de uma linha do arquivo
					linha = linha.trim();
					variavel = String.valueOf(linha.charAt(0));
					regras = Arrays.asList(linha.split(" -> ")[1].split(" \\| "));
					indiceProducao = buscarProducao(variavel, gramatica); 
					if(indiceProducao == -1){
						//Caso a produ��o n�o exista, cria-se uma nova e adiciona � gram�tica
						gramatica.add(new Producao(variavel, new ArrayList<String>(regras)));
					}
					else{
						//Caso a produ��o j� exista, apenas adiciona novas regras a ela.
						Producao producao = gramatica.get(indiceProducao);
						for(String regra : regras){
							producao.adicionarRegra(regra);
						}
					} 
				} 
			} while (linha != null);
			
			buffRead.close();
			return gramatica;

		} catch (IOException e) {
			System.out.println("Arquivo ou diret�rio n�o encontrado.");
			return null;
		}
	}
	
	public static void arquivoEscrita(String caminho){
		/*M�todo respons�vel pela abertura e escrita do arquivo de sa�da*/
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(caminho));
			String linha;
			do {
				linha = buffRead.readLine();
				if (linha != null) { 
					System.out.println(linha); 
				} 
			} while (linha != null);
			
			buffRead.close();

		} catch (Exception e) {
			System.out.println("Arquivo ou diret�rio n�o encontrado.");
		}
	}

}