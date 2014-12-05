import java.util.ArrayList;



import javax.swing.text.StyledEditorKit.BoldAction;

public class Chomsky {
	/*Classe respons�vel por manipular arquivos*/
	private ArrayList<Producao> gramatica;
	
	public Chomsky(ArrayList<Producao> gramatica){
		this.gramatica = (ArrayList<Producao>) gramatica.clone();
	}
	
	public void inicialNaoRecursivo(){
		/*M�todo respons�vel por retirar a recurs�o do s�mbolo inicial*/
		Producao inicial = this.gramatica.get(0);
		boolean recursivo = false;
		for (String regra : inicial.getRegras()){
			if (regra.indexOf('S') != -1){
				recursivo = true;
				break;
			}
		}
		if(recursivo){
			//Caso o s�mbolo inicial seja recursivo, um novo s�mbolo inicial S' 
			//� adicionado � gram�tica.
			ArrayList<String> regras = new ArrayList<String>();
			regras.add("S");
			inicial = new Producao("�", regras);
			gramatica.add(0,inicial);
		}
	}
	
	private ArrayList<String> buscarVariaveisAnulaveis(){
		/*M�todo que retorna uma lista de vari�veis de produ��es que possuem 
		  regra lambda (vari�veis anul�veis)*/
		int lambda;
		ArrayList<String> variaveisAnulaveis = new ArrayList<String>();
		for (Producao producao : gramatica.subList(1, gramatica.size())){
			lambda = producao.getRegras().indexOf(".");
			if (lambda != -1){
				variaveisAnulaveis.add(producao.getVariavel());
			}
		}
		return variaveisAnulaveis;
	}
	
	private boolean regraAnulavel(String regra, ArrayList<String> variaveisAnulaveis){
		/*M�todo que verifica se uma regra � anul�vel, dada uma lista de vari�veis 
		  anul�veis*/
		for(char variavel : regra.toCharArray()){
			if(variaveisAnulaveis.indexOf(String.valueOf(variavel)) == -1){
				return false;
			}
		}
		return true;
	}
	

	private void removerVariaveisAnulaveis(ArrayList<String> variaveisAnulaveis){
		/*M�todo respons�vel por remover as vari�veis anul�veis das regras da gram�tica*/
		for(String variavel : variaveisAnulaveis){
			for(Producao producao : gramatica){
				int indiceRegra = 0, qtdRegras = producao.getRegras().size();
				while(indiceRegra < qtdRegras){
					//Percorre as regras de cada produ��o para cada vari�vel anul�vel
					String regra = producao.getRegras().get(indiceRegra);
					int indiceAnulavel = -1;
					do{
						indiceAnulavel = regra.indexOf(variavel,indiceAnulavel+1);
						if (indiceAnulavel != -1){
							//Se houver vari�vel anul�vel em uma regra, gera uma nova regra
							//sem ela
							StringBuilder novaRegra = new StringBuilder(regra);
							novaRegra.deleteCharAt(indiceAnulavel);
							if(novaRegra.length() > 0){
								//Se a regra for diferente de lambda adiciona na produ��o
								producao.adicionarRegra(novaRegra.toString());
							}
							else if( producao.getVariavel().equals(gramatica.get(0).getVariavel()) ){
								//Se for lambda e for o s�mbolo inicial adiciona na produ��o
								producao.adicionarRegra(".");
							}
						}
					}while(indiceAnulavel != -1);
					indiceRegra++;
					qtdRegras = producao.getRegras().size();
				}
			}
		}
	}
	
	private void removerLambda(){
		/*M�todo respons�vel por remover todos os lambdas que n�o estejam no s�mbolo inicial*/
		for (Producao producao : gramatica.subList(1, gramatica.size())){
			producao.getRegras().remove(".");
		}
	}
	
	public void eliminarRegrasLambda(){
		/*M�todo respons�vel por eliminar as regras lambda*/
		//Busca as vari�veis anul�veis iniciais
		ArrayList<String> variaveisAnulaveis = buscarVariaveisAnulaveis();
		ArrayList<String> variaveisAnteriores;
		String novaVariavel = "";
		boolean flag;
		do {
			variaveisAnteriores = (ArrayList<String>) variaveisAnulaveis.clone();
			for (String variavel : variaveisAnteriores){
				//Para cada vari�vel anul�vel fa�a:
				flag = false;
				for(Producao producao : gramatica){
					//Para cada produ��o da gram�tica busque por regras anul�veis
					for(String regra : producao.getRegras()){
						if(regraAnulavel(regra, variaveisAnulaveis)){
							flag = true;
							novaVariavel = producao.getVariavel();
							break;
						}
					}
					if (flag){
						break;
					}
				}
				if(flag && (variaveisAnulaveis.indexOf(novaVariavel) == -1)){
					//Se uma produ��o possue uma regra anul�vel, ent�o esta vari�vel
					//� anul�vel
					variaveisAnulaveis.add(novaVariavel);
				}
			}
		} while (!variaveisAnulaveis.equals(variaveisAnteriores));
		removerVariaveisAnulaveis(variaveisAnulaveis);
		removerLambda();
	}
	
