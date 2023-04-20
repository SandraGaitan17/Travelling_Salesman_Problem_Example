import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Clase que contiene metodos para generar la poblacion inicial, conjunto de padres, cruces y mutaciones.
 * @author Gaitan Sandra
 */
public class Codificador {
    
    private double[][] red;//Matriz que contiene los pesos de las aristas
    private int N, numPobladores, pad;//N el tamanio de cada permutacion a crear, numPobladores tamanio de la poblacion inicial.
    //poblacion_inicial: Un arreglo de tours que almacena la poblacion inicial
    //padres: Un arreglo de tours con los padres seleccionados
    public Tour[] poblacion_inicial;
    LinkedList<Tour> hijos1 = new LinkedList<>();//Lista que va a guardar a los hijos.
    LinkedList<Tour> padres;
    private Random random;
    
    /**
     * Constructor de clase.
     * Asigna la poblaci&oacute;n inicial, y el conjunto de padres.
     * @param red La matriz con los pesos 
     * @param t Numero de pobladores.
     * @param totPadres Tamanio del conjunto de padres.
     */
    public Codificador(double[][] red, int t, int totPadres){
        this.red = red;
        this.N = this.red.length-1;
        this.numPobladores = t;
        this.padres = new LinkedList<>();
        this.random = new Random();
        this.poblacion_inicial = this.generar();
        this.pad = totPadres;
    }
    
    /**
     * Metodo que genera m permutaciones de tamanio n. Cada permutaci&oacute;n representa un tour.
     * @param n
     * @param m
     * @return 
     */
    private Tour[] generar(){
        Tour[] generacion = new Tour[this.numPobladores];
        int[] conjuntoInicial = new int[this.N];
        
        //Llenamos el conjunto inicial con los numeros {1, 2, ... , N = tam}
        for(int i = 0; i < this.N; i++){
            conjuntoInicial[i] = i+1;
        }
        int temp1, temp2;
        Tour permutacion;
        for(int i = 0; i < this.numPobladores; i++){
            int[] auxiliar = new int[this.N];
            System.arraycopy(conjuntoInicial, 0, auxiliar, 0, conjuntoInicial.length);
            for(int j = 0; j < this.N; j++){
                temp1 = random.nextInt(this.N);
                temp2 = auxiliar[j];                
                auxiliar[j] = auxiliar[temp1];
                auxiliar[temp1] = temp2;
            }
            permutacion = new Tour(auxiliar, this.setPeso(auxiliar));
            generacion[i] = permutacion;
        }
        return generacion;
    }
    
    public void seleccionPadres(){
        int elegidos = this.pad;
        Arrays.sort(this.poblacion_inicial);
        int salto = this.poblacion_inicial.length / elegidos;
        for(int i = 0; i < elegidos; i++)
            this.padres.add(this.poblacion_inicial[i]);
        
    }
    
    /**
     * Metodo que calcula el peso del tour dado.
     * @param trayectoria int[]
     * @return double
     */
    public double setPeso(int[] trayectoria){
        double peso = 0.0;
        for(int i = 0; i < trayectoria.length-1; i++){
            peso += this.red[trayectoria[i]][trayectoria[i+1]];
        }
        peso += this.red[trayectoria[trayectoria.length-1]][trayectoria[0]];
        return peso;
    }
    
