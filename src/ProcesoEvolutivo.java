import java.util.LinkedList;
import java.util.Random;

/**
 * Aqui se implementa el algoritmo genetico
 * @author Gaitan Sandra
 */
public class ProcesoEvolutivo {
    private Codificador codificador;
    private Random random;
    private double[][] red;
    /**
     * Constructor
     * Asigna la red con los pesos, el tamanio de la poblacion, y la cantidad de padres.
     * @param red double[][]
     * @param poblacion int
     * @param padres int
     */
    public ProcesoEvolutivo(double[][] red, int poblacion, int padres){
        codificador = new Codificador(red, poblacion, padres);
        random = new Random();
        this.red = red;
    }
    
    /**
     * Metodo que aplica un algoritmo genetico al problema del agente viajero
     * hace evolucionar la poblacion inicial x repeticiones.
     * @param repeticiones int
     */
    public void evolucionar(int repeticiones){
        /*
         * cruce: sirve para decidir cual se va a usar en una iteracion
         * mutacion: sirve para decidir que mutacion se va a usar en la iteracion
         */
        int i = 0, cruce, mutacion;
        while(i < repeticiones){
            System.out.println("\nIteracion: " + i);
            this.codificador.seleccionPadres();//Luego de reemplazar vuelve a seleccionar padres
            System.out.println("Padres: \n" + this.codificador.padres.toString());
            cruce = random.nextInt(3)+1;
            this.cruzar(cruce);
            System.out.println("Hijos obtenidos: \n" + this.codificador.hijos1.toString());
            mutacion = random.nextInt(2)+1;
            if(mutacion == 1) System.out.println("Podria mutar utilizando Displacement mutation");
            else if(mutacion == 2) System.out.println("Podria mutar utilizando Exchange mutation");
            this.mutar(mutacion);
            System.out.println("Luego de mutar: \n" + this.codificador.hijos1.toString());
            this.reemplazarPoblacion();
            
            i++;
        }
    }
    
    /**
     * Metodo que ayuda a decidir que cruce se va a utilizar.
     * @param opcion Un entero
     */
    private void cruzar(int opcion){
        /*
         * Se cruza padre 1 con 2, 2 con 3, 3 con 4, ...
         */
        switch(opcion){
            case 1: //Order crossover
                System.out.println("Cruce: Order Crossover" );
                for(int i = 0; i < this.codificador.padres.size()-2; i++)
                    this.codificador.OrderCrossOver(this.codificador.padres.get(i).getTrayectoria(), 
                            this.codificador.padres.get(i+1).getTrayectoria());
                break;
            case 2://Cycle crossover
                System.out.println("Cruce: Cycle Crossover" );
                 for(int i = 0; i < this.codificador.padres.size()-2; i++)
                    this.codificador.cycleCrossover(this.codificador.padres.get(i).getTrayectoria(), 
                            this.codificador.padres.get(i+1).getTrayectoria());
                break;
            case 3://Partially Maped Crossover
                System.out.println("Cruce: Partially Maped Crossover" );
                 for(int i = 0; i < this.codificador.padres.size()-2; i++){
                    this.codificador.partiallyMappedCrossover(this.codificador.padres.get(i).getTrayectoria(), 
                            this.codificador.padres.get(i+1).getTrayectoria());
                 }
                break;  
        }
    }
    
    /**
     * Metodo que ayuda a decidir si un hijo va a mutar o no, y en caso de que si decide
     * que mutacion va a usar. Solo mutan los hijos con probabilidad <= 0.2
     * @param mutacion int
     */
    private void mutar(int mutacion){
        int probabilidad, i = 0;
        while(i<this.codificador.hijos1.size()){
            probabilidad = ((int)(Math.random() * 5)+1);
            if(probabilidad <= 2)
                if(mutacion==1){
                    this.codificador.displacementAlternativo(this.codificador.hijos1.get(i).getTrayectoria());
                    this.codificador.hijos1.get(i).setMuto(true);
                    this.codificador.hijos1.get(i).setPeso(this.red);
                }else if(mutacion==2){
                    this.codificador.exchangeMutation(this.codificador.hijos1.get(i).getTrayectoria());
                    this.codificador.hijos1.get(i).setMuto(true);
                    this.codificador.hijos1.get(i).setPeso(this.red);
                }
            i++;
        }
    }
    
    private void reemplazarPoblacion(){
        /*
         * Todos los hijos reemplazan tours en la poblacion inicial :D
         * empezando por el peor padre.
         */
        int j = this.codificador.poblacion_inicial.length-1;
        while(!this.codificador.hijos1.isEmpty()){
            this.codificador.poblacion_inicial[j] = this.codificador.hijos1.removeFirst();
            j--;
        }
        this.codificador.padres = new LinkedList<>();
        
    }
}
