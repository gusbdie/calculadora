package br.com.gustavo.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {

    public enum TipoComando {
        ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA, INVERTER;
    }

    private static final Memoria instancia = new Memoria();
    //com o final, nem adiantará por um set pois não será possível 
    //alterar o valor
    private final List<MemoriaObservador> observadores =
        new ArrayList<>();

    private TipoComando ultimaOperacao = null;
    //armazena qual variável será executada na operação
    private boolean substituir = false;
    //ele que vai ser responsável pela troca de valor mostrado no display
    private String textoAtual = "";
    private String textoBuffer = "";
    //variavél responsável por armazenar os números do cálculo

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

        if(tipoComando == null){
            return;
        }else if(tipoComando == TipoComando.ZERAR){
            textoAtual = "";
            textoBuffer = "";
            substituir = false;
            ultimaOperacao = null;
        }else if(tipoComando == TipoComando.NUMERO 
        || tipoComando == TipoComando.VIRGULA){
            textoAtual = substituir ? texto : textoAtual + texto;
            substituir = false;
        }else if(tipoComando == TipoComando.INVERTER && textoAtual.contains("-")){
            textoAtual = textoAtual.substring(1);
        }else if(tipoComando == TipoComando.INVERTER && !textoAtual.contains("-")){
            textoAtual = "-" + textoAtual;
        }
        else{//aqui vai ficar os cálculos matemáticos
            substituir = true;
            textoAtual = obterResultadoOperacao();
            textoBuffer = textoAtual;
            ultimaOperacao = tipoComando;
        }

        observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
    }

    private String obterResultadoOperacao(){
        if(ultimaOperacao == null 
        || ultimaOperacao == TipoComando.IGUAL){
            return textoAtual;
        }

        double numeroBuffer = 
        Double.parseDouble(textoBuffer.replace(",", "."));
        double numeroAtual = 
        Double.parseDouble(textoAtual.replace(",", "."));
        
        double resultado = 0;

        if(ultimaOperacao == TipoComando.SOMA){
            resultado = numeroBuffer + numeroAtual;
        }else if(ultimaOperacao == TipoComando.SUB){
            resultado = numeroBuffer - numeroAtual;
        }else if(ultimaOperacao == TipoComando.MULT){
            resultado = numeroBuffer * numeroAtual;
        }else if(ultimaOperacao == TipoComando.DIV){
            resultado = numeroBuffer / numeroAtual;
        }

        String resultadoString = 
        Double.toString(resultado).replace(".", ",");
        boolean inteiro = resultadoString.endsWith(",0");
        //muito interessante esse endsWith, poupa muito trabalho
        return inteiro ? resultadoString.replace(",0", "") 
        : resultadoString;
    }

    private TipoComando detectarTipoComando(String texto){
        
        if(textoAtual.isEmpty() && texto == "0"){
            return null;
        }

        try{
            Integer.parseInt(texto);
            return TipoComando.NUMERO;
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
            }else if(",".equals(texto) && !textoAtual.contains(",")){
                return TipoComando.VIRGULA;
            }else if("+-".equals(texto)){
                return TipoComando.INVERTER;
            }
        }
        return null;
    }
}