    /**
     * Obtiene el indice en el que se encuentra el objeto e dentro del arreglo arr
     * @param e Un entero
     * @param arr Un arreglo de enteros
     * @return int 
     */
    private int indice(int e, int[] arr){
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == e) return i;
        }
        return -1;
    }
    
    /**
     * Metodo que implementa cycleCrossover
     * @param padre1 int[]
     * @param padre2 int[]
     */
    public void cycleCrossover(int[] padre1, int[] padre2){
        int[] hijo1 = new int[padre1.length], hijo2 = new int[padre1.length];
        Arrays.fill(hijo1, -1);
        Arrays.fill(hijo2, -1);
        /*
         * Etapa1 : Se agregan los elementos de los padres en los hijos en forma de ciclo.
         * P1:[1,2,3,4,5,6]
         * P2:[2,4,3,6,5,1]
         * H1:[1,2,x,4,x,6]
         */
        int temp1 = 0, temp2 = 0, contador1 = 0, contador2 = 0;
        boolean termino1 = false, termino2 = false;
        while(!termino1 || !termino2){
            if(!termino1){
                hijo1[temp1] = padre1[temp1];
                contador1++;
                if((temp1 = this.indice(padre2[temp1], padre1)) == 0) termino1 = true;
            }
            if(!termino2){
                hijo2[temp1] = padre2[temp1];
                contador2++;
                if((temp2 = this.indice(padre1[temp2], padre2)) == 0) termino2 = true;
            }
        }
        /*
         * Etapa2: Todos las ciudades que no entraron en el ciclo se intercambian con el 
         * otro padre y se asignan al hijo corresponiente.
         */
        if(contador1 < hijo1.length)
            for(int i = 0; i < hijo1.length; i++)
                if(hijo1[i] == -1) hijo1[i] = padre2[i];
        if(contador2 < hijo2.length)
            for(int i = 0; i < hijo2.length; i++)
                if(hijo2[i] == -1) hijo2[i] = padre1[i];
        //Agregamos los hijos obtenidos a nuestro conjunto de hijos.
        this.hijos1.add(new Tour(hijo1, this.setPeso(hijo1)));
        this.hijos1.add(new Tour(hijo2, this.setPeso(hijo2)));
    }
    
    
    /**
     * Metodo que implementa Partially Mapped Crossover
     * @param padre1 int[]
     * @param padre2 int[]
     */
    public void partiallyMappedCrossover(int[] padre1, int[] padre2){
        int mitad = padre1.length/2;
        /*
         * Etapa1: Obtenemos los indices dentro de los cuales estaran nuestros subtours iniciales.
         */
        int pos1 = (int)Math.floor(Math.random()*(0-mitad+1)+mitad);
        int pos2 = (int)Math.floor(Math.random()*(mitad-padre1.length+1)+padre1.length);
        int[] hijo1 = new int[padre1.length], hijo2 = new int[padre1.length];
        Arrays.fill(hijo1, -1);
        Arrays.fill(hijo2, -1);
        /*
         * Etapa 2: Intercambiamos los elementos que estan entre pos1 y pos2 de los padres y se 
         * asignan a los hijos.
         */
        for(int i = pos1; i <= pos2; i++){
            hijo1[i] = padre2[i];
            hijo2[i] = padre1[i];
        }
        /*
         * Etapa3: Agregamos a los hijos todos los elementos de los padres que no tienen conflicto luego del swap
         */
        for(int i = 0; i < pos1; i++){
            if(indice(padre1[i], hijo1) == -1) hijo1[i] = padre1[i];
            if(indice(padre2[i], hijo2) == -1) hijo2[i] = padre2[i];
        }
        for(int i = pos2+1; i < padre1.length; i++){
            if(indice(padre1[i], hijo1) == -1) hijo1[i] = padre1[i];
            if(indice(padre2[i], hijo2) == -1) hijo2[i] = padre2[i];
        }
        //Etapa 4 los elementos que tenian conflicto los cambiamos por un elemento adecuado.
        //Intercambiamos los elementos en conflicto de hijo1 por los de hijo2
        int temp1, temp2, temp3, temp4, i;
        boolean termino = false;
        //for(int i = 0; i < hijo1.length; i++){
        while(!termino){
            if((i = indice(-1, hijo1)) != -1){
                temp2 = indice(-1, hijo2);
                hijo1[i] = padre2[indice(-1, hijo2)];
                hijo2[temp2] = padre1[i];
            } else termino = true;            
        }
        //Agregamos los hijos obtenidos a nuestro conjunto de hijos.
        this.hijos1.add(new Tour(hijo1, this.setPeso(hijo1)));
        this.hijos1.add(new Tour(hijo2, this.setPeso(hijo2)));
    }
    
    /**
     * Metodo que implementa Order Crossover
     * @param padre1
     * @param padre2 
     */
    public void OrderCrossOver(int[] padre1, int[] padre2){
        int mitad = padre1.length/2;
        /*
         * Etapa1: Elegimos dos puntos al azar para obtener un subtour en cada padre
         */
        int pos1 = (int)Math.floor(Math.random()*(0-mitad+1)+mitad);
        int pos2 = (int)Math.floor(Math.random()*(mitad-padre1.length+1)+padre1.length);
        int[] hijo1 = new int[padre1.length], hijo2 = new int[padre1.length];
        /*
         * Etapa 2: Intercambiamos los elementos que estan entre pos1 y pos2 de los padres
         */
        for(int i = pos1; i <= pos2; i++){
            hijo1[i] = padre2[i];
            hijo2[i] = padre1[i];
        }
        /*
         * Etapa3: Obtenemos las secuencias correspondientes en cada padre.
         */
        LinkedList<Integer> secuencia1 = new LinkedList<>();
        LinkedList<Integer> secuencia2 = new LinkedList<>();
        int j = 0;
        for(int i = 0; i < padre1.length; i++){
            if(i > pos2){
                secuencia1.add(j, padre1[i]);
                secuencia2.add(j++, padre2[i]);   
            }else {
                secuencia1.add(i, padre1[i]);
                secuencia2.add(i, padre2[i]);
            }
        }
        /*
         * Eliminamos en la secuencia 1 la subsecuencia ya asignada en el hijo 1. 
         * Eliminamos en la secuencia 2 la subsecuencia ya asignada en el hijo 2.
         */
        for(int i = pos1; i <= pos2; i++){
            secuencia1.removeFirstOccurrence(padre2[i]);
            secuencia2.removeFirstOccurrence(padre1[i]);
        }
        
        /**
         * Etapa4: Luedo de limpiar las secuencias las agregamos en orden a cada hijo.
         */
        for(int i = pos2+1; i < padre1.length; i++){
            hijo1[i] = secuencia1.removeFirst();
            hijo2[i] = secuencia2.removeFirst();
        }
        for(int i = 0; i < pos1; i++){
            hijo1[i] = secuencia1.removeFirst();
            hijo2[i] = secuencia2.removeFirst();
        }
        //Agregamos los hijos obtenidos a nuestro conjunto de hijos.
        hijos1.add(new Tour(hijo1, this.setPeso(hijo1)));
        hijos1.add(new Tour(hijo2, this.setPeso(hijo2)));
    }
    
    /**
     * Metodo que realiza cambio reciproco
     * @param hijo int[]
     */
    public void exchangeMutation(int[] hijo){
        int mitad = hijo.length/2;
        int pos1 = (int)Math.floor(Math.random()*(0-mitad+1)+mitad);
        int pos2 = (int)Math.floor(Math.random()*(mitad-hijo.length+1)+hijo.length);
        int temp = hijo[pos1];
        hijo[pos1] = hijo[pos2];
        hijo[pos2] = temp;
    }
    
    /**
     * Metodo que implementa displacement mutation (un subtour)
     * @param hijo int[]
     */
    public void displacementMutation(int[] hijo){
        int mitad = hijo.length/2;
        int pos1 = (int)Math.floor(Math.random()*(0-mitad+1)+mitad);//Aleatorio entre el inicio y la mitad del arreglo
        int pos2 = (int)Math.floor(Math.random()*(mitad-hijo.length+1)+hijo.length);//Aleatorio entre la mitad y el fin de arreglo
        LinkedList<Integer> temp = new LinkedList<>();
        for(int i = pos1; i <= pos2; i++){
            temp.addLast(hijo[i]);
            hijo[i] = -1;
        }
        //Elegimos un lugar aleatorio en el arreglo pero restringido al tamanio del subtour elegido.
        int lugar = random.nextInt(hijo.length-temp.size());
        System.out.println("pos1: " + pos1 + " pos2: " + pos2 + " lugar: " + lugar);
        int j = 0;
        if(pos1 == lugar)
            for(int i = lugar; i <= pos2; i++)
                hijo[i] = temp.removeFirst();
        if(pos2==lugar){
            hijo[lugar] = hijo[pos1];
            for(int i = lugar+1, k = pos1+1; k < lugar; k++, i++ ){
                hijo[k] = hijo[i];
                hijo[i] = temp.removeFirst();
           }
        }
        if(pos1 < lugar){
            if(pos2 > lugar){
                //agregamos todos los elementos del subtour que no desplazan otros elementos
                for(int i = lugar; i < pos2; i++)
                    hijo[i] = temp.removeFirst();
                //
                for(int i = pos2; i <= (lugar+(pos2-pos1)); i++){
                    hijo[indice(-1, hijo)] = hijo[i];
                    hijo[i] = temp.removeFirst();
                }
            }            
        }
        if(lugar < pos1){
            int desplazar = pos2-pos1+1;
            //Desplazamos a todos los elementos que se tienen que recorrer
            for(int i = pos1-1; i >= lugar; i--){
                hijo[i+desplazar] = hijo[i];
            }
            //Insertamos los elementos del subtour seleccionado
            for(int i = lugar; i <= lugar+(pos2-pos1); i++){
                hijo[i] = temp.removeFirst();
            }
        }
        System.out.println(Arrays.toString(hijo));
        
    }
    
    public void displacementAlternativo(int[] hijo){
        int mitad = hijo.length/2;
        int pos1 = (int)Math.floor(Math.random()*(0-mitad+1)+mitad);//Aleatorio entre el inicio y la mitad del arreglo
        int pos2 = (int)Math.floor(Math.random()*(mitad-hijo.length+1)+hijo.length);//Aleatorio entre la mitad y el fin de arreglo
        LinkedList<Integer> temp = new LinkedList<>();
        LinkedList<Integer> hijotemp = new LinkedList<>();
        for(int i = 0; i < hijo.length; i++){
            if(i >= pos1 && i <= pos2){
                temp.addLast(hijo[i]);
                hijo[i] = -1;
            }
            else{
                hijotemp.addLast(hijo[i]);
            }
        }
        int lugar = random.nextInt(hijotemp.size()-1);
        int i = lugar;
        while(!temp.isEmpty()){
            hijotemp.add(i++, temp.removeFirst());
        }
        for(int j = 0; j < hijo.length; j++){
            hijo[j] = hijotemp.get(j);
        }
        
    }
}