	private ArrayList<ArrayList<String>> encontrarRegrasDaCadeia(){
		/*M�todo respons�vel por encontrar as regras da cadeia em uma gram�tica*/
		ArrayList<ArrayList<String>> chains = new ArrayList<ArrayList<String>>();
		for(Producao producao : gramatica){
			ArrayList<String> chain = new ArrayList<String>();
			chain.add(producao.getVariavel());
			ArrayList<String> prev = new ArrayList<String>();
			ArrayList<String> novo = new ArrayList<String>();
			do {
				novo = (ArrayList<String>) chain.clone();
				novo.removeAll(prev);
				prev = (ArrayList<String>) chain.clone();
				for(String variavel : novo){
					ArrayList<String> regras =  new ArrayList<String>();
					int indiceProducao = 0;
					while((indiceProducao < gramatica.size()) && regras.isEmpty()) {
						//Busca pelas regras da vari�vel atual
						if(variavel.equals(gramatica.get(indiceProducao).getVariavel())){
							regras = gramatica.get(indiceProducao).getRegras();
						}
						indiceProducao++;
					}
					for(String regra : regras){
						//Procura por regras da cadeia em uma produ��o
						if((regra.length() == 1)
							&&(Character.isUpperCase(regra.charAt(0)))
							&&(chain.indexOf(regra) == -1)){
							chain.add(regra);
						}
					}
				}
			} while (!chain.equals(prev));
			chains.add(chain);
		}
		return chains;
	}
	
	public void eliminarRegrasDaCadeia(){
		/*M�todo respons�vel por eliminar as regras da cadeia de uma gram�tica*/
		ArrayList<ArrayList<String>> chains = encontrarRegrasDaCadeia();
		int contChain = 0;
		for(ArrayList<String> chain : chains){
			for(String variavel : chain.subList(1, chain.size())){
				//Para cada chain percorre suas vari�veis excluindo a primeira vari�vel
				ArrayList<String> regras =  new ArrayList<String>();
				int indiceProducao = 0;
				while((indiceProducao < gramatica.size()) && regras.isEmpty()) {
					//Busca pelas regras da vari�vel atual
					if(variavel.equals(gramatica.get(indiceProducao).getVariavel())){
						regras = gramatica.get(indiceProducao).getRegras();
					}
					indiceProducao++;
				}
				//remove a cadeia da vari�vel atual e a corrige
				gramatica.get(contChain).getRegras().remove(variavel);
				for(String regra : regras){
					gramatica.get(contChain).adicionarRegra(regra);
				}
			}
			contChain++;
		}
	}
	
	private boolean verificarRegraTerminal(String regra, ArrayList<String> variaveisTerminais){
		/*M�todo respons�vel por verificar se uma regra � terminal*/
		for(char elemento : regra.toCharArray()){
			if( !Character.isLowerCase(elemento) 
				&& (elemento != '.')
				&& !variaveisTerminais.contains(String.valueOf(elemento)) ){
				return false;
			}
		}
		return true;
	}
	
	private ArrayList<String> encontrarTerm(){
		/*Percorre todas as produ��es da gram�tica procurando por s�mbolos terminais*/
		ArrayList<String> term = new ArrayList<String>();
		for(Producao producao : gramatica){
			int indiceRegra = 0;
			boolean temTerminal;
			do {
				//Procura por simbolos terminais em uma produ��o
				temTerminal = verificarRegraTerminal(producao.getRegras().get(indiceRegra), new ArrayList<String>());
				indiceRegra++;
			} while ((indiceRegra < producao.getRegras().size()) && !temTerminal);
			if(temTerminal && !term.contains(producao.getVariavel())){
				//Caso uma produ��o contenha terminais, � adicionada ao term
				term.add(producao.getVariavel());
			}
		}
		
		ArrayList<String> prev;
		do {
			prev = (ArrayList<String>) term.clone();
			for(Producao producao : gramatica){
				for(String regra : producao.getRegras()){
					//Procura por simbolos terminais em uma produ��o
					if(verificarRegraTerminal(regra, prev) 
						&& (term.indexOf(producao.getVariavel()) == -1)){
						term.add(producao.getVariavel());
					}
				}
			}
		} while (!prev.equals(term));
		return term;
	}
	
