package br.com.gustavo.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {

    public enum TipoComando {
        ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
    }

    private static final Memoria instancia = new Memoria();
    //com o final, nem adiantará por um set pois não será possível 
    //alterar o valor

    private String textoAtual = "";

    private final List<MemoriaObservador> observadores =
        new ArrayList<>();

    private Memoria(){

    }

    public static Memoria getInstancia(){
        return instancia;
    }

    public void adicionarObservador(MemoriaObservador observador){
        observadores.add(observador);
    }

    public String getTextoAtual(){
        return textoAtual.isEmpty() ? "0" : textoAtual;
        //com isso caso não haja nenhum valor, a calculadora irá mostrar 0
    }

    public void processarComando(String texto){

        //é possível criar ENUM's como variáveis
        TipoComando tipoComando = detectarTipoComando(texto);

        if("AC".equals(texto)){
            textoAtual = "";
        }else {
            textoAtual += texto;
        }

        observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
    }

    private TipoComando detectarTipoComando(String texto){
        
        if(textoAtual.isEmpty() && texto == "0"){
            return null;
        }

        try{
            Integer.parseInt(texto);
        }catch(NumberFormatException e) {
            //Aqui não é número

            if("AC".equals(texto)){
                return TipoComando.ZERAR;
            }else if("/".equals(texto)){
                return TipoComando.DIV;
            }else if("*".equals(texto)){
                return TipoComando.MULT;
            }else if("+".equals(texto)){
                return TipoComando.SOMA;
            }else if("-".equals(texto)){
                return TipoComando.SUB;
            }else if("=".equals(texto)){
                return TipoComando.IGUAL;
            }else if(",".equals(texto)){
                return TipoComando.VIRGULA;
            }
        }

        return null;
    }
}