	private ArrayList<String> encontrarReach(){
		/*Percorre todas as produ��es da gram�tica procurando por s�mbolos alcan��veis*/
		ArrayList<String> reach = new ArrayList<String>();
		reach.add(gramatica.get(0).getVariavel());
		ArrayList<String> prev = new ArrayList<String>();
		ArrayList<String> novo;
		do {
			novo = (ArrayList<String>) reach.clone();
			novo.removeAll(prev);
			prev = (ArrayList<String>) reach.clone();
			for(String variavel : novo){
				ArrayList<String> regras =  new ArrayList<String>();
				int indiceProducao = 0;
				while((indiceProducao < gramatica.size()) && regras.isEmpty()) {
					//Busca pelas regras da vari�vel atual
					if(variavel.equals(gramatica.get(indiceProducao).getVariavel())){
						regras = gramatica.get(indiceProducao).getRegras();
					}
					indiceProducao++;
				}
				for(String regra : regras){
					//Procura por s�mbolos alcan��veis em uma produ��o
					for(char variavel2 : regra.toCharArray()){
						if(Character.isUpperCase(variavel2) && !reach.contains(variavel2)){
							reach.add(String.valueOf(variavel2));
						}
					}
				}
			}
		} while (!reach.equals(prev));
		
		return reach;
	}
	
	public void eliminarSimbolosInuteis(){
		/*M�todo respons�vel por eliminar as produ��es in�teis*/
		//encontra os s�mbolos terminais
		ArrayList<String> term = encontrarTerm();
		int indiceProducao = 0;
		Producao producao;
		while (indiceProducao < gramatica.size()) {
			//remove as produ��es que n�o possuem s�mbolos terminais
			producao = gramatica.get(indiceProducao);
			if(!term.contains(producao.getVariavel())){
				for(Producao producao2 : gramatica){
					String regra;
					int indiceRegra = 0;
					while(indiceRegra < producao2.getRegras().size()){
						//remove as regras que n�o possuem s�mbolos terminais
						regra = producao2.getRegras().get(indiceRegra);
						if(regra.contains(producao.getVariavel())){
							producao2.getRegras().remove(regra);
							indiceRegra--;
						}
						indiceRegra++;
					}
				}
				gramatica.remove(producao);
				indiceProducao--;
			}
			indiceProducao++;
		}
		
		//encontra os s�mbolos alcan��veis
		ArrayList<String> reach = encontrarReach();
		indiceProducao = 0;
		while (indiceProducao < gramatica.size()) {
			//remove as produ��es que n�o possuem s�mbolos alcan��veis
			producao = gramatica.get(indiceProducao);
			if(!reach.contains(producao.getVariavel())){
				for(Producao producao2 : gramatica){
					String regra;
					int indiceRegra = 0;
					while(indiceRegra < producao2.getRegras().size()){
						//remove as regras que possuem s�mbolos n�o alcan��veis
						regra = producao2.getRegras().get(indiceRegra);
						if(regra.contains(producao.getVariavel())){
							producao2.getRegras().remove(regra);
							indiceRegra--;
						}
						indiceRegra++;
					}
				}
				gramatica.remove(producao);
				indiceProducao--;
			}
			indiceProducao++;
		}
		String variavel = gramatica.get(0).getVariavel();
		if(!variavel.equals("S") && !variavel.equals("�")){
			gramatica.get(0).setVariavel("S");	
		}
	}
	
	private int tamanhoRegra(String regra, ArrayList<String> variaveisGeradas){
		/*Met�do respons�vel por calcular o tamanho de uma regra, necess�rio para o 
		  formaNormalChomsky()*/
		if(variaveisGeradas.isEmpty()){
			return regra.length();
		}
		StringBuilder copiaRegra = new StringBuilder(regra);
		int tamanhoAbsoluto = regra.length(), 
			indiceVariavel = variaveisGeradas.size() - 1,
			indiceBusca, pesoVariaveisGeradas = 0;
		String variavel;
		do {
			variavel = variaveisGeradas.get(indiceVariavel);
			do {
				indiceBusca = copiaRegra.indexOf(variavel);
				if(indiceBusca != -1){
					//contabiliza o peso da vari�vel gerada, ou seja, o quanto
					//ela excedeu o tamanho unit�rio.
					copiaRegra.delete(indiceBusca, indiceBusca + variavel.length());
					pesoVariaveisGeradas += variavel.length() - 1;
				}
			} while (indiceBusca != -1);
			indiceVariavel--;
		} while (indiceVariavel >= 0);
		//As vari�veis geradas possuem tamanho mais que 1, portanto essa diferen�a deve
		//ser subtraida do tamanho absoluto para se chegar ao tamanho real.
		return tamanhoAbsoluto - pesoVariaveisGeradas;
	}
	
	private void primeiraTransformacao(ArrayList<String> variaveisGeradas){
		/*M�todo respons�vel por realizar o primeiro passo de Chomsky.
		  Transformar caracteres minusculos que est�o em conjunto com as regras em mai�sculo*/
		int indiceProducao = 0;
		Producao producao;
		while (indiceProducao < gramatica.size()){
			//Para cada produ��o da gram�tica
			producao = gramatica.get(indiceProducao);
			String regra;
			int indiceRegra = 0;
			while (indiceRegra < producao.getRegras().size()){
				//Para cada regra da produ��o
				regra = producao.getRegras().get(indiceRegra);
				if(tamanhoRegra(regra, variaveisGeradas) >= 2){
					//Se a regra possui tamanho maior ou igual a 2 (est� fora da FNC)
					int indiceVariavel = 0;
					char variavel;
					String novaVariavel= "";
					Producao novaProducao = null;
					while (indiceVariavel < regra.length()){
						variavel = regra.charAt(indiceVariavel);
						if(Character.isLowerCase(variavel)){
							//Se possui s�mbolos terminais na regra, uma nova produ��o
							//� gerada e a regra � corrigida.
							novaVariavel = Character.toUpperCase(variavel) + "'";
							ArrayList<String> novasRegras = new ArrayList<String>(); 
							novasRegras.add(String.valueOf(variavel));
							novaProducao = new Producao(novaVariavel, novasRegras);
							regra = regra.replaceAll(String.valueOf(variavel), novaVariavel);
							if(!variaveisGeradas.contains(novaVariavel)){
								variaveisGeradas.add(novaVariavel);
								gramatica.add(novaProducao);
							}
						}
						indiceVariavel++;
					}
					if(novaVariavel != ""){
						producao.getRegras().remove(indiceRegra);
						producao.getRegras().add(regra);
						indiceRegra--;
					}
				}
				indiceRegra++;
			}
			indiceProducao++;
		}
	}
	
	private void segundaTransformacao(ArrayList<String> variaveisGeradas){
		/*M�todo respons�vel por realizar o segundo passo de Chomsky.
		  Transformar uma regra em bin�ria*/
		int indiceProducao = 0, contNovasVariaveis = 1;
		Producao producao;
		while (indiceProducao < gramatica.size()){
			//Para cada produ��o da gram�tica
			producao = gramatica.get(indiceProducao);
			String regra;
			int indiceRegra = 0;
			ArrayList<String> regrasCorrigidas = new ArrayList<String>();
			while (indiceRegra < producao.getRegras().size()){
				//Para cada regra da produ��o
				regra = producao.getRegras().get(indiceRegra);
				if(tamanhoRegra(regra, variaveisGeradas) > 2){
					//Se a regra possui tamanho maior ou igual a 2 (est� fora da FNC)
					//Encontra o indice da nova regra
					int indiceNovaRegra = 1;
					for(String variavel : variaveisGeradas){
						if(regra.indexOf(variavel) == 0){
							indiceNovaRegra = variavel.length();
						}
					}
					//Gera a nova produ��o
					String novaVariavel = "T" + String.valueOf(contNovasVariaveis);
					ArrayList<String> novasRegras = new ArrayList<String>();
					novasRegras.add(regra.substring(indiceNovaRegra, regra.length()));
					Producao novaProducao = new Producao(novaVariavel, novasRegras);
					boolean producaoJaExiste = false;
					for(Producao p : gramatica){
						if(p.getVariavel().equals(novaVariavel)){
							producaoJaExiste = true;
							break;
						}
					}
					if(!producaoJaExiste){
						gramatica.add(novaProducao);
					}
					//Remove a regra errada e adiciona a corre��o a uma lista
					producao.getRegras().remove(indiceRegra); 
					regrasCorrigidas.add(regra.substring(0, indiceNovaRegra).concat(novaVariavel));
					indiceRegra--;
				}
				indiceRegra++;
			}
			//Adiciona a lista de corre��es � produ��o
			for(String regraCorrigida : regrasCorrigidas){
				producao.adicionarRegra(regraCorrigida);
			}
			indiceProducao++;
		}
	}
	
	public void formaNormalChomsky(){
		/*M�todo respons�vel por transformar uma gram�tica na forma normal de Chomsky*/
		ArrayList<String> variaveisGeradas = new ArrayList<String>();
		primeiraTransformacao(variaveisGeradas);
		segundaTransformacao(variaveisGeradas);
	}
	
	public ArrayList<Producao> getGramatica() {
		return gramatica;
	}

	public void setGramatica(ArrayList<Producao> gramatica) {
		this.gramatica = (ArrayList<Producao>)gramatica.clone();
	}

}